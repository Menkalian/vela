package de.menkalian.vela.loglib.logger.writer

import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.database.ILogDatabase
import de.menkalian.vela.loglib.database.dao.SqliteLogDatabase
import de.menkalian.vela.loglib.LogEntry

internal class DatabaseLogWriter private constructor(private val database: ILogDatabase) : LogWriter {

    companion object {
        private var defaultInstance: DatabaseLogWriter? = null
        fun getDefault(context: LoggerContext): DatabaseLogWriter {
            if (defaultInstance == null)
                defaultInstance = DatabaseLogWriter(SqliteLogDatabase(context))
            return defaultInstance!!
        }
    }

    override suspend fun onLogEntry(collected: Boolean, entry: LogEntry) {
        database.writeLogEntry(entry)
    }
}