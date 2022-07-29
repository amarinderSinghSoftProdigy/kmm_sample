package com.zealsoftsol.medico.core.extensions

import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.data.TokenInfo

internal inline val TokenInfo.isExpired: Boolean
    get() = Time.now > expiresAt