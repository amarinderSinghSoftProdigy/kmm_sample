package com.zealsoftsol.medico.data

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    var firstName: String = "",
    @Required
    var lastName: String = "",
    @Required
    var email: String = "",
    @SerialName("mobileNumber")
    @Required
    var phoneNumber: String = "",
    @Required
    var password: String = "",
    @Required
    var verifyPassword: String = "",
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
    var pincode: String = "",
    @Required
    var addressLine1: String = "",
    @Required
    var landmark: String = "",
    @Required
    var location: String = "",
    @SerialName("cityTown")
    @Required
    var city: String = "",
    @Required
    var district: String = "",
    @Required
    var state: String = "",
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
    var tradeName: String = "",
    @Required
    var gstin: String = "",
    @Required
    var panNumber: String = "",
    @Required
    var drugLicenseNo1: String = "",
    @Required
    var drugLicenseNo2: String = "",

    @Required
    var foodLicenseNo: String = "",

    @Required
    var hasFoodLicense: Boolean = false,

    @Required
    var state: String = "",

    var aadhaarCardNo: String = ""

) : UserRegistration() {

    companion object {
        const val DRUG_LICENSE_1_PREFIX = "20B"
        const val DRUG_LICENSE_2_PREFIX = "21B"
    }
}

@Serializable
data class UserRegistration4(
    var tradeProfile: UploadResponseData? = null,
    var drugLicense: UploadResponseData? = null,
    var foodLicense: UploadResponseData? = null,
) : UserRegistration()

@Serializable
data class UserValidation3(
    val tradeName: String? = null,
    val gstin: String? = null,
    val panNumber: String? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null,
    val foodLicense: Boolean = false,
    val foodLicenseNumber: String? = null,
    val aadhaarCardNo: String? = null
) : UserValidation()

@Serializable
data class LicenseDocumentData(
    val mobileNumber: String,
    val email: String,
    val userProfileDocumentRequest: ProfileImageUpload
)

@Serializable
data class UploadResponseData(
    val id: String = "",
    val documentType: String = "",
    var imageId: String = "",
    var cdnUrl: String = ""
)

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
    /*val drugLicenseStorageKey: String? = null,*/
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
    val ipAddress: String? = null,
    @Required
    val channel: String = "MOBILE",
    @Transient
    val isSeasonBoy: Boolean = false,

    @Required
    val hasFoodLicense: Boolean = false,

    val foodLicense: String? = null,
    val tradeProfile: String? = null,
    val drugLicenseProfile: String? = null,
    val foodLicenseProfile: String? = null

) {


    companion object {

        private fun convertToString(data: UploadResponseData?): String {
            return Json.encodeToString(data)
        }

        fun nonSeasonBoy(
            userRegistration1: UserRegistration1,
            userRegistration2: UserRegistration2,
            userRegistration3: UserRegistration3,
            userRegistration4: UserRegistration4,
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
            /*drugLicenseStorageKey = storageKey,*/
            pincode = userRegistration2.pincode,
            addressLine1 = userRegistration2.addressLine1,
            location = userRegistration2.location,
            landmark = userRegistration2.landmark,
            city = userRegistration2.city,
            district = userRegistration2.district,
            state = userRegistration2.state,
            ipAddress = ipAddress,
            hasFoodLicense = userRegistration3.hasFoodLicense,
            foodLicense = userRegistration3.foodLicenseNo,
            tradeProfile = convertToString(userRegistration4.tradeProfile),
            drugLicenseProfile = convertToString(userRegistration4.drugLicense),
            foodLicenseProfile = convertToString(userRegistration4.foodLicense),
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

class RegisterGlobal {
    var userReg1: UserRegistration1? = null
    var userReg2: UserRegistration2? = null
    var userReg3: UserRegistration3? = null
    var userReg4: UserRegistration4? = null
    var selection:String=""
}