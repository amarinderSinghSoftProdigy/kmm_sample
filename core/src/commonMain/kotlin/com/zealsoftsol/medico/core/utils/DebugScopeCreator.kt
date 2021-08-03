package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WelcomeScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.Expiry
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.StockInfo
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.data.Subscription
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.di.instance

/**
 * For debug purposes only! For testing use [BaseTestScope]
 */
object DebugScopeCreator {
    private inline val nav: Navigator
        get() = directDI.instance()

    fun signUpSelectUserType() {
        nav.setScope(SignUpScope.SelectUserType.get())
    }

    fun signUpAddressData(
        userType: UserType,
        email: String,
        phone: String,
    ) {
        nav.setScope(SignUpScope.SelectUserType.get())
        nav.setScope(
            SignUpScope.AddressData(
                UserRegistration1(
                    userType.serverValue,
                    "Test",
                    "User",
                    email,
                    phone,
                    "Qwerty12345",
                    "Qwerty12345",
                ),
                DataSource(null),
                DataSource(UserRegistration2()),
            )
        )
    }

    fun signUpDetailsNonSeasonBoy(
        userType: UserType,
        email: String,
        phone: String,
    ) {
        require(userType != UserType.SEASON_BOY) { "season boy not allowed" }

        nav.setScope(SignUpScope.SelectUserType.get())
        nav.setScope(
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
        nav.setScope(SignUpScope.SelectUserType.get())
        nav.setScope(
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

    fun signUpUploadDrugLicense(
        userType: UserType,
        email: String,
        phone: String,
    ) {
        require(userType != UserType.SEASON_BOY) { "season boy not allowed" }

        nav.setScope(SignUpScope.SelectUserType.get())
        nav.setScope(
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

    fun signUpUploadAadhaar(email: String, phone: String) {
        signUpDetailsSeasonBoy(email, phone)
        nav.setScope(
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

    fun signUpWelcomeScreen() {
        nav.setScope(WelcomeScope(testUser.fullName()))
    }

    fun limitedAccessMainScreen(
        type: UserType,
        isDocumentUploaded: Boolean
    ) {
        val user = testUser.copy(
            type = type,
            details = if (type == UserType.SEASON_BOY) User.Details.Aadhaar(
                "",
                ""
            ) else User.Details.DrugLicense("", "", "", "", "url"),
            isActivated = false,
            isDocumentUploaded = isDocumentUploaded
        )
        nav.setScope(
            LimitedAccessScope.get(
                user,
                ReadOnlyDataSource(MutableStateFlow(user)),
            )
        )
    }

    fun dashboardScreen() {
        nav.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
        nav.setScope(
            DashboardScope.get(
                testUser,
                ReadOnlyDataSource(MutableStateFlow(testUser)),
                ReadOnlyDataSource(MutableStateFlow(null)),
                ReadOnlyDataSource(MutableStateFlow(0)),
                ReadOnlyDataSource(MutableStateFlow(0)),
            )
        )
    }

    fun searchQueryScreen() {
        nav.setScope(SearchScope())
    }

    fun productScreen() {
        nav.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
        nav.setScope(
            ProductInfoScope(
                product = ProductSearch(
                    code = "VD000307",
                    formattedPrice = "₹114.78",
                    marginPercent = "20%",
                    id = "VPR001560",
                    manufacturer = "Abbott",
//                    manufacturerId = "MA000021",
                    name = "Delok 30 Capsule DR",
                    shortName = "Delok 30 Capsule DR",
                    uomName = "strip of 10 Capsule DR",
                    buyingOption = BuyingOption.BUY,
                    compositions = listOf("Duloxetine 30mg"),
                    formattedMrp = "211.84",
//                    productCategoryName = "",
                    stockInfo = StockInfo(
                        1,
                        Expiry(0, "", "#FF00FF"),
                        "In Stock",
                        StockStatus.IN_STOCK,
                    ),
                    standardUnit = "1x20",
                    sellerInfo = null,
                ),
                alternativeBrands = listOf(
                    AlternateProductData(
                        baseProductName = "code",
                        query = "query",
                        name = "Augmentin 626 Duo Tablet",
                        priceRange = "from ₹ 110.23 to ₹ 120.99",
                        manufacturerName = "Company Name",
                        availableVariants = "10 variants",
                    )
                ),
                isDetailsOpened = DataSource(false),
            )
        )
    }

    fun allStockistsList() {
        nav.setScope(ManagementScope.User.Stockist())
    }
}

private inline val testUser
    get() = User(
        "Test",
        "User",
        "test@mail.com",
        "000",
        "unitcode",
        UserType.STOCKIST,
        User.Details.DrugLicense("", "", "", "", "url"),
        true,
        true,
        AddressData("", "", "", "", 0.0, 0.0, "", 0, "", ""),
        Subscription(Subscription.Type.TRIAL, "valid untill some time", Time.now),
    )
