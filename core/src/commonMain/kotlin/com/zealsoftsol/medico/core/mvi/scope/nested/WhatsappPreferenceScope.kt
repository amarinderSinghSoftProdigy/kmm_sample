package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.WhatsappLanguagesItem

class WhatsappPreferenceScope(
    val string: String,
    val showAlert: DataSource<Boolean> = DataSource(false),
    val language: DataSource<LanguageItem> = DataSource(LanguageItem("", "")),
    val phoneNumber: DataSource<String> = DataSource(""),
    val availableLanguages: DataSource<List<WhatsappLanguagesItem>> = DataSource(emptyList())
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    /**
     * update data with values received from server
     */
    fun updateDataFromServer(
        languageCode: String,
        phoneNumber: String,
        availableLanguages: List<WhatsappLanguagesItem>?
    ) {
        val selectedLanguage: WhatsappLanguagesItem? =
            availableLanguages!!.find { it.code == languageCode }

        this.language.value = LanguageItem(selectedLanguage!!.name, languageCode)
        this.phoneNumber.value = phoneNumber
        this.availableLanguages.value = availableLanguages
    }

    init {
        EventCollector.sendEvent(getCurrentPreference())
    }

    //to hold selected language by user and its code
    data class LanguageItem(val language: String, val languageCode: String)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    /**
     * update the scope of alert dialog
     */
    fun changeAlertScope(enable: Boolean) {
        this.showAlert.value = enable
    }

    /**
     * get the current selected language by user
     */
    fun changeLanguage(language: String, languageCode: String) {
        this.language.value = LanguageItem(language, languageCode)
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
    private fun getCurrentPreference() =
        Event.Action.WhatsAppPreference.GetPreference

    /**
     * submit user preference to server
     */
    fun submit() = EventCollector.sendEvent(
        Event.Action.WhatsAppPreference.SavePreference(
            this.language.value.languageCode,
            this.phoneNumber.value,
        )
    )

}