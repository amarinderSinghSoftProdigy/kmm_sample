package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

sealed class MainScope : BaseScope() {
    abstract val user: DataSource<User>

    /**
     * Transition to [LogInScope] if successful
     */
    fun tryLogOut() = EventCollector.sendEvent(Event.Action.Auth.LogOut(true))

    sealed class LimitedAccess : MainScope(), CommonScope.UploadDocument {

        val isDocumentUploaded: Boolean
            get() = user.value.isDocumentUploaded

        abstract val supportedFileTypes: Array<FileType>

        data class NonSeasonBoy(
            override val user: DataSource<User>,
            override val errors: DataSource<ErrorCode?> = DataSource(null),
        ) : LimitedAccess() {

            override val supportedFileTypes: Array<FileType> =
                if (isDocumentUploaded) emptyArray() else FileType.forDrugLicense()

            fun uploadDrugLicense(base64: String, fileType: FileType) =
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(
                        base64,
                        fileType
                    )
                )
        }

        data class SeasonBoy(
            override val user: DataSource<User>,
            override val errors: DataSource<ErrorCode?> = DataSource(null),
            override val aadhaarData: DataSource<AadhaarData> = DataSource(AadhaarData("", "")),
            override val isVerified: DataSource<Boolean> = DataSource(false),
        ) : LimitedAccess(), CommonScope.AadhaarDataHolder {

            override val supportedFileTypes: Array<FileType> =
                if (isDocumentUploaded) emptyArray() else FileType.forAadhaar()

            fun uploadAadhaar(base64: String) =
                EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))
        }

        companion object {
            fun from(user: User): LimitedAccess {
                return if (user.type == UserType.SEASON_BOY)
                    SeasonBoy(DataSource(user))
                else
                    NonSeasonBoy(DataSource(user))
            }
        }
    }

    data class FullAccess(
        override val user: DataSource<User>,
    ) : MainScope()
}