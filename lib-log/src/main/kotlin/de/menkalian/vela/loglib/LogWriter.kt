package de.menkalian.vela.loglib

import de.menkalian.vela.loglib.logger.distributor.LogEventListener

interface LogWriter : LogEventListener {
    val minimumLoggerLevel: LogLevel
        get() = LogLevel.SILENT
}