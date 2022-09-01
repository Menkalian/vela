package de.menkalian.vela.loglib

import java.util.Locale

/**
 * Interface for an individual [Logger].
 * To obtain an instance, use [LoggerFactory.getLogger].
 *
 * @property enabled Whether this logger is enabled or not
 * @property name Unique name of this logger
 * @property minLogLevel Minimum importance handled by this logger
 */
interface Logger {
    val enabled: Boolean
    val name: String
    val minLogLevel: LogLevel

    /**
     * Log a message to the [LogLevel.ESSENTIAL] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun essential(msgFormat: String, vararg args: Any) = log(LogLevel.ESSENTIAL, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.ESSENTIAL] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun essential(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.ESSENTIAL, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.FATAL] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun fatal(msgFormat: String, vararg args: Any) = log(LogLevel.FATAL, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.FATAL] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun fatal(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.FATAL, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.ERROR] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun error(msgFormat: String, vararg args: Any) = log(LogLevel.ERROR, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.ERROR] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun error(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.ERROR, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.WARN] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun warn(msgFormat: String, vararg args: Any) = log(LogLevel.WARN, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.WARN] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun warn(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.WARN, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.INFO] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun info(msgFormat: String, vararg args: Any) = log(LogLevel.INFO, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.INFO] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun info(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.INFO, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.DEBUG] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun debug(msgFormat: String, vararg args: Any) = log(LogLevel.DEBUG, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.DEBUG] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun debug(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.DEBUG, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.TRACE] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun trace(msgFormat: String, vararg args: Any) = log(LogLevel.TRACE, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.TRACE] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun trace(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.TRACE, locale, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.SILENT] level.
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun silent(msgFormat: String, vararg args: Any) = log(LogLevel.SILENT, msgFormat, *args)

    /**
     * Log a message to the [LogLevel.SILENT] level.
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun silent(locale: Locale?, msgFormat: String, vararg args: Any) = log(LogLevel.SILENT, locale, msgFormat, *args)

    /**
     * Log a message to any level.
     * @param level [LogLevel] to use
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun log(level: LogLevel, msgFormat: String, vararg args: Any)

    /**
     * Log a message to any level.
     * @param level [LogLevel] to use
     * @param locale Optional locale to use for formatting
     * @param msgFormat Format string to create the message
     * @param args Arguments for formatting the given template
     */
    fun log(level: LogLevel, locale: Locale?, msgFormat: String, vararg args: Any)

    /**
     * Forward a [LogEntry] received from any other logger and write it to this logger as well.
     * @param entry [LogEntry] to log
     */
    fun logCollected(entry: LogEntry)
}