package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
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

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1) =
        mockValidationResponse<UserValidation1> { UserValidation1() }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2) =
        mockValidationResponse<UserValidation2> { UserValidation2() }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3) =
        mockValidationResponse<UserValidation3> { UserValidation3() }

    override suspend fun getLocationData(pincode: String) = mockFullResponse {
        LocationData(listOf("location"), listOf("city"), "district", "state") to PincodeValidation()
    }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload) =
        mockResponse { mockEmptyMapBody() }

    override suspend fun uploadDrugLicense(
        licenseData: DrugLicenseUpload
    ) = mockResponse {
        StorageKeyResponse("key")
    }

    override suspend fun signUp(submitRegistration: SubmitRegistration) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3) =
        mockValidationResponse<UserValidation3> { UserValidation3() }
}
