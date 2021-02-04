package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataHolder
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AadhaarData
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

sealed class SignUpScope(titleId: String) :
    Scope.Child.TabBar(TabBarInfo.Simple(ScopeIcon.BACK, titleId)),
    CommonScope.CanGoBack {

    val canGoNext: DataSource<Boolean> = DataSource(false)

    protected open fun checkCanGoNext() = Unit

    class SelectUserType private constructor(
        val userType: DataSource<UserType>,
    ) : SignUpScope("user_type") {

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

        companion object {
            fun get(userType: DataSource<UserType> = DataSource(UserType.STOCKIST)) =
                Host.TabBar(
                    childScope = SelectUserType(userType),
                    navigationSectionValue = null,
                )
        }
    }

    class PersonalData(
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?> = DataSource(null),
    ) : SignUpScope("personal_data") {

        init {
            checkCanGoNext()
        }

        fun changeFirstName(firstName: String) {
            trimInput(firstName, registration.value.firstName) {
                registration.value = registration.value.copy(firstName = it)
                checkCanGoNext()
            }
        }

        fun changeLastName(lastName: String) {
            trimInput(lastName, registration.value.lastName) {
                registration.value = registration.value.copy(lastName = it)
                checkCanGoNext()
            }
        }

        fun changeEmail(email: String) {
            trimInput(email, registration.value.email) {
                registration.value = registration.value.copy(email = it)
                checkCanGoNext()
            }
        }

        fun changePhoneNumber(phoneNumber: String) {
            trimInput(phoneNumber, registration.value.phoneNumber) {
                registration.value = registration.value.copy(phoneNumber = it)
                checkCanGoNext()
            }
        }

        fun changePassword(password: String) {
            trimInput(password, registration.value.password) {
                registration.value = registration.value.copy(password = it)
                checkCanGoNext()
            }
        }

        fun changeRepeatPassword(repeatPassword: String) {
            trimInput(repeatPassword, registration.value.verifyPassword) {
                registration.value = registration.value.copy(verifyPassword = it)
                checkCanGoNext()
            }
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

    class AddressData(
        internal val registrationStep1: UserRegistration1,
        val locationData: DataSource<LocationData?>,
        val registration: DataSource<UserRegistration2>,
        val userValidation: DataSource<UserValidation2?> = DataSource(null),
        val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
    ) : SignUpScope("address") {

        init {
            checkCanGoNext()
        }

        /**
         * Updates [locationData] with 1s debounce
         */
        fun changePincode(pincode: String) {
            if (pincode.length <= 6) {
                trimInput(pincode, registration.value.pincode) {
                    EventCollector.sendEvent(Event.Action.Registration.UpdatePincode(it))
                }
            }
        }

        fun changeAddressLine(address: String) {
            trimInput(address, registration.value.addressLine1) {
                registration.value = registration.value.copy(addressLine1 = it)
                checkCanGoNext()
            }
        }

        fun changeLocation(location: String) {
            trimInput(location, registration.value.location) {
                registration.value = registration.value.copy(location = it)
                checkCanGoNext()
            }
        }

        fun changeCity(city: String) {
            trimInput(city, registration.value.city) {
                registration.value = registration.value.copy(city = it)
                checkCanGoNext()
            }
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

    sealed class Details(
        titleId: String,
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
    ) : SignUpScope(titleId) {

        abstract val inputFields: List<Fields>

        class TraderData(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            val registration: DataSource<UserRegistration3> = DataSource(UserRegistration3()),
            val validation: DataSource<UserValidation3?> = DataSource(null),
        ) : Details("trader_details", registrationStep1, registrationStep2) {

            override val inputFields: List<Fields> = listOfNotNull(
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
                trimInput(tradeName, registration.value.tradeName) {
                    registration.value = registration.value.copy(tradeName = it)
                    checkCanGoNext()
                }
            }

            fun changeGstin(gstin: String) {
                if (gstin.length <= 15) {
                    trimInput(gstin, registration.value.gstin) {
                        registration.value = registration.value.copy(gstin = it)
                        checkCanGoNext()
                    }
                }
            }

            fun changePan(panNumber: String) {
                if (panNumber.length <= 10) {
                    trimInput(panNumber, registration.value.panNumber) {
                        registration.value = registration.value.copy(panNumber = it)
                        checkCanGoNext()
                    }
                }
            }

            fun changeDrugLicense1(drugLicenseNo: String) {
                if (drugLicenseNo.length <= 30) {
                    trimInput(drugLicenseNo, registration.value.drugLicenseNo1) {
                        registration.value = registration.value.copy(drugLicenseNo1 = it)
                        checkCanGoNext()
                    }
                }
            }

            fun changeDrugLicense2(drugLicenseNo: String) {
                if (drugLicenseNo.length <= 30) {
                    trimInput(drugLicenseNo, registration.value.drugLicenseNo2) {
                        registration.value = registration.value.copy(drugLicenseNo2 = it)
                        checkCanGoNext()
                    }
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
        }

        class Aadhaar(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            override val aadhaarData: DataSource<AadhaarData> = DataSource(AadhaarData("", "")),
        ) : Details("details", registrationStep1, registrationStep2),
            AadhaarDataHolder {

            override val isVerified: DataSource<Boolean> = canGoNext

            override val inputFields: List<Fields> = listOf(
                Fields.AADHAAR_CARD,
                Fields.SHARE_CODE,
            )

            /**
             * Transition to [LegalDocuments] if successful
             */
            fun addAadhaar() =
                EventCollector.sendEvent(Event.Action.Registration.AddAadhaar(aadhaarData.value))
        }

        enum class Fields {
            TRADE_NAME, GSTIN, PAN, LICENSE1, LICENSE2, AADHAAR_CARD, SHARE_CODE;
        }
    }

    /**
     * Should be handled as a root scope with minor differences in child scopes
     */
    sealed class LegalDocuments(
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        internal val registrationStep3: UserRegistration3,
    ) : SignUpScope("legal_documents"),
        CommonScope.PhoneVerificationEntryPoint,
        CommonScope.UploadDocument {

        init {
            canGoNext.value = true
        }

        fun skip() = EventCollector.sendEvent(Event.Action.Registration.Skip)

        class DrugLicense(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            registrationStep3: UserRegistration3,
            internal var storageKey: String? = null,
        ) : LegalDocuments(registrationStep1, registrationStep2, registrationStep3) {

            override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()
        }

        class Aadhaar(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            internal val aadhaarData: AadhaarData,
            internal var aadhaarFile: String? = null,
        ) : LegalDocuments(registrationStep1, registrationStep2, UserRegistration3()) {

            override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()
            override val isSeasonBoy = true
        }
    }
}

