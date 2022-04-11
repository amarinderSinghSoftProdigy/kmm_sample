package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration4
import com.zealsoftsol.medico.data.UserType

internal class AddEmployeeEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Employee>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Employee) =
        when (event) {
            is Event.Action.Employee.SelectUserType -> moveToPersonalDetailsScreen(event.userType)
            is Event.Action.Employee.Validate -> validate(event.userRegistration)
            is Event.Action.Employee.Aadhaar -> addAadhaar(event.aadhaarData)
            is Event.Action.Employee.UploadAadhaar -> uploadDocument(event)
            is Event.Action.Employee.MoveToViewEmployee -> moveToEmployeeScreen()
            is Event.Action.Employee.ViewEmployee -> viewEmployee()
        }

    private fun moveToEmployeeScreen() {
        navigator.withScope<EmployeeScope.SelectUserType> {
            setScope(EmployeeScope.ViewEmployee())
        }
    }

    private fun viewEmployee() {
    }

    private fun moveToPersonalDetailsScreen(userType: UserType) {
        navigator.withScope<EmployeeScope.SelectUserType> {
            it.userType.value = userType
            setScope(
                EmployeeScope.PersonalData(
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
            is UserRegistration1 -> navigator.withScope<EmployeeScope.PersonalData> {
                setScope(
                    EmployeeScope.AddressData(
                        registrationStep1 = it.registration.value,
                        locationData = DataSource(null),
                        registration = DataSource(UserRegistration2()),
                    )
                )
            }
            is UserRegistration2 -> navigator.withScope<EmployeeScope.AddressData> {
                /* val result = withProgress {
                     userRepo.signUpValidation2(userRegistration)
                 }
                 it.userValidation.value = result.validations
 */
                setScope(
                    EmployeeScope.Details.Aadhaar(
                        registrationStep1 = it.registrationStep1,
                        registrationStep2 = it.registration.value,
                    )
                )
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

    private fun addAadhaar(aadhaarData: AadhaarData) {
        navigator.withScope<EmployeeScope.Details.Aadhaar> {
            it.aadhaarData.value = aadhaarData
            setScope(
                EmployeeScope.SuccessEmployee()
            )
        }
    }

    private suspend fun uploadDocument(event: Event.Action.Employee) {
        navigator.withScope<CommonScope.UploadDocument> {
            var storageKey: String? = null
            val isSuccess = withProgress {
                when (event) {
                    is Event.Action.Employee.UploadAadhaar -> {
                        val userReg = (it as? SignUpScope.LegalDocuments.Aadhaar)?.registrationStep1
                        userRepo.uploadAadhaar(
                            aadhaar = requireNotNull(searchQueuesFor<AadhaarDataComponent>()).aadhaarData.value,
                            fileString = event.aadhaarAsBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: "",/*userRepo.requireUser().phoneNumber*/
                            email = userReg?.email ?: "",/*userRepo.requireUser().email*/
                        ).onError(navigator)
                            .isSuccess
                    }
                    else -> throw UnsupportedOperationException("unsupported event $event for uploadDocument()")
                }
            }
            if (isSuccess) {
                when (it) {
                    is EmployeeScope.Details.Aadhaar -> {
                        /*it.aadhaarFile =
                            (event as Event.Action.Employee.UploadAadhaar).aadhaarAsBase64
                        it.onDataValid(true)*/
                        //startOtp(it.registrationStep1.phoneNumber)
                    }
                    is LimitedAccessScope -> {
                        userRepo.loadUserFromServerV2().onError(navigator)
                    }
                    else -> throw UnsupportedOperationException("unknown UploadDocument common scope")
                }
            }
        }
    }
}