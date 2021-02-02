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
import com.zealsoftsol.medico.data.ManagementCriteria
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
        is Event.Action.Management.LoadStockists -> loadStockists(null, event.criteria)
        is Event.Action.Management.SearchStockists -> loadStockists(event.value, null)
        is Event.Action.Management.Select -> select(event.item)
        is Event.Action.Management.LoadRetailers -> TODO()
        is Event.Action.Management.LoadHospitals -> TODO()
        is Event.Action.Management.LoadSeasonBoys -> TODO()
        is Event.Action.Management.RequestSubscribe -> requestSubscribe(event.item)
        is Event.Action.Management.ChoosePayment -> choosePayment(event.paymentMethod)
        is Event.Action.Management.ChooseNumberOfDays -> chooseNumberOfDays(event.days)
    }

    private suspend fun loadStockists(search: String?, criteria: ManagementCriteria?) {
        navigator.withScope<ManagementScope.Stockist> {
            it.searchText.value = search.orEmpty()
            if (it.pagination.nextPage() == 0 || it.pagination.canLoadMore()) {
                load(withProgress = criteria != null, debounce = 500) {
                    val (result, isSuccess) = networkManagementScope.getStockists(
                        pagination = it.pagination,
                        unitCode = userRepo.requireUser().unitCode,
                        search = it.searchText.value,
                        criteria = criteria ?: requireNotNull(it.activeTab.value?.criteria),
                    )
                    if (isSuccess && result != null) {
                        it.pagination.setTotal(result.total)
                        it.items.value = it.items.value + result.data
                    }
                }
            }
        }
    }

    private fun select(item: Any) {
        when (item) {
            is EntityInfo -> navigator.withScope<ManagementScope<*>> {
                val hostScope = scope.value
                hostScope.bottomSheet.value = BottomSheet.PreviewManagementItem(item)
            }
            else -> "unknown item to select $item".warnIt()
        }
    }

    private fun requestSubscribe(managementItem: ManagementItem) {
        navigator.withScope<ManagementScope.Stockist> {
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
        navigator.withScope<ManagementScope.Stockist> {
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
        navigator.withScope<ManagementScope.Stockist> {
            subscribeRequest = requireNotNull(subscribeRequest).copy(noOfCreditDays = days)
            it.dismissNotification()
            subscribe()
            it.notifications.value = ManagementScope.ThankYou
        }
    }

    private suspend fun subscribe() {
        navigator.withProgress {
            val (error, isSuccess) = networkManagementScope.subscribeRequest(
                requireNotNull(
                    subscribeRequest
                )
            )
            if (isSuccess) {
                subscribeRequest = null
            } else {
                navigator.setHostError(error ?: ErrorCode())
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