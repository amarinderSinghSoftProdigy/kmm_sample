package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.PreferenceScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class PreferencesEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val preferenceScope: NetworkScope.PreferencesStore,

    ) : EventDelegate<Event.Action.Preferences>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Preferences) = when (event) {
        is Event.Action.Preferences.GetPreferences -> getPreferences()
        is Event.Action.Preferences.SetAutoConnectPreferences -> setPreferences(event.isEnabled)
        is Event.Action.Preferences.SaveAlertToggle -> saveAlertToggle(event.isEnabled)
    }

    private suspend fun getPreferences() {
        navigator.withScope<PreferenceScope> {
            withProgress {
                preferenceScope.getAutoApprovePreference()
                    .onSuccess { body ->
                        it.isAutoApproved.value = body.autoApprove.value
                        it.showAlertText.value = body.autoApprove.formatted
                    }.onError(navigator)
            }
        }
    }

    private suspend fun saveAlertToggle(value: Boolean) {
        navigator.withScope<PreferenceScope> {
            userRepo.saveAlertToggle(value)
        }
    }

    private suspend fun setPreferences(value: Boolean) {
        navigator.withScope<PreferenceScope> {
            withProgress {
                preferenceScope.setAutoApprovePreference(value)
            }.onSuccess { body ->
                it.showAlertText.value = body.autoApprove.formatted
                it.showAlert.value = true
            }.onError(navigator)
        }
    }
}