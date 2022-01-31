package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo

internal class OffersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Offers>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Offers) = when (event) {
        is Event.Action.Offers.GetOffers -> getOffers()
    }

    private suspend fun getOffers() {
        navigator.withScope<OffersScope> {
            val result = withProgress {
                userRepo.getOffersData()
            }
            result.onSuccess { _ ->
                //val data = result.getBodyOrNull()

            }.onError(navigator)
        }
    }
}