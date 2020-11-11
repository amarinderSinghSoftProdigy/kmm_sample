package com.zealsoftsol.medico.core.interop

import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.isFrozen

actual fun Any.ensureNeverFrozen() = ensureNeverFrozen()

actual val Any.isFrozen: Boolean
    get() = isFrozen