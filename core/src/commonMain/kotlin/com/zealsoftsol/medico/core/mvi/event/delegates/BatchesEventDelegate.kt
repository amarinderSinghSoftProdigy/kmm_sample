package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser

internal class BatchesEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val batchesScope: NetworkScope.BatchesStore,
) : EventDelegate<Event.Action.Batches>(navigator) {


    override suspend fun handleEvent(event: Event.Action.Batches) = when (event) {
        is Event.Action.Batches.GetBatches -> getBatches(event.spid)
    }

    private suspend fun getBatches(spid: String) {
        val user = userRepo.requireUser()

        navigator.withScope<BatchesScope> {
            withProgress {
                batchesScope.getBatches(unitCode = user.unitCode, spid)
                    .onSuccess { body ->
                        if (body.results[0].batches.isNotEmpty()) {
                            it.batchData.value = body.results
                        }
                    }.onError(navigator)
            }
        }
    }

}