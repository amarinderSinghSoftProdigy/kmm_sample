package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

internal class RegistrationEventDelegate(
    navigator: Navigator,
    private val authViewModel: AuthViewModel,
) : EventDelegate<Event.Action.Registration>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Registration) = when (event) {
        is Event.Action.Registration.SelectUserType -> selectUserType(event.userType)
        is Event.Action.Registration.SignUp -> trySignUp(event.userRegistration)
        is Event.Action.Registration.UpdatePincode -> updatePincode(event.pincode)
    }

    private fun selectUserType(userType: UserType) {
        navigator.withScope<SignUpScope.SelectUserType> {
            it.userType.value = userType
            transitionTo(
                SignUpScope.PersonalData(
                    registration = DataSource(UserRegistration1(userType = userType.serverValue)),
                    validation = DataSource(null),
                )
            )
        }
    }

    private suspend fun trySignUp(userRegistration: UserRegistration) {
        when (userRegistration) {
            is UserRegistration1 -> navigator.withScope<SignUpScope.PersonalData> {
                val validation = withProgress {
                    authViewModel.signUpPart1(userRegistration)
                }
                it.validation.value = validation.validation
                if (validation.isSuccess) {
                    transitionTo(
                        SignUpScope.AddressData(
                            registrationStep1 = it.registration.value,
                            locationData = DataSource(null),
                            registration = DataSource(UserRegistration2()),
                            validation = DataSource(null)
                        )
                    )
                }
            }
            is UserRegistration2 -> navigator.withScope<SignUpScope.AddressData> {
                val validation = withProgress {
                    authViewModel.signUpPart2(userRegistration)
                }
                it.validation.value = validation.validation
                if (validation.isSuccess) {
                    transitionTo(
                        SignUpScope.TraderData(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registration.value,
                            registration = DataSource(UserRegistration3()),
                            validation = DataSource(null)
                        )
                    )
                }
            }
            is UserRegistration3 -> navigator.withScope<SignUpScope.TraderData> {
                val validation = withProgress {
                    authViewModel.signUpPart3(userRegistration)
                }
                it.validation.value = validation.validation
                if (validation.isSuccess) {
//                    transitionTo(
//                    )
                }
            }
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<SignUpScope.AddressData> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val locationData = withProgress { authViewModel.getLocationData(pincode) }
                it.locationData.value = locationData
                if (locationData is Location.Data) {
                    it.registration.value = it.registration.value.copy(
                        district = locationData.district,
                        state = locationData.state,
                    )
                }
            }
        }
    }
}