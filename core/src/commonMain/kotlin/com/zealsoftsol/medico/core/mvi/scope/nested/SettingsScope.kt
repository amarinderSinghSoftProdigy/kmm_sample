package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.ProfileImageData
import com.zealsoftsol.medico.data.User

sealed class SettingsScope(
    private val titleId: String,
    val mUser: User,
    val unreadNotifications: ReadOnlyDataSource<Int>,
    private val showBackIcon: Boolean,
    val profileData: DataSource<ProfileImageData?> = DataSource(null)
) : Scope.Child.TabBar(), CommonScope.UploadDocument {

    init {
        sendEvent(action = Event.Action.Profile.GetProfileData)
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        if (showBackIcon)
            (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
        else
            TabBarInfo.NoIconTitle("", unreadNotifications)

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
        val unReadNotifications: ReadOnlyDataSource<Int>,
        val sections: kotlin.collections.List<Section>,
        val user: User,
        showBack: Boolean
    ) : SettingsScope("settings", user, unreadNotifications = unReadNotifications, showBack) {

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

        override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()
    }

    class Profile(val user: User) : Child.TabBar(),
        CommonScope.CanGoBack

    class Address(val addressData: AddressData, val user: User) : Child.TabBar(),
        CommonScope.CanGoBack

    class GstinDetails(val details: User.Details.DrugLicense, val user: User) : Child.TabBar(),
        CommonScope.CanGoBack

}