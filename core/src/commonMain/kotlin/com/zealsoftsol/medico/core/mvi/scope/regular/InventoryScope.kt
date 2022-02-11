package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope

class InventoryScope(): Scope.Child.TabBar(), CommonScope.CanGoBack {

    init {
        EventCollector.sendEvent(getInventory())
    }

    /**
     * get current Inventory of stockist
     */
    private fun getInventory() =
        Event.Action.Inventory.GetInventory

}