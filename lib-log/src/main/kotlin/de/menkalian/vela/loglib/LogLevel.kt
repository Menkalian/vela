package de.menkalian.vela.loglib

enum class LogLevel(internal val priority: Int) {
    ESSENTIAL(100),
    FATAL(80),
    ERROR(60),
    WARN(40),
    INFO(30),
    DEBUG(20),
    TRACE(10),
    SILENT(0);

    infix fun shows(other: LogLevel) = this.priority <= other.priority
}