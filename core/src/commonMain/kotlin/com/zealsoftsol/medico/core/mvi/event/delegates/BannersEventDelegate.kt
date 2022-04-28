package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.BannersScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class BannersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val bannersRepo: NetworkScope.BannersStore
) : EventDelegate<Event.Action.Banners>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Banners) = when (event) {
        is Event.Action.Banners.GetAllBanners -> getAllBanners(event.page, event.search)
    }

    /**
     * get all banners from server
     */
    private suspend fun getAllBanners(page: Int, search: String) {
        navigator.withScope<BannersScope> {
            val result = withProgress {
                bannersRepo.getAllBanners(page, search)
            }

            result.onSuccess { body ->
                it.updateBanners(body.results)
                it.totalItems = body.totalResults
            }.onError(navigator)
        }
    }

}