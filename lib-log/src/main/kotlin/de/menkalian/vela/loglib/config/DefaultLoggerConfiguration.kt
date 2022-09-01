package de.menkalian.vela.loglib.config

import de.menkalian.vela.loglib.CustomLogTag
import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerConfigurationBuilder
import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.logger.writer.DatabaseLogWriter
import de.menkalian.vela.loglib.logger.writer.JsonLogWriter
import de.menkalian.vela.loglib.logger.writer.StdoutLogWriter
import de.menkalian.vela.loglib.logger.writer.TextfileLogWriter
import de.menkalian.vela.loglib.logger.writer.config.FileLoggerConfiguration

internal data class DefaultLoggerConfiguration constructor(
    override val logSpecs: MutableList<LoggerConfiguration.LogSpec>,
    override val logWriterCreators: MutableList<(LoggerConfiguration.LogSpec) -> LogWriter>,
    override val customLogTags: MutableList<CustomLogTag>,
    private var defaultLogSpecVar: LoggerConfiguration.LogSpec,
    override var globalLogLevel: LogLevel,
    override var determineLogOrigin: Boolean,
    override var enableMultithreadLogging: Boolean,
) : LoggerConfiguration, LoggerConfigurationBuilder {
    override val defaultLogSpec: LoggerConfiguration.LogSpec
        get() = defaultLogSpecVar


    companion object {
        fun createDefault(): DefaultLoggerConfiguration {
            val defaultLogSpec = DefaultLogSpec.createDefault()
            return DefaultLoggerConfiguration(
                mutableListOf(defaultLogSpec),
                mutableListOf(),
                mutableListOf(),
                defaultLogSpec,
                LogLevel.DEBUG,
                determineLogOrigin = true,
                enableMultithreadLogging = true
            )
        }
    }

    init {
        if (logSpecs.contains(defaultLogSpec).not()) {
            logSpecs.add(defaultLogSpec)
        }
    }

    override fun withGlobalLogLevel(level: LogLevel): LoggerConfigurationBuilder {
        globalLogLevel = level
        return this
    }

    override fun withDetermineLogOrigin(determine: Boolean): LoggerConfigurationBuilder {
        determineLogOrigin = determine
        return this
    }

    override fun withMultithreadLogging(enable: Boolean): LoggerConfigurationBuilder {
        enableMultithreadLogging = enable
        return this
    }

    override fun withCustomLogTags(tags: List<CustomLogTag>): LoggerConfigurationBuilder {
        customLogTags.clear()
        customLogTags.addAll(tags)
        return this
    }

    override fun clearCustomLogTags(): LoggerConfigurationBuilder {
        customLogTags.clear()
        return this
    }

    override fun addCustomLogTag(tag: CustomLogTag): LoggerConfigurationBuilder {
        customLogTags.add(tag)
        return this
    }

    override fun removeCustomLogTag(tag: CustomLogTag): LoggerConfigurationBuilder {
        customLogTags.remove(tag)
        return this
    }

    override fun clearLoggers(): LoggerConfigurationBuilder {
        logSpecs.clear()
        logSpecs.add(defaultLogSpec)
        return this
    }

    override fun addLogger(config: LoggerConfigurationBuilder.LogSpecBuilder.() -> Unit): LoggerConfigurationBuilder {
        val spec = DefaultLogSpec.createDefault()
        config(spec)
        if (logSpecs.any { it.name == spec.name })
            throw IllegalArgumentException("Duplicate name \"${spec.name}\" not allowed")
        logSpecs.add(spec.build())
        return this
    }

    override fun removeLogger(name: String): LoggerConfigurationBuilder {
        logSpecs.removeIf { it.name == name && it != defaultLogSpec }
        return this
    }

    override fun clearLogWriters(): LoggerConfigurationBuilder {
        logWriterCreators.clear()
        return this
    }

    override fun addDatabaseLogWriter(context: LoggerContext): LoggerConfigurationBuilder {
        return addLogWriter { DatabaseLogWriter.getDefault(context) }
    }

    override fun addTextFileLogWriter(context: LoggerContext): LoggerConfigurationBuilder {
        return addLogWriter { spec ->
            TextfileLogWriter(
                spec, context,
                FileLoggerConfiguration(spec.maxLogFiles, spec.maxLogFileSizeKb, spec.compressRotatedFiles)
            )
        }
    }

    override fun addJsonFileLogWriter(context: LoggerContext): LoggerConfigurationBuilder {
        return addLogWriter { spec ->
            JsonLogWriter(
                spec,
                context,
                FileLoggerConfiguration(spec.maxLogFiles, spec.maxLogFileSizeKb, spec.compressRotatedFiles)
            )
        }
    }

    override fun addStdoutLogWriter(): LoggerConfigurationBuilder {
        return addLogWriter { StdoutLogWriter.default }
    }

    override fun addColoredStdoutLogWriter(): LoggerConfigurationBuilder {
        return addLogWriter { StdoutLogWriter.colored }
    }

    override fun addLogWriter(generator: (LoggerConfiguration.LogSpec) -> LogWriter): LoggerConfigurationBuilder {
        logWriterCreators.add(generator)
        return this
    }

    override fun removeLogWriter(generator: (LoggerConfiguration.LogSpec) -> LogWriter): LoggerConfigurationBuilder {
        logWriterCreators.remove(generator)
        return this
    }

    override fun configureDefaultLogger(config: LoggerConfigurationBuilder.LogSpecBuilder.() -> Unit): LoggerConfigurationBuilder {
        val newDefaultLogSpec = DefaultLogSpec.createOf(defaultLogSpec)
        config(newDefaultLogSpec)
        logSpecs.remove(defaultLogSpec)
        logSpecs.add(newDefaultLogSpec)
        defaultLogSpecVar = newDefaultLogSpec
        return this
    }

    override fun build(): LoggerConfiguration {
        return this
    }
}
