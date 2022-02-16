package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.Store

internal class StoresEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    private val notificationRepo: NotificationRepo,
    private val networkStoresScope: NetworkScope.Stores,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Stores>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Stores) = when (event) {
        is Event.Action.Stores.Load -> loadStores(event.isFirstLoad)
        is Event.Action.Stores.Search -> searchStores(event.value)
        is Event.Action.Stores.Select -> select(event.item)
        is Event.Action.Stores.ShowDetails -> openDetails(event.item)
        is Event.Action.Stores.ShowLargeImage -> selectProductLargeImage(event.item, event.type)
    }

    fun selectProductLargeImage(item: String, type: String?) {
        navigator.scope.value.bottomSheet.value = BottomSheet.ViewLargeImage(item, type)
    }

    private fun openDetails(item: EntityInfo) {
        navigator.withScope<StoresScope.StorePreview> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.PreviewManagementItem(
                item,
                isSeasonBoy = false,
                canSubscribe = false,
            )
        }
    }

    private suspend fun loadStores(isFirstLoad: Boolean) {
        loadHelper.load<StoresScope.All, Store>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchStores(search: String) {
        loadHelper.search<StoresScope.All, Store>(searchValue = search) {
            val user = userRepo.requireUser()
            networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private fun select(item: Store) {
        navigator.withScope<StoresScope.All> {
            setScope(
                StoresScope.StorePreview(
                    item,
                    cartRepo.getEntriesCountDataSource(),
                    notificationRepo.getUnreadMessagesDataSource()
                )
            )
        }
    }
}