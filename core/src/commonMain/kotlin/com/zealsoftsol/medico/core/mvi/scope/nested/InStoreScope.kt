package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.InStoreCart
import com.zealsoftsol.medico.data.InStoreCartEntry
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.InStoreUserRegistration
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.StoreSubmitResponse
import com.zealsoftsol.medico.data.Total
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserV2

class InStoreSellerScope(
    val unreadNotifications: ReadOnlyDataSource<Int>?,
    val userType: UserType
) :
    Scope.Child.TabBar(),
    Loadable<InStoreSeller> {

    override val items: DataSource<List<InStoreSeller>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()
    override val isRoot: Boolean = true

    /*init {
        EventCollector.sendEvent(Event.Action.InStore.SellerLoad(isFirstLoad = true))
    }*/

    companion object {
        fun get(
            user: UserV2,
            userDataSource: ReadOnlyDataSource<UserV2>,
            unreadNotifications: ReadOnlyDataSource<Int>?,
        ) = TabBarScope(
            childScope = InStoreSellerScope(
                unreadNotifications = unreadNotifications,
                userType = user.type
            ),
            initialTabBarInfo = TabBarInfo.NoHeader(),
            initialNavigationSection = NavigationSection(
                userDataSource,
                NavigationOption.default(user.type),
                NavigationOption.footer()
            ),
        )
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        if (userType == UserType.STOCKIST_EMPLOYEE) TabBarInfo.NoHeader("") else
            TabBarInfo.NoIconTitle("", unreadNotifications)

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.InStore.SellerLoad(isFirstLoad = false))

    fun loadItems(boolean: Boolean) =
        EventCollector.sendEvent(Event.Action.InStore.SellerLoad(isFirstLoad = boolean))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.InStore.SellerSearch(value))
        } else {
            false
        }
    }

    fun selectItem(item: InStoreSeller) =
        EventCollector.sendEvent(
            Event.Action.InStore.SellerSelect(
                item.unitCode,
                item.tradeName,
                item.city,
                item.phoneNumber
            )
        )

    fun deleteItem(item: InStoreSeller) =
        EventCollector.sendEvent(
            Event.Action.InStore.DeleteOrder(
                item.unitCode,
                item.id
            )
        )

    fun goToInStoreUsers() =
        EventCollector.sendEvent(Event.Transition.InStoreUsers)
}

class InStoreProductsScope(
    internal val unitCode: String,
    private val sellerName: String,
    private val address: String,
    private val phoneNumber: String
) : Scope.Child.TabBar() {

    val items: DataSource<List<InStoreProduct>> = DataSource(emptyList())
    val totalItems: DataSource<Int> = DataSource(0)
    val searchText: DataSource<String> = DataSource("")
    val cart: DataSource<InStoreCart?> = DataSource(null)
    val currentPage = DataSource(0)
    val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList())
    var showNoProducts: DataSource<Boolean> = DataSource(false)
    val showToast = DataSource(false)
    val toastData: DataSource<ToastItem?> = DataSource(null)

    data class ToastItem(val productName: String, val quantity: Double, val freeQuantity: Double)

    fun setCurrentPage(page: Int) {
        currentPage.value = page
    }

    fun setToast(toastItem: ToastItem?, showToast: Boolean) {
        toastData.value = toastItem
        this.showToast.value = showToast
    }

    fun selectImage(item: String) {
        val url = CdnUrlProvider.urlFor(
            item, CdnUrlProvider.Size.Px320
        )
        EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(url))
    }


    fun firstLoad() {
        currentPage.value = 0
        EventCollector.sendEvent(Event.Action.InStore.ProductLoad(isFirstLoad = true, 0))
    }

    /*//pass on the seller info to be displayed on header
    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.InStoreProductTitle(sellerName, address, phoneNumber)*/

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.StoreTitle(
            storeName = sellerName.uppercase(),
            showNotifications = false,
            event = Event.Action.Management.GetDetails(unitCode),
            cartItemsCount = null
        )
    }

    fun loadItems() =
        EventCollector.sendEvent(
            Event.Action.InStore.ProductLoad(
                isFirstLoad = false,
                page = currentPage.value,
                searchTerm = searchText.value
            )
        )

    fun search(value: String) {
        searchText.value = value
        if (value.isNotEmpty()) {
            currentPage.value = 0
            EventCollector.sendEvent(Event.Action.InStore.ProductSearch(value))
        } else {
            firstLoad()
        }
    }

    fun selectItem(item: InStoreProduct) =
        EventCollector.sendEvent(Event.Action.InStore.ProductSelect(item))

    fun goToInStoreCart(): Boolean = EventCollector.sendEvent(
        Event.Transition.InStoreCart(
            unitCode,
            cart.value?.buyerTradeName.orEmpty(),
            cart.value?.city.orEmpty(),
            cart.value?.mobileNumber.orEmpty()
        )
    )

    fun addToCart(
        code: String, spid: String, quantity: Double, freeQuantity: Double, productName: String
    ): Boolean =
        EventCollector.sendEvent(
            Event.Action.InStore.AddCartItem(
                productName,
                code,
                spid,
                quantity,
                freeQuantity,
                currentPage.value,
                searchText.value
            )
        )

    fun selectAutoComplete(it: AutoComplete) {
        searchText.value = it.suggestion
        loadItems()
    }
}

class InStoreUsersScope : Scope.Child.TabBar(), Loadable<InStoreUser>, CommonScope.CanGoBack {

    override val items: DataSource<List<InStoreUser>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(Event.Action.InStore.UserLoad(isFirstLoad = true))
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

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
        EventCollector.sendEvent(
            Event.Action.InStore.SellerSelect(
                item.buyerUnitCode,
                item.tradeName, item.addressData.city, item.mobileNumber
            )
        )

    fun goToInStoreCreateUser() =
        EventCollector.sendEvent(Event.Transition.InStoreAddUser)
}

class InStoreAddUserScope(
    val registration: DataSource<InStoreUserRegistration> = DataSource(InStoreUserRegistration()),
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(), CommonScope.WithNotifications {

    val locationData: DataSource<LocationData?> = DataSource(null)

    val canGoNext: DataSource<Boolean> = DataSource(false)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

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
    val name: String,
    internal val address: String,
    internal val phoneNumber: String,
    val items: DataSource<List<InStoreCartEntry>> = DataSource(emptyList()),
    val total: DataSource<Total?> = DataSource(null),
    val paymentMethod: DataSource<String> = DataSource("")
) : Scope.Child.TabBar() {

    val showNoCart = DataSource(false)

    init {
        EventCollector.sendEvent(Event.Action.InStore.LoadCart)
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.StoreTitle(
            storeName = name,
            showNotifications = false,
            event = Event.Action.Management.GetDetails(unitCode),
            cartItemsCount = null
        )
    }

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

class InStoreOrderPlacedScope(val tradeName: String, val order: StoreSubmitResponse) :
    Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    val isOfferSwiped = DataSource(false)

    fun goToOrders() = EventCollector.sendEvent(Event.Transition.InStore)

    fun submitReward() = EventCollector.sendEvent(Event.Action.InStore.SubmitReward(order.storeId))
}