package de.menkalian.vela.loglib

interface LoggerConfigurationBuilder : LoggerConfiguration {
    interface LogSpecBuilder : LoggerConfiguration.LogSpec {
        override var enabled: Boolean
        override var name: String
        override var minLogLevel: LogLevel
        override var nameMatchRegex: String
        override var classMatchRegex: String
        override var collectLogs: Boolean
        override var collectedLoggersRegex: String
        override var maxLogFiles: Int
        override var maxLogFileSizeKb: Int
        override var compressRotatedFiles: Boolean


        fun withEnabled(enabled: Boolean): LogSpecBuilder
        fun withName(name: String): LogSpecBuilder
        fun withMinloglevel(minLogLevel: LogLevel): LogSpecBuilder
        fun withNameMatchRegex(nameMatchRegex: String): LogSpecBuilder
        fun withClassMatchRegex(classMatchRegex: String): LogSpecBuilder
        fun withCollectLogs(collectLogs: Boolean): LogSpecBuilder
        fun withCollectedLoggersRegex(collectedLoggersRegex: String): LogSpecBuilder
        fun withMaxLogFiles(maxLogFiles: Int): LogSpecBuilder
        fun withMaxLogFileSizeKb(maxLogFileSizeKb: Int): LogSpecBuilder
        fun withCompressRotatedFiles(compressRotatedFiles: Boolean): LogSpecBuilder

        fun build(): LoggerConfiguration.LogSpec
    }

    override var globalLogLevel: LogLevel
    override var determineLogOrigin: Boolean
    override var enableMultithreadLogging: Boolean
    override val customLogTags: MutableList<CustomLogTag>

    fun withGlobalLogLevel(level: LogLevel): LoggerConfigurationBuilder
    fun withDetermineLogOrigin(determine: Boolean): LoggerConfigurationBuilder
    fun withMultithreadLogging(enable: Boolean): LoggerConfigurationBuilder
    fun withCustomLogTags(tags: List<CustomLogTag>): LoggerConfigurationBuilder

    fun clearCustomLogTags(): LoggerConfigurationBuilder
    fun addCustomLogTag(tag: CustomLogTag): LoggerConfigurationBuilder
    fun removeCustomLogTag(tag: CustomLogTag): LoggerConfigurationBuilder

    fun clearLoggers(): LoggerConfigurationBuilder
    fun addLogger(config: LogSpecBuilder.() -> Unit): LoggerConfigurationBuilder
    fun removeLogger(name: String): LoggerConfigurationBuilder

    fun clearLogWriters(): LoggerConfigurationBuilder
    fun addDatabaseLogWriter(context: LoggerContext): LoggerConfigurationBuilder
    fun addTextFileLogWriter(context: LoggerContext): LoggerConfigurationBuilder
    fun addJsonFileLogWriter(context: LoggerContext): LoggerConfigurationBuilder
    fun addStdoutLogWriter(): LoggerConfigurationBuilder
    fun addColoredStdoutLogWriter(): LoggerConfigurationBuilder
    fun addLogWriter(generator: (LoggerConfiguration.LogSpec) -> LogWriter): LoggerConfigurationBuilder
    fun removeLogWriter(generator: (LoggerConfiguration.LogSpec) -> LogWriter): LoggerConfigurationBuilder

    fun configureDefaultLogger(config: LogSpecBuilder.() -> Unit): LoggerConfigurationBuilder

    fun build(): LoggerConfiguration
}