package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.ManufacturerData
import com.zealsoftsol.medico.data.OfferStatus
import com.zealsoftsol.medico.data.OffersData
import com.zealsoftsol.medico.data.RecentProductInfo
import com.zealsoftsol.medico.data.StockStatusData
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserV2

/**
 * Entry scope for authorized activated users
 */
class DashboardScope private constructor(
    val userType: UserType,
    val unreadNotifications: ReadOnlyDataSource<Int>,
    val cartItemsCount: ReadOnlyDataSource<Int>,
    val manufacturerList: ReadOnlyDataSource<List<ManufacturerData>?>,
    val stockStatusData: ReadOnlyDataSource<StockStatusData?>,
    val recentProductInfo: ReadOnlyDataSource<RecentProductInfo?>,
    val promotionData: ReadOnlyDataSource<List<OffersData>?>
) : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.NoIconTitle("", unreadNotifications, cartItemsCount)

    val sections = when (userType) {
        UserType.STOCKIST -> listOf(
//            Section.NOTIFICATIONS,
//            Section.NEW_ORDERS,
            Section.RETAILER_COUNT,
            Section.HOSPITAL_COUNT,
            Section.STOCKIST_CONNECT,
//            Section.SEASON_BOY_COUNT
        )
        UserType.RETAILER, UserType.HOSPITAL -> listOf(
//            Section.NOTIFICATIONS,
//            Section.ORDERS,
            Section.STOCKIST_ADD,
            Section.STOCKIST_COUNT,
        )
        UserType.SEASON_BOY -> listOf(
//            Section.NOTIFICATIONS,
//            Section.ORDERS,
            Section.RETAILER_ADD,
            Section.RETAILER_COUNT,
            Section.STOCKIST_ADD,
            Section.STOCKIST_COUNT,
        )
        else -> listOf(
//            Section.NOTIFICATIONS,
//            Section.ORDERS,
            Section.RETAILER_ADD,
            Section.RETAILER_COUNT,
            Section.STOCKIST_ADD,
            Section.STOCKIST_COUNT,
        )
    }

    override val isRoot: Boolean = true

    init {
        EventCollector.sendEvent(Event.Action.Auth.UpdateDashboard)
    }

    /**
     * Move to offers screens
     */
    fun moveToOffersScreen(status: OfferStatus) {
        EventCollector.sendEvent(Event.Transition.Offers(status))
    }

    /**
     * Move to Inventory screens
     */
    fun moveToInventoryScreen(
        type: InventoryScope.InventoryType = InventoryScope.InventoryType.ALL,
        manufacturerCode: String = ""
    ) {
        EventCollector.sendEvent(Event.Transition.Inventory(type, manufacturerCode))
    }

    /**
     * Opens search screen with params required for search based on brand
     * @param searchTerm Search term for the brand
     * @param field Which field to search (for eg - manufacturer)
     */
    fun startBrandSearch(searchTerm: String, field: String) {
        val autoComplete = AutoComplete(
            query = field, details = "in Manufacturers",
            suggestion = searchTerm
        )
        EventCollector.sendEvent(Event.Transition.Search(autoComplete))
    }

    fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)

    fun sendEvent(event: Event) = EventCollector.sendEvent(event)

    fun goToOrders() =
        EventCollector.sendEvent(if (userType == UserType.STOCKIST) Event.Transition.PoOrdersAndHistory else Event.Transition.Orders)

    fun selectSection(section: Section) = section.event?.let(EventCollector::sendEvent) ?: false

    enum class Section(val stringId: String, val event: Event?) {
        STOCKIST_COUNT("stockists", Event.Transition.Management(UserType.STOCKIST)),
        STOCKIST_ADD("add_stockist", Event.Transition.Management(UserType.STOCKIST)),
        STOCKIST_CONNECT("stockists", Event.Transition.Management(UserType.STOCKIST)),
        RETAILER_COUNT("retailers", Event.Transition.Management(UserType.RETAILER)),
        RETAILER_ADD("add_retailer", Event.Transition.Management(UserType.RETAILER)),
        HOSPITAL_COUNT("hospitals", Event.Transition.Management(UserType.HOSPITAL));
//        SEASON_BOY_COUNT("season_boys", Event.Transition.Management(UserType.SEASON_BOY));

//        NOTIFICATIONS("notifications", Event.Transition.Notifications),
//        ORDERS("orders", Event.Transition.Orders),
//        NEW_ORDERS("purchase_orders", Event.Transition.NewOrders),

        val isClickable: Boolean
            get() = event != null
    }

    companion object {
        fun get(
            user: UserV2,
            userDataSource: ReadOnlyDataSource<UserV2>,
            manufacturerData: ReadOnlyDataSource<List<ManufacturerData>?>,
            unreadNotifications: ReadOnlyDataSource<Int>,
            cartItemsCount: ReadOnlyDataSource<Int>,
            stockStatusData: ReadOnlyDataSource<StockStatusData?>,
            recentProductInfo: ReadOnlyDataSource<RecentProductInfo?>,
            promotionData: ReadOnlyDataSource<List<OffersData>?>
        ) = TabBarScope(
            childScope = DashboardScope(
                user.type,
                unreadNotifications = unreadNotifications,
                cartItemsCount = cartItemsCount,
                manufacturerList = manufacturerData,
                stockStatusData = stockStatusData,
                recentProductInfo = recentProductInfo,
                promotionData = promotionData
            ),
            initialTabBarInfo = TabBarInfo.Search(
                notificationItemsCount = unreadNotifications,
                cartItemsCount = cartItemsCount
            ),
            initialNavigationSection = NavigationSection(
                userDataSource,
                NavigationOption.default(user.type),
                NavigationOption.footer()
            ),
        )
    }
}