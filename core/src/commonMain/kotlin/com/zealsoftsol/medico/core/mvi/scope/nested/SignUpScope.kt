package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.AddressComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.TraderDetailsComponent
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UploadResponseData
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRegistration4
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import android.util.Patterns


sealed class SignUpScope(private val titleId: String) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    val inputProgress: List<Int> = listOfNotNull(1, 2, 3, 4, 5)

    val canGoNext: DataSource<Boolean> = DataSource(false)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(
            title = StringResource.Static(titleId),
            titleColor = 0xff0084D4
        )
    }

    protected open fun checkCanGoNext() = Unit

    class SelectUserType private constructor(
        val userType: DataSource<UserType> = DataSource(UserType.STOCKIST),
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
            fun get() = TabBarScope(
                childScope = SelectUserType(),
                initialNavigationSection = null,
                initialTabBarInfo = TabBarInfo.Simple(icon = ScopeIcon.BACK, title = null)
            )
        }
    }

    class PersonalData(
        val isTermsAccepted: DataSource<Boolean> = DataSource(false),
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?> = DataSource(null),
    ) : SignUpScope("personal_data") {
        private var isPhoneValid = false

        init {
            checkCanGoNext()
        }

        fun changeTerms(isAccepted: Boolean) {
            isTermsAccepted.value = isAccepted
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

        fun setPhoneNumberValid(isValid: Boolean) {
            isPhoneValid = isValid
            checkCanGoNext()
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

        fun validEmail(email: String): Boolean {
            if (email.isEmpty()) {
                return true
            }
            val pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }

        fun validPhone(phone: String): Boolean {
            if (phone.isEmpty()) {
                return true
            }
            isPhoneValid = phone.length == 10
            return isPhoneValid
        }


        fun isValidPassword(str: String): Boolean {
            if (str.isEmpty()) {
                return true
            }
            val regex = ("^(?=.*[a-z])(?=."
                    + "*[A-Z])(?=.*\\d)"
                    + "(?=.*[-+_!@#$%^&*., ?]).+$")

            val p = regex.toRegex()
            return p.matches(str) && str.length >= 8
        }

        /**
         * Transition to [AddressData] if successful
         */
        fun validate(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                        && validEmail(email) && phoneNumber.isNotEmpty() && phoneNumber.length == 10
                        && password.isNotEmpty() && verifyPassword.isNotEmpty()
                        && verifyPassword == password && isPhoneValid
                        && isTermsAccepted.value && isValidPassword(password)
            }
        }
    }

    class AddressData(
        val registrationStep1: UserRegistration1,
        override val locationData: DataSource<LocationData?>,
        override val registration: DataSource<UserRegistration2>,
        val userValidation: DataSource<UserValidation2?> = DataSource(null),
        override val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
    ) : SignUpScope("address"), AddressComponent {

        val landmarkLimit = 30

        init {
            checkData()
        }

        override fun onDataValid(isValid: Boolean) {
            canGoNext.value = isValid
        }

        /**
         * Transition to [TraderData] or [LegalDocuments.Aadhaar] if successful
         */
        fun validate(userRegistration: UserRegistration2) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

    }

    sealed class Details(
        titleId: String,
        val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
    ) : SignUpScope(titleId) {

        abstract val inputFields: List<Fields>

        class TraderData(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            override val registration: DataSource<UserRegistration3> = DataSource(UserRegistration3()),
            override val validation: DataSource<UserValidation3?> = DataSource(null),
        ) : Details("trader_details", registrationStep1, registrationStep2),
            TraderDetailsComponent {
            val gstinLimit = 15
            val panLimit = 10
            val foodLicenseLimit = 14

            override val inputFields: List<Fields> = listOfNotNull(
                Fields.TRADE_NAME,
                Fields.GSTIN,
                Fields.PAN,
                Fields.LICENSE1,
                Fields.LICENSE2,
                Fields.FOOD_LICENSE,
            )

            init {
                changeFoodLicenseStatus(false)
                registration.value = registration.value.copy(state = registrationStep2.state)
                checkData()
                require(registrationStep1.userType != UserType.SEASON_BOY.serverValue) {
                    "trader data not available for season boy"
                }
            }

            override fun onDataValid(isValid: Boolean) {
                canGoNext.value = isValid
            }

            /**
             * Transition to [LegalDocuments] if successful
             */
            fun validate(userRegistration: UserRegistration3) =
                EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))
        }

        class Aadhaar(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            override val aadhaarData: DataSource<AadhaarData> = DataSource(AadhaarData("", "")),
        ) : Details("details", registrationStep1, registrationStep2),
            AadhaarDataComponent {

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
            TRADE_NAME, GSTIN, PAN, LICENSE1, LICENSE2, AADHAAR_CARD, SHARE_CODE, FOOD_LICENSE, FOOD_LICENSE_NUMBER;
        }
    }

    /**
     * Should be handled as a root scope with minor differences in child scopes
     */
    sealed class LegalDocuments(
        val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        val registrationStep3: UserRegistration3,
    ) : SignUpScope("legal_documents"),
        CommonScope.PhoneVerificationEntryPoint,
        CommonScope.UploadDocument {

        val registrationStep4: DataSource<UserRegistration4> = DataSource(UserRegistration4())

        fun validate(userRegistration: UserRegistration4) =
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

        fun skip() = EventCollector.sendEvent(Event.Action.Registration.Skip)

        class DrugLicense(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            registrationStep3: UserRegistration3,
            internal var storageKey: String? = null,
        ) : LegalDocuments(registrationStep1, registrationStep2, registrationStep3) {

            val tradeProfile: DataSource<UploadResponseData?> = DataSource(null)
            val drugLicense: DataSource<UploadResponseData?> = DataSource(null)
            val foodLicense: DataSource<UploadResponseData?> = DataSource(null)

            fun checkData() {
                val isValid =
                    drugLicense.value != null && tradeProfile.value != null && if (registrationStep3.hasFoodLicense) foodLicense.value != null else true
                onDataValid(isValid)
            }

            private fun onDataValid(isValid: Boolean) {
                val registrationStep = UserRegistration4()
                registrationStep.drugLicense = drugLicense.value
                registrationStep.tradeProfile = tradeProfile.value
                if (registrationStep3.hasFoodLicense)
                    registrationStep.foodLicense = foodLicense.value
                registrationStep4.value = registrationStep
                canGoNext.value = isValid
            }

            override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()
        }

        class Aadhaar(
            registrationStep1: UserRegistration1,
            registrationStep2: UserRegistration2,
            internal val aadhaarData: AadhaarData,
            internal var aadhaarFile: String? = null,
        ) : LegalDocuments(
            registrationStep1, registrationStep2,
            UserRegistration3()
        ) {

            fun onDataValid(isValid: Boolean) {
                canGoNext.value = isValid
            }

            override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()
            override val isSeasonBoy = true
        }
    }


    class PreviewDetails(
        val registrationStep1: UserRegistration1,
        val registrationStep2: UserRegistration2,
        val registrationStep3: UserRegistration3,
        val registrationStep4: UserRegistration4,
    ) : SignUpScope("preview"), CommonScope.PhoneVerificationEntryPoint {

        init {
            canGoNext.value = true
        }

        fun previewImage(item: String) =
            EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(item, type = "type"))

        fun submit() = EventCollector.sendEvent(Event.Action.Registration.Submit)
    }
}

