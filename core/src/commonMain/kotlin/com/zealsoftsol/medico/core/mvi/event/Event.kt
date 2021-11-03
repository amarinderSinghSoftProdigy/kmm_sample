package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserType
import kotlin.reflect.KClass

sealed class Event {
    abstract val typeClazz: KClass<*>

    sealed class Action : Event() {

        sealed class Auth : Action() {
            override val typeClazz: KClass<*> = Auth::class

            object LogIn : Auth()
            data class LogOut(val notifyServer: Boolean) : Auth()
            data class UpdateAuthCredentials(val emailOrPhone: String, val password: String) :
                Auth()
            object UpdateDashboard : Auth()
        }

        sealed class Otp : Action() {
            override val typeClazz: KClass<*> = Otp::class

            data class Send(val phoneNumber: String) : Otp()
            data class Submit(val otp: String) : Otp()
            object Resend : Otp()
        }

        sealed class ResetPassword : Action() {
            override val typeClazz: KClass<*> = ResetPassword::class

            data class ConfirmCurrent(val password: String) : ResetPassword()
            data class ConfirmNew(val password: String) : ResetPassword()
            object Finish : ResetPassword()
        }

        sealed class Registration : Action() {
            override val typeClazz: KClass<*> = Registration::class

            data class SelectUserType(val userType: UserType) : Registration()
            data class Validate(val userRegistration: UserRegistration) : Registration()
            data class AddAadhaar(val aadhaarData: AadhaarData) : Registration()
            data class UpdatePincode(val pincode: String) : Registration()
            data class UploadAadhaar(val aadhaarAsBase64: String) : Registration()
            data class UploadDrugLicense(val licenseAsBase64: String, val fileType: FileType) :
                Registration()
            object UploadFileTooBig : Registration()

            object SignUp : Registration()
            object Skip : Registration()
            object AcceptWelcome : Registration()
            object ShowUploadBottomSheet : Registration()

            object ConfirmCreateRetailer : Registration()
        }

        sealed class Search : Action() {
            override val typeClazz: KClass<*> = Search::class

            data class SearchInput(
                val isOneOf: Boolean,
                val search: String? = null,
                val query: HashMap<String, String> = hashMapOf(),
            ) : Search() {
                init {
                    if (search != null) {
                        query["search"] = search
                    }
                }
            }

            data class SearchAutoComplete(val value: String) : Search()
            data class SelectFilter(val filter: Filter, val option: Option) : Search()
            data class SearchFilter(val filter: Filter, val value: String) : Search()
            data class SelectAutoComplete(val autoComplete: AutoComplete) : Search()
            data class ClearFilter(val filter: Filter?) : Search()
            data class SelectSortOption(val option: SortOption?) : Search()
            object LoadMoreProducts : Search()
            object Reset : Search()
            object ToggleFilter : Search()
        }

        sealed class Product : Action() {
            override val typeClazz: KClass<*> = Product::class

            data class SelectFromSearch(val productCode: String) : Product()
            data class SelectAlternative(val data: AlternateProductData) : Product()
            data class BuyProduct(val product: ProductSearch, val buyingOption: BuyingOption) :
                Product()

            data class FilterBuyProduct(val filter: String) : Product()
            data class SelectSeasonBoyRetailer(
                val productCode: String,
                val sellerInfo: SellerInfo?,
            ) : Product()

            data class PreviewStockistBottomSheet(val sellerInfo: SellerInfo) : Product()
        }

        sealed class Management : Action() {
            override val typeClazz: KClass<*> = Management::class

            data class Select(val item: EntityInfo) : Management()
            data class Search(val value: String) : Management()
            data class Load(val isFirstLoad: Boolean) : Management()
            data class RequestSubscribe(val item: EntityInfo) : Management()
            data class ChoosePayment(val paymentMethod: PaymentMethod, val creditDays: Int?) :
                Management()
            object VerifyRetailerTraderDetails : Management()
        }

        sealed class Notification : Action() {
            override val typeClazz: KClass<*> = Notification::class

            data class Load(val isFirstLoad: Boolean) : Notification()
            data class Search(val value: String) : Notification()
            data class Select(val notification: NotificationData) : Notification()
            data class SelectAction(val action: NotificationAction) : Notification()
            data class ChangeOptions(val option: NotificationOption) : Notification()
            data class SelectFilter(val filter: NotificationFilter) : Notification()
//            object UpdateUnreadMessages: Notification()
        }

        sealed class Stores : Action() {
            override val typeClazz: KClass<*> = Stores::class

            data class Select(val item: Store) : Stores()
            data class Search(val value: String) : Stores()
            data class Load(val isFirstLoad: Boolean) : Stores()
        }

        sealed class Cart : Action() {
            override val typeClazz: KClass<*> = Cart::class

            data class AddItem(
                val sellerUnitCode: String?,
                val productCode: String,
                val buyingOption: BuyingOption,
                val id: CartIdentifier?,
                val quantity: Double,
                val freeQuantity: Double,
            ) : Cart()

            data class UpdateItem(
                val sellerUnitCode: String,
                val productCode: String,
                val buyingOption: BuyingOption,
                val id: CartIdentifier,
                val quantity: Double,
                val freeQuantity: Double,
            ) : Cart()

            data class RemoveItem(
                val sellerUnitCode: String,
                val productCode: String,
                val buyingOption: BuyingOption,
                val id: CartIdentifier,
            ) : Cart()

            data class RemoveSellerItems(val sellerUnitCode: String) : Cart()

            object LoadCart : Cart()
            object ClearCart : Cart()
            object PreviewCart : Cart()
            object ConfirmCartOrder : Cart()
            data class PlaceCartOrder(val checkForQuotedItems: Boolean) : Cart()
        }

        sealed class Help : Action() {
            override val typeClazz: KClass<*> = Help::class

            object GetHelp : Help()
        }

        sealed class Orders : Action() {
            override val typeClazz: KClass<*> = Orders::class

            data class Search(val value: String) : Orders()
            data class Load(val isFirstLoad: Boolean) : Orders()
            data class Select(val orderId: String, val type: OrderType) : Orders()

            data class ViewOrderAction(
                val action: ViewOrderScope.Action,
                val fromNotification: Boolean
            ) : Orders()

            data class ToggleCheckEntry(val entry: OrderEntry) : Orders()
            data class SelectEntry(val entry: OrderEntry) : Orders()
            data class SaveEntryQty(
                val entry: OrderEntry,
                val quantity: Double,
                val freeQuantity: Double,
                val ptr: Double,
                val batch: String,
                val expiry: String,
            ) : Orders()

            data class Confirm(val fromNotification: Boolean) : Orders()
        }

        sealed class Invoices : Action() {
            override val typeClazz: KClass<*> = Invoices::class

            data class Search(val value: String) : Invoices()
            data class Load(val isFirstLoad: Boolean) : Invoices()
            data class Select(val invoiceId: String, val isPoInvoice: Boolean) : Invoices()

            object ShowTaxInfo : Invoices()
            data class ShowTaxFor(val invoiceEntry: InvoiceEntry) : Invoices()

            data class ViewInvoiceAction(
                val action: ViewInvoiceScope.Action,
                val payload: Any?,
            ) : Invoices()
        }
    }

    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object Back : Transition()
        object Refresh : Transition()
        object SignUp : Transition()
        object Otp : Transition()
        object ChangePassword : Transition()
        object Search : Transition()
        object Dashboard : Transition()
        object Settings : Transition()
        object Profile : Transition()
        object Address : Transition()
        object GstinDetails : Transition()
        data class Management(val manageUserType: UserType) : Transition()
        object RequestCreateRetailer : Transition()
        object AddRetailerAddress : Transition()
//        data class PreviewUser(
//            val registration2: UserRegistration2,
//            val registration3: UserRegistration3,
//        ) : Transition()

        object Notifications : Transition()
        object Stores : Transition()
        object Cart : Transition()
        object Orders : Transition()
        object PoOrdersAndHistory : Transition()
        object MyInvoices : Transition()
        object PoInvoices : Transition()
    }
}