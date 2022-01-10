package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.OrderEntry

class OrderHsnEditScope(
    val orderEntry: OrderEntry,
    val showAlert: DataSource<Boolean> = DataSource(false)
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    val isChecked= DataSource(false)
    val quantity = DataSource(orderEntry.servedQty.value)
    val freeQuantity = DataSource(orderEntry.freeQty.value)
    val ptr = DataSource(orderEntry.price.value.toString())
    val batch = DataSource(orderEntry.batchNo)
    val expiry = DataSource(orderEntry.expiryDate?.formatted ?: "")

    fun updateQuantity(value: Double) {
        quantity.value = value
    }

    fun updateFreeQuantity(value: Double) {
        freeQuantity.value = value
    }

    fun updatePtr(value: String) {
        ptr.value = value
    }

    fun updateBatch(value: String) {
        batch.value = value
    }

    fun updateExpiry(value: String) {
        expiry.value = value
    }


    init {
        //EventCollector.sendEvent(getCurrentPreference())
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    /**
     * update the scope of alert dialog
     */
    fun changeAlertScope(enable: Boolean) {
        this.showAlert.value = enable
    }


    /**
     * get current product details
     */
    private fun getCurrentPreference() =
        Event.Action.WhatsAppPreference.GetPreference

    /**
     * submit user preference to server
     */
    fun submit() = EventCollector.sendEvent(
        Event.Action.WhatsAppPreference.SavePreference(
            "",""
        )
    )

}