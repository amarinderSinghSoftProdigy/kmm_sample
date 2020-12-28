package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.User

class MainTestScope : BaseTestScope() {

    inline fun limitedAccess(
        user: User,
        error: ErrorCode?
    ) {
        nav.setCurrentScope(
            MainScope.LimitedAccess(
                user = DataSource(user),
                errors = DataSource(error)
            )
        )
    }
    
}
