package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual suspend inline fun <T> StateFlow<T>.set(value: T) {
    (this as MutableStateFlow<T>).value = value
}

actual val currentThread: String
    get() = Thread.currentThread().toString()