package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.InventoryData

class InventoryScope(
    val mInventoryData: DataSource<InventoryData?> = DataSource(InventoryData(null, null, null)),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    init {
        EventCollector.sendEvent(getInventory())
    }

    /**
     * get current Inventory of stockist
     */
    private fun getInventory() =
        Event.Action.Inventory.GetInventory

    /**
     * update Inventory data with values received from server
     */
    fun updateDataFromServer(inventoryData: InventoryData) {
        mInventoryData.value = inventoryData
    }

}