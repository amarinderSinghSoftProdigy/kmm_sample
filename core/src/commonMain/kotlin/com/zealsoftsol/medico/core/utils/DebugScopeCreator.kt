package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.ProductData
import com.zealsoftsol.medico.data.CompositionsData
import com.zealsoftsol.medico.data.CodeName
import com.zealsoftsol.medico.data.MiniProductData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import org.kodein.di.instance

/**
 * For debug purposes only! For testing use [BaseTestScope]
 */
object DebugScopeCreator {
    private inline val nav: Navigator
        get() = directDI.instance()

    fun signUpDetailsNonSeasonBoy(
        userType: UserType,
        email: String,
        phone: String,
    ) {
        require(userType != UserType.SEASON_BOY) { "season boy not allowed" }

        nav.setCurrentScope(
            SignUpScope.Details.TraderData(
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
            )
        )
    }

    fun signUpDetailsSeasonBoy(
        email: String,
        phone: String,
    ) {
        nav.setCurrentScope(
            SignUpScope.Details.Aadhaar(
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
            )
        )
    }

    fun uploadDrugLicense(
        userType: UserType,
        email: String,
        phone: String,
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
                AadhaarData(
                    "887489598799",
                    "1111",
                ),
            )
        )
    }

    fun welcomeScreen() {
        nav.setCurrentScope(SignUpScope.Welcome(testUser.fullName()))
    }

    fun limitedAccessMainScreen(
        type: UserType,
        isDocumentUploaded: Boolean
    ) {
        nav.setCurrentScope(
            MainScope.LimitedAccess.from(
                testUser.copy(
                    type = type,
                    details = if (type == UserType.SEASON_BOY) User.Details.Aadhaar("", "") else User.Details.DrugLicense("url"),
                    isVerified = false,
                    isDocumentUploaded = isDocumentUploaded
                )
            )
        )
    }

    fun dashboardScreen() {
        nav.setCurrentScope(
            MainScope.Dashboard(
                user = DataSource(testUser)
            )
        )
    }

    fun searchQueryScreen() {
        nav.setCurrentScope(SearchScope())
    }

    fun productScreen() {
        nav.setCurrentScope(
            MainScope.ProductInfo(
                user = DataSource(testUser),
                product = ProductData(
                    active = true, 
                    code = "VD000307", 
                    compositionsData = listOf(CompositionsData(composition=CodeName(code="CC001561", name="Duloxetine"), 
                    strength=CodeName(code="CST000286", name="30 mg"))), 
                    drugTypeData = CodeName(code="DC000022", name="Capsule DR"), 
                    formattedPrice = "₹114.78", 
                    hsnCode = "3001", 
                    hsnPercentage = "0.0", 
                    ptr = "20.90", 
                    id = "VPR001560", 
                    isPrescriptionRequired = false, 
                    manufacturer = CodeName(code="MA000021", name="Abbott"),
                    medicineId = "MX7LLZ", 
                    mfgDivision = "", 
                    mrp = 145.1, 
                    name = "Delok 30 Capsule DR", 
                    price = 114.78, 
                    miniProductData = MiniProductData(code="PR001559", 
                    manufacture=CodeName(code="MNF001459", name="Abbott"), name="Delok"), 
                    score=0.0, 
                    shortName="Delok 30 Capsule DR", 
                    standardUnit="10", 
                    unitOfMeasureData=CodeName(code="US000058", name="strip of 10 Capsule DR")),
                alternativeBrands = listOf("a")
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
        User.Details.DrugLicense("url"),
        true,
        true,
    )
