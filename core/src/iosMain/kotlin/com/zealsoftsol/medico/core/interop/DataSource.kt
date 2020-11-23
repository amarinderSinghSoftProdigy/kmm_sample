package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

actual class DataSource<T> actual constructor(initialValue: T) {
    private val stateFlow = MutableStateFlow(initialValue)
    actual var value: T
        get() = stateFlow.value
        internal set(value) {
            stateFlow.value = value
        }
    val flow: StateFlow<T>
        get() = stateFlow

    fun observeOnUi(onChange: (T) -> Unit) = observe(MainScope(), onChange)

    private fun observe(scope: CoroutineScope, onChange: (T) -> Unit) {
        stateFlow.onEach {
            onChange(it)
        }.launchIn(scope)
    }
}