package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.ErrorCode

interface CommonScope {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : WithErrors

    interface CanGoBack : CommonScope {
        fun goBack() {
            EventCollector.sendEvent(Event.Transition.Back)
        }
    }

    interface WithErrors : CommonScope {
        val errors: DataSource<ErrorCode?>

        fun dismissError() {
            errors.value = null
        }
    }

    interface WithNotifications : CommonScope {
        val notifications: DataSource<ScopeNotification?>

        fun dismissNotification() {
            notifications.value = null
        }
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