package com.zealsoftsol.medico.core.interop

import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

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

    fun createUserShortcut(email: String, phone: String) = SignUpScope.LegalDocuments.DrugLicense(
        UserRegistration1(
            "stockist",
            "Test",
            "User",
            email,
            phone,
            "Qwerty12345",
            "Qwerty12345",
        ),
        UserRegistration2(
            "520001",
            "qq",
            "Vijayawada",
            "Vijayawada (Urban)",
            "Krishna",
            "Andhra Pradesh",
        ),
        UserRegistration3(
            "q",
            "37AADCB2230M2ZR",
            "",
            "qq",
            "qq",
        )
    )

    fun limitedAccessMainScreen(isDocumentUploaded: Boolean) = MainScope.LimitedAccess(
        user = DataSource(
            User(
                "Test",
                "User",
                null,
                "000",
                UserType.STOCKIST,
                false,
                if (isDocumentUploaded) null else "url"
            )
        )
    )

    fun fullAccessMainScreen() = MainScope.FullAccess(
        user = DataSource(
            User(
                "Test",
                "User",
                null,
                "000",
                UserType.STOCKIST,
                true,
                null
            )
        )
    )
}