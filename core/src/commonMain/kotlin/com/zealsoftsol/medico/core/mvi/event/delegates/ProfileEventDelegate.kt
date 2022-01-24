package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.WhatsappPreferenceScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.ErrorCode

internal class ProfileEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Profile>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Profile) = when (event) {
        is Event.Action.Profile.UploadUserProfile -> uploadDocument(event)
        is Event.Action.Profile.ShowUploadBottomSheet -> showUploadBottomSheet()
        is Event.Action.Profile.UploadFileTooBig -> uploadFileTooBig()
    }

    private suspend fun uploadDocument(event: Event.Action.Profile) {
        navigator.withScope<CommonScope.UploadDocument> {
            var storageKey: String? = null
            val isSuccess = withProgress {
                when (event) {
                    is Event.Action.Profile.UploadUserProfile -> {
                        val userReg =
                            (it as? SignUpScope.LegalDocuments.DrugLicense)?.registrationStep1
                        userRepo.uploadDrugLicense(
                            fileString = event.asBase64,
                            phoneNumber = userReg?.phoneNumber
                                ?: userRepo.requireUser().phoneNumber,
                            email = userReg?.email ?: userRepo.requireUser().email,
                            mimeType = event.fileType.mimeType,
                        ).onSuccess { body ->
                            storageKey = body.key
                        }.onError(navigator)
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

    private fun showUploadBottomSheet() {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.UploadDocuments(
                it.supportedFileTypes,
                it.isSeasonBoy,
            )
        }
    }

    private fun uploadFileTooBig() {
        navigator.withScope<CommonScope.UploadDocument> {
            setHostError(ErrorCode.uploadFileTooBig)
        }
    }
}