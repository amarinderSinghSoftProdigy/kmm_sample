package com.zealsoftsol.medico.core.extensions

var logger = Logger(Logger.Level.NONE)

data class Logger(val level: Level) {

    fun isLogEnabled() = level.value == Level.LOG.value
    fun isWarnEnabled() = level.value <= Level.WARN.value
    fun isErrorEnabled() = level.value <= Level.ERROR.value

    enum class Level(val value: Int) {
        NONE(0), LOG(1), WARN(2), ERROR(3);
    }
}

expect fun <T> T.logIt(): T

expect fun <T> T.warnIt(): T

expect fun <T> T.errorIt(): T

expect inline fun <T> T.log(header: String, dumpStack: Boolean = false): T