package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed class BaseDataSource<T> {
    abstract val flow: Flow<T>

    /**
     * for iOS only
     */
    fun observeOnUi(onChange: (T) -> Unit) = observe(MainScope(), onChange)

    private fun observe(scope: CoroutineScope, onChange: (T) -> Unit) {
        flow.onEach { onChange(it) }.launchIn(scope)
    }
}

class DataSource<T>(private val stateFlow: MutableStateFlow<T>) : BaseDataSource<T>() {

    var updateCount = 0
        private set

    constructor(initialValue: T) : this(MutableStateFlow(initialValue))

    internal var value: T
        get() = stateFlow.value
        internal set(value) {
            updateCount++
            stateFlow.value = value
        }
    override val flow: StateFlow<T>
        get() = stateFlow
}

class ReadOnlyDataSource<T>(override val flow: StateFlow<T>) : BaseDataSource<T>()