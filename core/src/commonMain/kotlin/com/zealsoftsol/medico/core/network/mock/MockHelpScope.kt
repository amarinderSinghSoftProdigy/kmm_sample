package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.HelpData

class MockHelpScope : NetworkScope.Help {

    init {
        "USING MOCK HELP SCOPE".logIt()
    }

    override suspend fun getHelp() = mockResponse<HelpData> {
        null
    }
}