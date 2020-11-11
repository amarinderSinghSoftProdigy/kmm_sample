package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class DataSource<T> actual constructor(initialValue: T) {
    private val stateFlow = MutableStateFlow(initialValue)
    actual var value: T
        get() = stateFlow.value
        set(value) {
            stateFlow.value = value
        }
    val flow: StateFlow<T>
        get() = stateFlow
}