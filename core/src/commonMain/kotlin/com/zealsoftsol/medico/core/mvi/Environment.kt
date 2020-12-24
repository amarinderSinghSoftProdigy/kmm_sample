package com.zealsoftsol.medico.core.mvi

internal var environment: Environment = Environment(
    otp = Environment.Otp(),
)

data class Environment internal constructor(
    val otp: Otp,
) {

    data class Otp(
        val resendTimer: Long = 1 * 60 * 1000L,
        val maxResendAttempts: Int = 3,
    )

    /**
     * Use only for testing, call override function from main thread only
     */
    companion object Override {

        fun otp(otp: Otp) {
            environment = environment.copy(otp = otp)
        }
    }
}

