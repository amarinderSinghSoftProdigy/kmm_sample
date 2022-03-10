package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AddressComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.extra.TraderDetailsComponent
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation3

sealed class ManagementScope : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    sealed class User(
        val tabs: List<Tab>,
        internal val forType: UserType,
    ) : ManagementScope(), Loadable<EntityInfo> {

        override val isRoot: Boolean = false

        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<EntityInfo>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
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

        fun search(value: String): Boolean {
            return if (searchText.value != value) {
                EventCollector.sendEvent(Event.Action.Management.Search(value))
            } else {
                false
            }
        }

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

    sealed class AddRetailer : ManagementScope() {

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
        val tradeName: String,
        val paymentMethod: DataSource<PaymentMethod> = DataSource(PaymentMethod.CREDIT),
        val creditDays: DataSource<String> = DataSource(""),
        val isSendEnabled: DataSource<Boolean> = DataSource(false),
    ) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = true
        override val title: String? = "choose_payment_method"
        override val body: String? = null

        init {
            paymentMethod.value = PaymentMethod.CASH
        }


        fun changePaymentMethod(paymentMethod: PaymentMethod) {
            this.paymentMethod.value = paymentMethod
            isSendEnabled.value = when (paymentMethod) {
                PaymentMethod.CASH -> true
                PaymentMethod.CREDIT -> creditDays.value.toIntOrNull() != null
            }
        }

        fun changeCreditDays(days: String) {
            if (days.length > 2) {
                return
            }
            isSendEnabled.value = when (this.paymentMethod.value) {
                PaymentMethod.CASH -> true
                PaymentMethod.CREDIT -> creditDays.value.toIntOrNull() != null
            }
            creditDays.value = days
            isSendEnabled.value = days.toIntOrNull() != null
        }

        fun sendRequest() =
            EventCollector.sendEvent(
                Event.Action.Management.ChoosePayment(
                    paymentMethod.value,
                    creditDays.value.toIntOrNull()
                )
            )
    }

    object ThankYou : ScopeNotification {
        override val isSimple: Boolean = true
        override val isDismissible: Boolean = true
        override val title: String? = "thank_you_for_request"
        override val body: String? = null
    }

    data class Congratulations(val tradeName: String) : ScopeNotification {
        override val dismissEvent: Event = Event.Transition.Refresh
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = true
        override val title: String? = "congratulations"
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