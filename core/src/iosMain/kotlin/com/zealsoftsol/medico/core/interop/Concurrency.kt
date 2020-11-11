package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.invoke
import platform.Foundation.NSThread

actual suspend inline fun <T> StateFlow<T>.set(value: T) = Dispatchers.Main {
    (this@set as MutableStateFlow<T>).value = value
}

actual val currentThread: String
    get() = NSThread.currentThread.toString()