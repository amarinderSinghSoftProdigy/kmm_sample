package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.CreateRetailer
import com.zealsoftsol.medico.data.CustomerData
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.ManagementCriteria
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.ProductBuyResponse
import com.zealsoftsol.medico.data.ProductResponse
import com.zealsoftsol.medico.data.ProductSeasonBoyRetailerSelectResponse
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SearchResponse
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.SubscribeRequest
import com.zealsoftsol.medico.data.UnreadNotifications
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

interface NetworkScope {

    @Deprecated("break down")
    interface Auth : NetworkScope {
        suspend fun login(request: UserRequest): Response.Wrapped<ErrorCode>
        suspend fun logout(): Boolean

        suspend fun checkCanResetPassword(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode>
        suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode>
    }

    interface SignUp : NetworkScope {
        suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1>
        suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2>
        suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3>

        @Deprecated("move to geo network scope")
        suspend fun getLocationData(pincode: String): Response.Body<LocationData, PincodeValidation>
        suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean
        suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): Response.Wrapped<StorageKeyResponse>
        suspend fun signUp(submitRegistration: SubmitRegistration): Response.Wrapped<ErrorCode>

        suspend fun verifyRetailerTraderDetails(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3>
        suspend fun createdRetailerWithSeasonBoy(data: CreateRetailer): Response.Wrapped<ErrorCode>
    }

    interface Password {
        suspend fun verifyPassword(password: String): Response.Wrapped<PasswordValidation>
        suspend fun changePassword(
            phoneNumber: String?,
            password: String
        ): Response.Wrapped<PasswordValidation>
    }

    interface Customer : NetworkScope {
        suspend fun getCustomerData(): Response.Wrapped<CustomerData>
    }

    interface Product : NetworkScope {
        suspend fun getProductData(productCode: String): Response.Wrapped<ProductResponse>
        suspend fun buyProductInfo(productCode: String): Response.Wrapped<ProductBuyResponse>
        suspend fun buyProductSelectSeasonBoyRetailer(
            productCode: String,
            unitCode: String,
            sellerUnitCode: String?,
        ): Response.Wrapped<ProductSeasonBoyRetailerSelectResponse>

        suspend fun getQuotedProductData(productCode: String): Response.Wrapped<ProductBuyResponse>
    }

    interface Search : NetworkScope {
        suspend fun search(
            query: List<Pair<String, String>>,
            unitCode: String?,
            latitude: Double,
            longitude: Double,
            pagination: Pagination,
        ): Response.Wrapped<SearchResponse>

        suspend fun autocomplete(input: String): Response.Wrapped<List<AutoComplete>>
    }

    interface Management : NetworkScope {
        suspend fun getManagementInfo(
            unitCode: String,
            isSeasonBoy: Boolean,
            forUserType: UserType,
            criteria: ManagementCriteria,
            search: String,
            pagination: Pagination,
        ): Response.Wrapped<PaginatedData<EntityInfo>>

        suspend fun subscribeRequest(subscribeRequest: SubscribeRequest): Response.Wrapped<ErrorCode>
    }

    interface Notification : NetworkScope {
        suspend fun sendFirebaseToken(token: String): Boolean
        suspend fun getNotifications(
            search: String,
            pagination: Pagination
        ): Response.Wrapped<PaginatedData<NotificationData>>

        suspend fun getUnreadNotifications(): Response.Wrapped<UnreadNotifications>

        suspend fun selectNotificationAction(
            id: String,
            actionRequest: NotificationActionRequest
        ): Response.Wrapped<ErrorCode>

        suspend fun getNotificationDetails(id: String): Response.Wrapped<NotificationDetails>
    }

    interface Stores : NetworkScope {
        suspend fun getStores(
            unitCode: String,
            search: String,
            pagination: Pagination,
        ): Response.Wrapped<PaginatedData<Store>>
    }

    interface Cart : NetworkScope {
        suspend fun getCart(unitCode: String): Response.Wrapped<CartData>
        suspend fun deleteCart(unitCode: String, cartId: String): Response.Wrapped<ErrorCode>

        suspend fun addCartEntry(request: CartRequest): Response.Wrapped<CartData>
        suspend fun updateCartEntry(request: CartRequest): Response.Wrapped<CartData>
        suspend fun deleteCartEntry(request: CartRequest): Response.Wrapped<CartData>

        suspend fun deleteSellerCart(
            unitCode: String,
            cartId: String,
            sellerUnitCode: String
        ): Response.Wrapped<CartData>

        suspend fun confirmCart(request: CartOrderRequest): Response.Wrapped<CartConfirmData>
        suspend fun submitCart(request: CartOrderRequest): Response.Wrapped<CartSubmitResponse>
    }

    interface Help : NetworkScope {
        suspend fun getHelp(): Response.Wrapped<HelpData>
    }
}