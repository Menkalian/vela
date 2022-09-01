package de.menkalian.vela.loglib.logger.distributor

import de.menkalian.vela.loglib.LogEntry
import kotlinx.coroutines.runBlocking

internal class SynchronousLogDistributor : LogDistributor {
    private val listeners = mutableListOf<LogEventListener>()

    override fun distribute(collected: Boolean, entry: LogEntry) {
        synchronized(this) {
            listeners.forEach {
                runBlocking {
                    it.onLogEntry(collected, entry)
                }
            }
        }
    }

    override fun addListener(listener: LogEventListener) {
        listeners.add(listener)
    }
}