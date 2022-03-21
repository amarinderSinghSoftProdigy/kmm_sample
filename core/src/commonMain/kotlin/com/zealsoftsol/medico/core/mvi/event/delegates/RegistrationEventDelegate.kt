package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
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
import com.zealsoftsol.medico.data.LicenseDocumentData
import com.zealsoftsol.medico.data.ProfileImageUpload
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRegistration4
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
        is Event.Action.Registration.UploadDocument -> uploadDocuments(
            event,
            event.type,
            event.path
        )
        is Event.Action.Registration.UploadFileTooBig -> uploadFileTooBig()
        is Event.Action.Registration.AddAadhaar -> addAadhaar(event.aadhaarData)
        is Event.Action.Registration.UploadAadhaar -> uploadDocument(event)
        is Event.Action.Registration.SignUp -> signUp()
        is Event.Action.Registration.Skip -> skipUploadDocuments()
        is Event.Action.Registration.Submit -> submit()
        is Event.Action.Registration.AcceptWelcome -> acceptWelcome()
        is Event.Action.Registration.ShowUploadBottomSheet -> showUploadBottomSheet()
        is Event.Action.Registration.ShowUploadBottomSheets -> showUploadBottomSheets(
            event.type,
            event.registrationStep1
        )
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
                val result = withProgress {
                    userRepo.signUpValidation1(userRegistration)
                }
                it.validation.value = result.validations
                result.onSuccess { _ ->
                    setScope(
                        SignUpScope.AddressData(
                            registrationStep1 = it.registration.value,
                            locationData = DataSource(null),
                            registration = DataSource(UserRegistration2()),
                        )
                    )
                }.onError(navigator)
            }
            is UserRegistration2 -> navigator.withScope<SignUpScope.AddressData> {
                val result = withProgress {
                    userRepo.signUpValidation2(userRegistration)
                }
                it.userValidation.value = result.validations
                result.onSuccess { _ ->
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
                }.onError(navigator)
            }
            is UserRegistration3 -> navigator.withScope<SignUpScope.Details.TraderData> {
                val result = withProgress {
                    userRepo.signUpValidation3(userRegistration)
                }
                it.validation.value = result.validations
                result.onSuccess { _ ->
                    setScope(
                        SignUpScope.LegalDocuments.DrugLicense(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registrationStep2,
                            registrationStep3 = it.registration.value,
                        )
                    )
                }.onError(navigator)
            }
            is UserRegistration4 -> {
                navigator.withScope<SignUpScope.LegalDocuments.DrugLicense> {
                    setScope(
                        SignUpScope.PreviewDetails(
                            registrationStep1 = it.registrationStep1,
                            registrationStep2 = it.registrationStep2,
                            registrationStep3 = it.registrationStep3,
                            registrationStep4 = it.registrationStep4.value,
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

    private suspend fun uploadDocuments(
        event: Event.Action.Registration,
        type: String,
        path: String
    ) {
        navigator.withScope<CommonScope.UploadDocument> {
            val result = withProgress {
                when (event) {
                    is Event.Action.Registration.UploadDocument -> {
                        userRepo.upoladDocument(
                            LicenseDocumentData(
                                event.registrationStep1.email,
                                event.registrationStep1.phoneNumber,
                                ProfileImageUpload(
                                    documentType = type,
                                    size = event.size,
                                    documentData = event.asBase64,
                                    mimeType = event.fileType.mimeType,
                                    name = event.type
                                )
                            )
                        )
                    }
                    else -> throw UnsupportedOperationException("unsupported event $event for uploadDocument()")
                }

            }
            result.onSuccess { body ->
                when (it) {
                    is SignUpScope.LegalDocuments.DrugLicense -> {
                        when (type) {
                            "TRADE_PROFILE" -> {
                                it.tradeProfile.value = body.copy(cdnUrl = path)
                                it.checkData()
                            }
                            "DRUG_LICENSE" -> {
                                it.drugLicense.value = body.copy(cdnUrl = path)
                                it.checkData()
                            }
                            "FOOD_LICENSE" -> {
                                it.foodLicense.value = body.copy(cdnUrl = path)
                                it.checkData()
                            }
                        }
                        //startOtp(it.registrationStep1.phoneNumber)
                    }
                    else -> throw UnsupportedOperationException("unknown UploadDocument common scope")
                }
            }.onError(navigator)
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
                        userRepo.uploadDrugLicense(
                            fileString = event.licenseAsBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: ""/*userRepo.requireUser().phoneNumber*/,
                            email = userReg?.email ?: ""/*userRepo.requireUser().email*/,
                            mimeType = event.fileType.mimeType,
                        ).onSuccess { body ->
                            storageKey = body.key
                        }.onError(navigator)
                            .isSuccess
                    }
                    is Event.Action.Registration.UploadAadhaar -> {
                        val userReg = (it as? SignUpScope.LegalDocuments.Aadhaar)?.registrationStep1
                        userRepo.uploadAadhaar(
                            aadhaar = requireNotNull(searchQueuesFor<AadhaarDataComponent>()).aadhaarData.value,
                            fileString = event.aadhaarAsBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: ""/*userRepo.requireUser().phoneNumber*/,
                            email = userReg?.email ?: ""/*userRepo.requireUser().email*/,
                        ).onError(navigator)
                            .isSuccess
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
                                it.onDataValid(true)
                            }
                        }
                        //startOtp(it.registrationStep1.phoneNumber)
                    }
                    is LimitedAccessScope -> {
                        userRepo.loadUserFromServer().onError(navigator)
                    }
                    else -> throw UnsupportedOperationException("unknown UploadDocument common scope")
                }
            }
        }
    }

    private suspend fun signUp() {
        val documents = navigator.searchQueuesFor<SignUpScope.LegalDocuments>()
        navigator.withProgress {
            when (documents) {
                is SignUpScope.LegalDocuments.DrugLicense -> userRepo.signUpNonSeasonBoy(
                    documents.registrationStep1,
                    documents.registrationStep2,
                    documents.registrationStep3,
                    documents.registrationStep4.value,
                )
                is SignUpScope.LegalDocuments.Aadhaar -> userRepo.signUpSeasonBoy(
                    documents.registrationStep1,
                    documents.registrationStep2,
                    documents.aadhaarData,
                    documents.aadhaarFile,
                )
                null -> return
            }
        }.onSuccess {
            userRepo.sendFirebaseToken()
            navigator.dropScope(Navigator.DropStrategy.ToRoot, updateDataSource = false)
            navigator.setScope(
                WelcomeScope(documents!!.registrationStep1.run { "$firstName $lastName" })
            )
        }.onError { dropToLogin(it) }
    }

    private suspend fun confirmCreateRetailer() {
        navigator.withScope<ManagementScope.AddRetailer.Address> {
            withProgress {
                userRepo.createRetailer(it.registration.value, it.registration3)
            }.onSuccess { _ ->
                dropScope(
                    Navigator.DropStrategy.To(ManagementScope.User.Retailer::class),
                    updateDataSource = false,
                )
                it.notifications.value = ManagementScope.Congratulations(it.registration3.tradeName)
            }.onError(navigator)
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
                "", UserRegistration1()
            )
        }
    }

    private fun showUploadBottomSheets(type: String, registrationStep1: UserRegistration1) {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.UploadDocuments(
                it.supportedFileTypes,
                it.isSeasonBoy,
                type,
                registrationStep1
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

    private fun submit() {
        navigator.withScope<SignUpScope.PreviewDetails> {
            startOtp(it.registrationStep1.phoneNumber)
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<AddressComponent> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val result = withProgress { userRepo.getLocationData(pincode) }
                it.pincodeValidation.value = result.validations
                result.onSuccess { body ->
                    it.locationData.value = body
                    it.registration.value = UserRegistration2(
                        pincode = pincode,
                        district = body.district,
                        state = body.state,
                    )
                }.onError(navigator)
            }
        }
    }

    private inline fun startOtp(phoneNumber: String) =
        EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
}