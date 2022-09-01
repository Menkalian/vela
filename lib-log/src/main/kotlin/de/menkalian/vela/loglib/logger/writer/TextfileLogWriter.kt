package de.menkalian.vela.loglib.logger.writer

import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.LogEntry
import de.menkalian.vela.loglib.logger.writer.config.FileLoggerConfiguration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class TextfileLogWriter(spec: LoggerConfiguration.LogSpec, context: LoggerContext, config: FileLoggerConfiguration) :
    FileLogWriter(spec, context, config, "log"), LogWriter {

    override suspend fun onLogEntry(collected: Boolean, entry: LogEntry) {
        val msg = formatter.format(entry.msgLocale, entry.msgFormat, *entry.msgArgs.toTypedArray())
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.timestampMs), ZoneId.systemDefault())
        val threadName = entry.threadName.padStart(20, ' ')
        val threadId = entry.threadId.toString().padEnd(12, ' ')
        val logLevel = entry.logLevel.name.padEnd(9, ' ')

        write(
            buildString {
                append(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).padEnd(23, '0')).append(" ")
                append("T:$threadName($threadId) ")
                append(logLevel).append(" ")
                append(entry.origin.padEnd(18, ' ')).append(" ")
                append(msg)
                append("\n")
            }
        )
    }
}
