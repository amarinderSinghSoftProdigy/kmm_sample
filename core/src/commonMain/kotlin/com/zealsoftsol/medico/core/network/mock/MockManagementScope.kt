package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.GeoData
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.InventoryCompanies
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.PaginatedData
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
    ) = mockResponse {
        longPaginatedData(20)
    }

    override suspend fun subscribeRequest(subscribeRequest: SubscribeRequest) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun selectNotificationAction(
        id: String,
        actionRequest: NotificationActionRequest
    ) = mockResponse {
        mockEmptyMapBody()
    }

    override suspend fun getCompanies(
        unitCode: String,
        page: Int
    ): BodyResponse<InventoryCompanies> {
        TODO("Not yet implemented")
    }

}

private fun longPaginatedData(size: Int) =
    PaginatedData(
        (0 until size)
            .map {
                EntityInfo(
                    geoData = GeoData(
                        distance = 10.0,
                        formattedDistance = "10 km away",
                        origin = GeoPoints(0.0, 0.0),
                        destination = GeoPoints(0.0, 0.0),
                        location = "India",
                        pincode = "520001",
                        city = "Delhi",
                        landmark = "Landmark",
                        addressLine = ""
                    ),
                    gstin = "123456789",
                    panNumber = "11111",
                    phoneNumber = "911111111199",
                    tradeName = "Pharmacy Doctors ${it + 1}",
                    unitCode = "12345",
                    subscriptionData = null,
                    drugLicenseNo1 = null,
                    drugLicenseNo2 = null,
                )
            },
        9999999,
        emptyList()
    )