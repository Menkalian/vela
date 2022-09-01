package de.menkalian.vela.loglib.logger.distributor

import de.menkalian.vela.loglib.LogEntry

fun interface LogEventListener {
    suspend fun onLogEntry(collected: Boolean, entry: LogEntry)
}