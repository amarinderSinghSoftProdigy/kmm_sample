package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.*

interface NetworkScope {

    @Deprecated("break down")
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): BodyResponse<TokenInfo>
        suspend fun logout(): AnyResponse

        suspend fun checkCanResetPassword(phoneNumber: String): AnyResponse
        suspend fun sendOtp(phoneNumber: String): AnyResponse
        suspend fun retryOtp(phoneNumber: String): AnyResponse
        suspend fun verifyOtp(phoneNumber: String, otp: String): BodyResponse<TokenInfo>
    }

    interface SignUp : NetworkScope {
        suspend fun signUpValidation1(userRegistration1: UserRegistration1): ValidationResponse<UserValidation1>
        suspend fun signUpValidation2(userRegistration2: UserRegistration2): ValidationResponse<UserValidation2>
        suspend fun signUpValidation3(userRegistration3: UserRegistration3): ValidationResponse<UserValidation3>

        @Deprecated("move to geo network scope")
        suspend fun getLocationData(pincode: String): Response<LocationData, PincodeValidation>
        suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): AnyResponse
        suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): BodyResponse<StorageKeyResponse>
        suspend fun signUp(submitRegistration: SubmitRegistration): AnyResponse

        suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3): ValidationResponse<UserValidation3>
        suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer): AnyResponse
    }

    interface Password {
        suspend fun verifyPassword(password: String): ValidationResponse<PasswordValidation>
        suspend fun changePassword(
            phoneNumber: String?,
            password: String
        ): ValidationResponse<PasswordValidation>
    }

    interface Customer : NetworkScope {
        suspend fun getDashboard(unitCode: String): BodyResponse<DashboardData>
        suspend fun getCustomerData(): BodyResponse<CustomerData>
    }

    interface Product : NetworkScope {
        suspend fun getProductData(productCode: String): BodyResponse<ProductResponse>
        suspend fun buyProductInfo(
            productCode: String,
            latitude: Double,
            longitude: Double,
        ): BodyResponse<ProductBuyResponse>

        suspend fun buyProductSelectSeasonBoyRetailer(
            productCode: String,
            unitCode: String,
            sellerUnitCode: String?,
            latitude: Double,
            longitude: Double,
        ): BodyResponse<ProductSeasonBoyRetailerSelectResponse>

        suspend fun getQuotedProductData(productCode: String): BodyResponse<ProductBuyResponse>
    }

    interface Search : NetworkScope {
        suspend fun search(
            sort: String?,
            query: List<Pair<String, String>>,
            unitCode: String?,
            latitude: Double,
            longitude: Double,
            pagination: Pagination,
        ): BodyResponse<SearchResponse>

        suspend fun autocomplete(
            input: String,
            unitCodeForStores: String?,
        ): BodyResponse<List<AutoComplete>>
    }

    interface Management : NetworkScope {
        suspend fun getManagementInfo(
            unitCode: String,
            isSeasonBoy: Boolean,
            forUserType: UserType,
            criteria: ManagementCriteria,
            search: String,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<EntityInfo>>

        suspend fun subscribeRequest(subscribeRequest: SubscribeRequest): AnyResponse
    }

    interface Notification : NetworkScope {
        suspend fun sendFirebaseToken(token: String): AnyResponse
        suspend fun getNotifications(
            search: String,
            filter: NotificationFilter,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<NotificationData>>

        suspend fun getUnreadNotifications(): BodyResponse<UnreadNotifications>

        suspend fun selectNotificationAction(
            id: String,
            actionRequest: NotificationActionRequest
        ): AnyResponse

        suspend fun getNotificationDetails(id: String): BodyResponse<NotificationDetails>
    }

    interface Stores : NetworkScope {
        suspend fun getStores(
            unitCode: String,
            search: String,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<Store>>
    }

    interface Cart : NetworkScope {
        suspend fun getCart(unitCode: String): BodyResponse<CartData>
        suspend fun deleteCart(unitCode: String, cartId: String): AnyResponse

        suspend fun addCartEntry(request: CartRequest): BodyResponse<CartData>
        suspend fun updateCartEntry(request: CartRequest): BodyResponse<CartData>
        suspend fun deleteCartEntry(request: CartRequest): BodyResponse<CartData>

        suspend fun deleteSellerCart(
            unitCode: String,
            cartId: String,
            sellerUnitCode: String
        ): BodyResponse<CartData>

        suspend fun confirmCart(request: CartOrderRequest): BodyResponse<CartConfirmData>
        suspend fun submitCart(request: CartOrderRequest): BodyResponse<CartSubmitResponse>
    }

    interface Orders : NetworkScope {

        suspend fun getOrders(
            type: OrderType,
            unitCode: String,
            search: String,
            from: Long?,
            to: Long?,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<Order>>

        suspend fun getOrder(
            type: OrderType,
            unitCode: String,
            orderId: String
        ): BodyResponse<OrderResponse>

        suspend fun saveNewOrderQty(request: OrderNewQtyRequest): BodyResponse<OrderResponse>
        suspend fun confirmOrder(request: ConfirmOrderRequest): AnyResponse

        suspend fun getInvoices(
            isPoInvoice: Boolean,
            unitCode: String,
            search: String,
            from: Long?,
            to: Long?,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<Invoice>>

        suspend fun getInvoice(
            isPoInvoice: Boolean,
            unitCode: String,
            invoiceId: String
        ): BodyResponse<InvoiceResponse>
    }

    interface Help : NetworkScope {
        suspend fun getHelp(): BodyResponse<HelpData>
    }

    interface Config : NetworkScope {
        suspend fun getConfig(): BodyResponse<ConfigData>
    }

    interface InStore : NetworkScope {
        suspend fun getInStoreSellers(
            unitCode: String,
            search: String,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<InStoreSeller>>

        suspend fun searchInStoreSeller(
            unitCode: String,
            search: String,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<InStoreProduct>>

        suspend fun getInStoreUsers(
            unitCode: String,
            search: String,
            pagination: Pagination,
        ): BodyResponse<PaginatedData<InStoreUser>>

        suspend fun addUser(
            registration: InStoreUserRegistration
        ): AnyResponse

        suspend fun getInStoreCart(unitCode: String): BodyResponse<InStoreCart>
        suspend fun deleteInStoreCart(unitCode: String, cartId: String): AnyResponse

        suspend fun addInStoreCartEntry(request: InStoreCartRequest): BodyResponse<InStoreCart>
        suspend fun updateInStoreCartEntry(request: InStoreCartRequest): BodyResponse<InStoreCart>
        suspend fun deleteInStoreCartEntry(
            unitCode: String,
            entryId: String
        ): BodyResponse<InStoreCart>

        suspend fun confirmInStoreCart(unitCode: String, id: String): AnyResponse
    }

    interface WhatsappStore : NetworkScope {
        suspend fun getWhatsappPreferences(unitCode: String): BodyResponse<WhatsappData>
        suspend fun saveWhatsappPreferences(
            language: String,
            phoneNumber: String,
            unitCode: String
        ): AnyResponse
    }

    interface OrderHsnEditStore : NetworkScope {
        suspend fun getHsnCodes(): BodyResponse<SearchData>
        suspend fun saveHsnCodes(unitCode: String): AnyResponse
    }

}