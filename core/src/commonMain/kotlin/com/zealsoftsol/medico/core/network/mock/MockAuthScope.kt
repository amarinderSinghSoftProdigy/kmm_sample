package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SimpleBody
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

class MockAuthScope : NetworkScope.Auth {
    override var token: String? = null

    init {
        "USING MOCK AUTH SCOPE".logIt()
    }

    override fun clearToken() {

    }

    override suspend fun login(request: UserRequest): SimpleBody<TokenInfo> = mockResponse {
        Response.Body(TokenInfo("token", 10000000, "", ""), type = "success")
    }

    override suspend fun logout(): Boolean = mockBooleanResponse()

    override suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> = mockResponse {
        Response.Wrapped(null, true)
    }

    override suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode> = mockResponse {
        Response.Wrapped(null, true)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode> =
        mockResponse { Response.Wrapped(null, true) }

    override suspend fun changePassword(
        phoneNumber: String,
        password: String
    ): Response.Wrapped<PasswordValidation> = mockResponse { Response.Wrapped(null, true) }

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun getLocationData(pincode: String): Response.Wrapped<Location.Data> {
        return mockResponse {
            Response.Wrapped(
                Location.Data(listOf("location"), listOf("city"), "district", "state"),
                true
            )
        }
    }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean = mockBooleanResponse()

    override suspend fun uploadDrugLicense(
        licenseData: DrugLicenseUpload
    ): Response.Wrapped<StorageKeyResponse> = mockResponse {
        Response.Wrapped(StorageKeyResponse("key"), true)
    }

    override suspend fun signUp(submitRegistration: SubmitRegistration): Boolean = mockResponse {
        true
    }
}