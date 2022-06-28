package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.Store

class MockStoresScope : NetworkScope.Stores {

    init {
        "USING MOCK STORES SCOPE".logIt()
    }

    override suspend fun getStores(
        unitCode: String,
        search: String,
        pagination: Pagination,
        manufacturers: String
    ): BodyResponse<PaginatedData<Store>> = mockResponse {
        PaginatedData(emptyList<Store>(), 0, emptyList())
    }
}