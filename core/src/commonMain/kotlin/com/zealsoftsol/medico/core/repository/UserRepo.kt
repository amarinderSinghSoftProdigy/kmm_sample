package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AuthCredentials

class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val settings: Settings
) {

    fun getAuthCredentials(): AuthCredentials {
        return AuthCredentials(
            settings.getString(AUTH_ID_KEY, ""),
            settings.getString(AUTH_PASS_KEY, "")
        )
    }

    companion object {
        private const val AUTH_ID_KEY = "auid"
        // TODO make secure
        private const val AUTH_PASS_KEY = "apass"
    }
}