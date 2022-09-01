package de.menkalian.vela.loglib

import java.io.File

/**
 * Context information required by the library.
 *
 * @property logFilesDirectory directory where logfiles should be stored
 * @property logDatabaseDirectory directory where SQLite-Databases should be stored
 */
interface LoggerContext {
    val logFilesDirectory: File
    val logDatabaseDirectory: File
}