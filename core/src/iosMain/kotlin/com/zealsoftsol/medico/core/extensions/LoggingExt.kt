package com.zealsoftsol.medico.core.extensions

actual fun <T> T.logIt(): T = also {
    if (logger.isLogEnabled()) println("KMP LOG: $this")
}

actual fun <T> T.warnIt(): T = also {
    if (logger.isWarnEnabled()) println("KMP WARN: $this")
}

actual fun <T> T.errorIt(): T = also {
    if (logger.isErrorEnabled()) println("KMP ERROR: $this")
}

actual inline fun <T> T.log(header: String, dumpStack: Boolean): T {
    if (logger.isLogEnabled()) {
        println("KMP LOG: $header")
        println("KMP LOG: $this")
    }
    return this
}