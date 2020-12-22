package com.zealsoftsol.medico.core.interop

actual object Time {
    actual val now: Long
        get() = System.currentTimeMillis()
}