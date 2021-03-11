package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.GeoData
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
        isSeasonBoy: Boolean,
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
                    geoData = GeoData(
                        distance = "10 km away",
                        origin = GeoPoints(0.0, 0.0),
                        destination = GeoPoints(0.0, 0.0),
                        location = "India",
                        pincode = "520001",
                        city = "Delhi",
                    ),
                    gstin = "123456789",
                    panNumber = "11111",
                    phoneNumber = "911111111199",
                    tradeName = "Pharmacy Doctors ${it + 1}",
                    unitCode = "12345",
                    subscriptionData = null,
                )
            },
        9999999,
    )