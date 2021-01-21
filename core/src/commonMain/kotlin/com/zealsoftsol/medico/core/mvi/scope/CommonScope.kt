package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User

interface CommonScope : Scopable {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : CommonScope {
        val supportedFileTypes: Array<FileType>
        val isSeasonBoy: Boolean
            get() = false

        fun showBottomSheet() =
            EventCollector.sendEvent(Event.Action.Registration.ShowUploadBottomSheet)
    }

    interface CanGoBack : CommonScope {

        fun goBack() = EventCollector.sendEvent(Event.Transition.Back)
    }

    interface WithNotifications : CommonScope {
        val notifications: DataSource<ScopeNotification?>

        fun dismissNotification() {
            notifications.value = null
        }
    }

    interface WithUser : CommonScope {
        val user: ReadOnlyDataSource<User>
    }
}

interface ScopeNotification {
    val isSimple: Boolean
    val isDismissible: Boolean

    /**
     * Localized string key
     */
    val title: String

    /**
     * Localized string key
     */
    val body: String?
}