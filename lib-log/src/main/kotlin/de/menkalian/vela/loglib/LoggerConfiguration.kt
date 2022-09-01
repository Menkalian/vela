package de.menkalian.vela.loglib

interface LoggerConfiguration {
    interface LogSpec {
        val enabled: Boolean

        val name: String
        val minLogLevel: LogLevel

        val nameMatchRegex: String
        val classMatchRegex: String

        val collectLogs: Boolean
        val collectedLoggersRegex: String

        val maxLogFiles: Int
        val maxLogFileSizeKb: Int
        val compressRotatedFiles: Boolean
    }

    val customLogTags: List<CustomLogTag>
    val defaultLogSpec: LogSpec
    val logSpecs: List<LogSpec>
    val logWriterCreators: List<(LogSpec) -> LogWriter>
    val globalLogLevel: LogLevel

    val enableMultithreadLogging: Boolean
    val determineLogOrigin: Boolean
}