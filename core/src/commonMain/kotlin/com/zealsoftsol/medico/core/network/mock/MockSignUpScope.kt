package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

class MockSignUpScope : NetworkScope.SignUp {

    init {
        "USING MOCK SIGN UP SCOPE".logIt()
    }

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> {
        return mockResponse { Response.Wrapped(null, true) }
    }

    override suspend fun getLocationData(pincode: String): Response.Body<LocationData, PincodeValidation> {
        return mockResponse {
            Response.Body(
                LocationData(listOf("location"), listOf("city"), "district", "state"),
                type = "success",
            )
        }
    }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean = mockResponse { true }

    override suspend fun uploadDrugLicense(
        licenseData: DrugLicenseUpload
    ): Response.Wrapped<StorageKeyResponse> = mockResponse {
        Response.Wrapped(StorageKeyResponse("key"), true)
    }

    override suspend fun signUp(submitRegistration: SubmitRegistration): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, true)
        }

    override suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, true)
        }

    override suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> =
        mockResponse {
            Response.Wrapped(UserValidation3(), true)
        }
}
