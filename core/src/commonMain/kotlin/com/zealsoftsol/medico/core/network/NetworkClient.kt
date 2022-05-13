package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.Interval
import com.zealsoftsol.medico.core.extensions.isExpired
import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.retry
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AddEmployee
import com.zealsoftsol.medico.data.AddInvoice
import com.zealsoftsol.medico.data.AllBanners
import com.zealsoftsol.medico.data.AllDeals
import com.zealsoftsol.medico.data.AnyResponse
import com.zealsoftsol.medico.data.AutoApprove
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BatchStatusUpdateRequest
import com.zealsoftsol.medico.data.BatchUpdateRequest
import com.zealsoftsol.medico.data.BatchesData
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.BuyerUsersData
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.ConfigData
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.CustomerDataV2
import com.zealsoftsol.medico.data.DashboardManufacturer
import com.zealsoftsol.medico.data.DashboardPromotion
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.EditOfferRequest
import com.zealsoftsol.medico.data.EmployeeRegistration1
import com.zealsoftsol.medico.data.EmployeeRegistration2
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.HeaderData
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.IOCResponse
import com.zealsoftsol.medico.data.InStoreCart
import com.zealsoftsol.medico.data.InStoreCartRequest
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.InStoreUserRegistration
import com.zealsoftsol.medico.data.InfoResponse
import com.zealsoftsol.medico.data.InvListingData
import com.zealsoftsol.medico.data.InventoryCompanies
import com.zealsoftsol.medico.data.InventoryData
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceDetails
import com.zealsoftsol.medico.data.InvoiceResponse
import com.zealsoftsol.medico.data.LicenseDocumentData
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.ManufacturersListData
import com.zealsoftsol.medico.data.MapBody
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.OfferData
import com.zealsoftsol.medico.data.OfferProduct
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.OfferStatus
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderResponse
import com.zealsoftsol.medico.data.OrderResponseInvoice
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.OtpRequest
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.PasswordResetRequest
import com.zealsoftsol.medico.data.PasswordResetRequest2
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.ProductBuyResponse
import com.zealsoftsol.medico.data.ProductResponse
import com.zealsoftsol.medico.data.ProductSeasonBoyRetailerSelectResponse
import com.zealsoftsol.medico.data.ProfileImageData
import com.zealsoftsol.medico.data.ProfileImageUpload
import com.zealsoftsol.medico.data.ProfileResponseData
import com.zealsoftsol.medico.data.PromotionTypeData
import com.zealsoftsol.medico.data.PromotionUpdateRequest
import com.zealsoftsol.medico.data.QrCodeData
import com.zealsoftsol.medico.data.RecentProductInfo
import com.zealsoftsol.medico.data.RefreshTokenRequest
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchDataItem
import com.zealsoftsol.medico.data.SearchResponse
import com.zealsoftsol.medico.data.SellerUsersData
import com.zealsoftsol.medico.data.StockStatusData
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubmitEmployeeRegistration
import com.zealsoftsol.medico.data.SubmitPaymentRequest
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UnreadNotifications
import com.zealsoftsol.medico.data.UpdateInvoiceRequest
import com.zealsoftsol.medico.data.UploadResponseData
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.ValidationResponse
import com.zealsoftsol.medico.data.VerifyOtpRequest
import com.zealsoftsol.medico.data.ViewEmployee
import com.zealsoftsol.medico.data.WhatsappData
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.invoke
import kotlinx.serialization.json.Json

class NetworkClient(
    engine: HttpClientEngineFactory<*>,
    private val tokenStorage: TokenStorage,
    useNetworkInterceptor: Boolean,
    private val crashOnServerError: Boolean,
    private val baseUrl: BaseUrl,
) : NetworkScope.Auth,
    NetworkScope.SignUp,
    NetworkScope.Password,
    NetworkScope.Customer,
    NetworkScope.Search,
    NetworkScope.Product,
    NetworkScope.Management,
    NetworkScope.Notification,
    NetworkScope.Stores,
    NetworkScope.Cart,
    NetworkScope.Help,
    NetworkScope.Orders,
    NetworkScope.Config,
    NetworkScope.InStore,
    NetworkScope.WhatsappStore,
    NetworkScope.InventoryStore,
    NetworkScope.ProfileImage,
    NetworkScope.OffersStore,
    NetworkScope.OrderHsnEditStore,
    NetworkScope.BatchesStore,
    NetworkScope.IOCStore,
    NetworkScope.IOCBuyerStore,
    NetworkScope.BottomSheetStore,
    NetworkScope.QrCodeStore,
    NetworkScope.PreferencesStore,
    NetworkScope.EmployeeStore,
    NetworkScope.BannersStore,
    NetworkScope.DealsStore,
    NetworkScope.ManufacturerStore {

    init {
        "USING NetworkClient with $baseUrl".logIt()
    }

    private val client = HttpClient(engine) {
        expectSuccess = true
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                createJson()
            )
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 20_000
        }
        if (useNetworkInterceptor) {
            addInterceptor(this)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    override suspend fun login(request: UserRequest) = simpleRequest {
        client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/medico/login") {
            jsonBody(request)
        }.also {
            it.getBodyOrNull()?.let(tokenStorage::saveMainToken)
        }
    }

    override suspend fun logout() = simpleRequest {
        client.post<AnyResponse>("${baseUrl.url}/medico/logout") {
            withMainToken()
        }
    }

    override suspend fun checkCanResetPassword(phoneNumber: String) = fullRequest {
        client.post<AnyResponse>("${baseUrl.url}/medico/forgetpwd") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(OtpRequest(phoneNumber))
        }
    }

    override suspend fun sendOtp(phoneNumber: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/notifications/sendOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }
        }

    override suspend fun retryOtp(phoneNumber: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/notifications/retryOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }
        }

    override suspend fun verifyOtp(phoneNumber: String, otp: String) =
        simpleRequest {
            val body =
                client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/notifications/verifyOTP") {
                    withTempToken(TempToken.REGISTRATION)
                    jsonBody(VerifyOtpRequest(phoneNumber, otp))
                }
            if (body.isSuccess) {
                body.getBodyOrNull()
                    ?.let { tokenStorage.saveTempToken(TempToken.UPDATE_PASSWORD.serverValue, it) }
            }
            body
        }

    override suspend fun verifyPassword(password: String) =
        fullRequest {
            client.post<ValidationResponse<PasswordValidation>>("${baseUrl.url}/myaccount/currentPwd") {
                withMainToken()
                jsonBody(mapOf("currentPassword" to password))
            }
        }

    override suspend fun changePassword(
        phoneNumber: String?,
        password: String
    ) = fullRequest {
        val url = if (phoneNumber != null)
            "${baseUrl.url}/medico/forgetpwd/update"
        else
            "${baseUrl.url}/myaccount/changePwd"
        client.post<ValidationResponse<PasswordValidation>>(url) {
            if (phoneNumber != null) {
                withTempToken(TempToken.UPDATE_PASSWORD)
                jsonBody(PasswordResetRequest(phoneNumber, password, password))
            } else {
                withMainToken()
                jsonBody(PasswordResetRequest2(password, password))
            }
        }
    }

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1) =
        fullRequest {
            client.post<ValidationResponse<UserValidation1>>("${baseUrl.url}/registration/step1") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration1)
            }
        }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2) =
        fullRequest {
            client.post<ValidationResponse<UserValidation2>>("${baseUrl.url}/registration/step2") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration2)
            }
        }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3) =
        fullRequest {
            client.post<ValidationResponse<UserValidation3>>("${baseUrl.url}/registration/v2/step3") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration3)
            }
        }

    override suspend fun uploadDocument(uploadData: LicenseDocumentData) =
        simpleRequest {
            client.post<BodyResponse<UploadResponseData>>("${baseUrl.url}/document/add/license") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(uploadData)
            }
        }

    override suspend fun getLocationData(pincode: String) =
        fullRequest {
            client.get<Response<LocationData, PincodeValidation>>("${baseUrl.url}/geo/pincode/$pincode") {
                withTempToken(TempToken.REGISTRATION)
            }
        }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload) = simpleRequest {
        client.post<AnyResponse>("${baseUrl.url}/upload/aadhaar") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(aadhaarData)
        }
    }

    override suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload) =
        simpleRequest {
            client.post<BodyResponse<StorageKeyResponse>>("${baseUrl.url}/upload/druglicense") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(licenseData)
            }
        }

    override suspend fun signUp(submitRegistration: SubmitRegistration) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/registration${if (submitRegistration.isSeasonBoy) "/seasonboys" else "/v2"}/submit") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(submitRegistration)
            }
        }

    override suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3) =
        fullRequest {
            client.post<ValidationResponse<UserValidation3>>("${baseUrl.url}/sbret/verify") {
                withMainToken()
                jsonBody(userRegistration3)
            }
        }

    override suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/sbret/add") {
                withMainToken()
                jsonBody(data)
            }
        }

    override suspend fun getCustomerDataV2() = simpleRequest {
        client.get<BodyResponse<CustomerDataV2>>("${baseUrl.url}/medico/customer/v2/details") {
            withMainToken()
        }
    }

    override suspend fun getCustomerData() = simpleRequest {
        client.get<BodyResponse<CustomerData>>("${baseUrl.url}/medico/customer/details") {
            withMainToken()
        }
    }

    override suspend fun getDashboardManufacturers(type: UserType) = simpleRequest {
        client.get<BodyResponse<DashboardManufacturer>>("${baseUrl.url}/dashboard/${type.serverValueSimple}/manufacturers") {
            withMainToken()
        }
    }

    override suspend fun getPromotionData(type: UserType) = simpleRequest {
        client.get<BodyResponse<DashboardPromotion>>("${baseUrl.url}/dashboard/${type.serverValueSimple}/promotions") {
            withMainToken()
        }
    }

    override suspend fun getStockStatusData(type: UserType) = simpleRequest {
        client.get<BodyResponse<StockStatusData>>("${baseUrl.url}/dashboard/${type.serverValueSimple}/inventory") {
            withMainToken()
        }
    }

    override suspend fun getRecentProducts(type: UserType) = simpleRequest {
        client.get<BodyResponse<RecentProductInfo>>("${baseUrl.url}/dashboard/${type.serverValueSimple}/recent-products-info") {
            withMainToken()
        }
    }

    override suspend fun search(
        sort: String?,
        query: List<Pair<String, String>>,
        unitCode: String?,
        latitude: Double,
        longitude: Double,
        pagination: Pagination,
        addPage: Boolean
    ) = simpleRequest {
        client.get<BodyResponse<SearchResponse>>("${baseUrl.url}/search/${if (unitCode == null) "global" else "stores"}") {
            withMainToken()
            url {
                parameters.apply {
                    query.forEach { (name, value) ->
                        set(name, value)
                    }
                    sort?.let { append("sort", it) }
                    unitCode?.let { append("unitCode", it) }
                    append("latitude", latitude.toString())
                    append("longitude", longitude.toString())
                    append(
                        "page", if (addPage) pagination.previousPage().toString()
                        else pagination.nextPage().toString()
                    )
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded(addPage)
        }
    }

    override suspend fun autocomplete(
        input: String,
        unitCodeForStores: String?
    ) = simpleRequest {
        val path =
            if (unitCodeForStores == null) "/search/v2/suggest" else "/search/stores/suggest-vvb"
        client.get<BodyResponse<List<AutoComplete>>>("${baseUrl.url}$path") {
            withMainToken()
            url {
                parameters.append("suggest", input)
                unitCodeForStores?.let { parameters.append("b2bUnitCode", it) }
            }
        }
    }

    override suspend fun loadStockist(
        latitude: Double,
        longitude: Double,
        imageCode: String,
        code: String
    ) = simpleRequest {
        val path = "/search/view/stockists"
        client.get<BodyResponse<List<ConnectedStockist>>>("${baseUrl.url}$path") {
            withMainToken()
            url {
                parameters.append("code", code)
                parameters.append("latitude", latitude.toString())
                parameters.append("longitude", longitude.toString())
                parameters.append("imageCode", imageCode)
            }
        }
    }

    override suspend fun getProductData(productCode: String) =
        simpleRequest {
            client.get<BodyResponse<ProductResponse>>("${baseUrl.url}/search/product/$productCode") {
                withMainToken()
            }
        }

    override suspend fun buyProductInfo(
        productCode: String,
        latitude: Double,
        longitude: Double,
    ) =
        simpleRequest {
            client.get<BodyResponse<ProductBuyResponse>>("${baseUrl.url}/search/buy/${productCode}") {
                withMainToken()
                url {
                    parameters.append("latitude", latitude.toString())
                    parameters.append("longitude", longitude.toString())
                }
            }
        }

    override suspend fun buyProductSelectSeasonBoyRetailer(
        productCode: String,
        unitCode: String,
        sellerUnitCode: String?,
        latitude: Double,
        longitude: Double
    ) = simpleRequest {
        client.get<BodyResponse<ProductSeasonBoyRetailerSelectResponse>>("${baseUrl.url}/search/sb/select/${productCode}") {
            withMainToken()
            url {
                parameters.append("buyerUnitCode", unitCode)
                if (sellerUnitCode != null) {
                    parameters.append("sellerUnitCode", sellerUnitCode)
                }
                parameters.append("latitude", latitude.toString())
                parameters.append("longitude", longitude.toString())
            }
        }
    }

    override suspend fun getQuotedProductData(productCode: String) =
        simpleRequest {
            client.get<BodyResponse<ProductBuyResponse>>("${baseUrl.url}/search/quote/${productCode}") {
                withMainToken()
            }
        }

    override suspend fun getManagementInfo(
        unitCode: String,
        isSeasonBoy: Boolean,
        forUserType: UserType,
        criteria: ManagementCriteria,
        search: String,
        pagination: Pagination
    ) = simpleRequest {
        client.get<BodyResponse<PaginatedData<EntityInfo>>>(
            "${baseUrl.url}/b2bapp/${forUserType.serverValueSimple}/${if (isSeasonBoy && forUserType == UserType.RETAILER) "sb/" else ""}$unitCode"
        ) {
            withMainToken()
            url {
                parameters.apply {
                    if (search.isNotEmpty()) append("search", search)
                    append("criteria", criteria.serverValue)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun subscribeRequest(subscribeRequest: SubscribeRequest) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/b2bapp/subscriptions/subscribe") {
                withMainToken()
                jsonBody(subscribeRequest)
            }
        }

    override suspend fun sendFirebaseToken(token: String) = simpleRequest {
        client.post<AnyResponse>("${baseUrl.url}/firebase/add/token") {
            withMainToken()
            jsonBody(mapOf("token" to token, "channel" to "MOBILE"))
        }
    }

    override suspend fun getNotifications(
        search: String,
        filter: NotificationFilter,
        pagination: Pagination,
    ) = simpleRequest {
        client.get<BodyResponse<PaginatedData<NotificationData>>>("${baseUrl.url}/notifications/all") {
            withMainToken()
            url {
                parameters.apply {
                    if (search.isNotEmpty()) append("search", search)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                    append("notificationType", filter.serverValue)
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getUnreadNotifications() =
        simpleRequest {
            client.get<BodyResponse<UnreadNotifications>>("${baseUrl.url}/notifications/unread") {
                withMainToken()
            }
        }

    override suspend fun selectNotificationAction(
        id: String,
        actionRequest: NotificationActionRequest,
    ) = simpleRequest {
        client.post<AnyResponse>("${baseUrl.url}/b2bapp/subscriptions/submit") {
            withMainToken()
            requireNotNull(actionRequest.subscriptionOption) { "only subscription option is supported" }
            jsonBody(
                mapOf(
                    "notificationId" to id,
                    "actionType" to actionRequest.action.name,
                    "paymentMethod" to actionRequest.subscriptionOption!!.paymentMethod.name,
                    "noOfCreditDays" to actionRequest.subscriptionOption!!.creditDays,
                    "sellersDiscount" to actionRequest.subscriptionOption!!.discountRate,
                )
            )
        }
    }

    override suspend fun getNotificationDetails(id: String) =
        simpleRequest {
            client.get<BodyResponse<NotificationDetails>>("${baseUrl.url}/notifications/$id/detail") {
                withMainToken()
            }
        }

    override suspend fun deleteNotification(id: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/notifications/delete") {
                withMainToken()
                jsonBody(mapOf("notificationId" to id))
            }
        }

    override suspend fun getStores(
        unitCode: String,
        search: String,
        pagination: Pagination,
    ) = simpleRequest {
        client.get<BodyResponse<PaginatedData<Store>>>("${baseUrl.url}/b2bapp/stores/${unitCode}") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getCart(unitCode: String) = simpleRequest {
        client.get<BodyResponse<CartData>>("${baseUrl.url}/cart") {
            withMainToken()
            url {
                parameters.append("buyerUnitCode", unitCode)
            }
        }
    }

    override suspend fun addCartEntry(request: CartRequest) =
        simpleRequest {
            client.post<BodyResponse<CartData>>("${baseUrl.url}/cart/addEntry") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun deleteCart(unitCode: String, cartId: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/cart/deleteCart") {
                withMainToken()
                jsonBody(mapOf("cartId" to cartId, "buyerUnitCode" to unitCode))
            }
        }

    override suspend fun updateCartEntry(request: CartRequest) =
        simpleRequest {
            client.post<BodyResponse<CartData>>("${baseUrl.url}/cart/updateEntry") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun deleteCartEntry(request: CartRequest) =
        simpleRequest {
            client.post<BodyResponse<CartData>>("${baseUrl.url}/cart/deleteEntry") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun deleteSellerCart(
        unitCode: String,
        cartId: String,
        sellerUnitCode: String
    ) = simpleRequest {
        client.post<BodyResponse<CartData>>("${baseUrl.url}/cart/deleteSellerCart") {
            withMainToken()
            jsonBody(
                mapOf(
                    "cartId" to cartId,
                    "buyerUnitCode" to unitCode,
                    "sellerUnitCode" to sellerUnitCode
                )
            )
        }
    }

    override suspend fun confirmCart(request: CartOrderRequest) =
        simpleRequest {
            client.post<BodyResponse<CartConfirmData>>("${baseUrl.url}/cart/confirm") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun submitCart(request: CartOrderRequest) =
        simpleRequest {
            client.post<BodyResponse<CartSubmitResponse>>("${baseUrl.url}/cart/submit") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun getHelp() = simpleRequest {
        client.get<BodyResponse<HelpData>>("${baseUrl.url}/medico/help") {
            withMainToken()
        }
    }

    override suspend fun getOrders(
        type: OrderType,
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination,
    ) = simpleRequest {
        client.get<BodyResponse<PaginatedData<Order>>>("${baseUrl.url}/orders${type.path}") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("b2bUnitCode", unitCode)
                    append("page", pagination.nextPage().toString())
                    from?.let { append("fromDate", it.toString()) }
                    to?.let { append("toDate", it.toString()) }
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getOrder(
        type: OrderType,
        unitCode: String,
        orderId: String
    ) = simpleRequest {
        client.get<BodyResponse<OrderResponse>>("${baseUrl.url}/orders/tax${type.path}$orderId") {
            withMainToken()
            url {
                parameters.apply {
                    append("b2bUnitCode", unitCode)
                }
            }
        }
    }

    override suspend fun getOrderInvoice(
        request: ConfirmOrderRequest
    ) = simpleRequest {
        client.post<BodyResponse<OrderResponseInvoice>>("${baseUrl.url}/orders/tax/po/preview") {
            withMainToken()
            jsonBody(request)
        }
    }

    override suspend fun confirmOrder(request: ConfirmOrderRequest) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/orders/tax/po/entries/accept") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun saveNewOrderQty(request: OrderNewQtyRequest) =
        simpleRequest {
            client.post<BodyResponse<OrderResponse>>("${baseUrl.url}/orders/po/entries/save") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun getInvoices(
        isPoInvoice: Boolean,
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination
    ) = simpleRequest {
        client.get<BodyResponse<PaginatedData<Invoice>>>("${baseUrl.url}/invoices${if (isPoInvoice) "/po" else ""}/tax") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("b2bUnitCode", unitCode)
                    append("page", pagination.nextPage().toString())
                    from?.let { append("fromDate", it.toString()) }
                    to?.let { append("toDate", it.toString()) }
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getInvoice(
        isPoInvoice: Boolean,
        unitCode: String,
        invoiceId: String
    ) = simpleRequest {
        client.get<BodyResponse<InvoiceResponse>>("${baseUrl.url}/invoices${if (isPoInvoice) "/po" else ""}/tax/$invoiceId") {
            withMainToken()
            url {
                parameters.apply {
                    append("b2bUnitCode", unitCode)
                }
            }
        }
    }

    override suspend fun getConfig(): BodyResponse<ConfigData> = simpleRequest {
        client.get("${baseUrl.url}/medico/config") {
            withMainToken()
        }
    }

    override suspend fun getInStoreSellers(
        unitCode: String,
        search: String,
        pagination: Pagination
    ): BodyResponse<PaginatedData<InStoreSeller>> = simpleRequest {
        client.get<BodyResponse<PaginatedData<InStoreSeller>>>("${baseUrl.url}/instore") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("b2bUnitCode", unitCode)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun searchInStoreSeller(
        unitCode: String,
        search: String,
        pagination: Pagination
    ): BodyResponse<PaginatedData<InStoreProduct>> = simpleRequest {
        client.get<BodyResponse<PaginatedData<InStoreProduct>>>("${baseUrl.url}/instore/search") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("b2bUnitCode", unitCode)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getInStoreUsers(
        unitCode: String,
        search: String,
        pagination: Pagination
    ): BodyResponse<PaginatedData<InStoreUser>> = simpleRequest {
        client.get<BodyResponse<PaginatedData<InStoreUser>>>("${baseUrl.url}/instore/users") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("b2bUnitCode", unitCode)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun addUser(registration: InStoreUserRegistration): AnyResponse =
        simpleRequest {
            client.post("${baseUrl.url}/instore/user/add") {
                withMainToken()
                jsonBody(registration)
            }
        }

    override suspend fun getInStoreCart(unitCode: String) = simpleRequest {
        client.get<BodyResponse<InStoreCart>>("${baseUrl.url}/instore/order/view") {
            withMainToken()
            url {
                parameters.append("b2bUnitCode", unitCode)
            }
        }
    }

    override suspend fun deleteInStoreCart(unitCode: String, cartId: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/instore/order/deleteOrder") {
                withMainToken()
                jsonBody(mapOf("id" to cartId, "buyerUnitCode" to unitCode))

            }
        }

    override suspend fun addInStoreCartEntry(request: InStoreCartRequest) =
        simpleRequest {
            client.post<BodyResponse<InStoreCart>>("${baseUrl.url}/instore/order/addEntry") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun updateInStoreCartEntry(request: InStoreCartRequest) =
        simpleRequest {
            client.post<BodyResponse<InStoreCart>>("${baseUrl.url}/instore/order/updateEntry") {
                withMainToken()
                jsonBody(request)
            }
        }

    override suspend fun deleteInStoreCartEntry(unitCode: String, entryId: String) =
        simpleRequest {
            client.post<BodyResponse<InStoreCart>>("${baseUrl.url}/instore/order/deleteEntry") {
                withMainToken()
                jsonBody(mapOf("entryId" to entryId, "buyerUnitCode" to unitCode))

            }
        }

    override suspend fun confirmInStoreCart(unitCode: String, id: String): AnyResponse {
        return simpleRequest {
            client.post("${baseUrl.url}/instore/order/confirm") {
                withMainToken()
                jsonBody(mapOf("id" to id, "buyerUnitCode" to unitCode))
            }
        }
    }

    override suspend fun deleteInStoreOrder(unitCode: String, id: String): AnyResponse {
        return simpleRequest {
            client.post("${baseUrl.url}/instore/order/deleteOrder") {
                withMainToken()
                jsonBody(mapOf("id" to id, "buyerUnitCode" to unitCode))
            }
        }
    }

    override suspend fun getWhatsappPreferences(unitCode: String) =
        simpleRequest {
            client.get<BodyResponse<WhatsappData>>("${baseUrl.url}/b2bapp/preference/whatsapp") {
                withMainToken()
                url {
                    parameters.append("b2bUnitCode", unitCode)
                }
            }
        }

    override suspend fun saveWhatsappPreferences(
        language: String,
        phoneNumber: String,
        unitCode: String
    ) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/b2bapp/preference/whatsapp/save") {
                withMainToken()
                jsonBody(mapOf("language" to language, "mobileNo" to phoneNumber))
                url {
                    parameters.append("b2bUnitCode", unitCode)
                }
            }
        }


    override suspend fun getHsnCodes(search: String, pagination: Pagination) = simpleRequest {
        client.get<BodyResponse<PaginatedData<SearchDataItem>>>("${baseUrl.url}/products/hsncodes/search") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun saveNewOrder(request: OrderNewQtyRequest) =
        simpleRequest {
            client.post<BodyResponse<OrderResponse>>("${baseUrl.url}/orders/tax/po/entry/save") {
                withMainToken()
                jsonBody(request)
            }

        }

    override suspend fun rejectEntry(orderEntryId: String, spid: String, reasonCode: String) =
        simpleRequest {
            client.post<BodyResponse<OrderResponse>>("${baseUrl.url}/orders/tax/po/entry/reject") {
                withMainToken()
                jsonBody(
                    mapOf(
                        "orderEntryId" to orderEntryId,
                        "spid" to spid,
                        "reasonCode" to reasonCode
                    )
                )
            }
        }

    override suspend fun acceptEntry(orderEntryId: String, spid: String) =
        simpleRequest {
            client.post<BodyResponse<OrderResponse>>("${baseUrl.url}/orders/tax/po/entry/accept") {
                withMainToken()
                jsonBody(mapOf("orderEntryId" to orderEntryId, "spid" to spid))
            }
        }

    override suspend fun takeActionOnOrderEntries(orderData: ConfirmOrderRequest): BodyResponse<OrderResponse> =
        simpleRequest {
            client.post("${baseUrl.url}/orders/tax/po/confirm") {
                withMainToken()
                jsonBody(orderData)
            }
        }

    override suspend fun changePaymentMethod(
        unitCode: String,
        orderId: String,
        type: String
    ): AnyResponse = simpleRequest {
        client.post("${baseUrl.url}/orders/tax/po/type/edit") {
            withMainToken()
            jsonBody(
                mapOf(
                    "b2bUnitCode" to unitCode,
                    "orderId" to orderId,
                    "type" to type
                )
            )
        }
    }

    override suspend fun editDiscount(
        unitCode: String,
        orderId: String,
        discount: Double
    ): BodyResponse<OrderResponse> = simpleRequest {
        client.post("${baseUrl.url}/orders/tax/po/discount/add") {
            withMainToken()
            jsonBody(
                mapOf(
                    "b2bUnitCode" to unitCode,
                    "orderId" to orderId,
                    "discount" to discount.toString()
                )
            )
        }
    }

    // Utils

    override suspend fun saveProfileImageData(
        profileImageData: ProfileImageUpload,
        type: String
    ) = simpleRequest {
        client.post<BodyResponse<ProfileResponseData>>("${baseUrl.url}/document/user/profile") {
            withMainToken()
            jsonBody(profileImageData)
        }
    }


    override suspend fun getProfileImageData() =
        simpleRequest {
            client.get<BodyResponse<ProfileImageData>>("${baseUrl.url}/b2bapp/profiles") {
                withMainToken()
            }
        }

    //View offers/Update offers methods
    override suspend fun getOffersData(
        unitCode: String,
        search: String?,
        manufacturer: ArrayList<String>?,
        status: OfferStatus,
        page: Int
    ): BodyResponse<OfferData> = simpleRequest {
        client.get("${baseUrl.url}/promotions") {
            withMainToken()
            withB2bCodeToken(unitCode)
            url {
                parameters.apply {
                    append("page", page.toString())
                    append("pageSize", "10")
                    append("status", status.value)
                    if (!search.isNullOrEmpty()) append("search", search)
                    manufacturer?.forEach {
                        append("manufacturers", it)
                    }
                }
            }
        }
    }


    override suspend fun updateOffer(
        unitCode: String,
        request: PromotionUpdateRequest
    ) = simpleRequest {
        client.post<BodyResponse<String>>("${baseUrl.url}/promotions/update") {
            withMainToken()
            withB2bCodeToken(unitCode)
            jsonBody(request)
        }
    }

    //Create offer methods
    override suspend fun getPromotionTypes(
        unitCode: String
    ) = simpleRequest {
        client.get<BodyResponse<PromotionTypeData>>("${baseUrl.url}/promotions/types") {
            withMainToken()
            withB2bCodeToken(unitCode)
        }
    }

    override suspend fun autocompleteOffers(
        input: String,
        unitCode: String
    ) = simpleRequest {
        val path = "/search/promotions/suggest"
        client.get<BodyResponse<List<AutoComplete>>>("${baseUrl.url}$path") {
            withMainToken()
            url {
                parameters.append("b2bUnitCode", unitCode)
                parameters.append("suggest", input)
            }
        }
    }

    override suspend fun getUsers(
        unitCode: String,
        search: String?,
        pagination: Pagination
    ) = simpleRequest {
        client.get<BodyResponse<SellerUsersData>>("${baseUrl.url}/ioc/seller/search") {
            withMainToken()
            url {
                parameters.apply {
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                    if (!search.isNullOrEmpty()) append("search", search)
                }
            }
        }
    }

    override suspend fun retailerInvoiceDetails(
        unitCode: String,
    ) = simpleRequest {
        client.get<BodyResponse<InvListingData>>("${baseUrl.url}/ioc/seller/$unitCode") {
            withMainToken()
        }
    }

    override suspend fun invoiceDetails(
        invoiceId: String,
    ) = simpleRequest {
        client.get<BodyResponse<InvoiceDetails>>("${baseUrl.url}/ioc/seller/invoice/$invoiceId") {
            withMainToken()
        }
    }

    override suspend fun getBuyers(
        unitCode: String,
        search: String?,
        pagination: Pagination
    ) = simpleRequest {
        client.get<BodyResponse<BuyerUsersData>>("${baseUrl.url}/ioc/buyer/search") {
            withMainToken()
            url {
                parameters.apply {
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                    if (!search.isNullOrEmpty()) append("search", search)
                }
            }
        }
    }

    override suspend fun buyerInvoiceListing(
        unitCode: String,
    ) = simpleRequest {
        client.get<BodyResponse<InvListingData>>("${baseUrl.url}/ioc/buyer/$unitCode") {
            withMainToken()
        }
    }

    override suspend fun buyerInvoiceDetails(
        invoiceId: String,
    ) = simpleRequest {
        client.get<BodyResponse<InvoiceDetails>>("${baseUrl.url}/ioc/buyer/invoice/$invoiceId") {
            withMainToken()
        }
    }

    override suspend fun submitPayment(
        request: SubmitPaymentRequest,
    ) = simpleRequest {
        client.post<BodyResponse<String>>("${baseUrl.url}/ioc/buyer/paynow") {
            withMainToken()
            jsonBody(request)
        }
    }

    override suspend fun getDetails(
        unitCode: String,
    ) = simpleRequest {
        client.get<BodyResponse<HeaderData>>("${baseUrl.url}/b2bapp/detail/${unitCode}") {
            withMainToken()
        }
    }


    override suspend fun getRetailers(
        unitCode: String,
        search: String?,
        pagination: Pagination
    ) = simpleRequest {
        client.get<BodyResponse<IOCResponse>>("${baseUrl.url}/ioc/users") {
            withMainToken()
            withB2bCodeToken(unitCode)
            url {
                parameters.apply {
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                    if (!search.isNullOrEmpty()) append("search", search)
                }
            }
        }
    }


    override suspend fun submitInvoice(
        request: AddInvoice
    ) = simpleRequest {
        client.post<BodyResponse<InfoResponse>>("${baseUrl.url}/ioc/add/invoice") {
            withMainToken()
            jsonBody(request)
        }
    }

    override suspend fun updateInvoice(
        request: UpdateInvoiceRequest
    ) = simpleRequest {
        client.post<BodyResponse<String>>("${baseUrl.url}/ioc/seller/invoice/edit") {
            withMainToken()
            jsonBody(request)
        }
    }

    override suspend fun getAutocompleteItem(
        input: String,
        unitCode: String
    ) = simpleRequest {
        val path = "/search/promotions/suggest/search"
        client.get<BodyResponse<OfferProduct>>("${baseUrl.url}$path") {
            withMainToken()
            url {
                parameters.append("search", input)
                parameters.append("b2bUnitCode", unitCode)
            }
        }
    }

    override suspend fun saveOffer(
        unitCode: String,
        request: OfferProductRequest
    ) = simpleRequest {
        val path = "/promotions/submit"
        client.post<BodyResponse<String>>("${baseUrl.url}$path") {
            withMainToken()
            withB2bCodeToken(unitCode)
            jsonBody(request)
        }
    }

    override suspend fun editOffer(
        unitCode: String,
        promoCode: String,
        request: OfferProductRequest
    ) = simpleRequest {
        val path = "/promotions/edit/confirm"
        client.post<BodyResponse<String>>("${baseUrl.url}$path") {
            withMainToken()
            withB2bCodeToken(unitCode)
            jsonBody(EditOfferRequest(promoCode, request))
        }
    }

    override suspend fun getBatches(
        unitCode: String,
        spid: String
    ) = simpleRequest {
        client.get<BodyResponse<BatchesData>>("${baseUrl.url}/inventory/view/batches") {
            withMainToken()
            withB2bCodeToken(unitCode)
            url {
                parameters.append("spid", spid)
            }
        }
    }

    override suspend fun editBatches(
        unitCode: String,
        request: BatchUpdateRequest
    ) = simpleRequest {
        client.post<BodyResponse<String>>("${baseUrl.url}/inventory/save") {
            withMainToken()
            withB2bCodeToken(unitCode)
            jsonBody(request)
        }
    }

    override suspend fun updateBatchStatus(
        unitCode: String,
        request: BatchStatusUpdateRequest
    ) = simpleRequest {
        client.post<BodyResponse<String>>("${baseUrl.url}/inventory/status") {
            withMainToken()
            withB2bCodeToken(unitCode)
            jsonBody(request)
        }
    }

    override suspend fun getInventoryData(
        unitCode: String,
        search: String?,
        page: Int,
        manufacturer: String?,
        stockStatus: InventoryScope.InventoryType,
        status: InventoryScope.StockStatus
    ): BodyResponse<InventoryData> = simpleRequest {
        client.get("${baseUrl.url}/inventory/view") {
            withMainToken()
            header("X-TENANT-ID", unitCode)
            url {
                parameters.apply {
                    if (!search.isNullOrEmpty())
                        append("search", search)
                    if (!manufacturer.isNullOrEmpty())
                        append("manufacturer", manufacturer)
                    append("status", status.toString())
                    append("stockStatus", stockStatus.toString())
                    append("page", page.toString())
                    append("pageSize", Pagination.DEFAULT_ITEMS_PER_PAGE.toString())
                }
            }
        }
    }

    override suspend fun getQrCode(): BodyResponse<QrCodeData> =
        client.get("${baseUrl.url}/b2bapp/delivery/qr/code") {
            withMainToken()
        }


    override suspend fun regenerateQrCode(qrCode: String): BodyResponse<QrCodeData> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/delivery/qr/regenerate") {
                withMainToken()
                url {
                    parameters.append("qrCode", qrCode)
                }
            }
        }

    override suspend fun getAutoApprovePreference(): BodyResponse<AutoApprove> =
        client.get("${baseUrl.url}/b2bapp/preference/autoapprove") {
            withMainToken()
        }

    override suspend fun setAutoApprovePreference(isEnabled: Boolean): BodyResponse<AutoApprove> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/preference/autoapprove/save") {
                withMainToken()
                jsonBody(mapOf("autoApprove" to isEnabled))
            }
        }

    override suspend fun submitPersonalDetails(userRegistration1: EmployeeRegistration1): BodyResponse<AddEmployee> =
        simpleRequest {
            client.post("${baseUrl.url}/medico/employee/step1") { // Step1 is validated against the authorisation service (medico-auth0)
                withMainToken()
                jsonBody(userRegistration1)
            }
        }

    override suspend fun submitAddressDetails(userRegistration2: EmployeeRegistration2): BodyResponse<AddEmployee> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/employee/step2") {
                withMainToken()
                jsonBody(userRegistration2)
            }
        }

    override suspend fun submitAadhaarDetails(aadhaar: String): BodyResponse<AddEmployee> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/employee/step3") {
                withMainToken()
                jsonBody(mapOf("aadhaarCardNo" to aadhaar))
            }
        }

    override suspend fun submitEmployee(employee: SubmitEmployeeRegistration): BodyResponse<AddEmployee> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/employee/submit") {
                withMainToken()
                jsonBody(employee)
            }
        }

    override suspend fun getAllEmployees(): BodyResponse<ViewEmployee> =
        client.get("${baseUrl.url}/b2bapp/employee/all") {
            withMainToken()
        }

    override suspend fun deleteEmployee(id: String): BodyResponse<AddEmployee> =
        simpleRequest {
            client.post("${baseUrl.url}/b2bapp/employee/delete") {
                withMainToken()
                jsonBody(mapOf("id" to id))
            }
        }

    override suspend fun getCompanies(
        unitCode: String,
        page: Int
    ): BodyResponse<InventoryCompanies> =
        simpleRequest {
            client.post("${baseUrl.url}/inventory/companies/view") {
                header("X-TENANT-ID", unitCode)
                withMainToken()
                url {
                    parameters.append("page", page.toString())
                    parameters.append("pageSize", Pagination.DEFAULT_ITEMS_PER_PAGE.toString())
                }
            }
        }

    override suspend fun getAllBanners(
        page: Int,
        search: String
    ): BodyResponse<AllBanners> = simpleRequest {
        client.get("${baseUrl.url}/dashboard/banners/all") {
            withMainToken()
            url {
                if (search.isNotEmpty())
                    parameters.append("search", search)
                parameters.append("page", page.toString())
                parameters.append("pageSize", Pagination.DEFAULT_ITEMS_PER_PAGE.toString())
            }
        }
    }

    override suspend fun getAllDeals(
        page: Int,
        search: String,
        unitCode: String,
        promoCode: String
    ): BodyResponse<AllDeals> = simpleRequest {
        client.get("${baseUrl.url}/dashboard/deals/all") {
            withMainToken()
            url {
                if (search.isNotEmpty())
                    parameters.append("search", search)
                parameters.append("page", page.toString())
                parameters.append("pageSize", Pagination.ITEMS_PER_PAGE_10.toString())
                parameters.append("b2bUnitCode", unitCode)
                parameters.append("promoType", promoCode)
            }
        }
    }

    override suspend fun getManufacturers(
        page: Int,
        search: String
    ): BodyResponse<ManufacturersListData> = simpleRequest {
        client.get("${baseUrl.url}/products/mnfr/search") {
            withMainToken()
            url {
                if (search.isNotEmpty())
                    parameters.append("search", search)
                parameters.append("page", page.toString())
                parameters.append("pageSize", Pagination.DEFAULT_ITEMS_PER_PAGE.toString())
            }
        }
    }

    // Utils
    private inline fun HttpRequestBuilder.withB2bCodeToken(finalToken: String) {
        applyHeader(finalToken)
    }

    private suspend inline fun HttpRequestBuilder.withMainToken() {
        val finalToken = tokenStorage.getMainToken()?.let { _ ->
            retry(Interval.Linear(100, 5)) {
                val tokenInfo = requireNotNull(tokenStorage.getMainToken())
                if (tokenInfo.isExpired) {
                    fetchMainToken(tokenInfo)?.also(tokenStorage::saveMainToken)
                } else {
                    tokenInfo
                }
            }
        }

        if (finalToken != null) {
            applyHeader(finalToken)
        } else {
            "no main token for request".warnIt()
            EventCollector.sendEvent(Event.Action.Auth.LogOut(false))
        }
    }

    private suspend inline fun HttpRequestBuilder.withTempToken(tokenType: TempToken) {
        val finalToken = retry(Interval.Linear(100, 5)) {
            val tokenInfo =
                tokenStorage.getTempTokenOnce(tokenType.serverValue)?.takeIf { !it.isExpired }
            if (tokenInfo == null || tokenInfo.id != tokenType.serverValue) {
                when (tokenType) {
                    TempToken.REGISTRATION -> fetchRegistrationToken()
                    TempToken.UPDATE_PASSWORD -> tokenInfo
                }?.also {
                    tokenStorage.saveTempToken(tokenType.serverValue, it)
                }
            } else {
                tokenStorage.saveTempToken(tokenType.serverValue, tokenInfo)
                tokenInfo
            }
        }
        if (finalToken != null) {
            applyHeader(finalToken)
        } else {
            "no temp token (${tokenType.serverValue}) for request".warnIt()
        }
    }

    private suspend fun fetchMainToken(currentToken: TokenInfo): TokenInfo? {
        return client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/medico/refresh-token") {
            applyHeader(currentToken)
            jsonBody(RefreshTokenRequest(currentToken.refreshToken))
        }.getBodyOrNull()
    }

    private suspend inline fun fetchRegistrationToken(): TokenInfo? {
        return client.get<BodyResponse<TokenInfo>>("${baseUrl.url}/registration")
            .getBodyOrNull()
    }

    private inline fun HttpRequestBuilder.applyHeader(tokenInfo: TokenInfo) {
        header("Authorization", "Bearer ${tokenInfo.token}")
    }

    private inline fun HttpRequestBuilder.applyHeader(tokenInfo: String) {
        header("X-TENANT-ID", tokenInfo)
    }

    private inline fun HttpRequestBuilder.jsonBody(body: Any) {
        contentType(ContentType.parse("application/json"))
        this.body = body
    }

    private suspend inline fun <T, V> fullRequest(
        dispatcher: CoroutineDispatcher = ktorDispatcher,
        crossinline call: suspend HttpClient.() -> Response<T, V>,
    ): Response<T, V> = dispatcher {
        val result = runCatching { client.call() }
        if (result.isSuccess || crashOnServerError) {
            result.getOrThrow()
        } else {
            Response(
                error = ErrorCode.somethingWentWrong,
                type = "error",
            )
        }
    }

    private suspend inline fun <T> simpleRequest(
        dispatcher: CoroutineDispatcher = ktorDispatcher,
        crossinline call: suspend HttpClient.() -> Response<T, MapBody>,
    ): Response<T, MapBody> = fullRequest<T, MapBody>(dispatcher, call)

    enum class TempToken(val serverValue: String) {
        UPDATE_PASSWORD("updatepwd"),
        REGISTRATION("registration");
    }

    enum class BaseUrl(val url: String) {
        DEV("https://develop-api-gateway.medicostores.com"),
        STAG("https://staging-api-gateway.medicostores.com"),
        PROD("https://partner-api-gateway.medicostores.com");
    }


}

expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

internal fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
    // TODO remove after kserilize lib updated from 1.2.1
    useAlternativeNames = false
}
