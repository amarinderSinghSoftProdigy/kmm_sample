package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.core.network.NetworkScope

internal class SearchEventDelegate(
    navigator: Navigator,
    private val networkSearchScope: NetworkScope.Search,
) : EventDelegate<Event.Action.Search>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Search) = when (event) {
        is Event.Action.Search.Query -> query(event.value)
    }

    private suspend fun query(value: String) {
        navigator.withScope<SearchScope> {
            it.searchInput.value = value
            val (result, isSuccess) = networkSearchScope.search(value)
            if (isSuccess && result != null) {
                "facets".warnIt()
                result.facets.forEach {
                    it.logIt()
                }
                "products".warnIt()
                result.products.forEach {
                    it.logIt()
                }
            } else {

            }
        }
    }
}