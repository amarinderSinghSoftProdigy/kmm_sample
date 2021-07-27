package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.ensureNeverFrozen
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event

internal abstract class EventDelegate<T : Event>(protected val navigator: Navigator) {

    init {
        ensureNeverFrozen()
    }

    abstract suspend fun handleEvent(event: T)

    suspend fun genericHandle(event: Event) = handleEvent(event as T)
}