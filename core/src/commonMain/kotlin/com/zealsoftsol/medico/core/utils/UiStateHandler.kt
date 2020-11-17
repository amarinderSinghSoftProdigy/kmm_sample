package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.data.UiState
import com.zealsoftsol.medico.data.UiStateWithProgress

class UiStateHandler<T: UiState>(private val initial: T) {
    private val queue = ArrayDeque<UiStateWithProgress<T>>().apply {
        addFirst(UiStateWithProgress(initial, false))
    }
    val dataSource: DataSource<UiStateWithProgress<T>> = DataSource(queue.first())

    fun setProgress(isInProgress: Boolean) {
        newState(dataSource.value.uiState, isInProgress)
    }

    fun newState(uiState: T, isInProgress: Boolean = false) {
        if (queue.first().uiState.id == uiState.id) {
            queue.removeFirst()
        }
        queue.addFirst(UiStateWithProgress(uiState, isInProgress))
        dataSource.value = queue.first()
    }

    inline fun <S> updateUiState(update: S.() -> S): Boolean {
        return (dataSource.value.uiState as? S)?.let {
            val updatedState = it.update() as T
            newState(updatedState)
        } != null
    }

    fun dropCurrentState() {
        queue.removeFirst()
    }

    fun goBack(): UiStateWithProgress<T>? {
        return if (queue.size > 1) {
            val removed = queue.removeFirst()
            dataSource.value = queue.first()
            removed
        } else {
            "can not go back, queue contains single element".errorIt()
            null
        }
    }

    fun clear() {
        queue.clear()
        queue.addFirst(UiStateWithProgress(initial, false))
        dataSource.value = queue.first()
    }
}