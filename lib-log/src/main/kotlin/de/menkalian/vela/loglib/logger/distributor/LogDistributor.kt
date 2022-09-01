package de.menkalian.vela.loglib.logger.distributor

import de.menkalian.vela.loglib.LogEntry

internal interface LogDistributor {
    fun addListener(listener: LogEventListener)
    fun distribute(collected: Boolean, entry: LogEntry)
}