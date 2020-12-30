package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource

abstract class BaseScope {
    val isInProgress: DataSource<Boolean> = DataSource(false)
    val queueId: String = this::class.qualifiedName.orEmpty()

    object Root : BaseScope()
}