package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchResponse

class MockSearchScope : NetworkScope.Search {

    init {
        "USING MOCK SEARCH SCOPE".logIt()
    }

    override suspend fun search(
        sort: String?,
        query: List<Pair<String, String>>,
        unitCode: String?,
        latitude: Double,
        longitude: Double,
        pagination: Pagination
    ): Response.Wrapped<SearchResponse> = mockResponse {
        Response.Wrapped(SearchResponse(emptyList(), emptyList(), 0, emptyList()), true)
    }

    override suspend fun autocomplete(input: String): Response.Wrapped<List<AutoComplete>> =
        mockResponse {
            Response.Wrapped(emptyList(), true)
        }
}