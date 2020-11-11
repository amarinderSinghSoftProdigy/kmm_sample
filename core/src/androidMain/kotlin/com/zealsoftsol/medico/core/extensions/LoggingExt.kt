package com.zealsoftsol.medico.core.extensions

import android.util.Log

actual fun <T> T.logIt(): T = also { Log.d("happy", it.toString()) }

actual fun <T> T.warnIt(): T = also { Log.w("happy", it.toString()) }

actual fun <T> T.errorIt(): T = also { Log.e("happy", it.toString()) }

actual inline fun <T> T.log(header: String, dumpStack: Boolean): T {
    Log.e("happy", header)
    Log.d("happy", this.toString())
    if (dumpStack)
        Thread.dumpStack()
    return this
}