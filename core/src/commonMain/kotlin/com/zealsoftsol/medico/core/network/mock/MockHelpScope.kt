package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.Response

class MockHelpScope : NetworkScope.Help {

    init {
        "USING MOCK HELP SCOPE".logIt()
    }

    override suspend fun getHelp(): Response.Wrapped<HelpData> = mockResponse {
        Response.Wrapped(null, false)
    }
}