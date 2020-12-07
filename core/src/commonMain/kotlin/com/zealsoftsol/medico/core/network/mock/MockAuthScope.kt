package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.ValidationData

class MockAuthScope : NetworkScope.Auth {
    override var token: String? = null

    init {
        "USING MOCK AUTH SCOPE".logIt()
    }

    override suspend fun login(request: UserRequest): TokenInfo? = mockResponse {
        TokenInfo("token", 10000000, "", "")
    }

    override suspend fun logout(): Boolean = mockBooleanResponse()

    override suspend fun sendOtp(phoneNumber: String): Boolean = mockBooleanResponse()

    override suspend fun retryOtp(phoneNumber: String): Boolean = mockBooleanResponse()

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Boolean =
        mockBooleanResponse()

    override suspend fun changePassword(
        phoneNumber: String,
        password: String
    ): ValidationData<PasswordValidation> = mockResponse { ValidationData(null, true) }

    override suspend fun signUpPart1(userRegistration1: UserRegistration1): ValidationData<UserValidation1> {
        return mockResponse { ValidationData(null, true) }
    }

    override suspend fun signUpPart2(userRegistration2: UserRegistration2): ValidationData<UserValidation2> {
        return mockResponse { ValidationData(null, true) }
    }

    override suspend fun signUpPart3(userRegistration3: UserRegistration3): ValidationData<UserValidation3> {
        return mockResponse { ValidationData(null, true) }
    }

    override suspend fun getLocationData(pincode: String): Location.Data {
        return mockResponse {
            Location.Data(listOf("location"), listOf("city"), "district", "state")
        }
    }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean = mockBooleanResponse()

    override suspend fun uploadDrugLicense(
        licenseData: String,
        phoneNumber: String
    ): StorageKeyResponse? = mockResponse {
        StorageKeyResponse("key")
    }
}