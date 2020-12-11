package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.isDocumentUploaded
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import kotlinx.coroutines.delay

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
                        )
                    )
                }
            }
            is UserRegistration2 -> navigator.withScope<SignUpScope.AddressData> {
                val validation = withProgress {
                    userRepo.signUpValidation2(userRegistration)
                }
                it.userValidation.value = validation.entity
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
                    storageKey = storageKey.key,
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
                cached = SignUpScope.LegalDocuments.Aadhaar(
                    it.registrationStep1,
                    it.registrationStep2,
                    it.aadhaarData,
                    it.errors,
                    aadhaarUploaded = true,
                )
                startOtp(it.registrationStep1.phoneNumber)
            } else {
                it.errors.value = ErrorCode()
            }
        }
    }

    private suspend fun signUp() {
        val documents = cached!!
        val signUpSuccess = navigator.withProgress {
            userRepo.signUp(
                documents.registrationStep1,
                documents.registrationStep2,
                documents.registrationStep3,
                (documents as? SignUpScope.LegalDocuments.DrugLicense)?.storageKey
            )
        }
        if (signUpSuccess) {
            val (error, isSuccess) = navigator.withProgress {
                // TODO temporary measure to avoid server race condition
                delay(5000)
                userRepo.login(
                    documents.registrationStep1.email,
                    documents.registrationStep1.password
                )
            }
            if (isSuccess) {
                navigator.setCurrentScope(
                    MainScope.LimitedAccess(isDocumentUploaded = documents.isDocumentUploaded)
                )
                userRepo.getUser()
            } else {
                navigator.dropScopesToRoot()
                navigator.withScope<LogInScope> {
                    it.errors.value = error
                }
            }
        } else {
            navigator.dropScopesToRoot()
            navigator.withScope<LogInScope> {
                it.errors.value = ErrorCode()
            }
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
                val locationResponse = withProgress { userRepo.getLocationData(pincode) }
                val (location, isSuccess) = locationResponse.getWrappedBody()
                if (isSuccess) {
                    it.locationData.value = location
                    if (location != null) {
                        it.registration.value = it.registration.value.copy(
                            district = location.district,
                            state = location.state,
                        )
                    }
                } else {
                    it.pincodeValidation.value = locationResponse.validations
                }
            }
        }
    }

    private inline fun startOtp(phoneNumber: String) =
        EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
}