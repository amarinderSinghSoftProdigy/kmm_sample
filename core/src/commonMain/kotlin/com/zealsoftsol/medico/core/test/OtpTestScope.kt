package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.data.ErrorCode

class OtpTestScope : BaseTestScope() {

    inline fun phoneNumberInput(
        phoneNumber: String,
        isForRegisteredUsers: Boolean,
        error: ErrorCode?,
    ) {
        nav.setCurrentScope(
            OtpScope.PhoneNumberInput(
                phoneNumber = DataSource(phoneNumber),
                isForRegisteredUsersOnly = isForRegisteredUsers,
                errors = DataSource(error),
            )
        )
    }
}