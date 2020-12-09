package com.zealsoftsol.medico.core.interop

import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3

class ScopeCreator {
    fun legalDocumentsDrugLicense() = SignUpScope.LegalDocuments.DrugLicense(
        registrationStep1 = UserRegistration1(
            phoneNumber = "",
        ),
        registrationStep2 = UserRegistration2(

        ),
        registrationStep3 = UserRegistration3(

        ),
    )

    fun legalDocumentsAadhaar() = SignUpScope.LegalDocuments.Aadhaar(
        registrationStep1 = UserRegistration1(
            phoneNumber = "",
        ),
        registrationStep2 = UserRegistration2(

        ),
        aadhaarData = DataSource(
            AadhaarData(cardNumber = "", shareCode = "")
        ),
    )
}