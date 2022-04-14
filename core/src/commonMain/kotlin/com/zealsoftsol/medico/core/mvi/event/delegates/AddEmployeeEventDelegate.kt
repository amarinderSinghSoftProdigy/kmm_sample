package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeAddressComponent
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.EmployeeRegistration
import com.zealsoftsol.medico.data.EmployeeRegistration1
import com.zealsoftsol.medico.data.EmployeeRegistration2
import com.zealsoftsol.medico.data.SubmitEmployeeRegistration
import com.zealsoftsol.medico.data.UserType

internal class AddEmployeeEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val employeeRepo: NetworkScope.EmployeeStore
) : EventDelegate<Event.Action.Employee>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Employee) =
        when (event) {
            is Event.Action.Employee.SelectUserType -> moveToPersonalDetailsScreen(event.userType)
            is Event.Action.Employee.Validate -> validate(event.userRegistration)
            is Event.Action.Employee.Aadhaar -> addAadhaar(event.aadhaarData)
            is Event.Action.Employee.ViewEmployee -> viewEmployee()
            is Event.Action.Employee.UpdatePincode -> updatePincode(event.pincode)
            is Event.Action.Employee.DeleteEmployee -> deleteEmployee(event.id)
        }

    private suspend fun deleteEmployee(id: String) {
        navigator.withScope<EmployeeScope.SelectUserType> {
            val result = withProgress {
                employeeRepo.deleteEmployee(id)
            }

            result.onSuccess { _ ->
                it.employeeDeleted()
            }.onError(navigator)
        }
    }

    private suspend fun viewEmployee() {
        navigator.withScope<EmployeeScope.SelectUserType> {
            val result = withProgress {
                employeeRepo.getAllEmployees()
            }

            result.onSuccess { data ->
                it.employeeData.value = data.results
            }.onError(navigator)
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<EmployeeAddressComponent> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val result = withProgress { userRepo.getLocationData(pincode) }
                it.pincodeValidation.value = result.validations
                result.onSuccess { body ->
                    it.locationData.value = body
                    it.registration.value = EmployeeRegistration2(
                        pincode = pincode,
                        district = body.district,
                        state = body.state,
                    )
                }.onError(navigator)
            }
        }
    }

    private fun moveToPersonalDetailsScreen(userType: UserType) {
        navigator.withScope<EmployeeScope.SelectUserType> {
            it.userType.value = userType
            setScope(
                EmployeeScope.PersonalData(
                    registration = DataSource(
                        EmployeeRegistration1(
                            userType = it.userType.value.serverValue,
                        )
                    ),
                    validation = DataSource(null),
                )
            )
        }
    }

    /**
     * send address and personal details of emloyee to server
     */
    private suspend fun validate(userRegistration: EmployeeRegistration) {
        when (userRegistration) {
            is EmployeeRegistration1 -> navigator.withScope<EmployeeScope.PersonalData> {

                val result = withProgress {
                    employeeRepo.submitPersonalDetails(userRegistration)
                }

                result.onSuccess { _ ->
                    setScope(
                        EmployeeScope.AddressData(
                            registrationStep1 = it.registration.value,
                            locationData = DataSource(null),
                            registration = DataSource(EmployeeRegistration2()),
                        )
                    )
                }.onError(navigator)

            }
            is EmployeeRegistration2 -> navigator.withScope<EmployeeScope.AddressData> {
                val result = withProgress {
                    employeeRepo.submitAddressDetails(userRegistration)
                }

                result.onSuccess { _ ->
                    setScope(
                        EmployeeScope.Details.Aadhaar(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registration.value,
                        )
                    )
                }.onError(navigator)
            }

        }
    }

    /**
     * send aadhaar number to server
     */
    private suspend fun addAadhaar(aadhaarData: AadhaarData) {
        navigator.withScope<EmployeeScope.Details.Aadhaar> {
            val result = withProgress {
                employeeRepo.submitAadhaarDetails(aadhaarData.cardNumber)
            }

            result.onSuccess {
                submitFinalData()

            }.onError(navigator)
            it.aadhaarData.value = aadhaarData
        }
    }

    /**
     * send all details of employee to server
     */
    private suspend fun submitFinalData() {
        navigator.withScope<EmployeeScope.Details.Aadhaar> {
            val result = withProgress {
                employeeRepo.submitEmployee(
                    SubmitEmployeeRegistration.employee(
                        aadhaarCardNo = it.aadhaarData.value.cardNumber,
                        userRegistration1 = it.registrationStep1,
                        userRegistration2 = it.registrationStep2
                    )
                )
            }

            result.onSuccess {
                setScope(
                    EmployeeScope.SuccessEmployee()
                )
            }.onError(navigator)
        }
    }
}