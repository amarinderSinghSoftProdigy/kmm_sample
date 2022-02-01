package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.OfferData

internal class OffersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val loadHelper: LoadHelper,
    private val networkOffersScope: NetworkScope.OffersStore,
) : EventDelegate<Event.Action.Offers>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Offers) = when (event) {
        is Event.Action.Offers.GetOffers -> getOffers()
    }

    private suspend fun getOffers() {
        loadHelper.load<OffersScope, OfferData>(isFirstLoad = true) {
            val user = userRepo.requireUser()
            networkOffersScope.getOffersData(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination
            ).getBodyOrNull()
        }
    }
}