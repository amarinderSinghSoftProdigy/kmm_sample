package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode

internal class ProfileEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Profile>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Profile) = when (event) {
        is Event.Action.Profile.UploadUserProfile -> uploadDocument(event)
        is Event.Action.Profile.ShowUploadBottomSheet -> showUploadBottomSheet(event.type)
        is Event.Action.Profile.UploadFileTooBig -> uploadFileTooBig()
        is Event.Action.Profile.GetProfileData -> getProfileData()
    }

    private suspend fun getProfileData() {
        navigator.withScope<SettingsScope> {
            val result = withProgress {
                userRepo.getProfileImageData()
            }
            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                it.profileData.value = data
            }.onError(navigator)
        }
    }

    private suspend fun uploadDocument(event: Event.Action.Profile) {
        navigator.withScope<SettingsScope> {
            val result = withProgress {
                when (event) {
                    is Event.Action.Profile.UploadUserProfile -> {
                        userRepo.uploadProfileImage(
                            size = event.size,
                            fileString = event.asBase64,
                            mimeType = event.fileType.mimeType,
                            type = event.type
                        )
                    }
                    else -> throw UnsupportedOperationException("unsupported event $event for uploadDocument()")
                }

            }
            result.onSuccess { body ->
                when (body.documentType) {
                    "USER_PROFILE_PIC" -> {
                        it.profileData.value =
                            it.profileData.value?.copy(userProfile = body.cdnUrl)
                    }
                    "TRADE_PROFILE" -> {
                        it.profileData.value =
                            it.profileData.value?.copy(tradeProfile = body.cdnUrl)
                    }
                }
            }.onError(navigator)
        }
    }

    private fun showUploadBottomSheet(type: String) {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.UploadProfileData(
                type,
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