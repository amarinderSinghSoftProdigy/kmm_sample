package com.zealsoftsol.medico.core.extensions

actual fun <T> T.logIt(): T = also { println("happy $this") }
actual fun <T> T.warnIt(): T = also { println("happy $this") }
actual fun <T> T.errorIt(): T = also { println("happy $this") }

actual inline fun <T> T.log(header: String, dumpStack: Boolean): T {
    println("happy $header")
    println("happy $this")
//    if (dumpStack)
//        Thread.dumpStack()
    return this
}