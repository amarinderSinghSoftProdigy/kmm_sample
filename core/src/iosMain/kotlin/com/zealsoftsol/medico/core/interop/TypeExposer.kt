package com.zealsoftsol.medico.core.interop

import com.zealsoftsol.medico.core.data.FileType

interface TypeExposer {
    fun getFileTypeCompanion(): FileType.Utils
}