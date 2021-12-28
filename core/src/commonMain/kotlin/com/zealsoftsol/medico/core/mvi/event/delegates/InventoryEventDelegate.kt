package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.repository.UserRepo

internal class InventoryEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Inventory>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Inventory) = when(event){
        Event.Action.Inventory.GetInventory -> getInventory()
    }

    private suspend fun getInventory(){

    }

}