package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class AutoApprove(
    val autoApprove: FormattedData<Boolean>
)
