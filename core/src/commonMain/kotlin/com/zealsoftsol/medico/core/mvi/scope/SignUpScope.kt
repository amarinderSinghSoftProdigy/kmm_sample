package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.utils.AadhaarVerification
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

sealed class SignUpScope : BaseScope(), CanGoBack {

    val canGoNext: DataSource<Boolean> = DataSource(false)

    protected open fun checkCanGoNext() {

    }

    companion object {
        private val PAN_REGEX = Regex("^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?\$")
        private val GSTIN_REGEX =
            Regex("([0][1-9]|[1-2][0-9]|[3][0-7])([A-Z]{5})([0-9]{4})([A-Z]{1}[1-9A-Z]{1})([Z]{1})([0-9A-Z]{1})+")
    }

    data class SelectUserType(
        val userType: DataSource<UserType> = DataSource(UserType.STOCKIST),
    ) : SignUpScope() {

        init {
            canGoNext.value = true
        }

        fun chooseUserType(userType: UserType) {
            this.userType.value = userType
        }

        /**
         * Transition to [PersonalData]
         */
        fun goToPersonalData() =
            EventCollector.sendEvent(Event.Action.Registration.SelectUserType(userType.value))
    }

    data class PersonalData(
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?>,
    ) : SignUpScope() {

        init {
            checkCanGoNext()
        }

        fun changeFirstName(firstName: String) {
            registration.value = registration.value.copy(firstName = firstName)
            checkCanGoNext()
        }

        fun changeLastName(lastName: String) {
            registration.value = registration.value.copy(lastName = lastName)
            checkCanGoNext()
        }

        fun changeEmail(email: String) {
            registration.value = registration.value.copy(email = email)
            checkCanGoNext()
        }

        fun changePhoneNumber(phoneNumber: String) {
            registration.value = registration.value.copy(phoneNumber = phoneNumber)
            checkCanGoNext()
        }

        fun changePassword(password: String) {
            registration.value = registration.value.copy(password = password)
            checkCanGoNext()
        }

        fun changeRepeatPassword(repeatPassword: String) {
            registration.value = registration.value.copy(verifyPassword = repeatPassword)
            checkCanGoNext()
        }

        /**
         * Transition to [AddressData] if successful
         */
        fun validate(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                        && phoneNumber.isNotEmpty() && password.isNotEmpty()
                        && verifyPassword.isNotEmpty() && verifyPassword == password
            }
        }
    }

    data class AddressData(
        internal val registrationStep1: UserRegistration1,
        val locationData: DataSource<LocationData?>,
        val registration: DataSource<UserRegistration2>,
        val userValidation: DataSource<UserValidation2?> = DataSource(null),
        val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
    ) : SignUpScope() {

        init {
            checkCanGoNext()
        }

        /**
         * Updates [locationData] with 1s debounce
         */
        fun changePincode(pincode: String) {
            if (pincode.length <= 6) {
                EventCollector.sendEvent(Event.Action.Registration.UpdatePincode(pincode))
            }
        }

        fun changeAddressLine(address: String) {
            registration.value = registration.value.copy(addressLine1 = address)
            checkCanGoNext()
        }

        fun changeLocation(location: String) {
            registration.value = registration.value.copy(location = location)
            checkCanGoNext()
        }

        fun changeCity(city: String) {
            registration.value = registration.value.copy(city = city)
            checkCanGoNext()
        }

        /**
         * Transition to [TraderData] or [LegalDocuments.Aadhaar] if successful
         */
        fun validate(userRegistration: UserRegistration2) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                pincode.length == 6 && addressLine1.isNotEmpty() && location.isNotEmpty()
                        && city.isNotEmpty() && district.isNotEmpty() && state.isNotEmpty()
            }
        }
    }

    data class TraderData(
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        val registration: DataSource<UserRegistration3>,
        val validation: DataSource<UserValidation3?>,
    ) : SignUpScope() {

        val isPanValid: Boolean
            get() = registration.value.panNumber.isEmpty() || PAN_REGEX.matches(registration.value.panNumber)

        val isGstinValid: Boolean
            get() = registration.value.gstin.isEmpty() || GSTIN_REGEX.matches(registration.value.gstin)

        val inputFields: List<Fields> = listOfNotNull(
            Fields.TRADE_NAME,
            Fields.PAN.takeIf { registrationStep1.userType == UserType.STOCKIST.serverValue },
            Fields.GSTIN,
            Fields.LICENSE1,
            Fields.LICENSE2,
        )

        init {
            checkCanGoNext()
            require(registrationStep1.userType != UserType.SEASON_BOY.serverValue) {
                "trader data not available for season boy"
            }
        }

        fun changeTradeName(tradeName: String) {
            registration.value = registration.value.copy(tradeName = tradeName)
            checkCanGoNext()
        }

        fun changeGstin(gstin: String) {
            if (gstin.length <= 15) {
                registration.value = registration.value.copy(gstin = gstin)
                checkCanGoNext()
            }
        }

        fun changePan(panNumber: String) {
            if (panNumber.length <= 10) {
                registration.value = registration.value.copy(panNumber = panNumber)
                checkCanGoNext()
            }
        }

        fun changeDrugLicense1(drugLicenseNo: String) {
            if (drugLicenseNo.length <= 30) {
                registration.value = registration.value.copy(drugLicenseNo1 = drugLicenseNo)
                checkCanGoNext()
            }
        }

        fun changeDrugLicense2(drugLicenseNo: String) {
            if (drugLicenseNo.length <= 30) {
                registration.value = registration.value.copy(drugLicenseNo2 = drugLicenseNo)
                checkCanGoNext()
            }
        }

        /**
         * Transition to [LegalDocuments] if successful
         */
        fun validate(userRegistration: UserRegistration3) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                tradeName.isNotEmpty()
                        && (gstin.isNotEmpty() || panNumber.isNotEmpty())
                        && drugLicenseNo1.isNotEmpty() && drugLicenseNo2.isNotEmpty()
            }
        }

        enum class Fields {
            TRADE_NAME, GSTIN, PAN, LICENSE1, LICENSE2;
        }
    }

    /**
     * Should be handled as a root scope with minor differences in child scopes
     */
    sealed class LegalDocuments(
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        internal val registrationStep3: UserRegistration3,
    ) : SignUpScope(), WithErrors, CommonScope.PhoneVerificationEntryPoint {

        abstract val supportedFileTypes: Array<FileType>

        class DrugLicense(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            registrationStep3: UserRegistration3,
            override val errors: DataSource<ErrorCode?> = DataSource(null),
            internal val storageKey: String? = null,
        ) : LegalDocuments(registrationStep1, registrationStep2, registrationStep3),
            CommonScope.UploadDocument {

            override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()

            init {
                canGoNext.value = true
            }

            /**
             * Transition to [OtpScope.AwaitVerification] if successful
             */
            fun upload(base64: String, fileType: FileType) =
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(base64, fileType)
                )

            fun skip() = EventCollector.sendEvent(Event.Action.Registration.Skip)
        }

        class Aadhaar(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            val aadhaarData: DataSource<AadhaarData> = DataSource(AadhaarData("", "")),
            override val errors: DataSource<ErrorCode?> = DataSource(null),
            internal val aadhaarFile: String? = null,
        ) : LegalDocuments(registrationStep1, registrationStep2, UserRegistration3()) {

            override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()

            fun changeCard(card: String) {
                if (card.length <= 12) {
                    aadhaarData.value = aadhaarData.value.copy(cardNumber = card)
                    checkCanGoNext()
                }
            }

            fun changeShareCode(shareCode: String) {
                if (shareCode.length <= 4) {
                    aadhaarData.value = aadhaarData.value.copy(shareCode = shareCode)
                    checkCanGoNext()
                }
            }

            /**
             * Transition to [OtpScope.AwaitVerification] if successful
             */
            fun upload(base64: String) =
                EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))

            override fun checkCanGoNext() {
                canGoNext.value = aadhaarData.value.run {
                    cardNumber.length == 12 && AadhaarVerification.isValid(cardNumber) && shareCode.length == 4
                }
            }
        }
    }

    class Welcome(val fullName: String) : SignUpScope() {
        init {
            canGoNext.value = false
        }

        fun accept() = EventCollector.sendEvent(Event.Action.Registration.AcceptWelcome)
    }
}