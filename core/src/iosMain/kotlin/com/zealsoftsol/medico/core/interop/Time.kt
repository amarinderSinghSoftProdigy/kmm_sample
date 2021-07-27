package com.zealsoftsol.medico.core.interop

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object Time {
    actual val now: Long
        get() = NSDate().timeIntervalSince1970.toLong() * 1000
}