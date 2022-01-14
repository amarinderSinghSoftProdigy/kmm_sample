package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.SearchDataItem

class OrderHsnEditScope(
    val orderEntries: List<OrderEntry>,
    val index: Int,
    val showAlert: DataSource<Boolean> = DataSource(false)
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    val selectedIndex = DataSource(index)
    val selectedHsnCode = DataSource("")

    private var hsnList = ArrayList<SearchDataItem>()

    var orderEntry: DataSource<OrderEntry> = DataSource(orderEntries[selectedIndex.value])

//    val isChecked = DataSource(false)
//    val hsnCode = DataSource(orderEntry.value.hsnCode)
//    val quantity = DataSource(orderEntry.value.servedQty.value)
//    val freeQuantity = DataSource(orderEntry.value.freeQty.value)
//    val ptr = DataSource(orderEntry.value.price.value.toString())
//    val batch = DataSource(orderEntry.value.batchNo)
//    val expiry = DataSource(orderEntry.value.expiryDate?.formatted ?: "")

    /**
     * Update this whenever user switches the line index so that you get correct data for order entries
     */
    fun updateSelectedIndex(currentIndex: Int) {
        this.selectedIndex.value = currentIndex
        orderEntry.value = orderEntries[currentIndex]
    }

    /**
     * get the HSN code selected by user from bottomsheet
     */
    fun getSelectedHsnCode(selectedHsnCode: String){
        this.selectedHsnCode.value = selectedHsnCode
    }


    fun updateDataFromServer(
        hsnCode: ArrayList<SearchDataItem>
    ) {
        this.hsnList = hsnCode
    }

    fun selectEntry() =
        EventCollector.sendEvent(Event.Action.OrderHsn.SelectHsn)


//    fun updateQuantity(value: Double) {
//        quantity.value = value
//    }
//
//    fun updateFreeQuantity(value: Double) {
//        freeQuantity.value = value
//    }
//
//    fun updatePtr(value: String) {
//        ptr.value = value
//    }
//
//    fun updateBatch(value: String) {
//        batch.value = value
//    }
//
//    fun updateHsnCode(value: String) {
//        hsnCode.value = value
//    }
//
//    fun updateExpiry(value: String) {
//        expiry.value = value
//    }


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
     * submit data to server
     */
    fun submit() {}

}