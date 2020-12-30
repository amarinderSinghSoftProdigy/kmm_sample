package com.zealsoftsol.medico.core.network.mock

import kotlinx.coroutines.delay

private const val DEFAULT_DELAY = 1000L

internal suspend fun <T> mockResponse(delay: Long = DEFAULT_DELAY, response: () -> T): T {
    delay(delay)
    return response()
}