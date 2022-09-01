package de.menkalian.vela.loglib

import de.menkalian.vela.loglib.factory.DefaultLoggerFactory

/**
 * Management class for the configuration and the loggers
 * @property configuration [LoggerConfiguration] in use
 */
interface LoggerFactory {
    val configuration: LoggerConfiguration

    /**
     * Obtains the default logger.
     * @return [Logger] for common usage
     */
    fun getDefaultLogger(): Logger

    /**
     * Obtain all loggers
     * @return all [Logger] instances available
     */
    fun getLoggers(): List<Logger>

    /**
     * Obtain a logger matching the given name
     */
    fun getLogger(name: String): Logger

    /**
     * Obtain a logger for the given object
     */
    fun getLogger(obj: Any): Logger

    /**
     * Obtain a logger for the given class
     */
    fun getLogger(clazz: Class<*>): Logger

    /**
     * Modify the configuration of the library.
     *
     * **THIS MUST NOT BE CALLED AFTER ANY LOGGERS ARE OBTAINED BY THE OTHER METHODS!**
     * The configuration must happen before any logger is obtained.
     */
    fun configure(config: LoggerConfigurationBuilder.() -> Unit)

    /**
     * Container for the [LoggerFactory] instance.
     */
    companion object {
        /**
         * Get the currently available [LoggerFactory] instance
         */
        @JvmStatic
        fun getFactory(): LoggerFactory = DefaultLoggerFactory
    }
}