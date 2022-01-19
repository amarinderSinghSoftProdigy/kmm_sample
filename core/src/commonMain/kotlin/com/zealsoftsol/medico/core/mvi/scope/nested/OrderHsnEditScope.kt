package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.DeclineReason
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.SearchDataItem

class OrderHsnEditScope(
    private val orderID: String,
    val declineReason: List<DeclineReason>,
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


    var orderEntry: DataSource<OrderEntry> = DataSource(orderEntries[selectedIndex.value])
    var showHsnBottomSheet = DataSource(false)
    var showWarningBottomSheet = DataSource(false)
    var showDeclineReasonsBottomSheet = DataSource(false)
    val selectedDeclineReason = DataSource("")

    /**
     * values used for pagination
     */
    override val pagination: Pagination = Pagination()
    override val items: DataSource<List<SearchDataItem>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")

    /**
     * values to be sent to server when order is accepted
     */
    val quantity = DataSource(orderEntry.value.servedQty.value)
    val freeQuantity = DataSource(orderEntry.value.freeQty.value)
    val ptr = DataSource(orderEntry.value.price.value)
    val batch = DataSource(orderEntry.value.batchNo)
    val expiry = DataSource(orderEntry.value.expiryDate?.formatted ?: "")
    val discount = DataSource(orderEntry.value.discount.value)
    val mrp = DataSource(orderEntry.value.mrp.value)
    val selectedHsnCode = DataSource(orderEntry.value.hsnCode)

    /**
     * Update this whenever user switches the line index so that you get correct data for order entries
     */
    fun updateSelectedIndex(currentIndex: Int) {
        this.selectedIndex.value = currentIndex
        orderEntry.value = orderEntries[currentIndex]
        selectedHsnCode.value = orderEntry.value.hsnCode
        quantity.value = orderEntry.value.servedQty.value
        freeQuantity.value = orderEntry.value.freeQty.value
        ptr.value = orderEntry.value.price.value
        batch.value = orderEntry.value.batchNo
        expiry.value = orderEntry.value.expiryDate?.formatted ?: ""
        mrp.value = orderEntry.value.mrp.value
        discount.value = orderEntry.value.discount.value
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
     * manage decline bottom sheet  visibility
     */
    fun manageDeclineBottomSheetVisibility(openSheet: Boolean){
        this.showDeclineReasonsBottomSheet.value = openSheet
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


    /**
     * update following entries for sending the entered values to server
     */
    fun updateQuantity(value: Double) {
        quantity.value = value
    }

    fun updateFreeQuantity(value: Double) {
        freeQuantity.value = value
    }

    fun updatePtr(value: Double) {
        ptr.value = value
    }

    fun updateBatch(value: String) {
        batch.value = value
    }


    fun updateExpiry(value: String) {
        expiry.value = value
    }

    fun updateHsnCode(value: String) {
        selectedHsnCode.value = value
    }

    fun updateMrp(value: Double){
        mrp.value = value
    }

    fun updateDiscount(value: Double){
        discount.value = value
    }
    /****************************************/

    /**
     * update the reason selected for decliening the order entry
     */
    fun updateDeclineReason(reason: String){
        this.selectedDeclineReason.value = reason
        rejectEntry()
    }

    /**
     * update the scope of alert dialog
     */
    fun changeAlertScope(enable: Boolean) {
        this.showAlert.value = enable
    }

    /**
     * reject an order entry
     */
    fun rejectEntry(){
//        EventCollector.sendEvent(Event.Action.OrderHsn.RejectOrderEntry(
//            orderEntryId = orderEntry.value.id,
//            spid = orderEntry.value.spid,
//            reasonCode = selectedDeclineReason.value
//        ))
        "decline reason".log(Event.Action.OrderHsn.RejectOrderEntry(
            orderEntryId = orderEntry.value.id,
            spid = orderEntry.value.spid,
            reasonCode = selectedDeclineReason.value
        ).toString())
    }

    /**
     * submit data to server
     */
    fun saveEntry() {

        "data".log(Event.Action.OrderHsn.SaveOrderEntry(
            orderId = orderID,
            orderEntryId = orderEntry.value.id,
            servedQty = quantity.value,
            freeQty = freeQuantity.value,
            price = ptr.value,
            batchNo = batch.value,
            expiryDate = expiry.value,
            mrp = mrp.value,
            hsnCode = selectedHsnCode.value,
            discount = discount.value
        ).toString())

//        EventCollector.sendEvent(Event.Action.OrderHsn.SaveOrderEntry(
//            orderId = orderID,
//            orderEntryId = orderEntry.value.id,
//            servedQty = quantity.value,
//            freeQty = freeQuantity.value,
//            price = ptr.value.toDouble(),
//            batchNo = batch.value,
//            expiryDate = expiry.value,
//        mrp = mrp.value,
//        hsnCode = hsnCode.value,
//        discount = discount.value
//        ))
    }
}