package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.StorageKeyResponse
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

    fun clearToken()

    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): Response.Wrapped<TokenInfo>
        suspend fun logout(): Boolean
        suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode>
        suspend fun changePassword(
            phoneNumber: String,
            password: String,
        ): Response.Wrapped<PasswordValidation>

        suspend fun signUpPart1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1>
        suspend fun signUpPart2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2>
        suspend fun signUpPart3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3>
        suspend fun getLocationData(pincode: String): Response.Wrapped<Location.Data>
        suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean
        suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): Response.Wrapped<StorageKeyResponse>
    }
}