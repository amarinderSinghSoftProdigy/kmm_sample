package com.zealsoftsol.medico.core.interop

actual fun Any.ensureNeverFrozen() {
}

actual val Any.isFrozen: Boolean
    get() = false