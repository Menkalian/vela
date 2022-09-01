package de.menkalian.vela.loglib.logger.writer

import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LogEntry
import de.menkalian.vela.loglib.logger.formatter.Formatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class StdoutLogWriter private constructor(private val enableAnsiColor: Boolean) : LogWriter {
    private val formatter = Formatter()

    private val ansiReset = if (enableAnsiColor) "\u001B[39m" else ""
    private val timestampColor = getAnsiRGB(226u, 227u, 179u)
    private val threadColor = getAnsiRGB(125u, 47u, 181u)
    private val originColor = getAnsiRGB(47u, 181u, 65u)

    override suspend fun onLogEntry(collected: Boolean, entry: LogEntry) {
        val msg = formatter.format(entry.msgLocale, entry.msgFormat, *entry.msgArgs.toTypedArray())
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.timestampMs), ZoneId.systemDefault())
        val threadName = entry.threadName
        val threadId = entry.threadId.toString()
        val logLevel = entry.logLevel.name.padEnd(9, ' ')

        println(
            buildString {
                append(timestampColor).append(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).padEnd(23, '0')).append(" ")
                append(colorOf(entry.logLevel)).append(logLevel).append(" ")
                append(threadColor).append("T:$threadName($threadId) ")
                append(originColor).append(entry.origin.padEnd(18, ' ')).append(" ")
                append(colorOf(entry.logLevel)).append(msg)
                append(ansiReset)
            }
        )
    }

    companion object {
        val default = StdoutLogWriter(false)
        val colored = StdoutLogWriter(true)
    }

    private fun colorOf(level: LogLevel): String {
        return when (level) {
            LogLevel.ESSENTIAL -> getAnsiRGB(255u, 56u, 245u)
            LogLevel.FATAL     -> getAnsiRGB(201u, 0u, 81u)
            LogLevel.ERROR     -> getAnsiRGB(255u, 69u, 69u)
            LogLevel.WARN      -> getAnsiRGB(250u, 176u, 85u)
            LogLevel.INFO      -> getAnsiRGB(55u, 192u, 230u)
            LogLevel.DEBUG     -> getAnsiRGB(183u, 219u, 154u)
            LogLevel.TRACE     -> getAnsiRGB(145u, 145u, 145u)
            LogLevel.SILENT    -> getAnsiRGB(201u, 226u, 255u)
        }
    }

    private fun getAnsiRGB(red: UByte, green: UByte, blue: UByte): String {
        if (enableAnsiColor) {
            return "\u001B[38;2;$red;$green;${blue}m"
        } else {
            return ""
        }
    }
}