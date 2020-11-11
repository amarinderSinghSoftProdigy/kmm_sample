package com.zealsoftsol.medico.core.extensions

expect fun <T> T.logIt(): T

expect fun <T> T.warnIt(): T

expect fun <T> T.errorIt(): T

expect inline fun <T> T.log(header: String, dumpStack: Boolean = false): T