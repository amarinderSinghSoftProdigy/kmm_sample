package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AddEmployeeEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.BankDetailsEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.BannersEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.BatchesEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.CartEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.DealsEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.DemoEventDelegates
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.HelpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.IOCBuyerEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.IOCEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.InStoreEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.InventoryEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.InvoicesEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ManagementEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ManufacturerEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.NotificationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OcrEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OffersEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OrdersEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OrdersHsnEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OtpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PasswordEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PreferencesEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ProductEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ProfileEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.QrCodeEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RegistrationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RewardsEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.SearchEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.StoresEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.WhatsappEventDelegate
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getBannerDataSource
import com.zealsoftsol.medico.core.repository.getBrandsDataSource
import com.zealsoftsol.medico.core.repository.getCategoriesDataSource
import com.zealsoftsol.medico.core.repository.getDealsDataSource
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getManufacturerDataSource
import com.zealsoftsol.medico.core.repository.getPromotionsDataSource
import com.zealsoftsol.medico.core.repository.getRecentProductsDataSource
import com.zealsoftsol.medico.core.repository.getStockConnectedDataSource
import com.zealsoftsol.medico.core.repository.getStockDataSource
import com.zealsoftsol.medico.core.repository.getStockistEmpBannerDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSourceV2
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.repository.requireUserOld
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.core.utils.TapModeHelper
import com.zealsoftsol.medico.data.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EventCollector(
    navigator: Navigator,
    searchNetworkScope: NetworkScope.Search,
    productNetworkScope: NetworkScope.Product,
    managementNetworkScope: NetworkScope.Management,
    storesNetworkScope: NetworkScope.Stores,
    helpNetworkScope: NetworkScope.Help,
    ordersNetworkScope: NetworkScope.Orders,
    inStoreNetworkScope: NetworkScope.InStore,
    inventoryScope: NetworkScope.InventoryStore,
    offersNetworkScope: NetworkScope.OffersStore,
    orderHsnScope: NetworkScope.OrderHsnEditStore,
    batchesScope: NetworkScope.BatchesStore,
    qrCodeScope: NetworkScope.QrCodeStore,
    iocNetworkScope: NetworkScope.IOCStore,
    iocBuyerNetworkScope: NetworkScope.IOCBuyerStore,
    employeeStore: NetworkScope.EmployeeStore,
    preferenceNetworkScope: NetworkScope.PreferencesStore,
    bannersNetworkScope: NetworkScope.BannersStore,
    dealsNetworkScope: NetworkScope.DealsStore,
    manufacturerScope: NetworkScope.ManufacturerStore,
    demo: NetworkScope.DemoData,
    rewardsNetworkScope: NetworkScope.RewardsStore,
    bankDetailsScope: NetworkScope.BankDetailsStore,
    private val notificationRepo: NotificationRepo,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    tapModeHelper: TapModeHelper,
) {
    private val loadHelperScope = CoroutineScope(compatDispatcher)

    private val delegateMap = mapOf<KClass<*>, EventDelegate<*>>(
        Event.Transition::class to TransitionEventDelegate(
            navigator,
            userRepo,
            notificationRepo,
            cartRepo,
        ),
        Event.Action.Auth::class to AuthEventDelegate(
            navigator,
            userRepo,
            notificationRepo,
            cartRepo
        ),
        Event.Action.Otp::class to OtpEventDelegate(navigator, userRepo),
        Event.Action.ResetPassword::class to PasswordEventDelegate(navigator, userRepo),
        Event.Action.Registration::class to RegistrationEventDelegate(navigator, userRepo),
        Event.Action.Search::class to SearchEventDelegate(navigator, userRepo, searchNetworkScope),
        Event.Action.Product::class to ProductEventDelegate(
            navigator,
            userRepo,
            productNetworkScope,
            tapModeHelper,
            cartRepo
        ),
        Event.Action.Management::class to ManagementEventDelegate(
            navigator,
            userRepo,
            managementNetworkScope,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Notification::class to NotificationEventDelegate(
            navigator,
            notificationRepo,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Stores::class to StoresEventDelegate(
            navigator,
            userRepo,
            cartRepo,
            notificationRepo,
            storesNetworkScope,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Cart::class to CartEventDelegate(
            navigator,
            userRepo,
            cartRepo,
        ),
        Event.Action.Help::class to HelpEventDelegate(
            navigator,
            helpNetworkScope,
        ),
        Event.Action.Orders::class to OrdersEventDelegate(
            navigator,
            userRepo,
            ordersNetworkScope,
            productNetworkScope,
            LoadHelper(navigator, loadHelperScope),
            tapModeHelper,
            cartRepo
        ),
        Event.Action.OrderHsn::class to OrdersHsnEventDelegate(
            navigator,
            userRepo,
            orderHsnScope,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Invoices::class to InvoicesEventDelegate(
            navigator,
            userRepo,
            ordersNetworkScope,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.InStore::class to InStoreEventDelegate(
            navigator,
            userRepo,
            inStoreNetworkScope,
            LoadHelper(navigator, loadHelperScope),
            cartRepo
        ),
        Event.Action.WhatsAppPreference::class to WhatsappEventDelegate(navigator, userRepo),
        Event.Action.Inventory::class to InventoryEventDelegate(
            navigator,
            userRepo,
            inventoryScope
        ),
        Event.Action.Profile::class to ProfileEventDelegate(navigator, userRepo),
        Event.Action.Offers::class to OffersEventDelegate(
            navigator,
            userRepo,
            offersNetworkScope
        ),
        Event.Action.Batches::class to BatchesEventDelegate(navigator, userRepo, batchesScope),
        Event.Action.QrCode::class to QrCodeEventDelegate(navigator, qrCodeScope),
        Event.Action.IOC::class to IOCEventDelegate(
            navigator,
            userRepo,
            iocNetworkScope
        ),
        Event.Action.IOCBuyer::class to IOCBuyerEventDelegate(
            navigator,
            userRepo,
            iocBuyerNetworkScope
        ),
        Event.Action.Employee::class to AddEmployeeEventDelegate(
            navigator,
            userRepo,
            employeeStore
        ),
        Event.Action.Preferences::class to PreferencesEventDelegate(
            navigator,
            userRepo,
            preferenceNetworkScope
        ),
        Event.Action.Banners::class to BannersEventDelegate(
            navigator,
            userRepo,
            cartRepo,
            bannersNetworkScope
        ),

        Event.Action.Deals::class to DealsEventDelegate(
            navigator,
            userRepo,
            cartRepo,
            dealsNetworkScope
        ),

        Event.Action.Ocr::class to OcrEventDelegate(
            navigator,
            userRepo,
        ),

        Event.Action.Manufacturers::class to ManufacturerEventDelegate(
            navigator,
            userRepo,
            manufacturerScope
        ),
        Event.Action.Demo::class to DemoEventDelegates(
            navigator,
            demo
        ),
        Event.Action.Rewards::class to RewardsEventDelegate(
            navigator,
            userRepo,
            rewardsNetworkScope
        ),
        Event.Action.BankData::class to BankDetailsEventDelegate(
            navigator,
            userRepo,
            bankDetailsScope
        )
    )

    init {
        var lastActionTime = Time.now
        val scope = CoroutineScope(compatDispatcher)
        events.onEach {
            lastActionTime = Time.now
            delegateMap[it.typeClazz]!!.genericHandle(it)
        }.launchIn(scope)
        scope.launch {
            while (isActive) {
                delay(60_000)
                if (Time.now - lastActionTime > userRepo.configFlow.value.sessionTimeout) {
                    sendEvent(Event.Action.Auth.LogOut(notifyServer = true))
                }
            }
        }
    }

    fun getStartingScope(): Scope {
        return when (userRepo.getUserAccess()) {
            UserRepo.UserAccess.FULL_ACCESS -> /*if (userRepo.userV2Flow.value!!.type == UserType.STOCKIST_EMPLOYEE)
                InStoreSellerScope.get(
                    userRepo.userV2Flow.value!!,
                    userRepo.getUserDataSourceV2(),
                    null
                )
            else*/
                DashboardScope.get(
                    user = userRepo.requireUser(),
                    userDataSource = userRepo.getUserDataSourceV2(),
                    manufacturerData = userRepo.getManufacturerDataSource(),
                    unreadNotifications = notificationRepo.getUnreadMessagesDataSource(),
                    cartItemsCount = cartRepo.getEntriesCountDataSource(),
                    stockStatusData = userRepo.getStockDataSource(),
                    recentProductInfo = userRepo.getRecentProductsDataSource(),
                    promotionData = userRepo.getPromotionsDataSource(),
                    dealsData = userRepo.getDealsDataSource(),
                    categoriesData = userRepo.getCategoriesDataSource(),
                    brandsData = userRepo.getBrandsDataSource(),
                    bannerData = userRepo.getBannerDataSource(),
                    stockistEmpBannerData = userRepo.getStockistEmpBannerDataSource(),
                    stockConnectedData = userRepo.getStockConnectedDataSource()
                )

            UserRepo.UserAccess.LIMITED_ACCESS -> LimitedAccessScope.get(
                userRepo.requireUserOld(),
                userRepo.getUserDataSource(),
                userRepo.getUserDataSourceV2(),
            )
            UserRepo.UserAccess.NO_ACCESS -> LogInScope(DataSource(userRepo.getAuthCredentials()))
        }
    }

    fun getAlertToggle(): Boolean {
        return userRepo.getAlertToggle()
    }


    fun updateData() {
        if (userRepo.getUserAccess() != UserRepo.UserAccess.NO_ACCESS) {
            GlobalScope.launch(compatDispatcher) {
                userRepo.loadUserFromServerV2()
            }
            if (userRepo.userV2Flow.value!!.type != UserType.STOCKIST_EMPLOYEE) {
                GlobalScope.launch(compatDispatcher) {
                    cartRepo.loadCartFromServer(userRepo.requireUser().unitCode)
                }
            }
            GlobalScope.launch(compatDispatcher) {
                userRepo.loadConfig()
            }
            if (userRepo.userV2Flow.value!!.type != UserType.STOCKIST_EMPLOYEE) {
                GlobalScope.launch(compatDispatcher) {
                    notificationRepo.loadUnreadMessagesFromServer()
                }
            }
        }
    }

    companion object {
        private val events = MutableSharedFlow<Event>(1, 0, BufferOverflow.DROP_OLDEST)

        fun sendEvent(event: Event) = events.tryEmit(event)
    }
}