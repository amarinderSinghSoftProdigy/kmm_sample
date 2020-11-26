package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation
import kotlinx.serialization.json.Json

class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val settings: Settings,
    private val phoneEmailVerifier: PhoneEmailVerifier,
) {
    val authState: AuthState
        get() = if (settings.hasKey(AUTH_USER_KEY)) AuthState.AUTHORIZED else AuthState.NOT_AUTHORIZED

    init {
        if (authState == AuthState.AUTHORIZED) {
            fetchUser()?.let {
                networkAuthScope.token = it.token
            }
        }
    }

    suspend fun login(authCredentials: AuthCredentials): Boolean {
        settings.putString(AUTH_ID_KEY, authCredentials.phoneNumberOrEmail)
        settings.putString(AUTH_PASS_KEY, authCredentials.password)
        return networkAuthScope.login(
            UserRequest(authCredentials.phoneNumberOrEmail, authCredentials.password)
        )?.let {
            networkAuthScope.token = it.token
            val json = Json.encodeToString(
                User.serializer(),
                User(
                    authCredentials.getEmail(),
                    authCredentials.getPhoneNumber().orEmpty(),
                    it.token,
                    "",
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

    suspend fun sendOtp(phoneNumber: String): Boolean {
        return networkAuthScope.sendOtp(phoneNumber.toServerFormat())
    }

    suspend fun submitOtp(phoneNumber: String, otp: String): Boolean {
        return networkAuthScope.verifyOtp(phoneNumber.toServerFormat(), otp)
    }

    suspend fun changePassword(phoneNumber: String, newPassword: String): Boolean {
        return networkAuthScope.changePassword(phoneNumber.toServerFormat(), newPassword)
    }

    suspend fun resendOtp(phoneNumber: String): Boolean {
        return networkAuthScope.retryOtp(phoneNumber.toServerFormat())
    }

    suspend fun signUpPartially(userRegistration: UserRegistration): UserValidation? {
        return when (userRegistration) {
            is UserRegistration1 -> networkAuthScope.signUpPart1(userRegistration)
            is UserRegistration2 -> networkAuthScope.signUpPart2(userRegistration)
            is UserRegistration3 -> networkAuthScope.signUpPart3(userRegistration)
        }
    }

    private fun fetchUser(): User? {
        val user = runCatching {
            Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
        }.getOrNull()
        if (user == null) "error fetching user".errorIt()
        return user
    }

    private inline fun String.toServerFormat() = replace("+", "").let {
        val leadingZeros = (12 - it.length).coerceAtLeast(0)
        buildString {
            repeat(leadingZeros) {
                append("0")
            }
            append(it)
        }
    }

    companion object {
        // TODO make secure
        private const val AUTH_ID_KEY = "auid"
        private const val AUTH_PASS_KEY = "apass"
        private const val AUTH_USER_KEY = "ukey"
    }
}