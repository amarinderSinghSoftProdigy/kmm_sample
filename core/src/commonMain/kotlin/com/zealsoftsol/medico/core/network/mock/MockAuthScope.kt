package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRequest

class MockAuthScope : NetworkScope.Auth {

    init {
        "USING MOCK AUTH SCOPE".logIt()
    }

    override suspend fun login(request: UserRequest): BodyResponse<TokenInfo> = mockResponse {
        null
    }

    override suspend fun logout() = mockResponse { mockEmptyMapBody() }

    override suspend fun checkCanResetPassword(phoneNumber: String) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun sendOtp(phoneNumber: String) = mockResponse {
        mockEmptyMapBody()
    }

    override suspend fun retryOtp(phoneNumber: String) = mockResponse {
        mockEmptyMapBody()
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String) =
        mockResponse<TokenInfo> { null }
}
