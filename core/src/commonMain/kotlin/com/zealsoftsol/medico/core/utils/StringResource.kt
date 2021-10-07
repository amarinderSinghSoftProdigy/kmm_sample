package com.zealsoftsol.medico.core.utils

sealed class StringResource {

    data class Static(val id: String) : StringResource()
    data class Raw(val string: String?) : StringResource()
}