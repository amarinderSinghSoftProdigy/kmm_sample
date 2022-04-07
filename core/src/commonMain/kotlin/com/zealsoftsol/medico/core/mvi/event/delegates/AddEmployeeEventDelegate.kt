package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.AddEmployeeScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRegistration4
import com.zealsoftsol.medico.data.UserType

internal class AddEmployeeEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.AddEmployee>(navigator) {

    override suspend fun handleEvent(event: Event.Action.AddEmployee) =
        when (event) {
            is Event.Action.AddEmployee.SelectUserType -> moveToPersonalDetailsScreen(event.userType)
            is Event.Action.AddEmployee.Validate -> validate(event.userRegistration)
        }

    private fun moveToPersonalDetailsScreen(userType: UserType) {
        navigator.withScope<AddEmployeeScope.SelectUserType> {
            it.userType.value = userType
            setScope(
                AddEmployeeScope.PersonalData(
                    registration = DataSource(
                        UserRegistration1(
                            userType = it.userType.value.serverValue,
                        )
                    ),
                    validation = DataSource(null),
                )
            )
        }
    }

    private suspend fun validate(userRegistration: UserRegistration) {
        when (userRegistration) {
            is UserRegistration1 -> navigator.withScope<AddEmployeeScope.PersonalData> {
                setScope(
                    AddEmployeeScope.AddressData(
                        registrationStep1 = it.registration.value,
                        locationData = DataSource(null),
                        registration = DataSource(UserRegistration2()),
                    )
                )
              /*  val result = withProgress {
                    userRepo.signUpValidation1(userRegistration)
                }
                it.validation.value = result.validations
                result.onSuccess { _ ->
                    setScope(
                        AddEmployeeScope.AddressData(
                            registrationStep1 = it.registration.value,
                            locationData = DataSource(null),
                            registration = DataSource(UserRegistration2()),
                        )
                    )
                }.onError(navigator)*/
            }
            is UserRegistration2 -> navigator.withScope<AddEmployeeScope.AddressData> {
                val result = withProgress {
                    userRepo.signUpValidation2(userRegistration)
                }
                it.userValidation.value = result.validations

                /*result.onSuccess { _ ->
                    val nextScope =
                        if (it.registrationStep1.userType == UserType.SEASON_BOY.serverValue) {
                            AddEmployeeScope.Details.Aadhaar(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                            )
                        } else {
                            AddEmployeeScope.Details.TraderData(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                            )
                        }
                    setScope(nextScope)
                }.onError(navigator)*/
            }
            is UserRegistration3 -> {
                /*navigator.withScope<AddEmployeeScope.Details.TraderData> {
                val result = withProgress {
                    userRepo.signUpValidation3(userRegistration)
                }
                it.validation.value = result.validations
                result.onSuccess { _ ->
                    setScope(
                        AddEmployeeScope.LegalDocuments.DrugLicense(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registrationStep2,
                            registrationStep3 = it.registration.value,
                        )
                    )
                }.onError(navigator)
            }*/
            }
            is UserRegistration4 -> {
              /*  navigator.withScope<AddEmployeeScope.LegalDocuments.DrugLicense> {
                    setScope(
                        AddEmployeeScope.PreviewDetails(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registrationStep2,
                            registrationStep3 = it.registrationStep3,
                            registrationStep4 = it.registrationStep4.value,
                        )
                    )
                }*/
            }
        }
    }

}