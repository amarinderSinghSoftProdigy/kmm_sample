package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRequest

class MockAuthScope : NetworkScope.Auth {
    override var token: String? = null

    init {
        "USING MOCK AUTH SCOPE".logIt()
    }

    override suspend fun login(request: UserRequest): TokenInfo? = mockResponse {
        TokenInfo("token", 10000000, "")
    }

    override suspend fun logout(): Boolean = mockBooleanResponse()

    override suspend fun sendOtp(phoneNumber: String): Boolean = mockBooleanResponse()

    override suspend fun retryOtp(phoneNumber: String): Boolean = mockBooleanResponse()

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Boolean =
        mockBooleanResponse()

    override suspend fun changePassword(phoneNumber: String, password: String): Boolean =
        mockBooleanResponse()
}