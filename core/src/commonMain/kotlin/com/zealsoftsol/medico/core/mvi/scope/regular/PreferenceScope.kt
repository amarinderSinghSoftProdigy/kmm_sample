package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope

class PreferenceScope : Scope.Child.TabBar(), CommonScope.CanGoBack {

    init {
        EventCollector.sendEvent(Event.Action.Preferences.GetPreferences)
    }

    val showAlert = DataSource(false)
    val showAlertText = DataSource("")
    val isAutoApproved = DataSource(false)

    /**
     * show success or error message
     */
    fun showAlertBottomSheet(value: Boolean) {
        showAlert.value = value
    }

    /**
     * change preference value
     */
    fun updateAutoApprovePreference(value: Boolean) {
        isAutoApproved.value = value
    }

    /**
     * submit final preferences
     */
    fun submitPreference() {
        EventCollector.sendEvent(Event.Action.Preferences.SetAutoConnectPreferences(isAutoApproved.value))
    }

}
