package de.menkalian.vela.loglib.logger.writer

import de.menkalian.vela.loglib.LogWriter
import de.menkalian.vela.loglib.LoggerConfiguration
import de.menkalian.vela.loglib.LoggerContext
import de.menkalian.vela.loglib.logger.formatter.Formatter
import de.menkalian.vela.loglib.logger.writer.config.FileLoggerConfiguration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream

@Suppress("LeakingThis")
internal abstract class FileLogWriter(spec: LoggerConfiguration.LogSpec, context: LoggerContext, config: FileLoggerConfiguration, extension: String) :
    LogWriter {
    protected val formatter = Formatter()

    private val fileNamePattern: String
    private val fileSizeLimit: Long
    private val compressArchiveFiles: Boolean
    private val fileAmountLimit: Int
    private val logStreamMutex = Mutex(false)

    private var currentFileSize: Long = 0
    private var fileOutputStream: FileOutputStream

    init {
        context.logFilesDirectory.mkdirs()
        fileNamePattern = File(context.logFilesDirectory, "${spec.name}.%d.${extension}").absolutePath
        fileSizeLimit = 1024L * config.maxLogFileSizeKb
        fileAmountLimit = config.maxLogFiles
        compressArchiveFiles = config.compressRotatedFiles

        fileOutputStream = FileOutputStream(getFile(1), true)
        currentFileSize = getFile(1).length()
    }

    protected suspend fun write(str: String) {
        logStreamMutex.withLock {
            fileOutputStream.write(str.toByteArray())
            fileOutputStream.flush()

            currentFileSize += str.length
            checkFileRotation()
        }
    }

    private fun checkFileRotation() {
        if (currentFileSize >= fileSizeLimit) {
            fileOutputStream.close()
            for (n in fileAmountLimit - 1 downTo 2) {
                getFile(n, compressArchiveFiles).renameTo(getFile(n + 1, compressArchiveFiles))
            }

            if (compressArchiveFiles) {
                val gzipOutputStream = GZIPOutputStream(FileOutputStream(getFile(2, true)))
                gzipOutputStream.write(getFile(1).readBytes())
                gzipOutputStream.close()
            } else {
                getFile(1).copyTo(getFile(2), true)
            }

            fileOutputStream = FileOutputStream(getFile(1))
            currentFileSize = 0
        }
    }

    private fun getFile(no: Int, compressed: Boolean = false): File {
        return File(
            fileNamePattern.format(no - 1)
                    + if (compressed) ".gz" else ""
        )
    }
}