package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.PasswordValidation

class MockPasswordScope : NetworkScope.Password {

    init {
        "USING MOCK PASSWORD SCOPE".logIt()
    }

    override suspend fun verifyPassword(password: String) =
        mockValidationResponse<PasswordValidation> {
            PasswordValidation()
        }

    override suspend fun changePassword(
        phoneNumber: String?,
        password: String
    ) = mockValidationResponse<PasswordValidation> {
        PasswordValidation()
    }
}