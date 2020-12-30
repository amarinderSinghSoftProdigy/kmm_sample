package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataHolder
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Product
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

abstract class MainScope : BaseScope() {
    open val navigationSection: NavigationSection =
        NavigationSection(NavigationOption.default(), NavigationOption.footer())

    sealed class LimitedAccess : MainScope(), CommonScope.UploadDocument, NavAndSearchMainScope {

        override val navigationSection: NavigationSection =
            NavigationSection(NavigationOption.limited(), NavigationOption.footer())

        abstract val supportedFileTypes: Array<FileType>

        data class NonSeasonBoy(
            override val user: DataSource<User>,
            override val errors: DataSource<ErrorCode?> = DataSource(null),
        ) : LimitedAccess() {

            override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()

            fun uploadDrugLicense(base64: String, fileType: FileType) =
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(
                        user.value.phoneNumber,
                        user.value.email,
                        base64,
                        fileType
                    )
                )
        }

        data class SeasonBoy(
            override val user: DataSource<User>,
            override val errors: DataSource<ErrorCode?> = DataSource(null),
            override val aadhaarData: DataSource<AadhaarData> = DataSource(
                AadhaarData((user.value.details as User.Details.Aadhaar).cardNumber, "")
            ),
            override val isVerified: DataSource<Boolean> = DataSource(false),
        ) : LimitedAccess(), AadhaarDataHolder {

            override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()

            fun uploadAadhaar(base64: String) = EventCollector.sendEvent(
                Event.Action.Registration.UploadAadhaar(
                    user.value.phoneNumber,
                    user.value.email,
                    base64,
                )
            )
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

    data class Dashboard(
        override val user: DataSource<User>,
    ) : MainScope(), NavAndSearchMainScope

    data class ProductInfo(
        override val user: DataSource<User>,
        val product: Product,
        val isDetailsOpened: DataSource<Boolean> = DataSource(false),
    ) : MainScope(), NavAndSearchMainScope, CanGoBack {

        fun toggleDetails() {
            isDetailsOpened.value = !isDetailsOpened.value
        }

        fun addToCart() {

        }
    }
}

interface NavAndSearchMainScope {
    val user: DataSource<User>

    fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search)
}