package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AddressComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.extra.TraderDetailsComponent
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation3

sealed class ManagementScope(
    icon: ScopeIcon = ScopeIcon.HAMBURGER,
) : Scope.Child.TabBar(TabBarInfo.Search(icon)) {

    sealed class User(
        val tabs: List<Tab>,
        internal val forType: UserType,
    ) : ManagementScope(), Loadable<EntityInfo> {

        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<EntityInfo>> = DataSource(emptyList())
        val activeTab: DataSource<Tab> = DataSource(tabs.first())
        override val searchText: DataSource<String> = DataSource("")

        init {
            EventCollector.sendEvent(Event.Action.Management.Load(isFirstLoad = true))
        }

        fun selectTab(tab: Tab) {
            searchText.value = ""
            pagination.reset()
            items.value = emptyList()
            activeTab.value = tab
            EventCollector.sendEvent(Event.Action.Management.Load(isFirstLoad = true))
        }

        fun selectItem(item: EntityInfo) =
            EventCollector.sendEvent(Event.Action.Management.Select(item))

        fun search(value: String) = EventCollector.sendEvent(Event.Action.Management.Search(value))

        fun loadItems() =
            EventCollector.sendEvent(Event.Action.Management.Load(isFirstLoad = false))

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
            fun requestCreateRetailer() =
                EventCollector.sendEvent(Event.Transition.RequestCreateRetailer)
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

    sealed class AddRetailer : ManagementScope(ScopeIcon.BACK) {

        val canGoNext: DataSource<Boolean> = DataSource(false)

        abstract fun next(): Boolean

        class TraderDetails(
            val isTermsAccepted: DataSource<Boolean> = DataSource(false),
            override val registration: DataSource<UserRegistration3>,
            override val validation: DataSource<UserValidation3?> = DataSource(null),
        ) : AddRetailer(),
            TraderDetailsComponent {

            override fun onDataValid(isValid: Boolean) {
                canGoNext.value = isValid && isTermsAccepted.value
            }

            fun changeTerms(isAccepted: Boolean) {
                isTermsAccepted.value = isAccepted
                checkData()
            }

            override fun next() =
                EventCollector.sendEvent(Event.Action.Management.VerifyRetailerTraderDetails)
        }

        class Address(
            val registration3: UserRegistration3,
            override val registration: DataSource<UserRegistration2>,
            override val locationData: DataSource<LocationData?> = DataSource(null),
            override val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
            override val notifications: DataSource<ScopeNotification?> = DataSource(null),
        ) : AddRetailer(),
            AddressComponent,
            CommonScope.WithNotifications {

            override fun onDataValid(isValid: Boolean) {
                canGoNext.value = isValid
            }

            override fun next() =
                EventCollector.sendEvent(Event.Action.Registration.ConfirmCreateRetailer)
        }
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

    data class Congratulations(val tradeName: String) : ScopeNotification {
        override val dismissEvent: Event = Event.Transition.Refresh
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = true
        override val title: String = "congratulations"
        override val body: String = "retailer_added_template"
    }

    enum class Tab(val stringId: String, val criteria: ManagementCriteria) {
        YOUR_RETAILERS("your_retailers", ManagementCriteria.PERSONAL),
        YOUR_SEASON_BOYS("your_season_boys", ManagementCriteria.PERSONAL),
        YOUR_STOCKISTS("your_stockists", ManagementCriteria.PERSONAL),
        ALL_STOCKISTS("all_stockists", ManagementCriteria.ALL),
        YOUR_HOSPITALS("your_hospitals", ManagementCriteria.PERSONAL);
    }
}