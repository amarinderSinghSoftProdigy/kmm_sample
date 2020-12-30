package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchResponse

class MockSearchScope : NetworkScope.Search {

    override suspend fun search(
        product: String,
        manufacturer: String,
        query: List<Pair<String, String>>
    ): Response.Wrapped<SearchResponse> = mockResponse {
        Response.Wrapped(SearchResponse(emptyList(), emptyList(), 0), true)
    }
}