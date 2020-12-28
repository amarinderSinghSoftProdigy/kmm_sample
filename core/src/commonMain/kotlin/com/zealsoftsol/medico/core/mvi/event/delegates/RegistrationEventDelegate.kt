package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataHolder
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

internal class RegistrationEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Registration>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Registration) = when (event) {
        is Event.Action.Registration.SelectUserType -> selectUserType(event.userType)
        is Event.Action.Registration.Validate -> validate(event.userRegistration)
        is Event.Action.Registration.UpdatePincode -> updatePincode(event.pincode)
        is Event.Action.Registration.UploadDrugLicense -> uploadDrugLicense(
            event.phoneNumber,
            event.email,
            event.licenseAsBase64,
            event.fileType,
        )
        is Event.Action.Registration.AddAadhaar -> addAadhaar(event.aadhaarData)
        is Event.Action.Registration.UploadAadhaar -> uploadAadhaar(
            event.phoneNumber,
            event.email,
            event.aadhaarAsBase64,
        )
        is Event.Action.Registration.SignUp -> signUp()
        is Event.Action.Registration.Skip -> skipUploadDocuments()
        is Event.Action.Registration.AcceptWelcome -> acceptWelcome()
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
                            SignUpScope.Details.Aadhaar(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                            )
                        } else {
                            SignUpScope.Details.TraderData(
                                registrationStep1 = it.registrationStep1,
                                registrationStep2 = it.registration.value,
                            )
                        }
                    setCurrentScope(nextScope)
                }
            }
            is UserRegistration3 -> navigator.withScope<SignUpScope.Details.TraderData> {
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

    private fun addAadhaar(aadhaarData: AadhaarData) {
        navigator.withScope<SignUpScope.Details.Aadhaar> {
            it.aadhaarData.value = aadhaarData
            setCurrentScope(
                SignUpScope.LegalDocuments.Aadhaar(
                    registrationStep1 = it.registrationStep1,
                    registrationStep2 = it.registrationStep2,
                    aadhaarData = aadhaarData,
                )
            )
        }
    }

    private suspend fun uploadDrugLicense(
        phoneNumber: String,
        email: String,
        license: String,
        fileType: FileType,
    ) {
        navigator.withCommonScope<CommonScope.UploadDocument> {
            val (storageKey, isSuccess) = withProgress {
                userRepo.uploadDrugLicense(
                    fileString = license,
                    phoneNumber = phoneNumber,
                    email = email,
                    mimeType = fileType.mimeType,
                )
            }
            if (isSuccess) {
                when (it) {
                    is SignUpScope.LegalDocuments.DrugLicense -> {
                        it.storageKey = storageKey?.key
                        startOtp(it.registrationStep1.phoneNumber)
                    }
                    is MainScope.LimitedAccess -> {
                        userRepo.loadUserFromServer()?.let { user ->
                            it.user.value = user
                        } ?: run {
                            it.errors.value = ErrorCode()
                        }
                    }
                    else -> throw UnsupportedOperationException("unknown UploadDocument common scope")
                }
            } else {
                it.errors.value = ErrorCode()
            }
        }
    }

    private suspend fun uploadAadhaar(
        phoneNumber: String,
        email: String,
        aadhaar: String,
    ) {
        navigator.withCommonScope<CommonScope.UploadDocument> {
            val aadhaarData = requireNotNull(searchQueueFor<AadhaarDataHolder>()).aadhaarData.value
            val isSuccess = withProgress {
                userRepo.uploadAadhaar(
                    aadhaar = aadhaarData,
                    fileString = aadhaar,
                    phoneNumber = phoneNumber,
                    email = email,
                )
            }
            if (isSuccess) {
                when (it) {
                    is SignUpScope.LegalDocuments.Aadhaar -> {
                        it.aadhaarFile = aadhaar
                        startOtp(it.registrationStep1.phoneNumber)
                    }
                    is MainScope.LimitedAccess -> {
                        userRepo.loadUserFromServer()?.let { user ->
                            it.user.value = user
                        } ?: run {
                            it.errors.value = ErrorCode()
                        }
                    }
                    else -> throw UnsupportedOperationException("unknown AadhaarDataHolder common scope")
                }
            } else {
                it.errors.value = ErrorCode()
            }
        }
    }

    private suspend fun signUp() {
        val documents = navigator.searchQueueFor<SignUpScope.LegalDocuments>()
        val (error, isSuccess) = navigator.withProgress {
            when (documents) {
                is SignUpScope.LegalDocuments.DrugLicense -> userRepo.signUpNonSeasonBoy(
                    documents.registrationStep1,
                    documents.registrationStep2,
                    documents.registrationStep3,
                    documents.storageKey,
                )
                is SignUpScope.LegalDocuments.Aadhaar -> userRepo.signUpSeasonBoy(
                    documents.registrationStep1,
                    documents.registrationStep2,
                    documents.aadhaarData,
                    documents.aadhaarFile,
                )
                else -> Response.Wrapped(ErrorCode(), false)
            }
        }
        if (isSuccess) {
            navigator.clearQueue(withRoot = false)
            navigator.setCurrentScope(
                SignUpScope.Welcome(documents!!.registrationStep1.run { "$firstName $lastName" })
            )
        } else {
            dropToLogin(error)
        }
    }

    private fun acceptWelcome() {
        dropToLogin(null)
    }

    private fun dropToLogin(errorCode: ErrorCode?) {
        navigator.dropScopesToRoot()
        navigator.withScope<LogInScope> {
            it.errors.value = errorCode
        }
    }

    private fun skipUploadDocuments() {
        navigator.withScope<SignUpScope.LegalDocuments> {
            startOtp(it.registrationStep1.phoneNumber)
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<SignUpScope.AddressData> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val locationResponse = withProgress { userRepo.getLocationData(pincode) }
                it.pincodeValidation.value = locationResponse.validations
                val (location, isSuccess) = locationResponse.getWrappedBody()
                if (isSuccess) {
                    it.locationData.value = location
                    if (location != null) {
                        it.registration.value = it.registration.value.copy(
                            district = location.district,
                            state = location.state,
                        )
                    }
                }
            }
        }
    }

    private inline fun startOtp(phoneNumber: String) =
        EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
}