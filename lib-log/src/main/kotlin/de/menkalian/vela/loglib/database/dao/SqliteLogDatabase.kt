package de.menkalian.vela.loglib.database.dao

import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.database.ILogDatabase
import de.menkalian.vela.loglib.LogEntry
import de.menkalian.vela.loglib.logger.formatter.Formatter
import org.jetbrains.exposed.sql.CompositeSqlLogger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class SqliteLogDatabase(context: LoggerContext) : ILogDatabase {
    private val db: Database
    private val formatter = Formatter()

    init {
        db = Database.connect(
            "jdbc:sqlite:${File(context.logDatabaseDirectory, "LOGS.DB3").absolutePath}",
            driver = "org.sqlite.JDBC",
            databaseConfig = DatabaseConfig.invoke {
                this.sqlLogger = CompositeSqlLogger()
            })

        transaction(db) {
            SchemaUtils.create(LogEntryData)
        }
    }

    override fun writeLogEntry(logEntry: LogEntry) {
        transaction(db) {
            LogEntryData.LogEntryEntry.new {
                logger = logEntry.logger.name
                logLevel = logEntry.logLevel
                logLevelInt = logEntry.logLevel.priority
                threadName = logEntry.threadName
                threadId = logEntry.threadId
                origin = logEntry.origin
                timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(logEntry.timestampMs),
                    ZoneId.systemDefault()
                )
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .padEnd(23, '0')
                timestampMs = logEntry.timestampMs
                message = formatter.format(logEntry.msgLocale, logEntry.msgFormat, *logEntry.msgArgs.toTypedArray())
            }
        }
    }
}