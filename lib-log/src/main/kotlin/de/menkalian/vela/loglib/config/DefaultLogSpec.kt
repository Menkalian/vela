package de.menkalian.vela.loglib.config

import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerConfigurationBuilder

internal data class DefaultLogSpec(
    override var enabled: Boolean,
    override var name: String,
    override var minLogLevel: LogLevel,
    override var nameMatchRegex: String,
    override var classMatchRegex: String,
    override var collectLogs: Boolean,
    override var collectedLoggersRegex: String,
    override var maxLogFiles: Int,
    override var maxLogFileSizeKb: Int,
    override var compressRotatedFiles: Boolean,
) : LoggerConfiguration.LogSpec, LoggerConfigurationBuilder.LogSpecBuilder {
    companion object {
        fun createDefault(): DefaultLogSpec {
            return DefaultLogSpec(
                true,
                "Default",
                LogLevel.DEBUG,
                "",
                "",
                false,
                "",
                10,
                1 * 1024,
                true,
            )
        }

        fun createOf(logSpec: LoggerConfiguration.LogSpec): DefaultLogSpec {
            if (logSpec is DefaultLogSpec) {
                return logSpec.copy()
            }

            return DefaultLogSpec(
                logSpec.enabled,
                logSpec.name,
                logSpec.minLogLevel,
                logSpec.nameMatchRegex,
                logSpec.classMatchRegex,
                logSpec.collectLogs,
                logSpec.collectedLoggersRegex,
                logSpec.maxLogFiles,
                logSpec.maxLogFileSizeKb,
                logSpec.compressRotatedFiles,
            )
        }
    }

    override fun withEnabled(enabled: Boolean): LoggerConfigurationBuilder.LogSpecBuilder {
        this.enabled = enabled
        return this
    }

    override fun withName(name: String): LoggerConfigurationBuilder.LogSpecBuilder {
        this.name = name
        return this
    }

    override fun withMinloglevel(minLogLevel: LogLevel): LoggerConfigurationBuilder.LogSpecBuilder {
        this.minLogLevel = minLogLevel
        return this
    }

    override fun withNameMatchRegex(nameMatchRegex: String): LoggerConfigurationBuilder.LogSpecBuilder {
        this.nameMatchRegex = nameMatchRegex
        return this
    }

    override fun withClassMatchRegex(classMatchRegex: String): LoggerConfigurationBuilder.LogSpecBuilder {
        this.classMatchRegex = classMatchRegex
        return this
    }

    override fun withCollectLogs(collectLogs: Boolean): LoggerConfigurationBuilder.LogSpecBuilder {
        this.collectLogs = collectLogs
        return this
    }

    override fun withCollectedLoggersRegex(collectedLoggersRegex: String): LoggerConfigurationBuilder.LogSpecBuilder {
        this.collectedLoggersRegex = collectedLoggersRegex
        return this
    }

    override fun withMaxLogFiles(maxLogFiles: Int): LoggerConfigurationBuilder.LogSpecBuilder {
        this.maxLogFiles = maxLogFiles
        return this
    }

    override fun withMaxLogFileSizeKb(maxLogFileSizeKb: Int): LoggerConfigurationBuilder.LogSpecBuilder {
        this.maxLogFileSizeKb = maxLogFileSizeKb
        return this
    }

    override fun withCompressRotatedFiles(compressRotatedFiles: Boolean): LoggerConfigurationBuilder.LogSpecBuilder {
        this.compressRotatedFiles = compressRotatedFiles
        return this
    }

    override fun build(): LoggerConfiguration.LogSpec {
        return this
    }
}
