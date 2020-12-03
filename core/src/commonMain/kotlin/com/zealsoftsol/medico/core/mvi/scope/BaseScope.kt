package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

abstract class BaseScope {
    val isInProgress: DataSource<Boolean> = DataSource(false)
    val queueId: String = this::class.simpleName.orEmpty()

    object Root : BaseScope()
}

interface CanGoBack {
    fun goBack() {
        EventCollector.sendEvent(Event.Transition.Back)
    }
}