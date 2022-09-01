package de.menkalian.vela.loglib.logger.distributor

import de.menkalian.vela.loglib.LogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CoroutineLogDistributor : LogDistributor {
    private val listeners = mutableListOf<LogEventListener>()
    private val dispatcher = CoroutineScope(Dispatchers.Default)

    override fun distribute(collected: Boolean, entry: LogEntry) {
        listeners.forEach {
            dispatcher.launch {
                it.onLogEntry(collected, entry)
            }
        }
    }

    override fun addListener(listener: LogEventListener) {
        listeners.add(listener)
    }
}