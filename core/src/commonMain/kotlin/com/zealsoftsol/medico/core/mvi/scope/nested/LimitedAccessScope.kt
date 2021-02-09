package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

sealed class LimitedAccessScope : Scope.Child.TabBar(TabBarInfo.Simple(ScopeIcon.HAMBURGER, null)),
    CommonScope.UploadDocument,
    CommonScope.WithUser {

    class NonSeasonBoy(
        override val user: ReadOnlyDataSource<User>,
    ) : LimitedAccessScope() {

        override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()
    }

    class SeasonBoy(
        override val user: ReadOnlyDataSource<User>,
        override val aadhaarData: DataSource<AadhaarData>,
        override val isVerified: DataSource<Boolean>,
    ) : LimitedAccessScope(), AadhaarDataComponent {

        override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()
        override val isSeasonBoy = true
    }

    companion object {
        fun get(user: User, dataSource: ReadOnlyDataSource<User>): Host.TabBar {
            return Host.TabBar(
                if (user.type == UserType.SEASON_BOY) {
                    val details = (user.details as User.Details.Aadhaar)
                    SeasonBoy(
                        user = dataSource,
                        aadhaarData = DataSource(
                            AadhaarData(
                                details.cardNumber,
                                details.shareCode
                            )
                        ),
                        isVerified = DataSource(false),
                    )
                } else {
                    NonSeasonBoy(
                        user = dataSource,
                    )
                },
                NavigationSection(
                    dataSource,
                    NavigationOption.limited(),
                    NavigationOption.footer()
                ),
            )
        }
    }
}