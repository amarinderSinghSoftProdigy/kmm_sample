package com.zealsoftsol.medico.core.extensions

import platform.Foundation.NSLog

actual fun <T> T.logIt(): T = also {
    if (logger.isLogEnabled()) NSLog("KMP LOG: $this")
}

actual fun <T> T.warnIt(): T = also {
    if (logger.isWarnEnabled()) NSLog("KMP WARN: $this")
}

actual fun <T> T.errorIt(): T = also {
    if (logger.isErrorEnabled()) NSLog("KMP ERROR: $this")
}

actual inline fun <T> T.log(header: String, dumpStack: Boolean): T {
    if (logger.isLogEnabled()) {
        NSLog("KMP LOG: $header")
        NSLog("KMP LOG: $this")
    }
    return this
}