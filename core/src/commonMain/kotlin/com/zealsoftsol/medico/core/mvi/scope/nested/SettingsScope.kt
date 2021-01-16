package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.CustomerAddressData
import com.zealsoftsol.medico.data.User

sealed class SettingsScope(
    icon: ScopeIcon,
    titleId: String,
) : Scope.Child.TabBar(TabBarInfo.Simple(icon, titleId)),
    CommonScope.CanGoBack {

    class List(
        val sections: kotlin.collections.List<Section>
    ) : SettingsScope(ScopeIcon.HAMBURGER, "settings") {

        enum class Section(
            private val event: Event,
            val stringId: String,
        ) {
            PROFILE(Event.Transition.Profile, "personal_profile"),
            CHANGE_PASSWORD(Event.Action.ResetPassword.RequestChange, "change_password"),
            ADDRESS(Event.Transition.Address, "address"),
            GSTIN_DETAILS(Event.Transition.GstinDetails, "gstin_details");

            fun select() = EventCollector.sendEvent(event)

            internal companion object {
                fun all() = listOf(PROFILE, CHANGE_PASSWORD, ADDRESS, GSTIN_DETAILS)
                fun simple() = listOf(PROFILE, CHANGE_PASSWORD)
            }
        }
    }

    class Profile(val user: User) : SettingsScope(ScopeIcon.BACK, "personal_profile")

    class Address(val addressData: CustomerAddressData) : SettingsScope(ScopeIcon.BACK, "address") {

        fun openMap(): Boolean = TODO("open map")
    }

    class GstinDetails(val details: User.Details.DrugLicense) :
        SettingsScope(ScopeIcon.BACK, "gstin_details")
}