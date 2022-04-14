package com.zealsoftsol.medico.core.mvi.scope.nested

import android.util.Patterns
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.EmployeeData
import com.zealsoftsol.medico.data.EmployeeRegistration1
import com.zealsoftsol.medico.data.EmployeeRegistration2
import com.zealsoftsol.medico.data.EmployeeValidation1
import com.zealsoftsol.medico.data.EmployeeValidation2
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserType

open class EmployeeScope(private val titleId: String) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {
    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(
            title = StringResource.Static(titleId),
            titleColor = 0xff0084D4
        )
    }

    val canGoNext: DataSource<Boolean> = DataSource(false)
    protected open fun checkCanGoNext() = Unit
    val inputProgress: List<Int> = listOfNotNull(1, 2, 3)

    enum class OptionSelected {
        ADD_EMPLOYEE, ADD_PARTNER
    }

    class SelectUserType private constructor(
        val userType: DataSource<UserType> = DataSource(UserType.EMPLOYEE),
    ) : EmployeeScope("user_type") {
        init {
            canGoNext.value = true
            EventCollector.sendEvent(Event.Action.Employee.ViewEmployee)
        }

        private var clickedPosition = 0

        val employeeData = DataSource<List<EmployeeData>>(emptyList())

        fun deleteEmployee(id: String, position: Int) {
            this.clickedPosition = position
            EventCollector.sendEvent(Event.Action.Employee.DeleteEmployee(id))
        }

        fun employeeDeleted() {
            employeeData.value.toMutableList().removeAt(clickedPosition)
        }

        fun chooseUserType(userType: UserType) {
            this.userType.value = userType
        }

        /**
         * Transition to [PersonalData]
         */
        fun goToPersonalData() =
            EventCollector.sendEvent(Event.Action.Employee.SelectUserType(userType.value))

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
        val registration: DataSource<EmployeeRegistration1>,
        val validation: DataSource<EmployeeValidation1?> = DataSource(null),
    ) : EmployeeScope("personal_profile") {
        //private var isPhoneValid = false
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
            if (phoneNumber.length > 10) return
            trimInput(phoneNumber, registration.value.phoneNumber) {
                registration.value = registration.value.copy(phoneNumber = it)
                checkCanGoNext()
            }
        }

        /*fun setPhoneNumberValid(isValid: Boolean) {
            isPhoneValid = isValid
            checkCanGoNext()
        }*/
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
            return phone.length == 10
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
        fun validate(userRegistration: EmployeeRegistration1) =
            EventCollector.sendEvent(Event.Action.Employee.Validate(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                        && validEmail(email) && phoneNumber.isNotEmpty() && phoneNumber.length == 10
                        && password.isNotEmpty() && verifyPassword.isNotEmpty()
                        && verifyPassword == password && isTermsAccepted.value
                        && isValidPassword(password)
            }
        }
    }

    class AddressData(
        val registrationStep1: EmployeeRegistration1,
        override val locationData: DataSource<LocationData?>,
        override val registration: DataSource<EmployeeRegistration2>,
        val userValidation: DataSource<EmployeeValidation2?> = DataSource(null),
        override val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
    ) : EmployeeScope("address"), EmployeeAddressComponent {
        val landmarkLimit = 30

        init {
            checkData()
        }

        override fun onDataValid(isValid: Boolean) {
            canGoNext.value = isValid
        }

        fun validate(userRegistration: EmployeeRegistration2) =
            EventCollector.sendEvent(Event.Action.Employee.Validate(userRegistration))
    }

    sealed class Details(
        titleId: String,
        val registrationStep1: EmployeeRegistration1,
        val registrationStep2: EmployeeRegistration2,
    ) : EmployeeScope(titleId) {
        abstract val inputFields: List<Fields>

        class Aadhaar(
            registrationStep1: EmployeeRegistration1,
            registrationStep2: EmployeeRegistration2,
            override val aadhaarData: DataSource<AadhaarData> = DataSource(AadhaarData("", "")),
        ) : Details("details", registrationStep1, registrationStep2),
            AadhaarDataComponent, CommonScope.PhoneVerificationEntryPoint,
            CommonScope.UploadDocument {
            override val isVerified: DataSource<Boolean> = canGoNext
            override val inputFields: List<Fields> = listOf(
                Fields.AADHAAR_CARD,
                Fields.SHARE_CODE,
            )

            fun addAadhaar() {
                EventCollector.sendEvent(Event.Action.Employee.Aadhaar(aadhaarData.value))
            }

            fun validate() {
                canGoNext.value = isVerified.value
            }

            override val supportedFileTypes: Array<FileType> = FileType.forAadhaar()
            override val isSeasonBoy = false
        }

        enum class Fields { AADHAAR_CARD, SHARE_CODE }
    }

    class SuccessEmployee : EmployeeScope("employees") {
        fun goToMenu() = EventCollector.sendEvent(Event.Transition.Menu)
    }
}

interface EmployeeAddressComponent : Scopable {
    val locationData: DataSource<LocationData?>
    val registration: DataSource<EmployeeRegistration2>
    val pincodeValidation: DataSource<PincodeValidation?>

    fun onDataValid(isValid: Boolean)

    fun checkData() {
        val isValid = registration.value.run {
            pincode.length == 6 && addressLine1.isNotEmpty() && landmark.isNotEmpty() && location.isNotEmpty()
                    && city.isNotEmpty() && district.isNotEmpty() && state.isNotEmpty()
        }
        onDataValid(isValid)
    }

    /**
     * Updates [locationData] with 1s debounce
     */
    fun changePincode(pincode: String) {
        if (pincode.length <= 6) {
            trimInput(pincode, registration.value.pincode) {
                EventCollector.sendEvent(Event.Action.Employee.UpdatePincode(it))
            }
        }
    }

    fun changeAddressLine(address: String) {
        trimInput(address, registration.value.addressLine1) {
            registration.value = registration.value.copy(addressLine1 = it)
            checkData()
        }
    }

    fun changeLandmark(landmark: String) {
        if (landmark.length > 30) return
        trimInput(landmark, registration.value.landmark) {
            registration.value = registration.value.copy(landmark = it)
            checkData()
        }
    }

    fun changeLocation(location: String) {
        trimInput(location, registration.value.location) {
            registration.value = registration.value.copy(location = it)
            checkData()
        }
    }

    fun changeCity(city: String) {
        trimInput(city, registration.value.city) {
            registration.value = registration.value.copy(city = it)
            checkData()
        }
    }
}
