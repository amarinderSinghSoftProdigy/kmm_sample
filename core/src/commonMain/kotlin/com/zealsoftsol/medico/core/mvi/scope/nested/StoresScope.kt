package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.regular.BaseSearchScope
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.Store

sealed class StoresScope(
    icon: ScopeIcon = ScopeIcon.HAMBURGER,
) : Scope.Child.TabBar(TabBarInfo.Search(icon)) {

    class All : StoresScope(), Loadable<Store> {

        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<Store>> = DataSource(emptyList())
        override val searchText: DataSource<String> = DataSource("")

        init {
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = true))
        }

        fun selectItem(item: Store) =
            EventCollector.sendEvent(Event.Action.Stores.Select(item))

        fun search(value: String) = EventCollector.sendEvent(Event.Action.Stores.Search(value))

        fun loadItems() =
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = false))
    }

    class StorePreview(
        val store: Store,
        override val productSearch: DataSource<String> = DataSource(""),
        override val isFilterOpened: DataSource<Boolean> = DataSource(false),
        override val filters: DataSource<List<Filter>> = DataSource(emptyList()),
        override val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
        override val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
    ) : StoresScope(ScopeIcon.BACK), BaseSearchScope, CommonScope.CanGoBack {

        override val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList())
        override val pagination: Pagination = Pagination()
        override val unitCode: String = store.sellerUnitCode
        override val supportsAutoComplete: Boolean = false

        init {
            EventCollector.sendEvent(Event.Action.Search.SearchInput(isOneOf = true))
        }

        override fun goBack(): Boolean {
            reset()
            return super.goBack()
        }
    }
}