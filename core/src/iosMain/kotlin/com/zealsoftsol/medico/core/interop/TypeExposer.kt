package com.zealsoftsol.medico.core.interop

import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.User

interface TypeExposer {
    fun getFileTypeCompanion(): FileType.Utils

    fun getFilterCompanion(): Filter.Ids
    
    fun getUserDetailsAadhaar(): User.Details.Aadhaar
    fun getUserDetailsDrugLicense(): User.Details.DrugLicense
}