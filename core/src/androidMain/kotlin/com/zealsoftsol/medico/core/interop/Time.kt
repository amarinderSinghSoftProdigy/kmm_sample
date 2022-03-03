package com.zealsoftsol.medico.core.interop

actual object Time {
    actual val now: Long
        get() = System.currentTimeMillis()

    actual val endTime: Long
        get() = System.currentTimeMillis() + 31557600000
}