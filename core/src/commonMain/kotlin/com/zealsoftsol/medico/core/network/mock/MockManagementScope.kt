package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.Response

class MockManagementScope : NetworkScope.Management {

    override suspend fun getAllStockists(page: Int): Response.Wrapped<List<EntityInfo>> =
        mockResponse {
            Response.Wrapped(emptyList(), true)
        }

    override suspend fun getSubscribedStockists(
        page: Int,
        unitCode: String
    ): Response.Wrapped<List<EntityInfo>> = mockResponse {
        Response.Wrapped(emptyList(), true)
    }
}