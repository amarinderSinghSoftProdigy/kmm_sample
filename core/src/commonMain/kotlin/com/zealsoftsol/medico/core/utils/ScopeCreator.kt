package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
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

/**
 * For debug purposes only! For testing use [BaseTestScope]
 */
@Deprecated("rename to DebugScopeCreator")
object ScopeCreator {
    private inline val nav: Navigator
        get() = directDI.instance()

    @Deprecated("remove")
    object Shortcuts {

        /**
         * Shortcut to [LogInScope] with filled data
         */
        @Deprecated("remove, use test scopes")
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

        /**
         * Shortcut to [OtpScope.PhoneNumberInput] with filled data
         */
        @Deprecated("remove, use test scopes")
        fun createOtpPhoneNumberInputShortcut(
            phoneNumber: String,
            error: ErrorCode?
        ) {
            nav.setCurrentScope(
                OtpScope.PhoneNumberInput(
                    phoneNumber = DataSource(phoneNumber),
                    isForRegisteredUsersOnly = true,
                    errors = DataSource(error)
                )
            )
        }

        /**
         * Shortcut to [MainScope.LimitedAccess] with filled data
         */
        @Deprecated("leave only type and document upload fields, rename to limitedAppAccess")
        fun createLimitedAppAccessShortcut(
            firstName: String,
            lastName: String,
            type: UserType,
            isDocumentUploaded: Boolean
        ) {
            nav.setCurrentScope(
                MainScope.LimitedAccess(
                    user = DataSource(
                        testUser.copy(
                            type = type,
                            isVerified = false,
                            isDocumentUploaded = isDocumentUploaded
                        )
                    )
                )
            )
        }
    }

    fun uploadDrugLicense(
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

    fun uploadAadhaar(email: String, phone: String) {
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

    fun welcomeScreen() {
        nav.setCurrentScope(SignUpScope.Welcome(testUser.fullName()))
    }

    fun fullAccessMainScreen() {
        nav.setCurrentScope(
            MainScope.FullAccess(
                user = DataSource(testUser)
            )
        )
    }
}

private inline val testUser
    get() = User(
        "Test",
        "User",
        "test@mail.com",
        "000",
        UserType.STOCKIST,
        true,
        true,
    )
