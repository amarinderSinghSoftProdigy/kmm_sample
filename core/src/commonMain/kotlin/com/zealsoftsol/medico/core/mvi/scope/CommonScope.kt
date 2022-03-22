package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserV2

interface CommonScope : Scopable {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : CommonScope {
        val supportedFileTypes: Array<FileType>
        val isSeasonBoy: Boolean
            get() = false

        fun showBottomSheet() =
            EventCollector.sendEvent(Event.Action.Registration.ShowUploadBottomSheet)

        fun showBottomSheet(type: String, registrationStep1: UserRegistration1) =
            EventCollector.sendEvent(
                Event.Action.Registration.ShowUploadBottomSheets(
                    type,
                    registrationStep1
                )
            )
    }

    interface CanGoBack : CommonScope {

        fun goBack() = EventCollector.sendEvent(Event.Transition.Back)
    }

    interface WithNotifications : CommonScope {
        val notifications: DataSource<ScopeNotification?>

        fun dismissNotification() {
            notifications.value?.dismissEvent?.let { EventCollector.sendEvent(it) } ?: kotlin.run {
                notifications.value = null
            }
        }
    }

    interface WithUserV2 : CommonScope {
        val userV2: ReadOnlyDataSource<UserV2>
    }
    interface WithUser : CommonScope {
        val user: ReadOnlyDataSource<User>
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