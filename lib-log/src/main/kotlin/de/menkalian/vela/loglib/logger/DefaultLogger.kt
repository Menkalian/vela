package de.menkalian.vela.loglib.logger

import de.menkalian.vela.loglib.LogEntry
import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.Logger
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.logger.distributor.LogDistributor
import java.util.Locale

internal class DefaultLogger(
    private val logSpec: LoggerConfiguration.LogSpec,
    private val distributor: LogDistributor,
    private val enableOrigin: Boolean,
    globalLogLevel: LogLevel
) : Logger {
    override val enabled: Boolean = logSpec.enabled
    override val name: String = logSpec.name
    private val defaultLocale = Locale.getDefault()

    override val minLogLevel: LogLevel =
        if (globalLogLevel.priority > logSpec.minLogLevel.priority) {
            globalLogLevel
        } else {
            logSpec.minLogLevel
        }

    override fun log(level: LogLevel, msgFormat: String, vararg args: Any) {
        log(level, defaultLocale, msgFormat, *args)
    }

    override fun log(level: LogLevel, locale: Locale?, msgFormat: String, vararg args: Any) {
        if (level.priority < minLogLevel.priority || !this.enabled) {
            return
        }

        val thread = Thread.currentThread()
        val origin = if (enableOrigin) determineOrigin() else ""

        distributor.distribute(
            false,
            LogEntry(
                logSpec,
                level,
                thread.name,
                thread.id,
                origin,
                System.currentTimeMillis(),
                locale,
                msgFormat,
                args.toList()
            )
        )
    }

    override fun logCollected(entry: LogEntry) {
        if (entry.logLevel.priority < minLogLevel.priority || !this.enabled) {
            return
        }
        val gatheredEntry = entry.copy(
            logger = logSpec,
            msgFormat = "${entry.logLevel}/${entry.logger.name} ${entry.msgFormat}"
        )
        distributor.distribute(true, gatheredEntry)
    }

    private fun determineOrigin(): String {
        val caller = Thread.currentThread().stackTrace
            .first {
                it.className.startsWith(Thread::class.qualifiedName!!).not()
                        && it.className.startsWith(DefaultLogger::class.qualifiedName!!).not()
                        && it.className.startsWith(Logger::class.qualifiedName!!).not()
            }
        return "${caller.className}(${caller.fileName})#${caller.methodName}:${caller.lineNumber}"
    }

    override fun toString(): String {
        return "Logger(name='$name')"
    }
}