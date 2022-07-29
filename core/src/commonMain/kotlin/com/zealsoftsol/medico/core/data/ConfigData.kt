package com.zealsoftsol.medico.core.data

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(val sessionTimeout: Long = 1000 * 60 * 480)