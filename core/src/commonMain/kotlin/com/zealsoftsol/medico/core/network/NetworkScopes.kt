package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

interface NetworkScope {
    var token: String?
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): TokenInfo?
        suspend fun logout(): Boolean
        suspend fun sendOtp(phoneNumber: String): Boolean
        suspend fun retryOtp(phoneNumber: String): Boolean
        suspend fun verifyOtp(phoneNumber: String, otp: String): Boolean
        suspend fun changePassword(phoneNumber: String, password: String): Boolean
        suspend fun signUpPart1(userRegistration1: UserRegistration1): UserValidation1?
        suspend fun signUpPart2(userRegistration2: UserRegistration2): UserValidation2?
        suspend fun signUpPart3(userRegistration3: UserRegistration3): UserValidation3?
    }
}