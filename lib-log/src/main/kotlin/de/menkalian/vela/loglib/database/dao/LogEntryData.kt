package de.menkalian.vela.loglib.database.dao

import de.menkalian.vela.loglib.LogLevel
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal object LogEntryData : LongIdTable() {
    val logger = varchar("logger", 255)
    val logLevel = enumeration<LogLevel>("logLevel")
    val logLevelInt = integer("logLevelInt")
    val threadName = varchar("threadName", 255)
    val threadId = long("threadId")
    val origin = text("origin")
    val timestamp = varchar("timestamp", 23)
    val timestampMs = long("timestampMs")
    val message = text("message")

    class LogEntryEntry(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<LogEntryEntry>(LogEntryData)

        var logger by LogEntryData.logger
        var logLevel by LogEntryData.logLevel
        var logLevelInt by LogEntryData.logLevelInt
        var threadName by LogEntryData.threadName
        var threadId by LogEntryData.threadId
        var origin by LogEntryData.origin
        var timestamp by LogEntryData.timestamp
        var timestampMs by LogEntryData.timestampMs
        var message by LogEntryData.message
    }
}