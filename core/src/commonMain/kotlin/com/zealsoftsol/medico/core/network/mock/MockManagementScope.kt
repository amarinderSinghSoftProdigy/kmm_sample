package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.Response

class MockManagementScope : NetworkScope.Management {

    init {
        "USING MOCK MANAGEMENT SCOPE".logIt()
    }

    override suspend fun getAllStockists(pagination: Pagination): Response.Wrapped<PaginatedData<EntityInfo>> =
        mockResponse {
            Response.Wrapped(
                PaginatedData(
                    listOf(
                        EntityInfo(
                            GeoPoints(0.0, 0.0),
                            "10 km away",
                            "123456789",
                            "India",
                            "11111",
                            "520001",
                            GeoPoints(0.0, 0.0),
                            "Delhi",
                            "Pharmacy Doctors",
                            null,
                        )
                    ),
                    1
                ),
                true,
            )
        }

    override suspend fun getSubscribedStockists(
        pagination: Pagination,
        unitCode: String
    ): Response.Wrapped<PaginatedData<EntityInfo>> = mockResponse {
        Response.Wrapped(PaginatedData(emptyList(), 0), true)
    }
}