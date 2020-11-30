package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class DataSource<T>(initialValue: T) {
    protected val stateFlow = MutableStateFlow(initialValue)
    internal var value: T
        get() = stateFlow.value
        internal set(value) {
            stateFlow.value = value
        }
    val flow: StateFlow<T>
        get() = stateFlow
}