package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.CustomerAddressData
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerMetaData
import com.zealsoftsol.medico.data.Response

class MockCustomerScope : NetworkScope.Customer {

    override var token: String? = null

    init {
        "USING MOCK CUSTOMER SCOPE".logIt()
    }

    override fun clearToken() {

    }

    override suspend fun getCustomerData(): Response.Wrapped<CustomerData> = mockResponse {
        Response.Wrapped(
            CustomerData(
                "",
                CustomerAddressData("", "", "", 0.0, "", 0.0, 0, "", ""),
                CustomerMetaData(true, "", ""),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
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