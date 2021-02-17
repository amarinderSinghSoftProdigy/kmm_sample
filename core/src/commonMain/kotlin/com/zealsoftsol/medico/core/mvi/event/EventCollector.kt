package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ManagementEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.NotificationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OtpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PasswordEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.ProductEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RegistrationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.SearchEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

internal class EventCollector(
    navigator: Navigator,
    searchNetworkScope: NetworkScope.Search,
    productNetworkScope: NetworkScope.Product,
    managementNetworkScope: NetworkScope.Management,
    notificationNetworkScope: NetworkScope.Notification,
    private val userRepo: UserRepo,
) {
    private val loadHelperScope = CoroutineScope(compatDispatcher)

    private val delegateMap = mapOf<KClass<*>, EventDelegate<*>>(
        Event.Transition::class to TransitionEventDelegate(navigator, userRepo),
        Event.Action.Auth::class to AuthEventDelegate(navigator, userRepo),
        Event.Action.Otp::class to OtpEventDelegate(navigator, userRepo),
        Event.Action.ResetPassword::class to PasswordEventDelegate(navigator, userRepo),
        Event.Action.Registration::class to RegistrationEventDelegate(navigator, userRepo),
        Event.Action.Search::class to SearchEventDelegate(navigator, userRepo, searchNetworkScope),
        Event.Action.Product::class to ProductEventDelegate(
            navigator,
            userRepo,
            productNetworkScope
        ),
        Event.Action.Management::class to ManagementEventDelegate(
            navigator,
            userRepo,
            managementNetworkScope,
            LoadHelper(navigator, loadHelperScope),
        ),
        Event.Action.Notification::class to NotificationEventDelegate(
            navigator,
            userRepo,
            notificationNetworkScope,
            LoadHelper(navigator, loadHelperScope),
        )
    )

    init {
        events.onEach {
            delegateMap[it.typeClazz]!!.genericHandle(it)
        }.launchIn(CoroutineScope(compatDispatcher))
    }

    fun getStartingScope(): Scope {
        return when (userRepo.getUserAccess()) {
            UserRepo.UserAccess.FULL_ACCESS -> DashboardScope.get(
                user = userRepo.requireUser(),
                userDataSource = userRepo.getUserDataSource(),
            )
            UserRepo.UserAccess.LIMITED_ACCESS -> LimitedAccessScope.get(
                userRepo.requireUser(),
                userRepo.getUserDataSource(),
            )
            UserRepo.UserAccess.NO_ACCESS -> LogInScope(DataSource(userRepo.getAuthCredentials()))
        }
    }

    fun checkUser() {
        if (userRepo.getUserAccess() != UserRepo.UserAccess.NO_ACCESS) GlobalScope.launch(
            compatDispatcher
        ) {
            userRepo.loadUserFromServer()
        }
    }

    companion object {
        private val events = MutableSharedFlow<Event>(1, 0, BufferOverflow.DROP_OLDEST)

        fun sendEvent(event: Event) = events.tryEmit(event)
    }
}