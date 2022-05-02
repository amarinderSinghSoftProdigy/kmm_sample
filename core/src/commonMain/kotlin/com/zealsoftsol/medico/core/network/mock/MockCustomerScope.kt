package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.ConnectedUserData
import com.zealsoftsol.medico.data.CountData
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerDataV2
import com.zealsoftsol.medico.data.CustomerMetaData
import com.zealsoftsol.medico.data.DashboardData
import com.zealsoftsol.medico.data.ProductSold
import com.zealsoftsol.medico.data.RecentProductInfo
import com.zealsoftsol.medico.data.StockStatusData
import com.zealsoftsol.medico.data.UserType

class MockCustomerScope : NetworkScope.Customer {

    init {
        "USING MOCK CUSTOMER SCOPE".logIt()
    }

    override suspend fun getCustomerData() = mockResponse {
        getMockCustomerData()
    }

    override suspend fun getCustomerDataV2() = mockResponse {
        getMockCustomerDataV2()
    }

    override suspend fun getDashboard(unitCode: String): BodyResponse<DashboardData> =
        mockResponse {
            DashboardData(
                ConnectedUserData(
                    CountData(1, 1, 1),
                    CountData(1, 1, 1),
                    CountData(1, 1, 1),
                    CountData(1, 1, 1)
                ), 10, 10, RecentProductInfo(
                    listOf(ProductSold(100, "product")),
                    listOf(ProductSold(100, "product")),
                    listOf(ProductSold(100, "product"))
                ), StockStatusData(10, 10, 10),
                dealsOfDay = emptyList()
            )
        }

    companion object {

        fun getMockCustomerData(userType: UserType = UserType.STOCKIST) = CustomerData(
            "2194129343",
            AddressData(
                "India",
                "landmark",
                "Delhi",
                "Vijayawada",
                0.0,
                0.0,
                "Some location",
                520001,
                "",
                ""
            ),
            CustomerMetaData(true, "", ""),
            userType.serverValue,
            "drug1",
            "drug2",
            "url",
            true,
            "test@mail.com",
            "Test",
            "12345",
            "User",
            "+1111111",
            "55532",
            "Test Trader",
            "0000000",
        )

        fun getMockCustomerDataV2(userType: UserType = UserType.STOCKIST) = CustomerDataV2(
            /*"2194129343",
            AddressData(
                "India",
                "landmark",
                "Delhi",
                "Vijayawada",
                0.0,
                0.0,
                "Some location",
                520001,
                "",
                ""
            ),*/
            CustomerMetaData(true, "", ""),
            userType.serverValue,
            /*"drug1",
            "drug2",
            "url",
            true,
            "test@mail.com",
            "Test",
            "12345",
            "User",
            "+1111111",
            "55532",
            "Test Trader",
            "0000000",*/
            "0000000",
            0.0, 0.0,"",""
        )
    }
}
