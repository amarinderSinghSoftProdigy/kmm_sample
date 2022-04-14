package com.zealsoftsol.medico.data

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class EmployeeRegistration
sealed class EmployeeValidation

@Serializable
data class EmployeeRegistration1(
    @Required
    val userType: String = "",
    @Required
    val firstName: String = "",
    @Required
    val lastName: String = "",
    @Required
    val email: String = "",
    @SerialName("mobileNumber")
    @Required
    val phoneNumber: String = "",
    @Required
    val password: String = "",
    @Required
    val verifyPassword: String = "",
) : EmployeeRegistration()

@Serializable
data class EmployeeValidation1(
    val email: String? = null,
    @SerialName("mobileNumber")
    val phoneNumber: String? = null,
    val password: String? = null,
    val verifyPassword: String? = null,
) : EmployeeValidation()

@Serializable
data class EmployeeRegistration2(
    @Required
    val pincode: String = "",
    @Required
    val addressLine1: String = "",
    @Required
    val landmark: String = "",
    @Required
    val location: String = "",
    @SerialName("cityTown")
    @Required
    val city: String = "",
    @Required
    val district: String = "",
    @Required
    val state: String = "",
) : EmployeeRegistration()

@Serializable
data class EmployeeValidation2(
    val addressLine1: String? = null,
    val location: String? = null,
    val landmark: String? = null,
    @SerialName("cityTown")
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
) : EmployeeValidation()

@Serializable
data class SubmitEmployeeRegistration(
    val customerType: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val password: String,
    val verifyPassword: String,
    val aadhaarCardNo: String? = null,
    @Required
    val termsAndConditions: Boolean = true,
    val pincode: String,
    val addressLine1: String,
    val landmark: String,
    val location: String,
    @SerialName("cityTown")
    val city: String,
    val district: String,
    val state: String,
) {
    companion object {

        private fun convertToString(data: UploadResponseData?): String {
            return Json.encodeToString(data)
        }

        fun employee(
            userRegistration1: EmployeeRegistration1,
            userRegistration2: EmployeeRegistration2,
            aadhaarCardNo: String
        ) = SubmitEmployeeRegistration(
            customerType = userRegistration1.userType,
            firstName = userRegistration1.firstName,
            lastName = userRegistration1.lastName,
            email = userRegistration1.email,
            phoneNumber = userRegistration1.phoneNumber,
            password = userRegistration1.password,
            verifyPassword = userRegistration1.verifyPassword,
            aadhaarCardNo = aadhaarCardNo,
            pincode = userRegistration2.pincode,
            addressLine1 = userRegistration2.addressLine1,
            location = userRegistration2.location,
            landmark = userRegistration2.landmark,
            city = userRegistration2.city,
            district = userRegistration2.district,
            state = userRegistration2.state,
        )
    }
}