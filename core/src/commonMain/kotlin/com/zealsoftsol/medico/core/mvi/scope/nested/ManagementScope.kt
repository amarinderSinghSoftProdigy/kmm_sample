package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.ManagementItem
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.UserType

sealed class ManagementScope<T : ManagementItem>(
    val tabs: List<Tab>,
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.HAMBURGER)) {

    val pagination: Pagination = Pagination()
    val items: DataSource<List<T>> = DataSource(emptyList())
    val activeTab: DataSource<Tab> = DataSource(tabs.first())
    val searchText: DataSource<String> = DataSource("")

    init {
        loadItems()
    }

    fun selectTab(tab: Tab) {
        searchText.value = ""
        pagination.reset()
        items.value = emptyList()
        activeTab.value = tab
        loadItems()
    }

    fun selectItem(item: T) = EventCollector.sendEvent(Event.Action.Management.Select(item))

    fun search(value: String) = EventCollector.sendEvent(Event.Action.Management.Search(value))

    fun loadItems() = EventCollector.sendEvent(Event.Action.Management.Load)

    sealed class User(
        tabs: List<Tab>,
        internal val forType: UserType,
    ) : ManagementScope<EntityInfo>(tabs) {

        class Stockist(
            override val notifications: DataSource<ScopeNotification?> = DataSource(null)
        ) : User(
            forType = UserType.STOCKIST,
            tabs = listOf(Tab.YOUR_STOCKISTS, Tab.ALL_STOCKISTS),
        ), CommonScope.WithNotifications

        class Retailer(val canAdd: Boolean) : User(
            forType = UserType.RETAILER,
            tabs = listOf(Tab.YOUR_RETAILERS),
        ) {
            fun createRetailer() = EventCollector.sendEvent(Event.Transition.CreateRetailer)
        }

        class Hospital : User(
            forType = UserType.HOSPITAL,
            tabs = listOf(Tab.YOUR_HOSPITALS),
        )

        class SeasonBoy : User(
            forType = UserType.SEASON_BOY,
            tabs = listOf(Tab.YOUR_SEASON_BOYS),
        )
    }


    // Notifications

    class ChoosePaymentMethod(
        val paymentMethod: DataSource<PaymentMethod> = DataSource(PaymentMethod.CREDIT),
    ) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = true
        override val title: String = "choose_payment_method"
        override val body: String? = null

        fun changePaymentMethod(paymentMethod: PaymentMethod) {
            this.paymentMethod.value = paymentMethod
        }

        fun sendRequest() =
            EventCollector.sendEvent(Event.Action.Management.ChoosePayment(paymentMethod.value))
    }

    class ChooseNumberOfDays(
        val days: DataSource<Int> = DataSource(0),
    ) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String = "enter_number_of_days"
        override val body: String? = null

        fun changeDays(days: Int) {
            this.days.value = days
        }

        fun save() =
            EventCollector.sendEvent(Event.Action.Management.ChooseNumberOfDays(days.value))
    }

    object ThankYou : ScopeNotification {
        override val isSimple: Boolean = true
        override val isDismissible: Boolean = true
        override val title: String = "thank_you_for_request"
        override val body: String? = null
    }

    enum class Tab(val stringId: String, val criteria: ManagementCriteria) {
        YOUR_RETAILERS("your_retailers", ManagementCriteria.PERSONAL),
        YOUR_SEASON_BOYS("your_season_boys", ManagementCriteria.PERSONAL),
        YOUR_STOCKISTS("your_stockists", ManagementCriteria.PERSONAL),
        ALL_STOCKISTS("all_stockists", ManagementCriteria.ALL),
        YOUR_HOSPITALS("your_hospitals", ManagementCriteria.PERSONAL);
    }
}