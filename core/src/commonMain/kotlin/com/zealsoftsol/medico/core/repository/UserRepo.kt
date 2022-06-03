package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AnyResponse
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BannerData
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.BrandsData
import com.zealsoftsol.medico.data.ConfigData
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerDataV2
import com.zealsoftsol.medico.data.DealsData
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.EmployeeBannerData
import com.zealsoftsol.medico.data.HeaderData
import com.zealsoftsol.medico.data.LicenseDocumentData
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManufacturerData
import com.zealsoftsol.medico.data.OffersData
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.ProfileImageData
import com.zealsoftsol.medico.data.ProfileImageUpload
import com.zealsoftsol.medico.data.ProfileResponseData
import com.zealsoftsol.medico.data.RecentProductInfo
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.StockStatusData
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UploadResponseData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRegistration4
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserV2
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.ValidationResponse
import com.zealsoftsol.medico.data.WhatsappData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val networkSignUpScope: NetworkScope.SignUp,
    private val networkPasswordScope: NetworkScope.Password,
    private val networkCustomerScope: NetworkScope.Customer,
    private val networkNotificationScope: NetworkScope.Notification,
    private val networkConfigScope: NetworkScope.Config,
    private val whatsappPreferenceScope: NetworkScope.WhatsappStore,
    private val profileImageScope: NetworkScope.ProfileImage,
    private val bottomSheetStore: NetworkScope.BottomSheetStore,
    private val settings: Settings,
    private val tokenStorage: TokenStorage,
    private val ipAddressFetcher: IpAddressFetcher,
) {

    private var cachedFirebaseToken: String? = null

    val userFlow: MutableStateFlow<User?> = MutableStateFlow(
        runCatching {
            Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
        }.getOrNull()
    )
    val userV2Flow: MutableStateFlow<UserV2?> = MutableStateFlow(
        runCatching {
            Json.decodeFromString(UserV2.serializer(), settings.getString(AUTH_USER_KEY_V2))
        }.getOrNull()
    )
    val configFlow: MutableStateFlow<ConfigData> = MutableStateFlow(ConfigData())
    val manufacturerFlow: MutableStateFlow<List<ManufacturerData>> = MutableStateFlow(emptyList())
    val stockDataFlow: MutableStateFlow<StockStatusData?> = MutableStateFlow(null)
    val recentProductFlow: MutableStateFlow<RecentProductInfo?> = MutableStateFlow(null)
    val promotionDataFlow: MutableStateFlow<List<OffersData>> = MutableStateFlow(emptyList())
    val bannerFlow: MutableStateFlow<List<BannerData>> = MutableStateFlow(emptyList())
    val brandsFlow: MutableStateFlow<List<BrandsData>> = MutableStateFlow(emptyList())
    val dealsFlow: MutableStateFlow<List<DealsData>> = MutableStateFlow(emptyList())
    val categoriesFlow: MutableStateFlow<List<BrandsData>> = MutableStateFlow(emptyList())
    val stockistEmployeeBannerFlow: MutableStateFlow<List<EmployeeBannerData>> = MutableStateFlow(emptyList())

    fun getUserAccess(): UserAccess {
        return userV2Flow.value?.let {
            if (it.isActivated) UserAccess.FULL_ACCESS else UserAccess.LIMITED_ACCESS
        } ?: UserAccess.NO_ACCESS
    }

    suspend fun login(login: String, password: String): BodyResponse<TokenInfo> {
        settings.putString(AUTH_LOGIN_KEY, login)
        return networkAuthScope.login(
            UserRequest(
                if (login.contains("@")) login else login.formatIndia(),
                password
            )
        )
    }

    suspend fun loadUserFromServerV2(): BodyResponse<CustomerDataV2> {
        val result = networkCustomerScope.getCustomerDataV2()
        userV2Flow.value = result.getBodyOrNull()?.let {
            val parsedType = UserType.parse(it.customerType) ?: run {
                "unknown user type".warnIt()
                return@let null
            }
            if (it.unitCode == null || it.metaData == null) {
                throw UnsupportedOperationException("can not create user without unitCode or customerMetaData")
            }
            val user = UserV2(
                it.unitCode!!,
                parsedType,
                it.metaData!!.activated,
                it.latitude,
                it.longitude,
                it.name,
                it.tradeName
            )
            val json = Json.encodeToString(UserV2.serializer(), user)
            settings.putString(AUTH_USER_KEY_V2, json)
            user
        }
        return result
    }

    suspend fun loadUserFromServer(): BodyResponse<CustomerData> {
        val result = networkCustomerScope.getCustomerData()
        userFlow.value = result.getBodyOrNull()?.let {
            val parsedType = UserType.parse(it.customerType) ?: run {
                "unknown user type".warnIt()
                return@let null
            }
            if (it.unitCode == null || it.metaData == null) {
                throw UnsupportedOperationException("can not create user without unitCode or customerMetaData")
            }
            val user = User(
                it.firstName,
                it.lastName,
                it.email,
                it.phoneNumber,
                it.unitCode!!,
                parsedType,
                when (parsedType) {
                    UserType.SEASON_BOY -> User.Details.Aadhaar(it.aadhaarCardNo!!, "")
                    else -> User.Details.DrugLicense(
                        it.tradeName,
                        it.gstin!!,
                        it.panNumber ?: "",
                        it.drugLicenseNo1!!,
                        it.drugLicenseNo2!!,
                        it.drugLicenseUrl,
                        it.dlExpiryDate,
                        it.flExpiryDate,
                        it.foodLicense
                    )
                },
                it.metaData!!.activated,
                it.isDocumentUploaded,
                it.addressData,
                it.subscription,
            )
            val json = Json.encodeToString(User.serializer(), user)
            settings.putString(AUTH_USER_KEY, json)
            user
        }
        return result
    }

    suspend fun loadConfig() {
        networkConfigScope.getConfig().onSuccess {
            configFlow.value = it
        }
    }

    /**
     * load dashboard data based on User Type
     */
    suspend fun loadDashboard() {

        val userType = requireUser().type

        if (userType == UserType.STOCKIST) {
            networkCustomerScope.getDashboardManufacturers(userType).onSuccess {
                manufacturerFlow.value = it.results
            }

            networkCustomerScope.getStockStatusData(userType).onSuccess {
                stockDataFlow.value = it
            }
            networkCustomerScope.getRecentProducts(userType).onSuccess {
                recentProductFlow.value = it
            }
            networkCustomerScope.getPromotionData(userType).onSuccess {
                promotionDataFlow.value = it.results
            }
        } else if (userType == UserType.RETAILER || userType == UserType.HOSPITAL) {
            networkCustomerScope.getBannerData(userType).onSuccess {
                bannerFlow.value = it.results
            }
            networkCustomerScope.getBrandsData(userType).onSuccess {
                brandsFlow.value = it.results
            }
            networkCustomerScope.getCategoriesData(userType).onSuccess {
                categoriesFlow.value = it.results
            }
            networkCustomerScope.getDealsOfTheDay(userType).onSuccess {
                dealsFlow.value = it.results
            }
        } else if (userType == UserType.STOCKIST_EMPLOYEE) {
            networkCustomerScope.getStockistEmployeeBannerData(userType).onSuccess {
                stockistEmployeeBannerFlow.value = it
            }
        }
    }

    suspend fun logout(): AnyResponse {
        return networkAuthScope.logout().also {
            if (it.isSuccess) clear()
        }
    }

    fun clear() {
        clearUserData()
        userFlow.value = null
        manufacturerFlow.value = emptyList()
    }

    fun getAuthCredentials(): AuthCredentials {
        val login = settings.getString(AUTH_LOGIN_KEY, "")
        return AuthCredentials(
            login,
            "",
        )
    }

    fun updateAuthCredentials(
        current: AuthCredentials,
        emailOrPhone: String,
        password: String
    ): AuthCredentials {
        return current.copy(
            emailOrPhone,
            password,
        )
    }

    suspend fun checkCanResetPassword(phoneNumber: String): AnyResponse {
        return networkAuthScope.checkCanResetPassword(phoneNumber.formatIndia())
    }

    suspend fun sendOtp(phoneNumber: String): AnyResponse {
        return networkAuthScope.sendOtp(phoneNumber.formatIndia())
    }

    suspend fun submitOtp(phoneNumber: String, otp: String): BodyResponse<TokenInfo> {
        return networkAuthScope.verifyOtp(phoneNumber.formatIndia(), otp)
    }

    suspend fun verifyPassword(password: String): ValidationResponse<PasswordValidation> {
        return networkPasswordScope.verifyPassword(password)
    }

    suspend fun changePassword(
        phoneNumber: String?,
        newPassword: String,
    ): ValidationResponse<PasswordValidation> {
        return networkPasswordScope.changePassword(phoneNumber, newPassword)
    }

    suspend fun resendOtp(phoneNumber: String): AnyResponse {
        return networkAuthScope.retryOtp(phoneNumber.formatIndia())
    }

    suspend fun signUpValidation1(userRegistration1: UserRegistration1): ValidationResponse<UserValidation1> {
        return networkSignUpScope.signUpValidation1(userRegistration1)
    }

    suspend fun signUpValidation2(userRegistration2: UserRegistration2): ValidationResponse<UserValidation2> {
        return networkSignUpScope.signUpValidation2(userRegistration2)
    }

    suspend fun signUpValidation3(userRegistration3: UserRegistration3): ValidationResponse<UserValidation3> {
        return networkSignUpScope.signUpValidation3(userRegistration3)
    }

    suspend fun upoladDocument(uploadData: LicenseDocumentData): BodyResponse<UploadResponseData> {
        return networkSignUpScope.uploadDocument(uploadData)
    }

    suspend fun getBottomSheetDetails(item: String): BodyResponse<HeaderData> {
        return bottomSheetStore.getDetails(item)
    }

    @Deprecated("move to separate network scope")
    suspend fun getLocationData(pincode: String): Response<LocationData, PincodeValidation> {
        return networkSignUpScope.getLocationData(pincode)
    }

    suspend fun signUpNonSeasonBoy(
        userRegistration1: UserRegistration1,
        userRegistration2: UserRegistration2,
        userRegistration3: UserRegistration3,
        userRegistration4: UserRegistration4,
    ): AnyResponse {
        return networkSignUpScope.signUp(
            SubmitRegistration.nonSeasonBoy(
                userRegistration1,
                userRegistration2,
                userRegistration3,
                userRegistration4,
                ipAddressFetcher.getIpAddress().orEmpty(),
            )
        )
    }

    suspend fun signUpSeasonBoy(
        userRegistration1: UserRegistration1,
        userRegistration2: UserRegistration2,
        aadhaarData: AadhaarData,
        aadhaar: String?,
    ): AnyResponse {
        return networkSignUpScope.signUp(
            SubmitRegistration.seasonBoy(
                userRegistration1,
                userRegistration2,
                aadhaarData,
                aadhaar,
                ipAddressFetcher.getIpAddress().orEmpty(),
            )
        )
    }

    suspend fun uploadAadhaar(
        aadhaar: AadhaarData,
        fileString: String,
        email: String,
        phoneNumber: String
    ): AnyResponse {
        return networkSignUpScope.uploadAadhaar(
            AadhaarUpload(
                cardNumber = aadhaar.cardNumber,
                shareCode = aadhaar.shareCode,
                email = email,
                phoneNumber = phoneNumber,
                fileString = fileString,
            )
        )
    }

    suspend fun uploadDrugLicense(
        fileString: String,
        mimeType: String,
        phoneNumber: String,
        email: String,
    ): BodyResponse<StorageKeyResponse> {
        return networkSignUpScope.uploadDrugLicense(
            DrugLicenseUpload(
                phoneNumber = phoneNumber,
                email = email,
                fileString = fileString,
                mimeType = mimeType,
            )
        )
    }

    suspend fun verifyRetailer(registration3: UserRegistration3): ValidationResponse<UserValidation3> {
        return networkSignUpScope.verifyRetailerTraderDetails(registration3)
    }

    suspend fun createRetailer(
        registration2: UserRegistration2,
        registration3: UserRegistration3,
    ): AnyResponse {
        val user = requireUser()
        require(user.type == UserType.SEASON_BOY) { "can only create from season boys" }
        return networkSignUpScope.createdRetailerWithSeasonBoy(
            CreateRetailer.from(
                unitCode = user.unitCode,
                registration2,
                registration3,
            )
        )
    }

    suspend fun sendFirebaseToken(token: String? = cachedFirebaseToken) {
        val customerType = userV2Flow.value?.type
        if (customerType != UserType.STOCKIST_EMPLOYEE && customerType != UserType.RETAILER_EMPLOYEE) {
            if (token != null) {
                cachedFirebaseToken = token
            }
            if (getUserAccess() == UserAccess.FULL_ACCESS && token != null) {
                networkNotificationScope.sendFirebaseToken(token)
            }
        }
    }

    suspend fun getWhatsappPreference(): BodyResponse<WhatsappData> {
        return whatsappPreferenceScope.getWhatsappPreferences(requireUser().unitCode)
    }

    suspend fun saveWhatsappPreference(
        language: String,
        phoneNumber: String,
    ): AnyResponse {
        return whatsappPreferenceScope.saveWhatsappPreferences(
            language,
            phoneNumber,
            requireUser().unitCode
        )
    }


    suspend fun uploadProfileImage(
        fileString: String,
        mimeType: String,
        type: String,
        size: String,
    ): BodyResponse<ProfileResponseData> {
        return profileImageScope.saveProfileImageData(
            ProfileImageUpload(
                size = size,
                name = type,
                mimeType = mimeType,
                documentType = type,
                documentData = fileString,
            ), type
        )
    }

    suspend fun getProfileImageData(): BodyResponse<ProfileImageData> {
        return profileImageScope.getProfileImageData()
    }

    fun getLocalSearchHistory(): List<AutoComplete> {
        return runCatching {
            Json.decodeFromString(
                ListSerializer(AutoComplete.serializer()), settings.getString(
                    LOCAL_SEARCH
                )
            ).reversed()
        }.getOrElse { emptyList() }
    }

    fun saveLocalSearchHistory(item: AutoComplete) {
        val list: MutableList<AutoComplete> = getLocalSearchHistory().toMutableList()
        if (list.isNotEmpty() && list.contains(item)) return else list.add(item)
        val json = Json.encodeToString(ListSerializer(AutoComplete.serializer()), list)
        settings.putString(LOCAL_SEARCH, json)
    }

    private fun clearUserData() {
        settings.remove(AUTH_USER_KEY)
        settings.remove(AUTH_USER_KEY_V2)
        settings.remove(LOCAL_SEARCH)
        tokenStorage.clear()
    }

    enum class UserAccess {
        FULL_ACCESS, LIMITED_ACCESS, NO_ACCESS
    }

    companion object {
        // TODO make secure
        private const val AUTH_LOGIN_KEY = "auid"
        private const val AUTH_USER_KEY = "ukey"
        private const val AUTH_USER_KEY_V2 = "ukeyV2"
        private const val LOCAL_SEARCH = "local_search"
    }
}

internal inline fun UserRepo.requireUser(): UserV2 =
    requireNotNull(userV2Flow.value) { "user can no be null" }

internal inline fun UserRepo.requireUserOld(): User =
    requireNotNull(userFlow.value) { "user can no be null" }

internal inline fun UserRepo.getUserDataSource(): ReadOnlyDataSource<User> = ReadOnlyDataSource(
    userFlow.filterNotNull().stateIn(GlobalScope, SharingStarted.Eagerly, requireUserOld())
)

internal inline fun UserRepo.getUserDataSourceV2(): ReadOnlyDataSource<UserV2> = ReadOnlyDataSource(
    userV2Flow.filterNotNull().stateIn(GlobalScope, SharingStarted.Eagerly, requireUser())
)

internal inline fun UserRepo.getManufacturerDataSource(): ReadOnlyDataSource<List<ManufacturerData>?> =
    ReadOnlyDataSource(
        manufacturerFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getStockDataSource(): ReadOnlyDataSource<StockStatusData?> =
    ReadOnlyDataSource(
        stockDataFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getRecentProductsDataSource(): ReadOnlyDataSource<RecentProductInfo?> =
    ReadOnlyDataSource(
        recentProductFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getPromotionsDataSource(): ReadOnlyDataSource<List<OffersData>?> =
    ReadOnlyDataSource(
        promotionDataFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getDealsDataSource(): ReadOnlyDataSource<List<DealsData>?> =
    ReadOnlyDataSource(
        dealsFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getBannerDataSource(): ReadOnlyDataSource<List<BannerData>?> =
    ReadOnlyDataSource(
        bannerFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getBrandsDataSource(): ReadOnlyDataSource<List<BrandsData>?> =
    ReadOnlyDataSource(
        brandsFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getCategoriesDataSource(): ReadOnlyDataSource<List<BrandsData>?> =
    ReadOnlyDataSource(
        categoriesFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )

internal inline fun UserRepo.getStockistEmpBannerDataSource(): ReadOnlyDataSource<List<EmployeeBannerData>?> =
    ReadOnlyDataSource(
        stockistEmployeeBannerFlow.stateIn(GlobalScope, SharingStarted.Eagerly, null)
    )
private inline fun String.formatIndia() = "91$this"