package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.BaseScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType

object ScopeCreator {

    object Shortcuts {

        /**
         * Shortcut to [SignUpScope.LegalDocuments.DrugLicense] with filled data
         */
        fun createUserNonSeasonBoyShortcut(
            userType: UserType,
            email: String,
            phone: String
        ): BaseScope {
            require(userType != UserType.SEASON_BOY) { "season boy not allowed" }
            return SignUpScope.LegalDocuments.DrugLicense(
                UserRegistration1(
                    userType.serverValue,
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
        }

        /**
         * Shortcut to [SignUpScope.LegalDocuments.Aadhaar] with filled data
         */
        fun createUserSeasonBoyShortcut(email: String, phone: String): BaseScope {
            return SignUpScope.LegalDocuments.Aadhaar(
                UserRegistration1(
                    UserType.SEASON_BOY.serverValue,
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
                DataSource(
                    AadhaarData(
                        "887489598799",
                        "1111",
                    )
                )
            )
        }
    }

    object Dummy {

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
}