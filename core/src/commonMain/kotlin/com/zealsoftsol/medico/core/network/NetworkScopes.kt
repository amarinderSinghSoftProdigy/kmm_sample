package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AnyResponse
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BatchStatusUpdateRequest
import com.zealsoftsol.medico.data.BatchUpdateRequest
import com.zealsoftsol.medico.data.BatchesData
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.ConfigData
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.DashboardData
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.InStoreCart
import com.zealsoftsol.medico.data.InStoreCartRequest
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.InStoreUserRegistration
import com.zealsoftsol.medico.data.InventoryData
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceResponse
import com.zealsoftsol.medico.data.LicenseDocumentData
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.OfferData
import com.zealsoftsol.medico.data.OfferProduct
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderResponse
import com.zealsoftsol.medico.data.OrderResponseInvoice
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaginatedData
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
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchDataItem
import com.zealsoftsol.medico.data.SearchResponse
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UnreadNotifications
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
import com.zealsoftsol.medico.data.WhatsappData


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
        suspend fun uploadDocument(uploadData: LicenseDocumentData): BodyResponse<UploadResponseData>
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
        suspend fun getOrderInvoice(
            request: ConfirmOrderRequest
        ): BodyResponse<OrderResponseInvoice>

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

        suspend fun takeActionOnOrderEntries(
            orderData: ConfirmOrderRequest
        ): BodyResponse<OrderResponse>

        suspend fun changePaymentMethod(
            unitCode: String,
            orderId: String,
            type: String
        ): AnyResponse

        suspend fun editDiscount(
            unitCode: String,
            orderId: String,
            discount: Double
        ): BodyResponse<OrderResponse>
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

        suspend fun deleteInStoreOrder(unitCode: String, id: String): AnyResponse
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
        suspend fun getHsnCodes(
            search: String,
            pagination: Pagination
        ): BodyResponse<PaginatedData<SearchDataItem>>

        suspend fun saveNewOrder(request: OrderNewQtyRequest): BodyResponse<OrderResponse>

        suspend fun rejectEntry(
            orderEntryId: String,
            spid: String,
            reasonCode: String
        ): BodyResponse<OrderResponse>

        suspend fun acceptEntry(orderEntryId: String, spid: String): BodyResponse<OrderResponse>

        suspend fun getBatches(
            unitCode: String,
            spid: String
        ): BodyResponse<BatchesData>
    }

    interface InventoryStore : NetworkScope {
        suspend fun getInventoryData(
            unitCode: String,
            search: String?,
            page: Int,
            manufacturer: String?
        ): BodyResponse<InventoryData>

        suspend fun getBatches(
            unitCode: String,
            spid: String
        ): BodyResponse<BatchesData>

        suspend fun editBatches(
            unitCode: String,
            request: BatchUpdateRequest
        ): BodyResponse<String>

        suspend fun updateBatchStatus(
            unitCode: String,
            request: BatchStatusUpdateRequest
        ): BodyResponse<String>
    }

    interface BatchesStore : NetworkScope {
        suspend fun getBatches(
            unitCode: String,
            spid: String
        ): BodyResponse<BatchesData>
    }

    interface ProfileImage : NetworkScope {
        suspend fun getProfileImageData(): BodyResponse<ProfileImageData>
        suspend fun saveProfileImageData(
            profileImageData: ProfileImageUpload, type: String
        ): BodyResponse<ProfileResponseData>
    }

    interface OffersStore : NetworkScope {
        //VIew offer methods
        suspend fun getOffersData(
            unitCode: String,
            search: String?,
            manufacturer: ArrayList<String>?,
            pagination: Pagination
        ): BodyResponse<OfferData>

        suspend fun updateOffer(
            unitCode: String,
            request: PromotionUpdateRequest
        ): BodyResponse<String>

        //Create offer methods
        suspend fun getPromotionTypes(
            unitCode: String,
        ): BodyResponse<PromotionTypeData>

        suspend fun autocompleteOffers(
            input: String,
            unitCode: String,
        ): BodyResponse<List<AutoComplete>>

        suspend fun getAutocompleteItem(
            input: String,
            unitCode: String,
        ): BodyResponse<OfferProduct>

        suspend fun saveOffer(
            unitCode: String,
            request: OfferProductRequest
        ): BodyResponse<String>

        suspend fun editOffer(
            unitCode: String,
            promoCode: String, request: OfferProductRequest
        ): BodyResponse<String>
    }

    interface QrCodeStore: NetworkScope {
        suspend fun getQrCode(): BodyResponse<QrCodeData>

        suspend fun regenerateQrCode(qrCode: String): BodyResponse<QrCodeData>

    }
}