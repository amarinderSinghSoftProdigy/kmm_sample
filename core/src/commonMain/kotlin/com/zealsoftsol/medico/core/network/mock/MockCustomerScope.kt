package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerDataV2
import com.zealsoftsol.medico.data.CustomerMetaData
import com.zealsoftsol.medico.data.DashboardManufacturer
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

    override suspend fun getDashboardManufacturers(type: UserType): BodyResponse<DashboardManufacturer> {
        TODO("Not yet implemented")
    }

    override suspend fun getStockStatusData(type: UserType): BodyResponse<StockStatusData> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecentProducts(type: UserType): BodyResponse<RecentProductInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerDataV2() = mockResponse {
        getMockCustomerDataV2()
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
