package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchResponse
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

interface NetworkScope {

    @Deprecated("break down")
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): Response.Wrapped<ErrorCode>
        suspend fun logout(): Boolean

        suspend fun checkCanResetPassword(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode>

        suspend fun changePassword(
            phoneNumber: String,
            password: String,
        ): Response.Wrapped<PasswordValidation>

        suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1>
        suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2>
        suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3>
        suspend fun getLocationData(pincode: String): Response.Body<LocationData, PincodeValidation>
        suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean
        suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): Response.Wrapped<StorageKeyResponse>
        suspend fun signUp(submitRegistration: SubmitRegistration): Response.Wrapped<ErrorCode>
    }

    interface Customer : NetworkScope {
        suspend fun getCustomerData(): Response.Wrapped<CustomerData>
    }

    interface Search : NetworkScope {
        suspend fun search(value: String): Response.Wrapped<SearchResponse>
    }
}