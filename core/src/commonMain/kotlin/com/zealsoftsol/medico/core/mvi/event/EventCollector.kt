package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.CartEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.HelpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.InvoicesEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ManagementEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.NotificationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OrdersEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OtpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PasswordEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ProductEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RegistrationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.SearchEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.StoresEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getDashboardDataSource
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.core.utils.TapModeHelper
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

internal class EventCollector(
    navigator: Navigator,
    searchNetworkScope: NetworkScope.Search,
    productNetworkScope: NetworkScope.Product,
    managementNetworkScope: NetworkScope.Management,
    storesNetworkScope: NetworkScope.Stores,
    helpNetworkScope: NetworkScope.Help,
    ordersNetworkScope: NetworkScope.Orders,
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
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Invoices::class to InvoicesEventDelegate(
            navigator,
            userRepo,
            ordersNetworkScope,
            LoadHelper(navigator, loadHelperScope),
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
                if (Time.now - lastActionTime > INACTIVITY_THRESHOLD_MS) {
                    sendEvent(Event.Action.Auth.LogOut(notifyServer = true))
                }
            }
        }
    }

    fun getStartingScope(): Scope {
        return when (userRepo.getUserAccess()) {
            UserRepo.UserAccess.FULL_ACCESS -> DashboardScope.get(
                user = userRepo.requireUser(),
                userDataSource = userRepo.getUserDataSource(),
                dashboardData = userRepo.getDashboardDataSource(),
                unreadNotifications = notificationRepo.getUnreadMessagesDataSource(),
                cartItemsCount = cartRepo.getEntriesCountDataSource(),
            )
            UserRepo.UserAccess.LIMITED_ACCESS -> LimitedAccessScope.get(
                userRepo.requireUser(),
                userRepo.getUserDataSource(),
            )
            UserRepo.UserAccess.NO_ACCESS -> LogInScope(DataSource(userRepo.getAuthCredentials()))
        }
    }

    fun updateData() {
        if (userRepo.getUserAccess() != UserRepo.UserAccess.NO_ACCESS) {
            GlobalScope.launch(compatDispatcher) {
                userRepo.loadUserFromServer()
            }
            GlobalScope.launch(compatDispatcher) {
                cartRepo.loadCartFromServer(userRepo.requireUser().unitCode)
            }
            GlobalScope.launch(compatDispatcher) {
                notificationRepo.loadUnreadMessagesFromServer()
            }
        }
    }

    companion object {
        private const val INACTIVITY_THRESHOLD_MS = 1000 * 60 * 30

        private val events = MutableSharedFlow<Event>(1, 0, BufferOverflow.DROP_OLDEST)

        fun sendEvent(event: Event) = events.tryEmit(event)
    }
}