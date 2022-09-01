package de.menkalian.vela.loglib.logger.writer.config

internal data class FileLoggerConfiguration(
    val maxLogFiles: Int,
    val maxLogFileSizeKb: Int,
    val compressRotatedFiles: Boolean,
) {
    companion object {
        fun default(): FileLoggerConfiguration {
            return FileLoggerConfiguration(
                10,
                1 * 1024, /* 1 MB */
                true
            )
        }
    }
}