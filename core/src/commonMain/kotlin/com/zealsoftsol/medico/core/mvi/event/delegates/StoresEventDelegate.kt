package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.Store

internal class StoresEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkStoresScope: NetworkScope.Stores,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Stores>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Stores) = when (event) {
        is Event.Action.Stores.Load -> loadStores(event.isFirstLoad)
        is Event.Action.Stores.Search -> searchStores(event.value)
        is Event.Action.Stores.Select -> select(event.item)
    }

    private suspend fun loadStores(isFirstLoad: Boolean) {
        loadHelper.load<StoresScope.All, Store>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            val (result, isSuccess) = networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun searchStores(search: String) {
        loadHelper.search<StoresScope.All, Store>(searchValue = search) {
            val user = userRepo.requireUser()
            val (result, isSuccess) = networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private fun select(item: Store) {
        navigator.withScope<StoresScope.All> {
            setScope(StoresScope.StorePreview(item))
        }
    }
}