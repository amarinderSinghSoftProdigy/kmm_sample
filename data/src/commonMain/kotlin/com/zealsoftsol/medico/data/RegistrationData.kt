package com.zealsoftsol.medico.data

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed class UserRegistration
sealed class UserValidation

@Serializable
data class PincodeValidation(
    val pincode: String = "",
)

@Serializable
data class UserRegistration1(
    @SerialName("customerType")
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
) : UserRegistration()

@Serializable
data class UserValidation1(
    val email: String? = null,
    @SerialName("mobileNumber")
    val phoneNumber: String? = null,
    val password: String? = null,
    val verifyPassword: String? = null,
) : UserValidation()

@Serializable
data class UserRegistration2(
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
) : UserRegistration()

@Serializable
data class UserValidation2(
    val addressLine1: String? = null,
    val location: String? = null,
    val landmark: String? = null,
    @SerialName("cityTown")
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
) : UserValidation()

@Serializable
data class UserRegistration3(
    @Required
    val tradeName: String = "",
    @Required
    val gstin: String = "",
    @Required
    val panNumber: String = "",
    @Required
    val drugLicenseNo1: String = "",
    @Required
    val drugLicenseNo2: String = "",

    val foodLicenseNumber: String = "",

    val foodLicense: Boolean = false,
) : UserRegistration() {

    companion object {
        const val DRUG_LICENSE_1_PREFIX = "20B"
        const val DRUG_LICENSE_2_PREFIX = "21B"
    }
}

@Serializable
data class UserValidation3(
    val tradeName: String? = null,
    val gstin: String? = null,
    val panNumber: String? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null,
    val foodLicense: Boolean = false,
    val foodLicenseNumber: String? = null,
) : UserValidation()

@Serializable
data class SubmitRegistration(
    @SerialName("customerType")
    val userType: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val password: String,
    val verifyPassword: String,
    // Season boy
    val aadhaarCardNo: String? = null,
    val shareCode: String? = null,
    val consent: Boolean? = null,
    val aadhaarUploadFile: String? = null,
    // Stockist, Retailer, Hospital
    val tradeName: String? = null,
    val gstin: String? = null,
    val panNumber: String? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null,
    val drugLicenseStorageKey: String? = null,
    // End
    @Required
    val receiveMarketingMails: Boolean = true,
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
    val ipAddress: String,
    @Required
    val channel: String = "MOBILE",
    @Transient
    val isSeasonBoy: Boolean = false,
) {
    companion object {

        fun nonSeasonBoy(
            userRegistration1: UserRegistration1,
            userRegistration2: UserRegistration2,
            userRegistration3: UserRegistration3,
            storageKey: String?,
            ipAddress: String,
        ) = SubmitRegistration(
            userType = userRegistration1.userType,
            firstName = userRegistration1.firstName,
            lastName = userRegistration1.lastName,
            email = userRegistration1.email,
            phoneNumber = userRegistration1.phoneNumber,
            password = userRegistration1.password,
            verifyPassword = userRegistration1.verifyPassword,
            tradeName = userRegistration3.tradeName,
            gstin = userRegistration3.gstin,
            panNumber = userRegistration3.panNumber,
            drugLicenseNo1 = userRegistration3.drugLicenseNo1,
            drugLicenseNo2 = userRegistration3.drugLicenseNo2,
            drugLicenseStorageKey = storageKey,
            pincode = userRegistration2.pincode,
            addressLine1 = userRegistration2.addressLine1,
            location = userRegistration2.location,
            landmark = userRegistration2.landmark,
            city = userRegistration2.city,
            district = userRegistration2.district,
            state = userRegistration2.state,
            ipAddress = ipAddress,
        )

        fun seasonBoy(
            userRegistration1: UserRegistration1,
            userRegistration2: UserRegistration2,
            aadhaarData: AadhaarData,
            aadhaar: String?,
            ipAddress: String,
        ) = SubmitRegistration(
            userType = userRegistration1.userType,
            firstName = userRegistration1.firstName,
            lastName = userRegistration1.lastName,
            email = userRegistration1.email,
            phoneNumber = userRegistration1.phoneNumber,
            password = userRegistration1.password,
            verifyPassword = userRegistration1.verifyPassword,
            aadhaarCardNo = aadhaarData.cardNumber,
            shareCode = aadhaarData.shareCode,
            aadhaarUploadFile = aadhaar,
            consent = true,
            pincode = userRegistration2.pincode,
            addressLine1 = userRegistration2.addressLine1,
            location = userRegistration2.location,
            landmark = userRegistration2.landmark,
            city = userRegistration2.city,
            district = userRegistration2.district,
            state = userRegistration2.state,
            ipAddress = ipAddress,
            isSeasonBoy = true,
        )
    }
}

@Serializable
data class CreateRetailer(
    @SerialName("sbUnitCode")
    val seasonBoyUnitCode: String,
    val tradeName: String,
    val gstin: String,
    val panNumber: String,
    val drugLicenseNo1: String,
    val drugLicenseNo2: String,
    val pincode: String,
    val addressLine1: String,
    val location: String,
    val landmark: String,
    @SerialName("cityTown")
    val city: String,
    val district: String,
    val state: String,
    val termsAndConditions: Boolean = true,
) {
    companion object {

        fun from(
            unitCode: String,
            userRegistration2: UserRegistration2,
            userRegistration3: UserRegistration3,
        ) = CreateRetailer(
            seasonBoyUnitCode = unitCode,
            tradeName = userRegistration3.tradeName,
            gstin = userRegistration3.gstin,
            panNumber = userRegistration3.panNumber,
            drugLicenseNo1 = userRegistration3.drugLicenseNo1,
            drugLicenseNo2 = userRegistration3.drugLicenseNo2,
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