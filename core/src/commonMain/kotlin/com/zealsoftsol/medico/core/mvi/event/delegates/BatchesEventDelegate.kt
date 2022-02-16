package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.repository.UserRepo

internal class BatchesEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Batches>(navigator) {


    override suspend fun handleEvent(event: Event.Action.Batches) = when (event) {
        else -> {}
    }


}