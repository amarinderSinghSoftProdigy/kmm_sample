package com.zealsoftsol.medico.core.mvi.scope.regular

import android.content.SharedPreferences
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope

class PreferenceScope : Scope.Child.TabBar(), CommonScope.CanGoBack, CommonScope.AlertScope {

    init {
        EventCollector.sendEvent(Event.Action.Preferences.GetPreferences)
    }

    override var isOrderAlert: DataSource<Boolean> = DataSource(true)
    val showAlert = DataSource(false)
    val showAlertText = DataSource("")
    val isAutoApproved = DataSource(false)

    /**
     * show success or error message
     */
    fun showAlertBottomSheet(value: Boolean) {
        showAlert.value = value
    }

    init {
        getAlertToggle()
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

    private fun getAlertToggle() = EventCollector.sendEvent(Event.Action.Cart.GetAlertToggle)

    fun updateOrderAlert(value: Boolean) {
        isOrderAlert.value = value
    }

    fun submitOrderAlert() {
        EventCollector.sendEvent(Event.Action.Preferences.SaveAlertToggle(isOrderAlert.value))
    }

}
