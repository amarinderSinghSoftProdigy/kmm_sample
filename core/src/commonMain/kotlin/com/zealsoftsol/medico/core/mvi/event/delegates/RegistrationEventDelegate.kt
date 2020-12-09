package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

internal class RegistrationEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Registration>(navigator) {

    private var cached: SignUpScope.LegalDocuments? = null

    override suspend fun handleEvent(event: Event.Action.Registration) = when (event) {
        is Event.Action.Registration.SelectUserType -> selectUserType(event.userType)
        is Event.Action.Registration.Validate -> validate(event.userRegistration)
        is Event.Action.Registration.UpdatePincode -> updatePincode(event.pincode)
        is Event.Action.Registration.UploadDrugLicense -> uploadDrugLicense(
            event.license,
            event.fileType
        )
        is Event.Action.Registration.UploadAadhaar -> uploadAadhaar(event.aadhaar)
        is Event.Action.Registration.SignUp -> signUp()
        is Event.Action.Registration.Skip -> skipUploadDocuments()
    }

    private fun selectUserType(userType: UserType) {
        navigator.withScope<SignUpScope.SelectUserType> {
            it.userType.value = userType
            setCurrentScope(
                SignUpScope.PersonalData(
                    registration = DataSource(
                        UserRegistration1(
                            userType = userType.serverValue,
                        )
                    ),
                    validation = DataSource(null),
                )
            )
        }
    }

    private suspend fun validate(userRegistration: UserRegistration) {
        when (userRegistration) {
            is UserRegistration1 -> navigator.withScope<SignUpScope.PersonalData> {
                val validation = withProgress {
                    userRepo.signUpValidation1(userRegistration)
                }
                it.validation.value = validation.entity
                if (validation.isSuccess) {
                    setCurrentScope(
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
                    userRepo.signUpValidation2(userRegistration)
                }
                it.validation.value = validation.entity
                if (validation.isSuccess) {
                    val nextScope =
                        if (it.registrationStep1.userType == UserType.SEASON_BOY.serverValue) {
                            SignUpScope.LegalDocuments.Aadhaar(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                            )
                        } else {
                            SignUpScope.TraderData(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                                registration = DataSource(UserRegistration3()),
                                validation = DataSource(null)
                            )
                        }
                    setCurrentScope(nextScope)
                }
            }
            is UserRegistration3 -> navigator.withScope<SignUpScope.TraderData> {
                val validation = withProgress {
                    userRepo.signUpValidation3(userRegistration)
                }
                it.validation.value = validation.entity
                if (validation.isSuccess) {
                    setCurrentScope(
                        SignUpScope.LegalDocuments.DrugLicense(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registrationStep2,
                            registrationStep3 = it.registration.value,
                        )
                    )
                }
            }
        }
    }

    private suspend fun uploadDrugLicense(license: String, fileType: FileType) {
        navigator.withScope<SignUpScope.LegalDocuments.DrugLicense> {
            val (storageKey, isSuccess) = withProgress {
                userRepo.uploadDrugLicense(
                    fileString = license,
                    phoneNumber = it.registrationStep1.phoneNumber,
                    mimeType = fileType.mimeType,
                )
            }
            if (isSuccess) {
                cached = SignUpScope.LegalDocuments.DrugLicense(
                    it.registrationStep1,
                    it.registrationStep2,
                    it.registrationStep3,
                    it.errors,
                    storageKey = storageKey?.key,
                )
                startOtp(it.registrationStep1.phoneNumber)
            } else {
                it.errors.value = ErrorCode()
            }
        }
    }

    private suspend fun uploadAadhaar(aadhaar: String) {
        navigator.withScope<SignUpScope.LegalDocuments.Aadhaar> {
            val isSuccess = withProgress {
                userRepo.uploadAadhaar(
                    aadhaar = it.aadhaarData.value,
                    fileString = aadhaar,
                    email = it.registrationStep1.email,
                    phoneNumber = it.registrationStep1.phoneNumber,
                )
            }
            if (isSuccess) {
                cached = it
                startOtp(it.registrationStep1.phoneNumber)
            } else {
                it.errors.value = ErrorCode()
            }
        }
    }

    private suspend fun signUp() {
        val signUpSuccess = navigator.withProgress {
            val documents = cached!!
            userRepo.signUp(
                documents.registrationStep1,
                documents.registrationStep2,
                documents.registrationStep3,
                (documents as? SignUpScope.LegalDocuments.DrugLicense)?.storageKey
            )
        }
        if (signUpSuccess) {
            navigator.setCurrentScope(
                MainScope(
                    isLimitedAppAccess = true,
                )
            )
        } else {
            // TODO show alert
            navigator.dropScopesToRoot()
        }
    }

    private fun skipUploadDocuments() {
        navigator.withScope<SignUpScope.LegalDocuments> {
            cached = it
            startOtp(it.registrationStep1.phoneNumber)
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<SignUpScope.AddressData> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val locationData = withProgress { userRepo.getLocationData(pincode) }
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

    private inline fun startOtp(phoneNumber: String) =
        EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
}