package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

sealed class MainScope : BaseScope() {
    abstract val user: DataSource<User>

    /**
     * Transition to [LogInScope] if successful
     */
    fun tryLogOut() = EventCollector.sendEvent(Event.Action.Auth.LogOut)

    data class LimitedAccess(
        override val user: DataSource<User>,
        override val errors: DataSource<ErrorCode?> = DataSource(null),
    ) : MainScope(), CommonScope.UploadDocument {

        val isDocumentUploaded: Boolean
            get() = !user.value.documentUrl.isNullOrEmpty()

        val isCameraOptionAvailable: Boolean
            get() = user.value.type != UserType.SEASON_BOY

        val supportedFileTypes: Array<FileType>
            get() = when {
                isDocumentUploaded -> emptyArray()
                user.value.type == UserType.SEASON_BOY -> FileType.forAadhaar()
                else -> FileType.forDrugLicense()
            }

        fun uploadAadhaar(base64: String) =
            EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))

        fun uploadDrugLicense(base64: String, fileType: FileType) =
            EventCollector.sendEvent(Event.Action.Registration.UploadDrugLicense(base64, fileType))
    }

    data class FullAccess(
        override val user: DataSource<User>,
    ) : MainScope()
}