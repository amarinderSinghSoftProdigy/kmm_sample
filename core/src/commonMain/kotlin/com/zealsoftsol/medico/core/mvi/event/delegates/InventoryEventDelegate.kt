package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser

internal class InventoryEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val inventoryScope: NetworkScope.InventoryStore,

    ) : EventDelegate<Event.Action.Inventory>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Inventory) = when (event) {
        is Event.Action.Inventory.GetInventory -> load(
            search = event.search,
            manufacturer = event.manufacturer,
            page = event.page
        )
    }

    /**
     * get the Inventory data and pass it to scope
     */
    private suspend fun load(
        search: String?,
        manufacturer: String?,
        page: Int
    ) {
        navigator.withScope<InventoryScope> {
            withProgress {
                inventoryScope.getInventoryData(
                    unitCode = userRepo.requireUser().unitCode,
                    manufacturer = manufacturer,
                    search = search,
                    page = page
                )
            }.onSuccess { body ->
                if (search.isNullOrEmpty()) {
                    it.stockStatusData.value = body.stockStatusData
                    it.onlineStatusData.value = body.onlineStatusData
                    it.stockExpiredData.value = body.stockExpiredData
                    it.updateManufacturersList(body.manufacturers)
                    it.updateProductsList(body.results)
                } else {
                    it.updateProductsList(body.results)
                }
                it.totalProducts = body.totalResults
            }.onError(navigator)
        }
    }
}