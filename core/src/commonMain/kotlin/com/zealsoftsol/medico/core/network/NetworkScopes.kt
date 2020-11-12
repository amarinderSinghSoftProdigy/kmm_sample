package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.data.UserInfo
import com.zealsoftsol.medico.data.UserRequest

interface NetworkScope {
    var token: String?
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): UserInfo?
        suspend fun logout(): Boolean
    }
}