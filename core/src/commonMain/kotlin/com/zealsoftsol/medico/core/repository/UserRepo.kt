package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRequest
import kotlinx.serialization.json.Json

class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val settings: Settings,
    private val phoneEmailVerifier: PhoneEmailVerifier,
) {
    val isLoggedIn: Boolean
        get() = settings.hasKey(AUTH_USER_KEY)

    init {
        if (isLoggedIn) {
            val user = Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
            networkAuthScope.token = user.token.log("token")
        }
    }

    suspend fun login(authCredentials: AuthCredentials): Boolean {
        settings.putString(AUTH_ID_KEY, authCredentials.phoneNumberOrEmail)
        settings.putString(AUTH_PASS_KEY, authCredentials.password)
        return networkAuthScope.login(
            UserRequest(authCredentials.phoneNumberOrEmail, authCredentials.password).log("request")
        ).log("response")?.let {
            networkAuthScope.token = it.token
            val json = Json.encodeToString(
                User.serializer(),
                User(
                    authCredentials.getEmail(),
                    authCredentials.getPhoneNumber().orEmpty(),
                    it.token,
                    it.userType,
                    authCredentials.password
                )
            )
            settings.putString(AUTH_USER_KEY, json)
            true
        } ?: false
    }

    suspend fun logout(): Boolean {
        return networkAuthScope.logout().also { isSuccess ->
            if (isSuccess) {
                settings.remove(AUTH_USER_KEY)
                networkAuthScope.token = null
            }
        }
    }

    fun getAuthCredentials(): AuthCredentials {
        val id = settings.getString(AUTH_ID_KEY, "")
        return AuthCredentials(
            id,
            phoneEmailVerifier.verify(id),
            settings.getString(AUTH_PASS_KEY, ""),
        )
    }

    fun updateAuthCredentials(current: AuthCredentials, emailOrPhone: String, password: String): AuthCredentials {
        return current.copy(
            emailOrPhone,
            phoneEmailVerifier.verify(emailOrPhone),
            password,
        )
    }

    companion object {
        // TODO make secure
        private const val AUTH_ID_KEY = "auid"
        private const val AUTH_PASS_KEY = "apass"
        private const val AUTH_USER_KEY = "ukey"
    }
}