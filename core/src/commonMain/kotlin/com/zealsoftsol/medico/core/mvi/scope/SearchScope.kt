package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.Product

sealed class SearchScope : BaseScope(), CanGoBack {

    data class Query(
        val productSearch: DataSource<String> = DataSource(""),
        val manufacturerSearch: DataSource<String> = DataSource(""),
        val filters: DataSource<List<Filter>> = DataSource(emptyList()),
        val products: DataSource<List<Product>> = DataSource(emptyList())
    ) : SearchScope() {

        init {
            searchProduct("")
        }

        fun selectFilter(filter: Filter, option: Option<String>) =
            EventCollector.sendEvent(Event.Action.Search.SelectFilter(filter, option))

        fun clearFilter(filter: Filter?) =
            EventCollector.sendEvent(Event.Action.Search.ClearFilter(filter))

        fun searchManufacturer(input: String) =
            EventCollector.sendEvent(Event.Action.Search.SearchManufacturer(input))

        fun searchProduct(input: String) =
            EventCollector.sendEvent(Event.Action.Search.SearchProduct(input))
    }

    class Result : SearchScope()
}