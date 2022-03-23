package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserV2

/**
 * Entry scope for authorized unactivated users
 */
sealed class LimitedAccessScope : Scope.Child.TabBar(),
    CommonScope.UploadDocument,
    CommonScope.WithUser, CommonScope.WithUserV2 {

    class NonSeasonBoy(
        override val user: ReadOnlyDataSource<User>,
        override val userV2: ReadOnlyDataSource<UserV2>,
    ) : LimitedAccessScope() {

        override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()
    }

    class SeasonBoy(
        override val user: ReadOnlyDataSource<User>,
        override val userV2: ReadOnlyDataSource<UserV2>,
        override val aadhaarData: DataSource<AadhaarData>,
        override val isVerified: DataSource<Boolean>,
    ) : LimitedAccessScope(), AadhaarDataComponent {

        override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()
        override val isSeasonBoy = true
    }

    companion object {
        fun get(
            user: User,
            dataSource: ReadOnlyDataSource<User>,
            dataSourceV2: ReadOnlyDataSource<UserV2>
        ): TabBarScope {
            return TabBarScope(
                childScope = if (user.type == UserType.SEASON_BOY) {
                    val details = (user.details as User.Details.Aadhaar)
                    SeasonBoy(
                        user = dataSource,
                        userV2 = dataSourceV2,
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
                        userV2 = dataSourceV2
                    )
                },
                initialTabBarInfo = TabBarInfo.Simple(title = null),
                initialNavigationSection = NavigationSection(
                    dataSourceV2,
                    NavigationOption.limited(),
                    NavigationOption.footer()
                ),
            )
        }
    }
}