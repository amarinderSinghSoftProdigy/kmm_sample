package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource

class WhatsappPreferenceScope(
    val string: String,
    val language: DataSource<String> = DataSource(""),
    val phoneNumber: DataSource<String> = DataSource(""),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    init {
        getCurrentPreference()
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    /**
     * get the current selected language by user
     */
    fun changeLanguage(language: String) {
        this.language.value = language
    }

    /**
     * get the current entered phone number by user
     */
    fun changePhoneNumber(phoneNumber: String) {
        this.phoneNumber.value = phoneNumber
    }

    /**
     * get current whatsapp preference of user
     */
    private fun getCurrentPreference() {
        Event.Action.WhatsAppPreference.GetPreference
    }

    /**
     * submit user preference to server
     */
    fun submit() = EventCollector.sendEvent(
        Event.Action.WhatsAppPreference.SavePreference(
            this.language.value,
            this.phoneNumber.value,
        )
    )

}