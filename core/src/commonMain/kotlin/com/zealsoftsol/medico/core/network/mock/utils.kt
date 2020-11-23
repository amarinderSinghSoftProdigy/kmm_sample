package com.zealsoftsol.medico.core.network.mock

import kotlinx.coroutines.delay

private const val DEFAULT_DELAY = 2000L

internal suspend fun mockBooleanResponse(delay: Long = DEFAULT_DELAY, value: Boolean = true) =
    mockResponse { value }

internal suspend fun <T> mockResponse(delay: Long = DEFAULT_DELAY, response: () -> T): T {
    delay(2000)
    return response()
}