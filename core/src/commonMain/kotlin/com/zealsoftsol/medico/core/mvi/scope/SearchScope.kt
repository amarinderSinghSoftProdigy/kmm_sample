package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

sealed class SearchScope : BaseScope(), CanGoBack {
    abstract val searchInput: DataSource<String>

    data class Query(
        override val searchInput: DataSource<String> = DataSource(""),
    ) : SearchScope() {

        init {
            searchInput("")
        }

        fun searchInput(input: String) =
            EventCollector.sendEvent(Event.Action.Search.Query(input))
    }

    data class Result(
        override val searchInput: DataSource<String>,
    ) : SearchScope()
}