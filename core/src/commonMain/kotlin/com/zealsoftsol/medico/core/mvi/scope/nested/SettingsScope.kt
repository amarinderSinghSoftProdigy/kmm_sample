package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User

sealed class SettingsScope(private val titleId: String) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(titleId))
    }

    class List(
        val sections: kotlin.collections.List<Section>
    ) : SettingsScope("settings") {

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

    class Profile(val user: User) : SettingsScope("personal_profile")

    class Address(val addressData: AddressData) : SettingsScope("address") {

        fun openMap(): Boolean = TODO("open map")
    }

    class GstinDetails(val details: User.Details.DrugLicense) : SettingsScope("gstin_details")
}