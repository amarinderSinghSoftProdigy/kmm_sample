package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.Response

class MockPasswordScope : NetworkScope.Password {

    override suspend fun verifyPassword(password: String): Response.Wrapped<PasswordValidation> =
        mockResponse {
            Response.Wrapped(null, true)
        }

    override suspend fun changePassword(
        phoneNumber: String?,
        password: String
    ): Response.Wrapped<PasswordValidation> = mockResponse {
        Response.Wrapped(null, true)
    }
}