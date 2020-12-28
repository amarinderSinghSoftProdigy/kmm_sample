package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.ErrorCode

abstract class BaseScope {
    val isInProgress: DataSource<Boolean> = DataSource(false)
    val queueId: String = this::class.simpleName.orEmpty()

    object Root : BaseScope()
}

interface CanGoBack {
    fun goBack() {
        EventCollector.sendEvent(Event.Transition.Back)
    }
}

interface WithErrors {
    val errors: DataSource<ErrorCode?>

    fun dismissError() {
        errors.value = null
    }
}

interface WithNotifications {
    val notifications: DataSource<ScopeNotification?>

    fun dismissNotification() {
        notifications.value = null
    }
}

interface ScopeNotification {
    /**
     * Localized string key
     */
    val title: String

    /**
     * Localized string key
     */
    val body: String
}