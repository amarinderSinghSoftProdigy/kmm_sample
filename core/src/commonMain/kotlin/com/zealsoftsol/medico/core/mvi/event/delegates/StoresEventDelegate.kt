package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.Value

internal class StoresEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    private val notificationRepo: NotificationRepo,
    private val networkStoresScope: NetworkScope.Stores,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Stores>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Stores) = when (event) {
        is Event.Action.Stores.Load -> loadStores(event.isFirstLoad, event.manufacturers)
        is Event.Action.Stores.Search -> searchStores(event.value, event.manufacturers)
        is Event.Action.Stores.Select -> select(event.item)
        is Event.Action.Stores.ShowLargeImage -> selectProductLargeImage(event.item, event.type)
        is Event.Action.Stores.ShowManufacturers -> showFilterManufacturers(event.data)
        is Event.Action.Stores.ApplyManufacturersFilter -> updateSelectedManufacturersFilters(event.filters)
    }

    /**
     * this will get the manufacturers selected by user to be applied as filter
     */
    private fun updateSelectedManufacturersFilters(filters: List<Value>) {
        navigator.withScope<StoresScope.StorePreview> {
            it.selectedFilters.value = filters
            it.startSearch(true, "")
        }
    }

    /**
     * show all the manufacturers to user that are available for filter
     * and send preselected filters if any
     */
    private fun showFilterManufacturers(data: List<Value>) {
        navigator.withScope<StoresScope.StorePreview> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.FilerManufacturers(
                data,
                it.selectedFilters.value,
                BottomSheet.FilerManufacturers.FilterScopes.STORES
            )
        }
    }

    fun selectProductLargeImage(item: String, type: String?) {
        navigator.scope.value.bottomSheet.value = BottomSheet.ViewLargeImage(item, type)
    }

    private suspend fun loadStores(isFirstLoad: Boolean, manufacturers:String) {
        loadHelper.load<StoresScope.All, Store>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
                manufacturers = manufacturers
            ).getBodyOrNull()
        }
    }

    private suspend fun searchStores(search: String, manufacturers:String) {
        loadHelper.search<StoresScope.All, Store>(searchValue = search) {
            val user = userRepo.requireUser()
            networkStoresScope.getStores(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
                manufacturers = manufacturers
            ).getBodyOrNull()
        }
    }

    private fun select(item: Store) {
        navigator.withScope<Scope> {
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