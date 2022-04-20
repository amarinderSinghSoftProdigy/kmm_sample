package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.HeaderData
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.UserType

internal class ManagementEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkManagementScope: NetworkScope.Management,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Management>(navigator) {

    private var subscribeRequest: SubscribeRequest? = null

    override suspend fun handleEvent(event: Event.Action.Management) = when (event) {
        is Event.Action.Management.Load -> loadUserManagement(event.isFirstLoad)
        is Event.Action.Management.Search -> searchUserManagement(event.value)
        is Event.Action.Management.RequestSubscribe -> requestSubscribe(
            event.item,
            event.connectingStockistUnitCode
        )
        is Event.Action.Management.GetDetails -> openRetailerDetails(event.item, event.showConnectionOption)
        is Event.Action.Management.ChoosePayment -> choosePayment(
            event.paymentMethod,
            event.creditDays
        )
        is Event.Action.Management.VerifyRetailerTraderDetails -> verifyRetailerTraderDetails()
        is Event.Action.Management.SelectAction -> selectAction(event.notificationId, event.action)
        is Event.Action.Management.GetCompanies -> getCompanies(event.page, event.unitCode)
    }

    /**
     * get companies list from server
     */
    private suspend fun getCompanies(page: Int, unitCode: String) {
        navigator.withScope<ManagementScope.CompaniesScope> {
            withProgress {
                networkManagementScope.getCompanies(unitCode, page)
                    .onSuccess { data ->
                        it.updateCompanies(data.results)
                        it.totalResults = data.totalResults
                    }.onError(navigator)
            }
        }
    }

    private suspend fun openRetailerDetails(item: String, showConnectionOption: Boolean) {
        navigator.withProgress {
            userRepo.getBottomSheetDetails(
                item
            )
        }.onSuccess { body ->
            navigator.withScope<Scope> {
                val hostScope = scope.value
                hostScope.bottomSheet.value = BottomSheet.PreviewManagementItem(
                    headerData = body,
                    isSeasonBoy = false,
                    canSubscribe = it is ManagementScope.User && it.activeTab.value == ManagementScope.Tab.ALL_STOCKISTS,
                    connectingStockistUnitCode = item,
                    userType = userRepo.userV2Flow.value!!.type,
                    showConnectOption = showConnectionOption
                )
            }
        }.onError(navigator)
    }

    private suspend fun loadUserManagement(isFirstLoad: Boolean) {
        loadHelper.load<ManagementScope.User, EntityInfo>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkManagementScope.getManagementInfo(
                unitCode = user.unitCode,
                isSeasonBoy = user.type == UserType.SEASON_BOY,
                forUserType = forType,
                criteria = activeTab.value.criteria,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchUserManagement(search: String) {
        loadHelper.search<ManagementScope.User, EntityInfo>(searchValue = search) {
            val user = userRepo.requireUser()
            networkManagementScope.getManagementInfo(
                unitCode = user.unitCode,
                isSeasonBoy = user.type == UserType.SEASON_BOY,
                forUserType = forType,
                criteria = activeTab.value.criteria,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }


    private fun requestSubscribe(entityInfo: HeaderData, connectingStockistUnitCode: String) {
        navigator.withScope<ManagementScope.User.Stockist> {
            val user = userRepo.requireUser()
            subscribeRequest = SubscribeRequest(
                buyerUnitCode = user.unitCode,
                sellerUnitCode = connectingStockistUnitCode,
                paymentMethod = "",
                noOfCreditDays = 0,
                customerType = user.type.serverValue,
            )
            scope.value.dismissBottomSheet()
            it.notifications.value = ManagementScope.ChoosePaymentMethod(entityInfo.tradeName)
        }
    }

    private suspend fun choosePayment(paymentMethod: PaymentMethod, creditDays: Int?) {
        navigator.withScope<ManagementScope.User.Stockist> {
            subscribeRequest = requireNotNull(subscribeRequest).copy(
                paymentMethod = paymentMethod.serverValue,
                noOfCreditDays = creditDays ?: 0,
            )
            it.notifications.value = null
            subscribe()
            it.notifications.value = ManagementScope.ThankYou
        }
    }

    private suspend fun subscribe() {
        navigator.withScope<ManagementScope.User> {
            withProgress {
                val result = networkManagementScope.subscribeRequest(
                    requireNotNull(subscribeRequest)
                ).onSuccess { _ ->
                    // lazy hack, it is better to ask the server for updated values
                    it.items.value = it.items.value
                        .filter { requireNotNull(subscribeRequest).sellerUnitCode != it.unitCode }
                    subscribeRequest = null
                }.onError(navigator)
            }
        }
    }

    private suspend fun verifyRetailerTraderDetails() {
        navigator.withScope<ManagementScope.AddRetailer.TraderDetails> {
            val result = withProgress {
                userRepo.verifyRetailer(it.registration.value)
            }
            it.validation.value = result.validations
            result.onSuccess {
                EventCollector.sendEvent(Event.Transition.AddRetailerAddress)
            }.onError(navigator)
        }
    }

    private suspend fun selectAction(id: String, action: NotificationActionRequest) {
        navigator.withScope<ManagementScope.User> {
            withProgress {
                networkManagementScope.selectNotificationAction(id, action)
            }.onSuccess { _ ->
                it.changeAlertVisibility(true)
            }.onError(navigator)
        }
    }
}