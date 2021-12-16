package com.zealsoftsol.medico.core.mvi.scope.nested

import android.util.Log
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource

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

    abstract fun submit(): Boolean

    class GetCurrent : WhatsappPreferenceScope("change_password") {

        override fun submit() = false
    }
}