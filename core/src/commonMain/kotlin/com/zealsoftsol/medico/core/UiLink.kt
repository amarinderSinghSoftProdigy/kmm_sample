package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.extensions.logger
import com.zealsoftsol.medico.core.mvi.Environment
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.notifications.FirebaseMessaging
import com.zealsoftsol.medico.core.notifications.FirebaseMessagingCenter
import org.kodein.di.DI
import org.kodein.di.instance

object UiLink {

    /**
     * This should be called when the app's process starts.
     *
     * [context] - for Android client only, for iOS pass any object
     * [useMocks] - if true configures the app to use mocked backend instead of a server
     * [useNavigatorSafeCasts] - the app will crash if navigator tries to reference the wrong scope
     * [useNetworkInterceptor] - prints detailed info about requests and responses with the server
     * [loggerLevel] - configures log level
     */
    fun appStart(
        context: Any,
        useMocks: Boolean,
        useNavigatorSafeCasts: Boolean,
        useNetworkInterceptor: Boolean,
        loggerLevel: Logger.Level,
    ): AppStartResult {
        logger = logger.copy(level = loggerLevel)
        if (useMocks) Environment.Override.mocks(Environment.Mocks())
        val di = startKodein(context, useMocks, useNavigatorSafeCasts, useNetworkInterceptor)
        val navigator = directDI.instance<Navigator>()
        val eventCollector = directDI.instance<EventCollector>()
        eventCollector.updateData()
        return AppStartResult(di, navigator, directDI.instance<FirebaseMessagingCenter>())
    }

    /**
     * This should be called whenever the app is being restored from the background with empty navigation stack
     */
    fun setStartingScope() {
        val navigator = directDI.instance<Navigator>()
        val eventCollector = directDI.instance<EventCollector>()
        navigator.setScope(eventCollector.getStartingScope())
    }

    data class AppStartResult(
        val di: DI,
        val navigator: UiNavigator,
        val firebaseMessaging: FirebaseMessaging
    )
}

