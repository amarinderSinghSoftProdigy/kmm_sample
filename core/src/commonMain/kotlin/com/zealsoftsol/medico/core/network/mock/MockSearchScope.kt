package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.ConnectedStockist
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
        pagination: Pagination,
        addPage:Boolean
    ) = mockResponse {
        SearchResponse(emptyList(), emptyList(), 0, emptyList())
    }

    override suspend fun getAlternateProducts(productCode: String): BodyResponse<List<AlternateProductData>> =
        mockResponse { emptyList() }

    override suspend fun autocomplete(
        input: String,
        unitCodeForStores: String?
    ) =
        mockResponse {
            emptyList<AutoComplete>()
        }

    override suspend fun loadStockist(
        latitude: Double,
        longitude: Double,
        imageCode: String,
        code: String
    ) = mockResponse {
        emptyList<ConnectedStockist>()
    }
}