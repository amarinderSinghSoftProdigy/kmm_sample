package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.Store

internal class IOCEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    private val notificationRepo: NotificationRepo,
    private val networkStoresScope: NetworkScope.IOCStore,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.IOC>(navigator) {

    override suspend fun handleEvent(event: Event.Action.IOC) = when (event) {
        is Event.Action.IOC.Load -> loadStores(event.isFirstLoad)
        is Event.Action.IOC.Search -> searchStores(event.value)
        is Event.Action.IOC.Select -> select(event.item)
    }


    private suspend fun loadStores(isFirstLoad: Boolean) {
        /*loadHelper.load<IocScope.IOCListing, Store>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }*/
    }

    private suspend fun searchStores(search: String) {
        /* loadHelper.search<StoresScope.All, Store>(searchValue = search) {
             val user = userRepo.requireUser()
             networkStoresScope.getStores(
                 unitCode = user.unitCode,
                 search = searchText.value,
                 pagination = pagination,
             ).getBodyOrNull()
         }*/
    }

    private fun select(item: String) {
        navigator.withScope<IocScope.IOCListing> {
            setScope(
                IocScope.IOCCreate(item)
            )
        }
    }
}