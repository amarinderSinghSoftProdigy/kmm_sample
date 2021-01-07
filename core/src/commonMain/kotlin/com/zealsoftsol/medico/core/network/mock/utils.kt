package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.mvi.environment
import kotlinx.coroutines.delay

internal suspend fun <T> mockResponse(
    delay: Long = environment.requireMocks().delay,
    response: () -> T,
): T {
    delay(delay)
    return response()
}