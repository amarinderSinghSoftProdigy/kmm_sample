package com.zealsoftsol.medico.core.storage

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.data.TokenInfo
import kotlinx.serialization.json.Json

class TokenStorage(private val settings: Settings) {

    private var token: TokenInfo? = runCatching {
        Json.decodeFromString(TokenInfo.serializer(), settings.getString(TOKEN_KEY))
    }.getOrNull()
    private val tempTokenMap = hashMapOf<String, TokenInfo>()

    fun getMainToken(): TokenInfo? = token

    fun getTempTokenOnce(id: String): TokenInfo? = tempTokenMap.remove(id)

    fun saveMainToken(tokenInfo: TokenInfo) {
        token = tokenInfo
        settings.putString(TOKEN_KEY, Json.encodeToString(TokenInfo.serializer(), tokenInfo))
    }

    fun saveTempToken(id: String, tokenInfo: TokenInfo) {
        tempTokenMap[id] = tokenInfo
    }

    fun clear() {
        token = null
        tempTokenMap.clear()
        settings.remove(TOKEN_KEY)
    }

    companion object {
        private const val TOKEN_KEY = "atok"
    }
}