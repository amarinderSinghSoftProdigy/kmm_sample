package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.BaseSearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.Store
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class BatchesEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkBatchScope: NetworkScope.Batches
) : EventDelegate<Event.Action.Batches>(navigator) {

    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Batches) = when (event) {
        is Event.Action.Batches.GetBatches -> loadBatches()
    }


    private suspend fun loadBatches() {
        navigator.withScope<BatchesScope> {
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = true,
            )
        }
    }

    private suspend inline fun BatchesScope.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkBatchScope.getBatches(unitCode = user.unitCode, spid).onSuccess { body ->
                println(" Reached ")
                batchData.value = body.results
            }.onError(navigator)
            onEnd()
        }
    }

    private suspend fun searchAsync(
        withDelay: Boolean,
        withProgress: Boolean,
        search: suspend () -> Unit
    ) {
        searchJob?.cancel()
        searchJob = coroutineContext.toScope().launch {
            if (withDelay) delay(500)
            if (withProgress) navigator.setHostProgress(true)
            search()
            if (withProgress) navigator.setHostProgress(false)
        }
    }
}