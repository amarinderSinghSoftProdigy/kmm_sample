package com.zealsoftsol.medico.core.mvi.scope.nested

import android.util.Patterns
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AddressComponent
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope.LegalDocuments
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2

open class AddEmployeeScope(private val titleId: String) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(
            title = StringResource.Static(titleId),
            titleColor = 0xff0084D4
        )
    }

    val canGoNext: DataSource<Boolean> = DataSource(false)
    protected open fun checkCanGoNext() = Unit
    val inputProgress: List<Int> = listOfNotNull(1, 2, 3, 4, 5)

    enum class OptionSelected {
        ADD_EMPLOYEE, VIEW_EMPLOYEE
    }


    class SelectUserType private constructor(
        val userType: DataSource<UserType> = DataSource(UserType.EMPLOYEE),
    ) : AddEmployeeScope("user_type") {

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
            EventCollector.sendEvent(Event.Action.AddEmployee.SelectUserType(userType.value))

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
    ) : AddEmployeeScope("personal_profile") {
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
        fun validate(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.AddEmployee.Validate(userRegistration))

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
        val registrationStep1: UserRegistration1,
        override val locationData: DataSource<LocationData?>,
        override val registration: DataSource<UserRegistration2>,
        val userValidation: DataSource<UserValidation2?> = DataSource(null),
        override val pincodeValidation: DataSource<PincodeValidation?> = DataSource(null),
    ) : AddEmployeeScope("address"), AddressComponent {

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
            EventCollector.sendEvent(Event.Action.AddEmployee.Validate(userRegistration))

    }

}