package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.ManagementItem
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.SubscribeRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class ManagementEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkManagementScope: NetworkScope.Management,
) : EventDelegate<Event.Action.Management>(navigator) {

    private var loadJob: Job? = null
    private var subscribeRequest: SubscribeRequest? = null

    override suspend fun handleEvent(event: Event.Action.Management) = when (event) {
        is Event.Action.Management.Load -> loadUserManagement()
        is Event.Action.Management.Search -> searchUserManagement(event.value)
        is Event.Action.Management.Select -> select(event.item)
        is Event.Action.Management.RequestSubscribe -> requestSubscribe(event.item)
        is Event.Action.Management.ChoosePayment -> choosePayment(event.paymentMethod)
        is Event.Action.Management.ChooseNumberOfDays -> chooseNumberOfDays(event.days)
    }

    private suspend fun loadUserManagement() {
        navigator.withScope<ManagementScope.User> {
            if (!navigator.scope.value.isInProgress.value && (it.pagination.nextPage() == 0 || it.pagination.canLoadMore())) {
                it.loadManagement(withProgress = true, addPage = true)
            }
        }
    }

    private suspend fun searchUserManagement(search: String) {
        navigator.withScope<ManagementScope.User> {
            it.pagination.reset()
            it.searchText.value = search
            it.loadManagement(withProgress = false, addPage = false)
        }
    }

    private fun select(item: Any) {
        when (item) {
            is EntityInfo -> navigator.withScope<ManagementScope.User> {
                val hostScope = scope.value
                hostScope.bottomSheet.value = BottomSheet.PreviewManagementItem(
                    item,
                    isSeasonBoy = it is ManagementScope.User.SeasonBoy,
                )
            }
            else -> "unknown item to select $item".warnIt()
        }
    }

    private fun requestSubscribe(managementItem: ManagementItem) {
        navigator.withScope<ManagementScope.User.Stockist> {
            managementItem as EntityInfo
            val user = userRepo.requireUser()
            subscribeRequest = SubscribeRequest(
                buyerUnitCode = user.unitCode,
                sellerUnitCode = managementItem.unitCode,
                paymentMethod = "",
                noOfCreditDays = 0,
                customerType = user.type.serverValue,
            )
            scope.value.dismissBottomSheet()
            it.notifications.value = ManagementScope.ChoosePaymentMethod()
        }
    }

    private suspend fun choosePayment(paymentMethod: PaymentMethod) {
        navigator.withScope<ManagementScope.User.Stockist> {
            subscribeRequest =
                requireNotNull(subscribeRequest).copy(paymentMethod = paymentMethod.serverValue)
            it.notifications.value = if (paymentMethod == PaymentMethod.CREDIT) {
                ManagementScope.ChooseNumberOfDays()
            } else {
                it.dismissNotification()
                subscribe()
                ManagementScope.ThankYou
            }
        }
    }

    private suspend fun chooseNumberOfDays(days: Int) {
        navigator.withScope<ManagementScope.User.Stockist> {
            subscribeRequest = requireNotNull(subscribeRequest).copy(noOfCreditDays = days)
            it.dismissNotification()
            subscribe()
            it.notifications.value = ManagementScope.ThankYou
        }
    }

    private suspend fun subscribe() {
        navigator.withScope<ManagementScope.User> {
            withProgress {
                val (error, isSuccess) = networkManagementScope.subscribeRequest(
                    requireNotNull(subscribeRequest)
                )
                if (isSuccess) {
                    // lazy hack, it is better to ask the server for updated values
                    it.items.value = it.items.value
                        .filter { requireNotNull(subscribeRequest).sellerUnitCode != it.unitCode }
                    subscribeRequest = null
                } else {
                    setHostError(error ?: ErrorCode())
                }
            }
        }
    }

    private suspend fun ManagementScope.User.loadManagement(
        withProgress: Boolean,
        addPage: Boolean
    ) {
        load(withProgress = withProgress, debounce = 500) {
            val (result, isSuccess) = networkManagementScope.getManagementInfo(
                unitCode = userRepo.requireUser().unitCode,
                forUserType = forType,
                criteria = activeTab.value.criteria,
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess && result != null) {
                pagination.setTotal(result.total)
                items.value = if (addPage) items.value + result.data else result.data
            }
        }
    }

    private suspend fun load(
        withProgress: Boolean = false,
        debounce: Long = 0,
        loader: suspend () -> Unit
    ) {
        if (withProgress) navigator.setHostProgress(true)
        loadJob?.cancel()
        loadJob = coroutineContext.toScope().launch {
            if (debounce > 0) delay(debounce)
            loader()
            if (withProgress) navigator.setHostProgress(false)
        }
    }
}