package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.EntityInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class ManagementEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkManagementScope: NetworkScope.Management,
) : EventDelegate<Event.Action.Management>(navigator) {

    private var loadJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Management) = when (event) {
        is Event.Action.Management.LoadAllStockists -> loadAllStockists()
        is Event.Action.Management.LoadSubscribedStockists -> loadSubscribedStockists()
        is Event.Action.Management.Filter -> filter(event.value)
        is Event.Action.Management.Select -> select(event.item)
        is Event.Action.Management.Subscribe -> subscribe(event.item)
        is Event.Action.Management.LoadRetailers -> TODO()
        is Event.Action.Management.LoadHospitals -> TODO()
        is Event.Action.Management.LoadSeasonBoys -> TODO()
    }

    private suspend fun loadAllStockists() {
        navigator.withScope<ManagementScope.Stockist> {
            if (it.pagination.nextPage() == 0 || it.pagination.canLoadMore()) {
                load {
                    val (result, isSuccess) = networkManagementScope.getAllStockists(it.pagination)
                    if (isSuccess && result != null) {
                        it.pagination.setTotal(result.total)
                        it.items.value = it.items.value + result.data
                    }
                }
            }
        }
    }

    private suspend fun loadSubscribedStockists() {
        navigator.withScope<ManagementScope.Stockist> {
            if (it.pagination.nextPage() == 0 || it.pagination.canLoadMore()) {
                load {
                    val (result, isSuccess) = networkManagementScope.getSubscribedStockists(
                        pagination = it.pagination,
                        unitCode = userRepo.requireUser().unitCode,
                    )
                    if (isSuccess && result != null) {
                        it.pagination.setTotal(result.total)
                        it.items.value = it.items.value + result.data
                    }
                }
            }
        }
    }

    private suspend fun filter(value: String?) {
        navigator.withScope<ManagementScope<*>> {
            it.searchText.value = value.orEmpty()
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

    private suspend fun subscribe(item: Any) {
        when (item) {
            is EntityInfo -> navigator.withScope<ManagementScope<*>> {

            }
            else -> "unknown item to subscribe to $item".warnIt()
        }
    }

    private suspend fun load(loader: suspend () -> Unit) {
        navigator.setHostProgress(true)
        loadJob?.cancel()
        loadJob = coroutineContext.toScope().launch {
            loader()
            navigator.setHostProgress(false)
        }
    }
}