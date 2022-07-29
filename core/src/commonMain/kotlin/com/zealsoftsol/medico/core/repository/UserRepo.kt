package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.core.data.AnyResponse
import com.zealsoftsol.medico.core.data.AuthCredentials
import com.zealsoftsol.medico.core.data.BodyResponse
import com.zealsoftsol.medico.core.data.ConfigData
import com.zealsoftsol.medico.core.data.HeaderData
import com.zealsoftsol.medico.core.data.TokenInfo
import com.zealsoftsol.medico.core.data.UserRequest
import kotlinx.coroutines.flow.MutableStateFlow


class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val bottomSheetStore: NetworkScope.BottomSheetStore,
    private val settings: Settings,
    private val tokenStorage: TokenStorage,
    private val ipAddressFetcher: IpAddressFetcher,
) {

    /*val userFlow: MutableStateFlow<User?> = MutableStateFlow(
        runCatching {
            Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
        }.getOrNull()
    )
    val userV2Flow: MutableStateFlow<UserV2?> = MutableStateFlow(
        runCatching {
            Json.decodeFromString(UserV2.serializer(), settings.getString(AUTH_USER_KEY_V2))
        }.getOrNull()
    )*/
    val configFlow: MutableStateFlow<ConfigData> = MutableStateFlow(ConfigData())

    fun getUserAccess(): UserAccess {
        return /*userV2Flow.value?.let {
            if (it.isActivated)*/ UserAccess.NO_ACCESS /*else UserAccess.LIMITED_ACCESS
        } ?: UserAccess.NO_ACCESS*/
    }

    fun saveAlertToggle(value: Boolean) {
        settings.putBoolean(ALERT_TOGGLE, value)
    }

    fun getAlertToggle(): Boolean {
        return settings.getBoolean(ALERT_TOGGLE, true)
    }

    suspend fun login(login: String, password: String): BodyResponse<TokenInfo> {
        settings.putString(AUTH_LOGIN_KEY, login)
        return networkAuthScope.login(
            UserRequest(
                if (login.contains("@")) login else login.formatIndia(),
                password
            )
        )
    }

    suspend fun logout(): AnyResponse {
        return networkAuthScope.logout().also {
            if (it.isSuccess) clear()
        }
    }

    fun clear() {
        clearUserData()
    }

    fun getAuthCredentials(): AuthCredentials {
        val login = settings.getString(AUTH_LOGIN_KEY, "")
        return AuthCredentials(
            login,
            "",
        )
    }

    fun updateAuthCredentials(
        current: AuthCredentials,
        emailOrPhone: String,
        password: String
    ): AuthCredentials {
        return current.copy(
            emailOrPhone,
            password,
        )
    }

    suspend fun checkCanResetPassword(phoneNumber: String): AnyResponse {
        return networkAuthScope.checkCanResetPassword(phoneNumber.formatIndia())
    }

    suspend fun sendOtp(phoneNumber: String): AnyResponse {
        return networkAuthScope.sendOtp(phoneNumber.formatIndia())
    }

    suspend fun submitOtp(phoneNumber: String, otp: String): BodyResponse<TokenInfo> {
        return networkAuthScope.verifyOtp(phoneNumber.formatIndia(), otp)
    }

    suspend fun resendOtp(phoneNumber: String): AnyResponse {
        return networkAuthScope.retryOtp(phoneNumber.formatIndia())
    }

    suspend fun getBottomSheetDetails(item: String): BodyResponse<HeaderData> {
        return bottomSheetStore.getDetails(item)
    }

    private fun clearUserData() {
        settings.remove(AUTH_USER_KEY)
        settings.remove(AUTH_USER_KEY_V2)
        settings.remove(LOCAL_SEARCH)
        tokenStorage.clear()
    }

    enum class UserAccess {
        FULL_ACCESS, NO_ACCESS
    }

    companion object {
        // TODO make secure
        const val ALERT_TOGGLE = "ALERT_TOGGLE"
        private const val AUTH_LOGIN_KEY = "auid"
        private const val AUTH_USER_KEY = "ukey"
        private const val AUTH_USER_KEY_V2 = "ukeyV2"
        private const val LOCAL_SEARCH = "local_search"
    }
}

private inline fun String.formatIndia() = "91$this"