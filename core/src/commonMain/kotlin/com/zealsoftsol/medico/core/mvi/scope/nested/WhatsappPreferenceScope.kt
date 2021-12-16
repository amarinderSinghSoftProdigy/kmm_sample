package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.WhatsappData

sealed class WhatsappPreferenceScope(
    private val titleId: String,
    val language: DataSource<String> = DataSource(""),
    val phoneNumber: DataSource<String> = DataSource(""),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(titleId))
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

    fun submit() = EventCollector.sendEvent(
        Event.Action.WhatsAppPreference.SavePreference(
            this.language.value,
            this.phoneNumber.value
        )
    )

    class GetPreference(body: WhatsappData?) : WhatsappPreferenceScope("whatsapp_preference") {
    }

    class SavePreference : WhatsappPreferenceScope("whatsapp_preference") {


        object PreferenceSavedSuccessfully : ScopeNotification {
            override val dismissEvent: Event = Event.Action.ResetPassword.Finish
            override val isSimple: Boolean = true
            override val isDismissible: Boolean = true
            override val title: String? = "success"
            override val body: String = "data_save_success"
        }
    }
}