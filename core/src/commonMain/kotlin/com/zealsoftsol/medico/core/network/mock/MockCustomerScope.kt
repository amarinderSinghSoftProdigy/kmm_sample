package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.CustomerAddressData
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerMetaData
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.UserType

class MockCustomerScope : NetworkScope.Customer {

    init {
        "USING MOCK CUSTOMER SCOPE".logIt()
    }

    override suspend fun getCustomerData(): Response.Wrapped<CustomerData> = mockResponse {
        Response.Wrapped(
            CustomerData(
                "",
                CustomerAddressData("", "", "", 0.0, "", 0.0, 0, "", ""),
                CustomerMetaData(true, "", ""),
                UserType.STOCKIST.serverValue,
                "",
                "",
                "",
                "",
                true,
                "",
                "Test",
                "",
                "User",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
            ),
            true
        )
    }
}
