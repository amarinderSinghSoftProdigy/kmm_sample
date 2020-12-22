package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import org.kodein.di.instance

object ScopeCreator {
    private inline val nav: Navigator
        get() = directDI.instance()

    object Shortcuts {

        /**
         * Shortcut to [LogInScope] with filled data
         */
        fun createLogInShortcut(
            phoneNumberOrEmail: String,
            type: AuthCredentials.Type,
            password: String,
            error: ErrorCode?
        ) {
            
            nav.setCurrentScope(
                LogInScope(
                    credentials = DataSource(
                        AuthCredentials(
                            phoneNumberOrEmail = phoneNumberOrEmail,
                            type = type,
                            password = password,
                    )),
                    errors = DataSource(error)
                )
            )

        }

        fun createLimitedAppAccessShortcut(
            firstName: String,
            lastName: String,
            type: UserType,
            isDocumentUploaded: Boolean
        ) {

            nav.setCurrentScope(
                MainScope.LimitedAccess(
                    user = DataSource(
                        User(
                            firstName,
                            lastName,
                            "email@example.com",
                            "+1234567890",
                            type,
                            false,
                            if (!isDocumentUploaded) null else "url"
                        )
                    )
                )
            )

        }

        /**
         * Shortcut to [SignUpScope.LegalDocuments.DrugLicense] with filled data
         */
        fun createUserNonSeasonBoyShortcut(
            userType: UserType,
            email: String,
            phone: String
        ) {
            require(userType != UserType.SEASON_BOY) { "season boy not allowed" }

            nav.setCurrentScope(
                SignUpScope.LegalDocuments.DrugLicense(
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
            )
        }

        /**
         * Shortcut to [SignUpScope.LegalDocuments.Aadhaar] with filled data
         */
        fun createUserSeasonBoyShortcut(email: String, phone: String) {
            nav.setCurrentScope(
                SignUpScope.LegalDocuments.Aadhaar(
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
            )
        }
    }

    object Dummy {

        fun legalDocumentsDrugLicense() {
            nav.setCurrentScope(
                SignUpScope.LegalDocuments.DrugLicense(
                    registrationStep1 = UserRegistration1(
                        phoneNumber = "",
                    ),
                    registrationStep2 = UserRegistration2(

                    ),
                    registrationStep3 = UserRegistration3(

                    ),
                )
            )
        }

        fun legalDocumentsAadhaar() {
            nav.setCurrentScope(
                SignUpScope.LegalDocuments.Aadhaar(
                    registrationStep1 = UserRegistration1(
                        phoneNumber = "",
                    ),
                    registrationStep2 = UserRegistration2(

                    ),
                    aadhaarData = DataSource(
                        AadhaarData(cardNumber = "", shareCode = "")
                    ),
                )
            )
        }

        fun limitedAccessMainScreen(isDocumentUploaded: Boolean) {
            nav.setCurrentScope(
                MainScope.LimitedAccess(
                    user = DataSource(
                        User(
                            "Test",
                            "User",
                            null,
                            "000",
                            UserType.STOCKIST,
                            false,
                            isDocumentUploaded,
                        )
                    )
                )
            )
        }

        fun fullAccessMainScreen() {
            nav.setCurrentScope(
                MainScope.FullAccess(
                    user = DataSource(
                        User(
                            "Test",
                            "User",
                            null,
                            "000",
                            UserType.STOCKIST,
                            true,
                            true,
                        )
                    )
                )
            )
        }
    }
}
