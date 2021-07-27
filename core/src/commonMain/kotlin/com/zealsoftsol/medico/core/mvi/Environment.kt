package com.zealsoftsol.medico.core.mvi

internal var environment: Environment = Environment(
    otp = Environment.Otp(),
    mocks = Environment.Mocks(),
)

data class Environment internal constructor(
    val otp: Otp,
    val mocks: Mocks?,
) {

    fun requireMocks() = requireNotNull(mocks) { "mocks not configured in environment" }

    data class Otp(
        val resendTimer: Long = 1 * 60 * 1000L,
        val maxResendAttempts: Int = 3,
    )

    data class Mocks(
        val delay: Long = 1000L,
    )

    /**
     * Use only for testing, call override function from main thread only
     */
    companion object Override {

        fun otp(otp: Otp) {
            environment = environment.copy(otp = otp)
        }

        fun mocks(mocks: Mocks?) {
            environment = environment.copy(mocks = mocks)
        }
    }
}

