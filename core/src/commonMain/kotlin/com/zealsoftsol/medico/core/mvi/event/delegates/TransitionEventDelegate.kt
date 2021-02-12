package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PreviewUserScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

internal class TransitionEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        navigator.apply {
            when (event) {
                is Event.Transition.Back -> dropScope()
                is Event.Transition.ForgetPassword -> setScope(
                    OtpScope.PhoneNumberInput.get(
                        phoneNumber = DataSource(""),
                        isForRegisteredUsersOnly = true,
                    )
                )
                is Event.Transition.SignUp -> setScope(SignUpScope.SelectUserType.get())
                is Event.Transition.Search -> setScope(SearchScope())
                is Event.Transition.Settings -> {
                    val user = userRepo.requireUser()
                    setScope(
                        SettingsScope.List(
                            if (user.type == UserType.SEASON_BOY)
                                SettingsScope.List.Section.simple(user.isActivated)
                            else
                                SettingsScope.List.Section.all(user.isActivated)
                        )
                    )
                }
                is Event.Transition.Profile -> setScope(
                    SettingsScope.Profile(userRepo.requireUser())
                )
                is Event.Transition.ChangePassword -> setScope(
                    PasswordScope.VerifyCurrent()
                )
                is Event.Transition.Address -> setScope(
                    SettingsScope.Address(userRepo.requireUser().addressData)
                )
                is Event.Transition.GstinDetails -> setScope(
                    SettingsScope.GstinDetails(userRepo.requireUser().details as User.Details.DrugLicense)
                )
                is Event.Transition.Management -> setScope(
                    when (event.manageUserType) {
                        UserType.STOCKIST -> ManagementScope.User.Stockist()
                        UserType.RETAILER -> ManagementScope.User.Retailer(
                            canAdd = userRepo.requireUser().type == UserType.SEASON_BOY
                        )
                        UserType.HOSPITAL -> ManagementScope.User.Hospital()
                        UserType.SEASON_BOY -> ManagementScope.User.SeasonBoy()
                    }
                )
                is Event.Transition.RequestCreateRetailer -> setScope(
                    ManagementScope.AddRetailer.TraderDetails(
                        registration = DataSource(UserRegistration3())
                    )
                )
                is Event.Transition.AddRetailerAddress -> withScope<ManagementScope.AddRetailer.TraderDetails> {
                    setScope(
                        ManagementScope.AddRetailer.Address(
                            it.registration.value,
                            DataSource(UserRegistration2()),
                        )
                    )
                }
                is Event.Transition.PreviewUser -> {
                    dropScope(
                        Navigator.DropStrategy.To(ManagementScope.User.Retailer::class),
                        updateDataSource = false
                    )
                    setScope(
                        PreviewUserScope(
                            event.registration2,
                            event.registration3
                        )
                    )
                }
                is Event.Transition.CloseNotification -> withScope<CommonScope.WithNotifications>(
                    forceSafe = true
                ) {
                    it.notifications.value = null
                }
            }
        }
    }
}