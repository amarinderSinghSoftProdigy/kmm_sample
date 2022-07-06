package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CategoriesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreAddUserScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreCartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreUsersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InvoicesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocBuyerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.MenuScope
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.scope.regular.BannersScope
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.mvi.scope.regular.DealsScope
import com.zealsoftsol.medico.core.mvi.scope.regular.DemoScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.scope.regular.ManufacturerScope
import com.zealsoftsol.medico.core.mvi.scope.regular.OcrScope
import com.zealsoftsol.medico.core.mvi.scope.regular.PreferenceScope
import com.zealsoftsol.medico.core.mvi.scope.regular.QrCodeScope
import com.zealsoftsol.medico.core.mvi.scope.regular.RewardsScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WhatsappPreferenceScope
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
import com.zealsoftsol.medico.core.repository.getUserDataSourceV2
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.repository.requireUserOld
import com.zealsoftsol.medico.data.ProductsData
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

internal class TransitionEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val notificationRepo: NotificationRepo,
    private val cartRepo: CartRepo,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        navigator.apply {
            when (event) {
                is Event.Transition.Back -> dropScope()
                is Event.Transition.Refresh -> refresh()
                is Event.Transition.Otp -> setScope(
                    OtpScope.PhoneNumberInput.get(
                        phoneNumber = DataSource(""),
                        isForRegisteredUsersOnly = true,
                    )
                )
                is Event.Transition.SignUp -> setScope(SignUpScope.SelectUserType.get())
                is Event.Transition.Search -> setScope(SearchScope(event.autoComplete))
                is Event.Transition.Dashboard -> {
                    dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                    setScope(
                        DashboardScope.get(
                            userRepo.requireUser(),
                            userRepo.getUserDataSourceV2(),
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
                    )
                }
                is Event.Transition.Settings -> {
                    val user = userRepo.requireUser()
                    setScope(
                        SettingsScope.List(
                            notificationRepo.getUnreadMessagesDataSource(),
                            if (user.type == UserType.SEASON_BOY)
                                SettingsScope.List.Section.simple(user.isActivated)
                            else
                                SettingsScope.List.Section.all(user.isActivated),
                            userRepo.requireUser(),
                            event.showBackIcon,
                        )
                    )
                }
                is Event.Transition.Profile -> setScope(
                    SettingsScope.Profile(userRepo.requireUserOld())
                )
                is Event.Transition.ChangePassword -> setScope(
                    PasswordScope.VerifyCurrent()
                )
                is Event.Transition.Address -> setScope(
                    SettingsScope.Address(
                        userRepo.requireUserOld().addressData,
                        userRepo.requireUserOld()
                    )
                )
                is Event.Transition.GstinDetails -> setScope(
                    SettingsScope.GstinDetails(
                        userRepo.requireUserOld().details as User.Details.DrugLicense,
                        userRepo.requireUserOld()
                    )
                )
                is Event.Transition.Management -> setScope(
                    when (event.manageUserType) {
                        UserType.STOCKIST -> ManagementScope.User.Stockist(search = event.search)
                        UserType.RETAILER -> ManagementScope.User.Retailer(
                            canAdd = userRepo.requireUser().type == UserType.SEASON_BOY
                        )
                        UserType.HOSPITAL -> ManagementScope.User.Hospital()
                        UserType.SEASON_BOY -> ManagementScope.User.SeasonBoy()
                        UserType.EMPLOYEE -> EmployeeScope.SelectUserType.get()
                        UserType.PARTNER -> EmployeeScope.SelectUserType.get()
                        UserType.STOCKIST_EMPLOYEE -> EmployeeScope.SelectUserType.get()
                        UserType.RETAILER_EMPLOYEE -> EmployeeScope.SelectUserType.get()
                        UserType.SUB_STOCKIST -> ManagementScope.User.Stockist(search = event.search)
                    }
                )
                is Event.Transition.RequestCreateRetailer -> setScope(
                    ManagementScope.AddRetailer.TraderDetails(
                        registration = DataSource(UserRegistration3())
                    )
                )
                is Event.Transition.AddRetailerAddress -> withScope<ManagementScope.AddRetailer.TraderDetails> {
                    setScope(
                        ManagementScope.AddRetailer.Address(
                            it.registration.value,
                            DataSource(UserRegistration2()),
                        )
                    )
                }
//                is Event.Transition.PreviewUser -> {
//                    dropScope(
//                        Navigator.DropStrategy.To(ManagementScope.User.Retailer::class),
//                        updateDataSource = false
//                    )
//                    setScope(
//                        PreviewUserScope(
//                            event.registration2,
//                            event.registration3
//                        )
//                    )
//                }
                is Event.Transition.Notifications -> setScope(
                    NotificationScope.All()
                )
                is Event.Transition.Stores -> setScope(
                    StoresScope.StorePreview(
                    DataSource(Store()),
                    notificationCount = notificationRepo.getUnreadMessagesDataSource(),
                    cartItemsCount = cartRepo.getEntriesCountDataSource()
                    )
                )
                is Event.Transition.StoreDetail -> setScope(
                    StoresScope.StorePreview(
                        DataSource(event.store),
                        cartRepo.getEntriesCountDataSource(),
                        notificationRepo.getUnreadMessagesDataSource()
                    )
                )
                is Event.Transition.Cart -> setScope(
                    CartScope(
                        items = ReadOnlyDataSource(cartRepo.entries),
                        total = ReadOnlyDataSource(cartRepo.total),
                        isContinueEnabled = ReadOnlyDataSource(cartRepo.isContinueEnabled),
                        unreadNotifications = notificationRepo.getUnreadMessagesDataSource(),
                        cartCount = cartRepo.getEntriesCountDataSource()
                    )
                )
                is Event.Transition.Orders -> setScope(
                    OrdersScope(
                        listOf(OrdersScope.Tab.ORDERS),
                        notificationRepo.getUnreadMessagesDataSource(),
                        cartRepo.getEntriesCountDataSource()
                    )
                )
                is Event.Transition.PoOrdersAndHistory -> setScope(
                    OrdersScope(
                        listOf(OrdersScope.Tab.PO_ORDERS, OrdersScope.Tab.HISTORY_ORDERS),
                        notificationRepo.getUnreadMessagesDataSource(),
                        cartRepo.getEntriesCountDataSource()
                    )
                )
                is Event.Transition.MyInvoices -> setScope(
                    InvoicesScope(
                        isPoInvoice = false,
                        notificationRepo.getUnreadMessagesDataSource()
                    )
                )
                is Event.Transition.Offers -> setScope(
                    OffersScope.ViewOffers("deal_offer", DataSource(event.status))
                )
                is Event.Transition.CreateOffers -> setScope(
                    OffersScope.CreateOffer("create_offer")
                )
                is Event.Transition.PoInvoices -> setScope(
                    InvoicesScope(
                        isPoInvoice = true,
                        notificationRepo.getUnreadMessagesDataSource()
                    )
                )
                is Event.Transition.InStore -> setScope(
                    InStoreSellerScope(
                        notificationRepo.getUnreadMessagesDataSource(),
                        userRepo.userV2Flow.value!!.type
                    )
                )
                is Event.Transition.InStoreUsers -> setScope(
                    InStoreUsersScope()
                )
                is Event.Transition.InStoreAddUser -> setScope(
                    InStoreAddUserScope()
                )
                is Event.Transition.InStoreCart -> setScope(
                    InStoreCartScope(event.unitcode, event.name, event.address, event.phoneNumber)
                )
                is Event.Transition.WhatsappPreference -> setScope(
                    WhatsappPreferenceScope("whatsapp_preference")
                )
                is Event.Transition.Inventory -> setScope(
                    InventoryScope(
                        DataSource(event.type),
                        manufacturerCode = event.manufacturer
                    )
                )
                is Event.Transition.Menu -> setScope(
                    MenuScope(
                        userRepo.requireUser(),
                        notificationRepo.getUnreadMessagesDataSource()
                    )
                )
                is Event.Transition.Batches -> setScope(
                    BatchesScope(
                        event.spid,
                        event.batchData,
                        event.selectedBatchData,
                        event.requiredQty,
                        ProductsData()
                    )
                )
                is Event.Transition.QrCode -> setScope(QrCodeScope())
                is Event.Transition.IOCSeller -> setScope(IocSellerScope.InvUserListing(userRepo.userV2Flow.value!!.type))
                is Event.Transition.IOCBuyer -> setScope(IocBuyerScope.InvUserListing())
                is Event.Transition.AddEmployee -> setScope(EmployeeScope.SelectUserType.get())
                is Event.Transition.Preference -> setScope(PreferenceScope())
                is Event.Transition.Companies -> setScope(
                    ManagementScope.CompaniesScope(
                        event.title,
                        event.unitCode
                    )
                )
                is Event.Transition.Banners -> setScope(BannersScope(cartRepo.getEntriesCountDataSource()))
                is Event.Transition.Deals -> setScope(DealsScope(cartRepo.getEntriesCountDataSource()))
                is Event.Transition.Ocr -> setScope(OcrScope())
                is Event.Transition.Manufacturers -> setScope(ManufacturerScope())
                is Event.Transition.Demo -> setScope(DemoScope.DemoListing())
                is Event.Transition.Rewards -> setScope(RewardsScope())
                is Event.Transition.OnlineOrders -> setScope(
                    OrdersScope(
                        listOf(OrdersScope.Tab.ONLINE_ORDERS),
                        notificationRepo.getUnreadMessagesDataSource(),
                        cartRepo.getEntriesCountDataSource()
                    )
                )
                is Event.Transition.Categories -> setScope(CategoriesScope())
            }
        }
    }
}