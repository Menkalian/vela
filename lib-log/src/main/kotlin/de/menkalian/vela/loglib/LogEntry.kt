package de.menkalian.vela.loglib

import java.util.Locale

/**
 * Class representing a single logging message (entry)
 *
 * @property logger LogSpec this entry is associated with
 * @property logLevel Importance of this entry
 * @property threadName Name of the thread this entry was logged from
 * @property threadId Identifier of the thread this entry was logged from
 * @property origin JVM-Class, Method and Linenumber this entry was logged from (is not filled if [LoggerConfiguration.determineLogOrigin] is not set `true`)
 * @property timestampMs Unix-Timestamp (Milliseconds) when this entry was logged at
 * @property msgLocale Optional [Locale] to use for formatting the message of this entry
 * @property msgFormat Format-String to create the message of this entry
 * @property msgArgs Arguments for formatting the message of this entry
 */
data class LogEntry(
    val logger: LoggerConfiguration.LogSpec,
    val logLevel: LogLevel,
    val threadName: String,
    val threadId: Long,
    val origin: String,
    val timestampMs: Long,
    val msgLocale: Locale?,
    val msgFormat: String,
    val msgArgs: List<Any>,
)
