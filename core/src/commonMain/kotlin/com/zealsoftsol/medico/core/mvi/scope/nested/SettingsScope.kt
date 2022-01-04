package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User

sealed class SettingsScope(private val titleId: String, val mUser: User) : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(titleId))
    }

    /**
     * Handle events
     */
    fun sendEvent(action: Event.Action? = null, transition: Event.Transition? = null) {

        if (action != null) {
            EventCollector.sendEvent(action)
        } else if (transition != null) {
            EventCollector.sendEvent(transition)
        }
    }

    class List(
        val sections: kotlin.collections.List<Section>,
        val user: User
    ) : SettingsScope("settings", user) {

        enum class Section(
            private val event: Event,
            val stringId: String,
        ) {
            PROFILE(Event.Transition.Profile, "personal_profile"),
            CHANGE_PASSWORD(Event.Transition.ChangePassword, "change_password"),
            ADDRESS(Event.Transition.Address, "address"),
            GSTIN_DETAILS(Event.Transition.GstinDetails, "gstin_details");

            fun select() = EventCollector.sendEvent(event)

            internal companion object {
                fun all(canChangePassword: Boolean) =
                    listOfNotNull(
                        PROFILE,
                        CHANGE_PASSWORD.takeIf { canChangePassword },
                        ADDRESS,
                        GSTIN_DETAILS,
                    )

                fun simple(canChangePassword: Boolean) =
                    listOfNotNull(
                        PROFILE,
                        CHANGE_PASSWORD.takeIf { canChangePassword },
                    )
            }
        }
    }

    class Profile(val user: User) : SettingsScope("personal_profile", user)

    class Address(val addressData: AddressData, val user: User) : SettingsScope("address", user) {

        fun openMap(): Boolean = TODO("open map")
    }

    class GstinDetails(val details: User.Details.DrugLicense, val user: User) :
        SettingsScope("gstin_details", user)

}