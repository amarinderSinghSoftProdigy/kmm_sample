package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

class MainTestScope : BaseTestScope() {

    inline fun limitedAccessSeasonBoy(
        user: User,
        error: ErrorCode?
    ) {
        require(user.type == UserType.SEASON_BOY) { "only season boys are allowed" }
        nav.setCurrentScope(
            MainScope.LimitedAccess.SeasonBoy(
                user = DataSource(user),
                errors = DataSource(error)
            )
        )
    }

    inline fun limitedAccessNonSeasonBoy(
        user: User,
        error: ErrorCode?
    ) {
        require(user.type != UserType.SEASON_BOY) { "only non season boys are allowed" }
        nav.setCurrentScope(
            MainScope.LimitedAccess.NonSeasonBoy(
                user = DataSource(user),
                errors = DataSource(error)
            )
        )
    }
}
