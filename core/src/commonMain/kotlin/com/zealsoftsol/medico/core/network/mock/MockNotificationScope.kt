package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope

class MockNotificationScope : NetworkScope.Notification {

    init {
        "USING MOCK NOTIFICATION SCOPE".logIt()
    }

    override suspend fun sendFirebaseToken(token: String): Boolean = mockResponse { true }
}