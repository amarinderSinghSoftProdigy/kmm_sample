package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.WhatsappPreferenceScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo

internal class WhatsappEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.WhatsAppPreference>(navigator) {

    override suspend fun handleEvent(event: Event.Action.WhatsAppPreference) = when (event) {
        is Event.Action.WhatsAppPreference.GetPreference -> getWhatsappPreference()
        is Event.Action.WhatsAppPreference.SavePreference -> saveWhatsappPreference(
            event.language,
            event.phoneNumber
        )
    }

    private suspend fun getWhatsappPreference() {
        navigator.withScope<WhatsappPreferenceScope.GetPreference> {
            val result = withProgress {
                userRepo.getWhatsappPreference()
            }

            result.onSuccess {
                setScope(WhatsappPreferenceScope.GetPreference(result.getBodyOrNull()?.body))
            }.onError(navigator)
        }
    }

    private suspend fun saveWhatsappPreference(language: String, phoneNumber: String) {
        navigator.withScope<WhatsappPreferenceScope.SavePreference> {
            val result = withProgress {
                userRepo.saveWhatsappPreference(language, phoneNumber)
            }

            result.onSuccess { _ ->
//                it.language.value = WhatsappPreferenceScope.SavePreference.PreferenceSavedSuccessfully
            }.onError(navigator)
        }
    }
}