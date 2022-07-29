package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.data.AnyResponse
import com.zealsoftsol.medico.core.data.BodyResponse
import com.zealsoftsol.medico.core.data.HeaderData
import com.zealsoftsol.medico.core.data.PasswordValidation
import com.zealsoftsol.medico.core.data.TokenInfo
import com.zealsoftsol.medico.core.data.UserRequest
import com.zealsoftsol.medico.core.data.ValidationResponse

interface NetworkScope {

    @Deprecated("break down")
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): BodyResponse<TokenInfo>
        suspend fun logout(): AnyResponse
        suspend fun checkCanResetPassword(phoneNumber: String): AnyResponse
        suspend fun sendOtp(phoneNumber: String): AnyResponse
        suspend fun retryOtp(phoneNumber: String): AnyResponse
        suspend fun verifyOtp(phoneNumber: String, otp: String): BodyResponse<TokenInfo>
    }

    interface BottomSheetStore : NetworkScope {
        suspend fun getDetails(
            unitCode: String,
        ): BodyResponse<HeaderData>
    }
}