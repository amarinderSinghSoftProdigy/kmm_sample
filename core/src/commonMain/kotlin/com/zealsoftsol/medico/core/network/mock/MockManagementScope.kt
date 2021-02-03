package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.UserType

class MockManagementScope : NetworkScope.Management {

    init {
        "USING MOCK MANAGEMENT SCOPE".logIt()
    }

    override suspend fun getManagementInfo(
        unitCode: String,
        forUserType: UserType,
        criteria: ManagementCriteria,
        search: String,
        pagination: Pagination
    ): Response.Wrapped<PaginatedData<EntityInfo>> {
        return mockResponse {
            Response.Wrapped(
                longPaginatedData(20),
                true,
            )
        }
    }

    override suspend fun subscribeRequest(subscribeRequest: SubscribeRequest): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, true)
        }
}

private fun longPaginatedData(size: Int) =
    PaginatedData(
        (0 until size)
            .map {
                EntityInfo(
                    GeoPoints(0.0, 0.0),
                    "10 km away",
                    "123456789",
                    "India",
                    "11111",
                    "911111111199",
                    "520001",
                    GeoPoints(0.0, 0.0),
                    "Delhi",
                    "Pharmacy Doctors ${it + 1}",
                    "12345",
                    null,
                )
            },
        9999999,
    )