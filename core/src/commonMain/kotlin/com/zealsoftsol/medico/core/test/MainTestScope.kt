package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import kotlinx.coroutines.flow.MutableStateFlow

class MainTestScope : BaseTestScope() {

    inline fun limitedAccessSeasonBoy(
        user: User,
        error: ErrorCode?
    ) {
        require(user.type == UserType.SEASON_BOY) { "only season boys are allowed" }
        nav.setScope(
            LimitedAccessScope.get(
                user,
                ReadOnlyDataSource(MutableStateFlow(user)),
            )
        )
        nav.setHostError(error)
    }

    inline fun limitedAccessNonSeasonBoy(
        user: User,
        error: ErrorCode?
    ) {
        require(user.type != UserType.SEASON_BOY) { "only non season boys are allowed" }
        nav.setScope(
            LimitedAccessScope.get(
                user,
                ReadOnlyDataSource(MutableStateFlow(user)),
            )
        )
        nav.setHostError(error)
    }
}
