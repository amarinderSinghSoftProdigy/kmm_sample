package com.zealsoftsol.medico.core.mvi.scope.nested

import android.util.Patterns
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope.AddressData
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1

open class AddEmployeeScope() : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    val canGoNext: DataSource<Boolean> = DataSource(false)
    protected open fun checkCanGoNext() = Unit
    val selectedUserType = DataSource<UserType?>(null)
    val inputProgress: List<Int> = listOfNotNull(1, 2, 3, 4, 5)

    enum class OptionSelected {
        ADD_EMPLOYEE, VIEW_EMPLOYEE
    }

    fun selectUserType(userType: UserType) {
        selectedUserType.value = userType
    }

    /**
     * Transition to [PersonalData]
     */
    fun goToPersonalData() =
        EventCollector.sendEvent(Event.Action.AddEmployee.SelectUserType(selectedUserType.value!!))

    class PersonalData(
        val isTermsAccepted: DataSource<Boolean> = DataSource(false),
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?> = DataSource(null),
    ) : AddEmployeeScope() {
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
            EventCollector.sendEvent(Event.Action.Registration.Validate(userRegistration))

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

}