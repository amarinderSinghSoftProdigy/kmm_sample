package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.SearchDataItem

class OrderHsnEditScope(
    val orderEntries: List<OrderEntry>,
    val index: Int,
    val showAlert: DataSource<Boolean> = DataSource(false)
) : Scope.Child.TabBar(), CommonScope.CanGoBack, Loadable<SearchDataItem> {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    init {
        getHsnCodes(true)
    }

    val selectedIndex = DataSource(index)
    val selectedHsnCode = DataSource("")


    var orderEntry: DataSource<OrderEntry> = DataSource(orderEntries[selectedIndex.value])
    var showHsnBottomSheet = DataSource(false)
    var showWarningBottomSheet = DataSource(false)

    override val pagination: Pagination = Pagination()
    override val items: DataSource<List<SearchDataItem>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")


//    val quantity = DataSource(orderEntry.value.servedQty.value)
//    val freeQuantity = DataSource(orderEntry.value.freeQty.value)
//    val ptr = DataSource(orderEntry.value.price.value.toString())
//    val batch = DataSource(orderEntry.value.batchNo)

    val expiry = DataSource(orderEntry.value.expiryDate?.formatted ?: "")
    val hsnCode = DataSource(orderEntry.value.hsnCode)

    /**
     * Update this whenever user switches the line index so that you get correct data for order entries
     */
    fun updateSelectedIndex(currentIndex: Int) {
        this.selectedIndex.value = currentIndex
        orderEntry.value = orderEntries[currentIndex]
    }


    /**
     * update whether to show hsn bottom sheet or not
     * @param openSheet true is want to display sheet else false
     * also reset search in case sheet is closed
     */
    fun manageBottomSheetVisibility(openSheet: Boolean) {
        if (!openSheet) {
            if (searchText.value.isNotEmpty()) {
                searchText.value = ""
                getHsnCodes(true)
            }
        }
        this.showHsnBottomSheet.value = openSheet
    }

    /**
     * mange visibility of warning bottom sheet
     */
    fun manageWarningBottomSheetVisibility(openSheet: Boolean) {
        this.showWarningBottomSheet.value = openSheet
    }


    /**
     * get Hsn codes from server
     */
    fun getHsnCodes(isFirstLoad: Boolean = false) =
        EventCollector.sendEvent(Event.Action.OrderHsn.Load(isFirstLoad))

    /**
     * search for scopes
     */
    fun search(value: String) =
        EventCollector.sendEvent(Event.Action.OrderHsn.Search(value))

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

    fun updateExpiry(value: String) {
        expiry.value = value
    }

    fun updateHsnCode(value: String) {
        hsnCode.value = value
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