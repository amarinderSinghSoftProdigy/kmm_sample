package com.zealsoftsol.medico.core.interop

import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.Option

interface TypeExposer {
    fun getFileTypeCompanion(): FileType.Utils
    
    fun getUserDetailsAadhaar(): User.Details.Aadhaar
    fun getUserDetailsDrugLicense(): User.Details.DrugLicense

    fun getOptionStringValue(): Option.StringValue
    fun getOptionViewMore(): Option.ViewMore
}