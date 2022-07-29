package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.data.FileType

interface CommonScope : Scopable {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : CommonScope {
        val supportedFileTypes: Array<FileType>
        val isSeasonBoy: Boolean
            get() = false

    }

    interface CanGoBack : CommonScope {

        fun goBack() = EventCollector.sendEvent(Event.Transition.Back)
    }

    interface AlertScope : Scopable {
        var isOrderAlert: DataSource<Boolean>
    }

    interface WithNotifications : CommonScope {
        val notifications: DataSource<ScopeNotification?>

        fun dismissNotification() {
            notifications.value?.dismissEvent?.let { EventCollector.sendEvent(it) } ?: kotlin.run {
                notifications.value = null
            }
        }
    }
}

interface ScopeNotification {
    val isSimple: Boolean
    val isDismissible: Boolean
    val dismissEvent: Event?
        get() = null

    /**
     * Localized string key
     */
    val title: String?

    /**
     * Localized string key
     */
    val body: String?
}