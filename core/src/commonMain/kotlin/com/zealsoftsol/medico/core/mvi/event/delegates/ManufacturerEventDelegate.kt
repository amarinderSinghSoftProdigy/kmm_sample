package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.ManufacturerScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class ManufacturerEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val manufacturerRepo: NetworkScope.ManufacturerStore
) : EventDelegate<Event.Action.Manufacturers>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Manufacturers) = when (event) {
        is Event.Action.Manufacturers.GetManufacturers -> getManufacturers(
            event.page,
            event.search,
            event.unitCode,
        )
        is Event.Action.Manufacturers.SelectItem -> selectItem()
        is Event.Action.Manufacturers.SearchManufacturers -> {}
    }


    private suspend fun selectItem() {

    }

    /**
     * get all deals from server
     */
    private suspend fun getManufacturers(
        page: Int,
        search: String,
        unitCode: String
    ) {
        navigator.withScope<ManufacturerScope> {
            val result = withProgress {
                manufacturerRepo.getManufacturers(page, search, unitCode)
            }

            result.onSuccess { body ->
              /*  if (it.manufacturers.value.isEmpty()) {
                    it.manufacturers.value = body.promoTypes
                }
                it.totalItems = body.pageableData.totalResults*/
            }.onError(navigator)
        }
    }

}