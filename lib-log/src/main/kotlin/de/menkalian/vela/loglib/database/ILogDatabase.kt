package de.menkalian.vela.loglib.database

import de.menkalian.vela.loglib.LogEntry

internal interface ILogDatabase {
    fun writeLogEntry(logEntry: LogEntry)
}