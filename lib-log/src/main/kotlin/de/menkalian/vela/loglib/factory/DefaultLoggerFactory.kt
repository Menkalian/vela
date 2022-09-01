package de.menkalian.vela.loglib.factory

import de.menkalian.vela.loglib.Logger
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerConfigurationBuilder
import de.menkalian.vela.loglib.LoggerFactory
import de.menkalian.vela.loglib.config.DefaultLoggerConfiguration
import de.menkalian.vela.loglib.logger.DefaultLogger
import de.menkalian.vela.loglib.logger.distributor.CoroutineLogDistributor
import de.menkalian.vela.loglib.logger.distributor.LogDistributor
import de.menkalian.vela.loglib.logger.distributor.SynchronousLogDistributor
import java.util.concurrent.atomic.AtomicBoolean

internal object DefaultLoggerFactory : LoggerFactory {
    override val configuration: DefaultLoggerConfiguration = DefaultLoggerConfiguration.createDefault()
    private val configured = AtomicBoolean()

    private val logSpecs: MutableList<LoggerConfiguration.LogSpec> = mutableListOf()
    private val loggers: MutableMap<LoggerConfiguration.LogSpec, Logger> = mutableMapOf()
    private var defaultLogger: Logger? = null

    override fun getDefaultLogger(): Logger {
        configureLoggers()
        return defaultLogger!!
    }

    override fun getLoggers(): List<Logger> {
        configureLoggers()
        return loggers.values.toList()
    }

    override fun getLogger(name: String): Logger {
        configureLoggers()
        return logSpecs
            .firstOrNull { it.name == name || it.nameMatchRegex.toRegex().matches(name) }
            ?.let { getLogger(it) }
            ?: getDefaultLogger()
    }

    override fun getLogger(obj: Any): Logger {
        return getLogger(obj::class.java)
    }

    override fun getLogger(clazz: Class<*>): Logger {
        configureLoggers()
        return logSpecs
            .firstOrNull { it.classMatchRegex.toRegex().matches(clazz.canonicalName) }
            ?.let { getLogger(it) }
            ?: getDefaultLogger()
    }

    private fun getLogger(logSpec: LoggerConfiguration.LogSpec): Logger {
        return loggers[logSpec] ?: getDefaultLogger()
    }

    override fun configure(config: LoggerConfigurationBuilder.() -> Unit) {
        if (configured.get())
            throw RuntimeException("Can not configure after a logger was retrieved")
        configuration.config()
    }

    private fun configureLoggers() {
        if (!configured.getAndSet(true)) {
            logSpecs.clear()
            logSpecs.addAll(configuration.logSpecs)

            val collectors =
                mutableListOf<Pair<LoggerConfiguration.LogSpec, Logger>>()
            val distributors =
                mutableListOf<Pair<LogDistributor, Logger>>()

            for (logSpec in logSpecs) {
                val distributor =
                    if (configuration.enableMultithreadLogging) {
                        CoroutineLogDistributor()
                    } else {
                        SynchronousLogDistributor()
                    }
                val logger = DefaultLogger(
                    logSpec,
                    distributor,
                    configuration.determineLogOrigin,
                    configuration.globalLogLevel
                )

                if (logSpec == configuration.defaultLogSpec)
                    defaultLogger = logger

                configuration.logWriterCreators.forEach {
                    distributor.addListener(it(logSpec))
                }

                loggers[logSpec] = logger
                distributors.add(distributor to logger)

                if (logSpec.collectLogs) {
                    collectors.add(logSpec to logger)
                }
            }

            for (collector in collectors) {
                val regex = collector.first.collectedLoggersRegex.toRegex()

                distributors
                    .filter { regex.matches(it.second.name) }
                    .forEach {
                        it.first.addListener { collected, entry ->
                            if (!collected)
                                collector.second.logCollected(entry)
                        }
                    }
            }
        }
    }
}