package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class RewardsEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val rewardsStoreScope: NetworkScope.RewardsStore,
    ) : EventDelegate<Event.Action.Rewards>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Rewards) = when (event) {
        else -> {}
    }

    private suspend fun getRewards() {
    }
}