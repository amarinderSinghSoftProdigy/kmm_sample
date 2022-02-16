package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo

internal class InventoryEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Inventory>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Inventory) = when(event){
        Event.Action.Inventory.GetInventory -> getInventory()
    }

    /**
     * get the Inventory data and pass it to scope
     */
    private suspend fun getInventory(){
        navigator.withScope<InventoryScope> {
            val result = withProgress {
                userRepo.getInventoryData()
            }

            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                data?.let { it1 -> it.updateDataFromServer(inventoryData = it1) }
            }.onError(navigator)
        }
    }

}