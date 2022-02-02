package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.B2BData
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.DeclineReason
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.GeoData
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderTax
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.TaxType

class OrdersScope(
    val tabs: List<Tab>, val unreadNotifications: ReadOnlyDataSource<Int>,
) : Scope.Child.TabBar(), Loadable<Order> {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.NoIconTitle("", unreadNotifications)

    override val isRoot: Boolean = false

    override val items: DataSource<List<Order>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    val activeTab: DataSource<Tab> = DataSource(tabs.first())

    val isFilterOpened: DataSource<Boolean> = DataSource(false)
    val dateRange: DataSource<DateRange?> = DataSource(null)

    fun firstLoad() = EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.Orders.Search(value))
        } else {
            false
        }
    }

    fun selectTab(tab: Tab) {
        searchText.value = ""
        pagination.reset()
        items.value = emptyList()
        dateRange.value = null
        activeTab.value = tab
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun selectItem(item: Order) =
        EventCollector.sendEvent(
            Event.Action.Orders.Select(
                item.info.id,
                activeTab.value.orderType
            )
        )

    fun setFrom(fromMs: Long) {
        this.dateRange.value = dateRange.value?.copy(fromMs = fromMs) ?: DateRange(fromMs = fromMs)
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun setTo(toMs: Long) {
        this.dateRange.value = dateRange.value?.copy(toMs = toMs) ?: DateRange(toMs = toMs)
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun clearFilters() {
        dateRange.value = null
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }

    enum class Tab(val stringId: String, val orderType: OrderType) {
        ORDERS("orders", OrderType.ORDER),
        PO_ORDERS("purchase_orders", OrderType.PURCHASE_ORDER),
        HISTORY_ORDERS("orders_history", OrderType.HISTORY),
    }
}

class ViewOrderScope(
    val orderId: String,
    val typeInfo: OrderType,
    override val canEdit: Boolean,
    override var order: DataSource<OrderTax?>,
    var b2bData: DataSource<B2BData?>,
    var entries: DataSource<List<OrderEntry>>,
    var declineReason: DataSource<List<DeclineReason>>,
) : Scope.Child.TabBar(), SelectableOrderEntry, CommonScope.WithNotifications {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        val b2bData = b2bData.value
        b2bData?.let {
            val address = GeoData(
                location = "${it.addressData.district} ${it.addressData.pincode}",
                city = it.addressData.city,
                pincode = it.addressData.pincode.toString(),
                distance = 0.0,
                formattedDistance = "",
                addressLine = it.addressData.address,
                destination = null,
                landmark = "",
                origin = GeoPoints(0.0, 0.0)
            )
            val item = EntityInfo(
                tradeName = it.tradeName,
                phoneNumber = it.phoneNumber,
                geoData = address,
                seasonBoyData = null,
                seasonBoyRetailerData = null,
                drugLicenseNo1 = it.drugLicenseNo1,
                drugLicenseNo2 = it.drugLicenseNo2,
                gstin = it.gstin,
                isVerified = true,
                panNumber = it.panNumber,
                subscriptionData = null,
                unitCode = ""
            )

            return TabBarInfo.StoreTitle(
                storeName = it.tradeName,
                showNotifications = false,
                event = Event.Action.Orders.ShowDetailsOfRetailer(item, this)
            )
        }

        return TabBarInfo.OnlyBackHeader("")

    }

    override val checkedEntries = DataSource(listOf<OrderEntry>())
    override val notifications: DataSource<ScopeNotification?> = DataSource(null)
    val actions = DataSource(listOf(Action.REJECT_ALL, Action.ACCEPT_ALL))
    val showAlert: DataSource<Boolean> = DataSource(false)
    val showPaymentTypeOption : DataSource<Boolean> = DataSource(false)
    val showEditDiscountOption : DataSource<Boolean> = DataSource(false)
    val paymentType: DataSource<String> = DataSource("")
    val discountValue : DataSource<Double> = DataSource(0.0)

    /**
     * get the details of selected order
     */

    fun updateData() =
        EventCollector.sendEvent(
            Event.Action.Orders.GetOrderDetails(
                orderId,
                typeInfo
            )
        )

    /**
     * update the scope of payment option dialog
     */
    fun showPaymentOptions(enable: Boolean) {
        this.showPaymentTypeOption.value = enable
    }

    /**
     * update the scope of edit discount dialog
     */
    fun showEditDiscountOption(enable: Boolean) {
        this.showEditDiscountOption.value = enable
    }

    /**
     * update the scope of alert dialog
     */
    fun changeAlertScope(enable: Boolean) {
        this.showAlert.value = enable
    }

    /**
     * update the payment type selected by user
     */
    fun updatePaymentMethod(paymentMethod: PaymentMethod){
        this.paymentType.value = paymentMethod.serverValue
    }

    /**
     * update the discount entered by user
     */
    fun updateDiscountValue(discount: String){
        this.discountValue.value = discount.toDouble()
    }

    /**
     * submit discount value to server
     */
    fun submitDiscountValue(){
        EventCollector.sendEvent(Event.Action.Orders.EditDiscount(orderId, this.discountValue.value))
    }

    /**
     * submit payment type value to server
     */
    fun submitPaymentValue(value: PaymentMethod) {
        EventCollector.sendEvent(Event.Action.Orders.ChangePaymentMethod(orderId, value.serverValue))
    }

    fun selectEntry(
        taxType: TaxType,
        retailerName: String,
        canEditOrderEntry: Boolean,
        declineReason: List<DeclineReason>,
        entry: List<OrderEntry>,
        index: Int
    ) {
        order.value?.info?.id?.let {
            EventCollector.sendEvent(
                Event.Action.Orders.SelectEntry(
                    taxType = taxType,
                    retailerName = retailerName,
                    canEditOrderEntry = canEditOrderEntry,
                    orderId = it,
                    declineReason = declineReason,
                    entry = entry,
                    index = index
                )
            )
        }
    }


    fun acceptAction(action: Action) =
        EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(action, false))

    internal fun calculateActions() {
        actions.value = if (checkedEntries.value.isEmpty()) Action.all else Action.onlyAccept
    }

    enum class Action(
        val stringId: String,
        val weight: Float,
        val bgColorHex: String,
        val textColorHex: String = "#003657"
    ) {
        REJECT_ALL("reject_all", 0.5f, "#ed5152", "#FFFFFF"),
        ACCEPT_ALL("accept_all", 0.5f, "#FFD600"),
        ACCEPT("accept", 1f, "#FFD600");

        internal companion object {
            val all = listOf(REJECT_ALL, ACCEPT_ALL)
            val onlyAccept = listOf(ACCEPT)
        }
    }

    data class ServeQuotedProduct(val continueAction: Action) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "serve_quoted"

        fun `continue`() =
            EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(continueAction, true))
    }

    data class RejectAll(val continueAction: Action) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "sure_reject_all"

        fun `continue`() =
            EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(continueAction, true))
    }
}

class ConfirmOrderScope(
    var b2bData: DataSource<B2BData?>,
    override val order: DataSource<OrderTax?>,
    internal var acceptedEntries: List<OrderEntry>,
    internal var rejectedEntries: List<OrderEntry>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
    var declineReason: DataSource<List<DeclineReason>>,
) : Scope.Child.TabBar(), SelectableOrderEntry, CommonScope.WithNotifications {

    //pass on the seller info to be displayed on header
    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        val b2bData = b2bData.value
        b2bData?.let {
            val address = GeoData(
                location = "${it.addressData.district} ${it.addressData.pincode}",
                city = it.addressData.city,
                pincode = it.addressData.pincode.toString(),
                distance = 0.0,
                formattedDistance = "",
                addressLine = it.addressData.address,
                destination = null,
                landmark = "",
                origin = GeoPoints(0.0, 0.0)
            )
            val item = EntityInfo(
                tradeName = it.tradeName,
                phoneNumber = it.phoneNumber,
                geoData = address,
                seasonBoyData = null,
                seasonBoyRetailerData = null,
                drugLicenseNo1 = it.drugLicenseNo1,
                drugLicenseNo2 = it.drugLicenseNo2,
                gstin = it.gstin,
                isVerified = true,
                panNumber = it.panNumber,
                subscriptionData = null,
                unitCode = ""
            )

            return TabBarInfo.StoreTitle(
                storeName = it.tradeName,
                showNotifications = false,
                event = Event.Action.Orders.ShowDetailsOfRetailer(item, this)
            )
        }

        return TabBarInfo.OnlyBackHeader("")

    }


    val actions = DataSource(listOf(Action.CONFIRM))
    val entries = DataSource(acceptedEntries)
    override val checkedEntries = DataSource(emptyList<OrderEntry>())
    val tabs = listOf(Tab.ACCEPTED, Tab.REJECTED)
    val activeTab = DataSource(Tab.ACCEPTED)
    override val canEdit: Boolean = true
    var showDeclineReasonsBottomSheet = DataSource(false)
    private val selectedDeclineReason = DataSource("")
    val showAlert: DataSource<Boolean> = DataSource(false)

    fun acceptAction(action: Action) {
        when (action) {
            Action.REJECT -> {
                rejectedEntries = rejectedEntries + checkedEntries.value
                acceptedEntries = acceptedEntries - checkedEntries.value
                refreshEntries()
                selectedDeclineReason.value =
                    "" // empty the decline reason as new entries are added to decline list
            }
            Action.ACCEPT -> {
                acceptedEntries = acceptedEntries + checkedEntries.value
                rejectedEntries = rejectedEntries - checkedEntries.value
                refreshEntries()
            }
            Action.CONFIRM -> {
                if (rejectedEntries.isNotEmpty() && selectedDeclineReason.value.isEmpty()) {
                    manageDeclineBottomSheetVisibility(true)
                } else {
                    if(rejectedEntries.isEmpty()){
                        selectedDeclineReason.value = ""
                    }
                    EventCollector.sendEvent(Event.Action.Orders.Confirm(fromNotification = false, selectedDeclineReason.value))
                }
            }
        }
    }

    fun selectTab(tab: Tab) {
        activeTab.value = tab
        refreshEntries()
    }

    private fun refreshEntries() {
        checkedEntries.value = emptyList()
        entries.value = when (activeTab.value) {
            Tab.ACCEPTED -> acceptedEntries
            Tab.REJECTED -> rejectedEntries
        }
        actions.value = listOf(Action.CONFIRM)
    }

    /**
     * update the scope of alert dialog
     */
    fun changeAlertScope(enable: Boolean) {
        this.showAlert.value = enable
    }

    /**
     * manage decline bottom sheet  visibility
     */
    fun manageDeclineBottomSheetVisibility(openSheet: Boolean) {
        this.showDeclineReasonsBottomSheet.value = openSheet
    }

    /**
     * update the reason selected for decliening the order entry
     */
    fun updateDeclineReason(reason: String) {
        this.selectedDeclineReason.value = reason
    }

    enum class Action(
        val stringId: String,
        val weight: Float,
        val bgColorHex: String,
        val textColorHex: String = "#003657"
    ) {
        REJECT("reject_selected", 1f, "#ed5152", "#FFFFFF"),
        ACCEPT("accept_selected", 1f, "#0084D4", "#FFFFFF"),
        CONFIRM("confirm", 1f, "#FFD600");
    }

    enum class Tab(val stringId: String, val bgColorHex: String) {
        REJECTED("rejected", "#ED5152"),
        ACCEPTED("accepted", "#0084D4");
    }

    data class AreYouSure(val reasonCode: String = "") : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "sure_confirm_order"

        fun confirm() =
            EventCollector.sendEvent(Event.Action.Orders.Confirm(fromNotification = true, reasonCode))
    }
}

class OrderPlacedScope(val order: OrderTax) : Scope.Child.TabBar() {

    override fun goHome() = EventCollector.sendEvent(Event.Transition.Back)
}

interface SelectableOrderEntry : Scopable {
    val order: DataSource<OrderTax?>
    val checkedEntries: DataSource<List<OrderEntry>>
    val canEdit: Boolean

    fun toggleCheck(entry: OrderEntry) =
        EventCollector.sendEvent(Event.Action.Orders.ToggleCheckEntry(entry))
}