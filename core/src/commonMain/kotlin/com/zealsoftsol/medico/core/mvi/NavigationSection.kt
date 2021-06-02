package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

data class NavigationSection(
    val user: ReadOnlyDataSource<User>,
    val main: List<NavigationOption> = NavigationOption.empty(),
    val footer: List<NavigationOption> = NavigationOption.empty(),
)

sealed class NavigationOption(private val event: Event, val stringId: String) {

    fun select() = EventCollector.sendEvent(event)

    object Dashboard : NavigationOption(Event.Transition.Dashboard, "dashboard")
    object Settings : NavigationOption(Event.Transition.Settings, "settings")
    object Help : NavigationOption(Event.Action.Help.GetHelp, "help")
    object Stockists : NavigationOption(Event.Transition.Management(UserType.STOCKIST), "stockists")
    object Retailers : NavigationOption(Event.Transition.Management(UserType.RETAILER), "retailers")
    object Hospitals : NavigationOption(Event.Transition.Management(UserType.HOSPITAL), "hospitals")
    object SeasonBoys :
        NavigationOption(Event.Transition.Management(UserType.SEASON_BOY), "season_boys")

    object Stores : NavigationOption(Event.Transition.Stores, "stores")

    object Orders : NavigationOption(Event.Transition.Orders, "orders")

    object NewOrders : NavigationOption(Event.Transition.NewOrders, "purchase_orders")

    object OrdersHistory : NavigationOption(Event.Transition.OrdersHistory, "orders_history")

    object Invoices : NavigationOption(Event.Transition.Invoices, "invoices")

    object LogOut : NavigationOption(Event.Action.Auth.LogOut(true), "log_out")

    companion object {
        internal fun empty() = emptyList<NavigationOption>()
        internal fun limited() = listOf(
            Settings,
        )

        internal fun default(userType: UserType) = listOfNotNull(
            Dashboard,
            Orders,
            NewOrders.takeIf { userType == UserType.STOCKIST },
            OrdersHistory.takeIf { userType == UserType.STOCKIST },
            Stockists,
            Retailers.takeIf { userType == UserType.STOCKIST || userType == UserType.SEASON_BOY },
            Hospitals.takeIf { userType == UserType.STOCKIST },
            SeasonBoys.takeIf { userType == UserType.STOCKIST },
            Stores,
            Invoices.takeIf { userType == UserType.STOCKIST },
            Help,
            Settings,
        )

        internal fun footer() = listOf(
            LogOut
        )
    }
}