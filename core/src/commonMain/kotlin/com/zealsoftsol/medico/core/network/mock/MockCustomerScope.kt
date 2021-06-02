package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerMetaData
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.UserType

class MockCustomerScope : NetworkScope.Customer {

    init {
        "USING MOCK CUSTOMER SCOPE".logIt()
    }

    override suspend fun getCustomerData(): Response.Wrapped<CustomerData> = mockResponse {
        Response.Wrapped(getMockCustomerData(), true)
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
                "Some location",
                0.0,
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
    }
}
