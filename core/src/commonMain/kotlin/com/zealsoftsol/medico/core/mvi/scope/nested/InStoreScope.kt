package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.InStoreCart
import com.zealsoftsol.medico.data.InStoreCartEntry
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.InStoreUserRegistration
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.Total

class InStoreSellerScope : Scope.Child.TabBar(), Loadable<InStoreSeller> {

    override val items: DataSource<List<InStoreSeller>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(Event.Action.InStore.SellerLoad(isFirstLoad = true))
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NewDesignLogo

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.InStore.SellerLoad(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.InStore.SellerSearch(value))
        } else {
            false
        }
    }

    fun selectItem(item: InStoreSeller) =
        EventCollector.sendEvent(Event.Action.InStore.SellerSelect(item.unitCode))

    fun goToInStoreUsers() =
        EventCollector.sendEvent(Event.Transition.InStoreUsers)
}

class InStoreProductsScope(internal val unitCode: String) : Scope.Child.TabBar(),
    Loadable<InStoreProduct> {

    override val items: DataSource<List<InStoreProduct>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()
    val cart: DataSource<InStoreCart?> = DataSource(null)

    fun firstLoad() = EventCollector.sendEvent(Event.Action.InStore.ProductLoad(isFirstLoad = true))

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.InStoreProductTitle

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.InStore.ProductLoad(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.InStore.ProductSearch(value))
        } else {
            false
        }
    }

    fun selectItem(item: InStoreProduct) =
        EventCollector.sendEvent(Event.Action.InStore.ProductSelect(item))

    fun goToInStoreCart(): Boolean = EventCollector.sendEvent(
        Event.Transition.InStoreCart(
            unitCode,
            cart.value?.buyerTradeName.orEmpty()
        )
    )
}

class InStoreUsersScope : Scope.Child.TabBar(), Loadable<InStoreUser> {

    override val items: DataSource<List<InStoreUser>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(Event.Action.InStore.UserLoad(isFirstLoad = true))
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NewDesignLogo

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.InStore.UserLoad(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.InStore.UserSearch(value))
        } else {
            false
        }
    }

    fun selectItem(item: InStoreUser) =
        EventCollector.sendEvent(Event.Action.InStore.SellerSelect(item.buyerUnitCode))

    fun goToInStoreCreateUser() =
        EventCollector.sendEvent(Event.Transition.InStoreAddUser)
}

class InStoreAddUserScope(
    val registration: DataSource<InStoreUserRegistration> = DataSource(InStoreUserRegistration()),
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(), CommonScope.WithNotifications {

    val locationData: DataSource<LocationData?> = DataSource(null)

    val canGoNext: DataSource<Boolean> = DataSource(false)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NewDesignLogo

    fun changePaymentMethod(paymentMethod: String) {
        val pm = when (paymentMethod) {
            PaymentMethod.CREDIT.serverValue -> PaymentMethod.CREDIT
            PaymentMethod.CASH.serverValue -> PaymentMethod.CASH
            else -> PaymentMethod.CASH
        }
        registration.value = registration.value.copy(paymentMethod = pm)
    }

    fun changeTradeName(tradeName: String) {
        trimInput(tradeName, registration.value.tradeName) {
            registration.value = registration.value.copy(tradeName = it)
            checkCanGoNext()
        }
    }

    fun changeGstin(gstin: String) {
        if (gstin.length <= 15) {
            trimInput(gstin, registration.value.gstin) {
                registration.value = registration.value.copy(gstin = it)
                checkCanGoNext()
            }
        }
    }

    fun changePan(panNumber: String) {
        if (panNumber.length <= 10) {
            trimInput(panNumber, registration.value.panNumber) {
                registration.value = registration.value.copy(panNumber = it)
                checkCanGoNext()
            }
        }
    }

    fun changeDrugLicense1(drugLicenseNo: String) {
        if (drugLicenseNo.length <= 30) {
            trimInput(drugLicenseNo, registration.value.drugLicenseNo1) {
                registration.value = registration.value.copy(drugLicenseNo1 = it)
                checkCanGoNext()
            }
        }
    }

    fun changeDrugLicense2(drugLicenseNo: String) {
        if (drugLicenseNo.length <= 30) {
            trimInput(drugLicenseNo, registration.value.drugLicenseNo2) {
                registration.value = registration.value.copy(drugLicenseNo2 = it)
                checkCanGoNext()
            }
        }
    }

    fun changePhoneNumber(phoneNumber: String) {
        if (phoneNumber.length > 10) return
        trimInput(phoneNumber, registration.value.phoneNumber) {
            registration.value = registration.value.copy(phoneNumber = it)
            checkCanGoNext()
        }
    }

    fun changePincode(pincode: String) {
        if (pincode.length <= 6) {
            trimInput(pincode, registration.value.pincode) {
                EventCollector.sendEvent(Event.Action.InStore.AddUserUpdatePincode(it))
            }
        }
    }

    fun changeAddressLine(address: String) {
        trimInput(address, registration.value.addressLine1) {
            registration.value = registration.value.copy(addressLine1 = it)
            checkCanGoNext()
        }
    }

    fun changeLandmark(landmark: String) {
        if (landmark.length > 15) return
        trimInput(landmark, registration.value.landmark) {
            registration.value = registration.value.copy(landmark = it)
            checkCanGoNext()
        }
    }

    fun changeLocation(location: String) {
        trimInput(location, registration.value.location) {
            registration.value = registration.value.copy(location = it)
            checkCanGoNext()
        }
    }

    fun changeCity(city: String) {
        trimInput(city, registration.value.city) {
            registration.value = registration.value.copy(city = it)
            checkCanGoNext()
        }
    }

    fun createUser() = EventCollector.sendEvent(Event.Action.InStore.AddUser)

    fun reset() {
        registration.value = InStoreUserRegistration()
    }

    private fun checkCanGoNext() {
        canGoNext.value = registration.value.isNotEmpty()
    }

    object UserAddedSuccessfully : ScopeNotification {
        override val isSimple: Boolean = true
        override val isDismissible: Boolean = false
        override val dismissEvent: Event = Event.Action.InStore.FinishAddUser
        override val title: String = "success_user_add"
        override val body: String? = null
    }
}

class InStoreCartScope(
    internal val unitCode: String,
    internal val name: String,
    val items: DataSource<List<InStoreCartEntry>> = DataSource(emptyList()),
    val total: DataSource<Total?> = DataSource(null),
) : Scope.Child.TabBar() {

    init {
        EventCollector.sendEvent(Event.Action.InStore.LoadCart)
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NewDesignTitle(name)

    fun updateItemCount(
        item: InStoreCartEntry,
        quantity: Double,
        freeQuantity: Double
    ): Boolean {
        if (quantity < 0 || freeQuantity < 0) return false
        return if (quantity == 0.0 && freeQuantity == 0.0) {
            removeItem(item)
        } else {
            EventCollector.sendEvent(
                Event.Action.InStore.UpdateCartItem(
                    item.productCode,
                    item.spid,
                    quantity,
                    freeQuantity,
                )
            )
        }
    }

    fun removeItem(item: InStoreCartEntry) =
        EventCollector.sendEvent(Event.Action.InStore.RemoveCartItem(item.id))

    fun clearCart() = EventCollector.sendEvent(Event.Action.InStore.ClearCart)

    fun continueWithCart() = EventCollector.sendEvent(Event.Action.InStore.ConfirmCartOrder)
}

class InStoreOrderPlacedScope(val tradeName: String) : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NewDesignLogo

    fun goToOrders() = EventCollector.sendEvent(Event.Transition.InStore)
}