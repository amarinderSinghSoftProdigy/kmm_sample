package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.UserRequest

class MockAuthScope : NetworkScope.Auth {

    init {
        "USING MOCK AUTH SCOPE".logIt()
    }

    override suspend fun login(request: UserRequest): Response.Wrapped<ErrorCode> = mockResponse {
        Response.Wrapped(null, true)
    }

    override suspend fun logout(): Boolean = mockResponse { true }

    override suspend fun checkCanResetPassword(phoneNumber: String): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, true)
        }

    override suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> = mockResponse {
        Response.Wrapped(null, true)
    }

    override suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode> = mockResponse {
        Response.Wrapped(null, true)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode> =
        mockResponse { Response.Wrapped(null, true) }
}
