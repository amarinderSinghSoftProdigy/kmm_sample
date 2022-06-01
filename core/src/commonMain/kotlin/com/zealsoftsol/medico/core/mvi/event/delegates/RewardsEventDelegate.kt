package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.RewardsScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class RewardsEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val rewardsStoreScope: NetworkScope.RewardsStore,
    ) : EventDelegate<Event.Action.Rewards>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Rewards) = when (event) {
        is Event.Action.Rewards.GetRewards -> getRewards()
    }

    private suspend fun getRewards() {
        navigator.withScope<RewardsScope> {
            val result = withProgress {
                rewardsStoreScope.getRewards()
            }
            result.onSuccess {body ->
                body.log(body.toString())
            }.onError(navigator)
        }
    }
}