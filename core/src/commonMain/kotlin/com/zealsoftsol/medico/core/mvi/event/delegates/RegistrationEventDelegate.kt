package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.AddressComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WelcomeScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ErrorCode
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
        is Event.Action.Registration.UploadDrugLicense -> uploadDocument(event)
        is Event.Action.Registration.UploadFileTooBig -> uploadFileTooBig()
        is Event.Action.Registration.AddAadhaar -> addAadhaar(event.aadhaarData)
        is Event.Action.Registration.UploadAadhaar -> uploadDocument(event)
        is Event.Action.Registration.SignUp -> signUp()
        is Event.Action.Registration.Skip -> skipUploadDocuments()
        is Event.Action.Registration.AcceptWelcome -> acceptWelcome()
        is Event.Action.Registration.ShowUploadBottomSheet -> showUploadBottomSheet()
        is Event.Action.Registration.ConfirmCreateRetailer -> confirmCreateRetailer()
    }

    private fun selectUserType(userType: UserType) {
        navigator.withScope<SignUpScope.SelectUserType> {
            it.userType.value = userType
            setScope(
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
                    setScope(
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
                    setScope(nextScope)
                }
            }
            is UserRegistration3 -> navigator.withScope<SignUpScope.Details.TraderData> {
                val validation = withProgress {
                    userRepo.signUpValidation3(userRegistration)
                }
                it.validation.value = validation.entity
                if (validation.isSuccess) {
                    setScope(
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
            setScope(
                SignUpScope.LegalDocuments.Aadhaar(
                    registrationStep1 = it.registrationStep1,
                    registrationStep2 = it.registrationStep2,
                    aadhaarData = aadhaarData,
                )
            )
        }
    }

    private fun uploadFileTooBig() {
        navigator.withScope<CommonScope.UploadDocument> {
            setHostError(ErrorCode.uploadFileTooBig)
        }
    }

    private suspend fun uploadDocument(event: Event.Action.Registration) {
        navigator.withScope<CommonScope.UploadDocument> {
            var storageKey: String? = null
            val isSuccess = withProgress {
                when (event) {
                    is Event.Action.Registration.UploadDrugLicense -> {
                        val userReg =
                            (it as? SignUpScope.LegalDocuments.DrugLicense)?.registrationStep1
                        val response = userRepo.uploadDrugLicense(
                            fileString = event.licenseAsBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: userRepo.requireUser().phoneNumber,
                            email = userReg?.email ?: userRepo.requireUser().email,
                            mimeType = event.fileType.mimeType,
                        )
                        storageKey = response.entity?.key
                        response.isSuccess
                    }
                    is Event.Action.Registration.UploadAadhaar -> {
                        val userReg = (it as? SignUpScope.LegalDocuments.Aadhaar)?.registrationStep1
                        userRepo.uploadAadhaar(
                            aadhaar = requireNotNull(searchQueuesFor<AadhaarDataComponent>()).aadhaarData.value,
                            fileString = event.aadhaarAsBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: userRepo.requireUser().phoneNumber,
                            email = userReg?.email ?: userRepo.requireUser().email,
                        )
                    }
                    else -> throw UnsupportedOperationException("unsupported event $event for uploadDocument()")
                }

            }
            if (isSuccess) {
                when (it) {
                    is SignUpScope.LegalDocuments -> {
                        when (it) {
                            is SignUpScope.LegalDocuments.DrugLicense -> {
                                it.storageKey = storageKey
                            }
                            is SignUpScope.LegalDocuments.Aadhaar -> {
                                it.aadhaarFile =
                                    (event as Event.Action.Registration.UploadAadhaar).aadhaarAsBase64
                            }
                        }
                        startOtp(it.registrationStep1.phoneNumber)
                    }
                    is LimitedAccessScope -> {
                        if (!userRepo.loadUserFromServer()) {
                            setHostError(ErrorCode())
                        }
                    }
                    else -> throw UnsupportedOperationException("unknown UploadDocument common scope")
                }
            } else {
                setHostError(ErrorCode())
            }
        }
    }

    private suspend fun signUp() {
        val documents = navigator.searchQueuesFor<SignUpScope.LegalDocuments>()
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
            userRepo.sendFirebaseToken()
            navigator.dropScope(Navigator.DropStrategy.ToRoot, updateDataSource = false)
            navigator.setScope(
                WelcomeScope(documents!!.registrationStep1.run { "$firstName $lastName" })
            )
        } else {
            dropToLogin(error)
        }
    }

    private suspend fun confirmCreateRetailer() {
        navigator.withScope<ManagementScope.AddRetailer.Address> {
            val (error, isSuccess) = withProgress {
                userRepo.createRetailer(it.registration.value, it.registration3)
            }
            if (isSuccess) {
                dropScope(
                    Navigator.DropStrategy.To(ManagementScope.User.Retailer::class),
                    updateDataSource = false,
                )
                it.notifications.value = ManagementScope.Congratulations(it.registration3.tradeName)
            } else {
                setHostError(error ?: ErrorCode())
            }
        }
    }

    private fun acceptWelcome() {
        dropToLogin(null)
    }

    private fun showUploadBottomSheet() {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.UploadDocuments(
                it.supportedFileTypes,
                it.isSeasonBoy,
            )
        }
    }

    private fun dropToLogin(errorCode: ErrorCode?) {
        navigator.dropScope(Navigator.DropStrategy.ToRoot)
        navigator.setHostError(errorCode)
    }

    private fun skipUploadDocuments() {
        navigator.withScope<SignUpScope.LegalDocuments> {
            startOtp(it.registrationStep1.phoneNumber)
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<AddressComponent> {
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