package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
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
        is Event.Action.Management.LoadRetailers -> TODO()
        is Event.Action.Management.LoadHospitals -> TODO()
        is Event.Action.Management.LoadSeasonBoys -> TODO()
    }

    private suspend fun loadAllStockists() {
        navigator.withScope<ManagementScope.Stockist> {
            if (it.canLoadMore()) {
                it.pagination.currentPage++
                load {
                    val (result, isSuccess) = networkManagementScope.getAllStockists(it.pagination.currentPage)
                    if (isSuccess && result != null) {
                        it.entities.value = it.entities.value + result
                    }
                }
            }
        }
    }

    private suspend fun loadSubscribedStockists() {

    }


    private suspend fun filter(value: String?) {

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