package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getBannerDataSource
import com.zealsoftsol.medico.core.repository.getBrandsDataSource
import com.zealsoftsol.medico.core.repository.getCategoriesDataSource
import com.zealsoftsol.medico.core.repository.getDealsDataSource
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getManufacturerDataSource
import com.zealsoftsol.medico.core.repository.getPromotionsDataSource
import com.zealsoftsol.medico.core.repository.getRecentProductsDataSource
import com.zealsoftsol.medico.core.repository.getStockDataSource
import com.zealsoftsol.medico.core.repository.getStockistEmpBannerDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSourceV2
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.repository.requireUserOld
import com.zealsoftsol.medico.data.UserType
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class AuthEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val notificationRepo: NotificationRepo,
    private val cartRepo: CartRepo,
) : EventDelegate<Event.Action.Auth>(navigator) {

    private var dashboardJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Auth) = when (event) {
        is Event.Action.Auth.LogIn -> authTryLogin()
        is Event.Action.Auth.LogOut -> authTryLogOut(event.notifyServer)
        is Event.Action.Auth.UpdateAuthCredentials -> authUpdateCredentials(
            event.emailOrPhone,
            event.password
        )
        is Event.Action.Auth.UpdateDashboard -> updateDashboard()
    }

    private suspend fun authTryLogin() {
        navigator.withScope<LogInScope> {
            withProgress {
                userRepo.login(
                    it.credentials.value.phoneNumberOrEmail,
                    it.credentials.value.password,
                )
            }.onSuccess { _ ->
                withProgress { userRepo.loadUserFromServerV2() }
                    .onSuccess {
                        withProgress {
                            userRepo.sendFirebaseToken()
                            //userRepo.loadConfig()
                            //notificationRepo.loadUnreadMessagesFromServer()
                            if (it.customerType != UserType.STOCKIST_EMPLOYEE.serverValue && it.customerType != UserType.RETAILER_EMPLOYEE.serverValue)
                                cartRepo.loadCartFromServer(userRepo.requireUser().unitCode)
                        }
                        dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                        val user = userRepo.requireUser()
                        setScope(
                            /*if (user.type == UserType.STOCKIST_EMPLOYEE) {
                                InStoreSellerScope.get(
                                    user,
                                    userRepo.getUserDataSourceV2(),
                                    null
                                )
                            } else {*/
                            if (user.isActivated)
                                DashboardScope.get(
                                    user = user,
                                    userDataSource = userRepo.getUserDataSourceV2(),
                                    manufacturerData = userRepo.getManufacturerDataSource(),
                                    unreadNotifications = notificationRepo.getUnreadMessagesDataSource(),
                                    cartItemsCount = cartRepo.getEntriesCountDataSource(),
                                    stockStatusData = userRepo.getStockDataSource(),
                                    recentProductInfo = userRepo.getRecentProductsDataSource(),
                                    promotionData = userRepo.getPromotionsDataSource(),
                                    dealsData = userRepo.getDealsDataSource(),
                                    categoriesData = userRepo.getCategoriesDataSource(),
                                    brandsData = userRepo.getBrandsDataSource(),
                                    bannerData = userRepo.getBannerDataSource(),
                                    stockistEmpBannerData = userRepo.getStockistEmpBannerDataSource()
                                )
                            else
                                LimitedAccessScope.get(
                                    userRepo.requireUserOld(),
                                    userRepo.getUserDataSource(),
                                    userRepo.getUserDataSourceV2()
                                )
                            //}
                        )
                    }.onError(navigator)
            }.onError{ error ->
                it.errorCode.value = error.body
                it.showCredentialError.value = true
                it.showToast.value = true
            }
        }
    }

    private suspend fun authTryLogOut(notifyServer: Boolean) {
        if (userRepo.userFlow.value != null) {
            if (notifyServer) {
                navigator.withProgress { userRepo.logout() }
                    .onSuccess {
                        navigator.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                        navigator.setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
                    }.onError(navigator)
            } else {
                userRepo.clear()
                navigator.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                navigator.setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
            }
        } else {
            userRepo.clear()
            navigator.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
            navigator.setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
        }
    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        navigator.withScope<LogInScope> {
            it.credentials.value =
                userRepo.updateAuthCredentials(it.credentials.value, emailOrPhone, password)
        }
    }

    private suspend fun updateDashboard() {
        dashboardJob?.cancel()
        dashboardJob = coroutineContext.toScope().launch { userRepo.loadDashboard() }
    }
}