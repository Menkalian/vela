package de.menkalian.vela.loglib.logger.writer

import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.LogEntry
import de.menkalian.vela.loglib.logger.writer.config.FileLoggerConfiguration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class JsonLogWriter(spec: LoggerConfiguration.LogSpec, context: LoggerContext, configuration: FileLoggerConfiguration) :
    FileLogWriter(spec, context, configuration, "log.json"), LogWriter {
    val serializer = Json

    override suspend fun onLogEntry(collected: Boolean, entry: LogEntry) {
        val serializableEntry = SerializableLogEntry(
            entry.logger.name,
            entry.logLevel,
            entry.threadName,
            entry.threadId,
            entry.origin,
            entry.timestampMs,
            formatter.format(entry.msgLocale, entry.msgFormat, *entry.msgArgs.toTypedArray())
        )
        write(serializer.encodeToString(serializableEntry) + "\n")
    }

    @kotlinx.serialization.Serializable
    data class SerializableLogEntry(
        val logger: String,
        val logLevel: LogLevel,
        val threadName: String,
        val threadId: Long,
        val origin: String,
        val timestampMs: Long,
        val msg: String,
    )
}