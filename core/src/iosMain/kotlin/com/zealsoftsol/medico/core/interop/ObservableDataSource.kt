package com.zealsoftsol.medico.core.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ObservableDataSource<T>(initialValue: T) : DataSource<T>(initialValue) {

    fun observeOnUi(onChange: (T) -> Unit) = observe(MainScope(), onChange)

    private fun observe(scope: CoroutineScope, onChange: (T) -> Unit) {
        stateFlow.onEach {
            onChange(it)
        }.launchIn(scope)
    }
}