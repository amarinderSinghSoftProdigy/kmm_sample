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
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceResponse
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.MapBody
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderResponse
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
import com.zealsoftsol.medico.data.RefreshTokenRequest
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchResponse
import com.zealsoftsol.medico.data.SimpleResponse
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UnreadNotifications
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.VerifyOtpRequest
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
import kotlinx.coroutines.invoke
import kotlinx.serialization.json.Json

class NetworkClient(
    engine: HttpClientEngineFactory<*>,
    private val tokenStorage: TokenStorage,
    useNetworkInterceptor: Boolean,
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
    NetworkScope.Orders {

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

    override suspend fun login(request: UserRequest): Response.Wrapped<ErrorCode> = ktorDispatcher {
        client.post<SimpleResponse<TokenInfo>>("${baseUrl.url}/medico/login") {
            jsonBody(request)
        }.also {
            it.getBodyOrNull()?.let(tokenStorage::saveMainToken)
        }.getWrappedError()
    }

    override suspend fun logout(): Boolean = ktorDispatcher {
        client.post<Response.Status>("${baseUrl.url}/medico/logout") {
            withMainToken()
        }.isSuccess
    }

    override suspend fun checkCanResetPassword(phoneNumber: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/medico/forgetpwd") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }.getWrappedError()
        }

    override suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/notifications/sendOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }.getWrappedError()
        }

    override suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/notifications/retryOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }.getWrappedError()
        }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            val body =
                client.post<SimpleResponse<TokenInfo>>("${baseUrl.url}/notifications/verifyOTP") {
                    withTempToken(TempToken.REGISTRATION)
                    jsonBody(VerifyOtpRequest(phoneNumber, otp))
                }
            if (body.isSuccess) {
                body.getBodyOrNull()
                    ?.let { tokenStorage.saveTempToken(TempToken.UPDATE_PASSWORD.serverValue, it) }
            }
            body.getWrappedError()
        }

    override suspend fun verifyPassword(password: String): Response.Wrapped<PasswordValidation> =
        ktorDispatcher {
            client.post<Response.Body<String, PasswordValidation>>("${baseUrl.url}/myaccount/currentPwd") {
                withMainToken()
                jsonBody(mapOf("currentPassword" to password))
            }.getWrappedValidation()
        }

    override suspend fun changePassword(
        phoneNumber: String?,
        password: String
    ): Response.Wrapped<PasswordValidation> =
        ktorDispatcher {
            val url = if (phoneNumber != null)
                "${baseUrl.url}/medico/forgetpwd/update"
            else
                "${baseUrl.url}/myaccount/changePwd"
            client.post<Response.Body<MapBody, PasswordValidation>>(url) {
                if (phoneNumber != null) {
                    withTempToken(TempToken.UPDATE_PASSWORD)
                    jsonBody(PasswordResetRequest(phoneNumber, password, password))
                } else {
                    withMainToken()
                    jsonBody(PasswordResetRequest2(password, password))
                }
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation1>>("${baseUrl.url}/registration/step1") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration1)
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation2>>("${baseUrl.url}/registration/step2") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration2)
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation3>>("${baseUrl.url}/registration/step3") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration3)
            }.getWrappedValidation()
        }

    override suspend fun getLocationData(pincode: String): Response.Body<LocationData, PincodeValidation> =
        ktorDispatcher {
            client.get<Response.Body<LocationData, PincodeValidation>>("${baseUrl.url}/geo/pincode/$pincode") {
                withTempToken(TempToken.REGISTRATION)
            }
        }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean = ktorDispatcher {
        client.post<Response.Status>("${baseUrl.url}/upload/aadhaar") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(aadhaarData)
        }.isSuccess
    }

    override suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): Response.Wrapped<StorageKeyResponse> =
        ktorDispatcher {
            client.post<SimpleResponse<StorageKeyResponse>>("${baseUrl.url}/upload/druglicense") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(licenseData)
            }.getWrappedBody()
        }

    override suspend fun signUp(submitRegistration: SubmitRegistration): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/registration${if (submitRegistration.isSeasonBoy) "/seasonboys" else ""}/submit") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(submitRegistration)
            }.getWrappedError()
        }

    override suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation3>>("${baseUrl.url}/sbret/verify") {
                withMainToken()
                jsonBody(userRegistration3)
            }.getWrappedValidation()
        }

    override suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/sbret/add") {
                withMainToken()
                jsonBody(data)
            }.getWrappedError()
        }

    override suspend fun getCustomerData(): Response.Wrapped<CustomerData> = ktorDispatcher {
        client.get<SimpleResponse<CustomerData>>("${baseUrl.url}/medico/customer/details") {
            withMainToken()
        }.getWrappedBody()
    }

    override suspend fun search(
        sort: String?,
        query: List<Pair<String, String>>,
        unitCode: String?,
        latitude: Double,
        longitude: Double,
        pagination: Pagination,
    ): Response.Wrapped<SearchResponse> = ktorDispatcher {
        client.get<SimpleResponse<SearchResponse>>("${baseUrl.url}/search/global") {
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
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun autocomplete(input: String): Response.Wrapped<List<AutoComplete>> =
        ktorDispatcher {
            client.get<SimpleResponse<List<AutoComplete>>>("${baseUrl.url}/search/suggest") {
                withMainToken()
                url {
                    parameters.append("suggest", input)
                }
            }.getWrappedBody()
        }

    override suspend fun getProductData(productCode: String): Response.Wrapped<ProductResponse> =
        ktorDispatcher {
            client.get<SimpleResponse<ProductResponse>>("${baseUrl.url}/search/product/$productCode") {
                withMainToken()
            }.getWrappedBody()
        }

    override suspend fun buyProductInfo(productCode: String): Response.Wrapped<ProductBuyResponse> =
        ktorDispatcher {
            client.get<SimpleResponse<ProductBuyResponse>>("${baseUrl.url}/search/buy/${productCode}") {
                withMainToken()
            }.getWrappedBody()
        }

    override suspend fun buyProductSelectSeasonBoyRetailer(
        productCode: String,
        unitCode: String,
        sellerUnitCode: String?
    ): Response.Wrapped<ProductSeasonBoyRetailerSelectResponse> = ktorDispatcher {
        client.get<SimpleResponse<ProductSeasonBoyRetailerSelectResponse>>("${baseUrl.url}/search/sb/select/${productCode}") {
            withMainToken()
            url {
                parameters.append("buyerUnitCode", unitCode)
                if (sellerUnitCode != null) {
                    parameters.append("sellerUnitCode", sellerUnitCode)
                }
            }
        }.getWrappedBody()
    }

    override suspend fun getQuotedProductData(productCode: String): Response.Wrapped<ProductBuyResponse> =
        ktorDispatcher {
            client.get<SimpleResponse<ProductBuyResponse>>("${baseUrl.url}/search/quote/${productCode}") {
                withMainToken()
            }.getWrappedBody()
        }

    override suspend fun getManagementInfo(
        unitCode: String,
        isSeasonBoy: Boolean,
        forUserType: UserType,
        criteria: ManagementCriteria,
        search: String,
        pagination: Pagination
    ): Response.Wrapped<PaginatedData<EntityInfo>> = ktorDispatcher {
        client.get<SimpleResponse<PaginatedData<EntityInfo>>>(
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
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun subscribeRequest(subscribeRequest: SubscribeRequest): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/b2bapp/subscriptions/subscribe") {
                withMainToken()
                jsonBody(subscribeRequest)
            }.getWrappedError()
        }

    override suspend fun sendFirebaseToken(token: String): Boolean = ktorDispatcher {
        client.post<Response.Status>("${baseUrl.url}/firebase/add/token") {
            withMainToken()
            jsonBody(mapOf("token" to token, "channel" to "MOBILE"))
        }.isSuccess
    }

    override suspend fun getNotifications(
        search: String,
        filter: NotificationFilter,
        pagination: Pagination,
    ): Response.Wrapped<PaginatedData<NotificationData>> = ktorDispatcher {
        client.get<SimpleResponse<PaginatedData<NotificationData>>>("${baseUrl.url}/notifications/all") {
            withMainToken()
            url {
                parameters.apply {
                    if (search.isNotEmpty()) append("search", search)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                    append("notificationType", filter.serverValue)
                }
            }
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getUnreadNotifications(): Response.Wrapped<UnreadNotifications> =
        ktorDispatcher {
            client.get<SimpleResponse<UnreadNotifications>>("${baseUrl.url}/notifications/unread") {
                withMainToken()
            }.getWrappedBody()
        }

    override suspend fun selectNotificationAction(
        id: String,
        actionRequest: NotificationActionRequest
    ): Response.Wrapped<ErrorCode> = ktorDispatcher {
        client.post<SimpleResponse<MapBody>>("${baseUrl.url}/b2bapp/subscriptions/submit") {
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
        }.getWrappedError()
    }

    override suspend fun getNotificationDetails(id: String): Response.Wrapped<NotificationDetails> =
        ktorDispatcher {
            client.get<SimpleResponse<NotificationDetails>>("${baseUrl.url}/b2bapp/notification/$id/detail") {
                withMainToken()
            }.getWrappedBody()
        }

    override suspend fun getStores(
        unitCode: String,
        search: String,
        pagination: Pagination,
    ): Response.Wrapped<PaginatedData<Store>> = ktorDispatcher {
        client.get<SimpleResponse<PaginatedData<Store>>>("${baseUrl.url}/b2bapp/stores/${unitCode}") {
            withMainToken()
            url {
                parameters.apply {
                    append("search", search)
                    append("page", pagination.nextPage().toString())
                    append("pageSize", pagination.itemsPerPage.toString())
                }
            }
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getCart(unitCode: String): Response.Wrapped<CartData> = ktorDispatcher {
        client.get<SimpleResponse<CartData>>("${baseUrl.url}/cart") {
            withMainToken()
            url {
                parameters.append("buyerUnitCode", unitCode)
            }
        }.getWrappedBody()
    }

    override suspend fun addCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        ktorDispatcher {
            client.post<SimpleResponse<CartData>>("${baseUrl.url}/cart/addEntry") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun deleteCart(unitCode: String, cartId: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/cart/deleteCart") {
                withMainToken()
                jsonBody(mapOf("cartId" to cartId, "buyerUnitCode" to unitCode))
            }.getWrappedError()
        }

    override suspend fun updateCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        ktorDispatcher {
            client.post<SimpleResponse<CartData>>("${baseUrl.url}/cart/updateEntry") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun deleteCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        ktorDispatcher {
            client.post<SimpleResponse<CartData>>("${baseUrl.url}/cart/deleteEntry") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun deleteSellerCart(
        unitCode: String,
        cartId: String,
        sellerUnitCode: String
    ): Response.Wrapped<CartData> = ktorDispatcher {
        client.post<SimpleResponse<CartData>>("${baseUrl.url}/cart/deleteSellerCart") {
            withMainToken()
            jsonBody(
                mapOf(
                    "cartId" to cartId,
                    "buyerUnitCode" to unitCode,
                    "sellerUnitCode" to sellerUnitCode
                )
            )
        }.getWrappedBody()
    }

    override suspend fun confirmCart(request: CartOrderRequest): Response.Wrapped<CartConfirmData> =
        ktorDispatcher {
            client.post<SimpleResponse<CartConfirmData>>("${baseUrl.url}/cart/confirm") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun submitCart(request: CartOrderRequest): Response.Wrapped<CartSubmitResponse> =
        ktorDispatcher {
            client.post<SimpleResponse<CartSubmitResponse>>("${baseUrl.url}/cart/submit") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun getHelp(): Response.Wrapped<HelpData> = ktorDispatcher {
        client.get<SimpleResponse<HelpData>>("${baseUrl.url}/medico/help") {
            withMainToken()
        }.getWrappedBody()
    }

    override suspend fun getOrders(
        type: OrderType,
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination,
    ): Response.Wrapped<PaginatedData<Order>> = ktorDispatcher {
        client.get<SimpleResponse<PaginatedData<Order>>>("${baseUrl.url}/orders${type.path}") {
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
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getOrder(
        type: OrderType,
        unitCode: String,
        orderId: String
    ): Response.Wrapped<OrderResponse> = ktorDispatcher {
        client.get<SimpleResponse<OrderResponse>>("${baseUrl.url}/orders${type.path}$orderId") {
            withMainToken()
            url {
                parameters.apply {
                    append("b2bUnitCode", unitCode)
                }
            }
        }.getWrappedBody()
    }

    override suspend fun confirmOrder(request: ConfirmOrderRequest): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleResponse<MapBody>>("${baseUrl.url}/orders/po/entries/accept") {
                withMainToken()
                jsonBody(request)
            }.getWrappedError()
        }

    override suspend fun saveNewOrderQty(request: OrderNewQtyRequest): Response.Wrapped<OrderResponse> =
        ktorDispatcher {
            client.post<SimpleResponse<OrderResponse>>("${baseUrl.url}/orders/po/entries/save") {
                withMainToken()
                jsonBody(request)
            }.getWrappedBody()
        }

    override suspend fun getInvoices(
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination
    ): Response.Wrapped<PaginatedData<Invoice>> = ktorDispatcher {
        client.get<SimpleResponse<PaginatedData<Invoice>>>("${baseUrl.url}/invoices") {
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
        }.getWrappedBody().also {
            if (it.isSuccess) pagination.pageLoaded()
        }
    }

    override suspend fun getInvoice(
        unitCode: String,
        invoiceId: String
    ): Response.Wrapped<InvoiceResponse> = ktorDispatcher {
        client.get<SimpleResponse<InvoiceResponse>>("${baseUrl.url}/invoices/$invoiceId") {
            withMainToken()
            url {
                parameters.apply {
                    append("b2bUnitCode", unitCode)
                }
            }
        }.getWrappedBody()
    }

    // Utils

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
                    TempToken.OTP -> fetchOtpToken()
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
        return client.post<SimpleResponse<TokenInfo>>("${baseUrl.url}/medico/refresh-token") {
            applyHeader(currentToken)
            jsonBody(RefreshTokenRequest(currentToken.refreshToken))
        }.getBodyOrNull()
    }

    private suspend inline fun fetchRegistrationToken(): TokenInfo? {
        return client.get<SimpleResponse<TokenInfo>>("${baseUrl.url}/registration")
            .getBodyOrNull()
    }

    private suspend inline fun fetchOtpToken(): TokenInfo? {
        TODO("no url for this type of token")
        return client.get<SimpleResponse<TokenInfo>>("new url")
            .getBodyOrNull()
    }

    private inline fun HttpRequestBuilder.applyHeader(tokenInfo: TokenInfo) {
        header("Authorization", "Bearer ${tokenInfo.token}")
    }

    private inline fun HttpRequestBuilder.jsonBody(body: Any) {
        contentType(ContentType.parse("application/json"))
        this.body = body
    }

//    private suspend inline fun <T> request(
//        dispatcher: CoroutineDispatcher = ktorDispatcher,
//        crossinline call: HttpClient.() -> T
//    ) = dispatcher {
//        val result = runCatching { client.call() }
//        if (result.isSuccess) {
//            result.getOrThrow()
//        } else {
//            if (result.exceptionOrNull() is )
//            null
//        }
//    }

    enum class TempToken(val serverValue: String) {
        OTP("otp"),
        UPDATE_PASSWORD("updatepwd"),
        REGISTRATION("registration");
    }

    enum class BaseUrl(val url: String) {
        DEV("https://develop-api-gateway.medicostores.com"),
//        PROD("no production url yet"),
    }
}

expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

internal fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
}

