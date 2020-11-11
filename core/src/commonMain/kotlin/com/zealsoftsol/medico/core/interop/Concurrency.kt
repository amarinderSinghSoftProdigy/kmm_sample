package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.flow.StateFlow

expect suspend inline fun <T> StateFlow<T>.set(value: T)

expect val currentThread: String