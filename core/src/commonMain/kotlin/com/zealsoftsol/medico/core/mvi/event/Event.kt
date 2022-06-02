package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocBuyerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.scope.regular.OrderHsnEditScope
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AddInvoice
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.data.BatchStatusUpdateRequest
import com.zealsoftsol.medico.data.BatchUpdateRequest
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.DeclineReason
import com.zealsoftsol.medico.data.EmployeeRegistration
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.HeaderData
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.data.InvoiceDetails
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.OfferStatus
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderTaxInfo
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.ProductsData
import com.zealsoftsol.medico.data.Promotions
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubmitPaymentRequest
import com.zealsoftsol.medico.data.TaxType
import com.zealsoftsol.medico.data.UpdateInvoiceRequest
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
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

            data class UploadDocument(
                val size: String,
                val asBase64: String,
                val fileType: FileType,
                val type: String,
                val path: String,
                val registrationStep1: UserRegistration1
            ) : Registration()

            object UploadFileTooBig : Registration()
            data class ShowUploadBottomSheets(
                val type: String,
                val registrationStep1: UserRegistration1
            ) : Registration()

            object SignUp : Registration()
            object Skip : Registration()
            object Submit : Registration()
            object AcceptWelcome : Registration()
            object ShowUploadBottomSheet : Registration()

            object ConfirmCreateRetailer : Registration()
        }

        sealed class Profile : Action() {
            override val typeClazz: KClass<*> = Profile::class

            data class UploadUserProfile(
                val size: String,
                val asBase64: String,
                val fileType: FileType,
                val type: String
            ) : Profile()

            object UploadFileTooBig : Profile()
            object GetProfileData : Profile()
            data class ShowUploadBottomSheet(val type: String) : Profile()
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

            object GetLocalSearchData : Search()

            data class SearchAutoComplete(val value: String, val sellerUnitCode: String? = null) :
                Search()

            data class SelectFilter(val filter: Filter, val option: Option) : Search()
            data class SearchFilter(val filter: Filter, val value: String) : Search()
            data class SelectAutoComplete(val autoComplete: AutoComplete) : Search()
            data class SelectAutoCompleteGlobal(val autoComplete: AutoComplete) : Search()
            data class ClearFilter(val filter: Filter?) : Search()
            data class SelectSortOption(val option: SortOption?) : Search()
            data class SelectBatch(val option: Boolean, val product: ProductSearch) : Search()
            data class ViewAllItems(val value: String) : Search()
            data class AddToCart(val product: ProductSearch) : Search()
            data class showToast(val msg: String, val cartData: CartData?) : Search()
            data class ResetButton(val item: Boolean) : Search()
            data class UpdateFree(val qty: Double, val id: String) : Search()
            object LoadMoreProducts : Search()
            object Reset : Search()
            object ToggleFilter : Search()
            data class ShowConnectedStockistBottomSheet(val stockist: List<ConnectedStockist>) :
                Search()

            data class LoadStockist(val code: String, val imageCode: String) : Search()
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
            data class ShowLargeImage(val url: String) : Product()
            data class ShowStockist(val stockist: List<ConnectedStockist>) : Product()
        }

        sealed class Management : Action() {
            override val typeClazz: KClass<*> = Management::class

            data class Search(val value: String) : Management()
            data class Load(val isFirstLoad: Boolean) : Management()
            data class GetDetails(val item: String, val showConnectionOption: Boolean = false) :
                Management()

            data class RequestSubscribe(
                val item: HeaderData,
                val connectingStockistUnitCode: String
            ) : Management()

            data class ChoosePayment(val paymentMethod: PaymentMethod, val creditDays: Int?) :
                Management()

            object VerifyRetailerTraderDetails : Management()
            data class SelectAction(
                val notificationId: String,
                val action: NotificationActionRequest
            ) : Management()

            data class GetCompanies(val unitCode: String, val page: Int) : Management()
        }

        sealed class Notification : Action() {
            override val typeClazz: KClass<*> = Notification::class

            object ClearAll : Notification()
            data class Load(val isFirstLoad: Boolean) : Notification()
            data class Search(val value: String) : Notification()
            data class Select(val notification: NotificationData) : Notification()
            data class DeleteNotification(val notificationId: String) : Notification()
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
            data class ShowLargeImage(val item: String, val type: String? = "") : Stores()
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

            data class OpenEditCartItem(
                val qtyInitial: Double,
                val freeQtyInitial: Double,
                val sellerCart: SellerCart,
                val item: CartItem,
                val cartScope: CartScope
            ) : Cart()

            data class RemoveSellerItems(val sellerUnitCode: String) : Cart()

            object LoadCart : Cart()
            object ClearCart : Cart()
            object PreviewCart : Cart()
            data class ConfirmCartOrder(val cartScope: Scope) : Cart()
            data class PlaceCartOrder(val checkForQuotedItems: Boolean) : Cart()
            object HideBackButton : Cart()
            data class SubmitReward(val rewardId: String) : Cart()
        }

        sealed class Help : Action() {
            override val typeClazz: KClass<*> = Help::class

            object GetContactUs : Help()
            object GetTandC : Help()
            object GetHelp : Help()
            data class ChangeTab(val index: String) : Help()
        }

        sealed class Orders : Action() {
            override val typeClazz: KClass<*> = Orders::class

            data class Search(val value: String) : Orders()
            data class Load(val isFirstLoad: Boolean) : Orders()
            data class Select(val orderId: String, val type: OrderType) : Orders()

            data class SelectBottomSheet(
                val orderDetails: OrderEntry?,
                val orderTaxDetails: OrderTaxInfo?,
                val reason: String,
                val scope: Scope
            ) : Orders()

            data class SelectItemBottomSheet(
                val orderDetails: OrderEntry,
                val scope: Scope
            ) : Orders()

            data class ViewOrderAction(
                val action: ViewOrderScope.Action,
                val fromNotification: Boolean
            ) : Orders()

            data class ViewOrderInvoiceAction(
                val orderId: String,
                val acceptedEntries: List<String>,
                val reasonCode: String? = null,
                val declineReasons: List<DeclineReason>
            ) : Orders()

            data class ToggleCheckEntry(val entry: OrderEntry) : Orders()
            data class SelectEntry(
                val taxType: TaxType,
                val retailerName: String,
                val canEditOrderEntry: Boolean,
                val orderId: String,
                val declineReason: List<DeclineReason>,
                val entry: List<OrderEntry>,
                val index: Int
            ) : Orders()

            data class SaveEntryQty(
                val entry: OrderEntry,
                val quantity: Double,
                val freeQuantity: Double,
                val ptr: Double,
                val batch: String,
                val expiry: String,
            ) : Orders()

            data class Confirm(val fromNotification: Boolean, val reasonCode: String) : Orders()

            data class ConfirmInvoice(val reasonCode: String) : Orders()

            data class GetOrderDetails(val orderId: String, val type: OrderType) : Orders()

            //data class ShowDetailsOfRetailer(val item: EntityInfo, val scope: Scope) : Orders()

            data class EditDiscount(val orderId: String, val discount: Double) : Orders()

            data class ChangePaymentMethod(val orderId: String, val type: String) : Orders()

            data class BuyProduct(val orderEntry: OrderEntry, val buyingOption: BuyingOption) :
                Orders()
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

        sealed class InStore : Action() {

            override val typeClazz: KClass<*> = InStore::class

            data class SellerSearch(val value: String) : InStore()
            data class SellerLoad(val isFirstLoad: Boolean) : InStore()
            data class SellerSelect(
                val unitcode: String,
                val sellerName: String,
                val address: String,
                val phoneNumber: String
            ) : InStore()

            data class DeleteOrder(
                val unitcode: String,
                val id: String
            ) : InStore()

            data class ProductSearch(val value: String) : InStore()
            data class ProductLoad(val isFirstLoad: Boolean) : InStore()
            data class ProductSelect(val item: InStoreProduct) : InStore()

            data class UserSearch(val value: String) : InStore()
            data class UserLoad(val isFirstLoad: Boolean) : InStore()

            data class AddUserUpdatePincode(val pincode: String) : InStore()
            object AddUser : InStore()
            object FinishAddUser : InStore()

            object LoadCart : InStore()
            object ClearCart : InStore()
            data class AddCartItem(
                val productCode: String,
                val spid: String,
                val quantity: Double,
                val freeQuantity: Double,
            ) : InStore()

            data class UpdateCartItem(
                val productCode: String,
                val spid: String,
                val quantity: Double,
                val freeQuantity: Double,
            ) : InStore()

            data class RemoveCartItem(
                val entryId: String,
            ) : InStore()

            object ConfirmCartOrder : InStore()

            data class SubmitReward(val storeId: String) : InStore()

        }

        sealed class WhatsAppPreference : Action() {
            override val typeClazz: KClass<*> = WhatsAppPreference::class

            data class SavePreference(
                val language: String,
                val phoneNumber: String,
            ) : WhatsAppPreference()

            object GetPreference : WhatsAppPreference()
        }


        sealed class Batches : Action() {
            override val typeClazz: KClass<*> = Batches::class

            data class GetBatches(val spid: String, val productsData: ProductsData) : Batches()
        }

        sealed class OrderHsn : Action() {
            override val typeClazz: KClass<*> = OrderHsn::class

            data class Load(val isFirstLoad: Boolean) : OrderHsn()

            data class Search(val value: String) : OrderHsn()

            data class SaveOrderEntry(
                val orderId: String,
                val orderEntryId: String,
                val servedQty: Double,
                val freeQty: Double,
                val price: Double,
                val batchNo: String,
                val expiryDate: String,
                val mrp: Double,
                val discount: Double,
                val hsnCode: String,
            ) : OrderHsn()

            data class RejectOrderEntry(
                val orderEntryId: String,
                val spid: String,
                val reasonCode: String
            ) : OrderHsn()

            data class AcceptOrderEntry(
                val orderEntryId: String,
                val spid: String,
            ) : OrderHsn()

            object GetBatches : OrderHsn()

        }

        sealed class Offers : Action() {
            override val typeClazz: KClass<*> = Offers::class

            data class ShowBottomSheet(
                val promotionType: String,
                val name: String,
                val active: Boolean
            ) :
                Offers()

            data class ShowEditBottomSheet(
                val promotion: Promotions
            ) : Offers()

            object LoadMoreProducts : Offers()
            object OpenCreateOffer : Offers()
            data class GetOffers(
                val search: String? = null,
                val query: ArrayList<String> = ArrayList(),
                val status: OfferStatus,
            ) : Offers()

            data class UpdateOffer(val promotionType: String, val active: Boolean) : Offers()
            data class EditOffer(val promoCode: String, val request: OfferProductRequest) : Offers()
            object GetTypes : Offers()
            data class SearchAutoComplete(val value: String) : Offers()
            data class SelectAutoComplete(val autoComplete: AutoComplete) : Offers()
            data class SaveOffer(val request: OfferProductRequest) : Offers()
            data class EditCreatedOffer(val promoCode: String, val request: OfferProductRequest) :
                Offers()

            data class ShowManufacturers(val showManufacturers: Boolean) : Offers()
        }

        sealed class Inventory : Action() {
            override val typeClazz: KClass<*> = Inventory::class

            data class GetInventory(
                val search: String? = null,
                val manufacturer: String? = null,
                val page: Int,
                val stockStatus: InventoryScope.InventoryType,
                val status: InventoryScope.StockStatus
            ) : Inventory()

            data class GetBatches(val spid: String, val productsData: ProductsData) : Inventory()
            data class EditBatch(val item: Batch, val productsData: ProductsData) :
                Inventory()

            data class UpdateBatchStatus(val batchData: BatchStatusUpdateRequest) :
                Inventory()

            data class UpdateBatch(val batchData: BatchUpdateRequest) : Inventory()
        }

        sealed class QrCode : Action() {
            override val typeClazz: KClass<*> = QrCode::class

            object GetQrCode : QrCode()
            data class RegenerateQrCode(val qrCode: String) : QrCode()
        }

        sealed class IOC : Action() {
            override val typeClazz: KClass<*> = IOC::class

            //Create IOC and listing
            data class UploadInvoice(
                val size: String,
                val asBase64: String,
                val fileType: FileType,
                val type: String
            ) : IOC()

            data class Select(val item: RetailerData) : IOC()
            data class Search(val value: String) : IOC()
            object LoadMoreProducts : IOC()
            data class UpdateIOC(
                val request: UpdateInvoiceRequest,
                val sellerScope: IocSellerScope
            ) : IOC()

            data class ShowUploadBottomSheets(
                val type: String
            ) : IOC()

            data class SubmitInvoice(val value: AddInvoice) : IOC()

            //Methods for InvBuyerUserListing
            data class LoadUsers(val search: String? = null) : IOC()
            object LoadMoreUsers : IOC()
            data class OpenIOCListing(val item: InvUserData) : IOC()
            object OpenCreateIOC : IOC()

            //Methods for InvBuyerLisitng
            data class LoadInvListing(val unitCode: String) : IOC()
            data class OpenIOCDetails(val item: BuyerDetailsData) : IOC()

            //Methods for InvDetails
            data class LoadInvDetails(val invoiceId: String) : IOC()

            data class OpenEditIOCBottomSheet(
                val item: BuyerDetailsData, val outStand: Double, val sellerScope: IocSellerScope
            ) : IOC()

        }

        sealed class IOCBuyer : Action() {
            override val typeClazz: KClass<*> = IOCBuyer::class

            //Methods for Pay now
            data class OpenPayNow(
                val unitCode: String,
                val invoiceId: String,
                val outStand: Double,
                val type: IocBuyerScope.PaymentTypes,
                val details: InvoiceDetails?
            ) : IOCBuyer()

            data class SubmitPayment(val item: SubmitPaymentRequest, val mobile: String) :
                IOCBuyer()

            //Methods for InvUserListing
            data class LoadUsers(val search: String? = null) : IOCBuyer()
            object LoadMoreUsers : IOCBuyer()
            data class OpenIOCListing(val item: InvUserData) : IOCBuyer()

            //Methods for InvLisitng
            data class LoadInvListing(val unitCode: String) : IOCBuyer()
            data class OpenIOCDetails(
                val unitCode: String,
                val tradeName: String,
                val invoiceId: String
            ) : IOCBuyer()

            //Methods for InvDetails
            data class LoadInvDetails(val invoiceId: String) : IOCBuyer()
            data class OpenPaymentMethod(
                val unitCode: String,
                val invoiceId: String,
                val outStand: Double,
                val details: InvoiceDetails?
            ) : IOCBuyer()

            object ClearScopes : IOCBuyer()
        }

        sealed class Employee : Action() {
            override val typeClazz: KClass<*> = Employee::class

            data class SelectUserType(val userType: UserType) : Employee()
            data class Validate(val userRegistration: EmployeeRegistration) : Employee()
            data class Aadhaar(val aadhaarData: AadhaarData) : Employee()
            data class UpdatePincode(val pincode: String) : Employee()
            object ViewEmployee : Employee()
            data class DeleteEmployee(val id: String) : Employee()
            object SubmitOtp : Employee()
            object SubmitFinalData : Employee()
        }

        sealed class Preferences : Action() {
            override val typeClazz: KClass<*> = Preferences::class

            object GetPreferences : Preferences()
            data class SetAutoConnectPreferences(val isEnabled: Boolean) : Preferences()
        }

        sealed class Banners : Action() {
            override val typeClazz: KClass<*> = Banners::class

            data class GetAllBanners(val page: Int, val search: String) : Banners()

            data class AddItemToCart(
                val sellerUnitCode: String?,
                val productCode: String,
                val buyingOption: BuyingOption,
                val id: CartIdentifier?,
                val quantity: Double,
                val freeQuantity: Double,
            ) : Banners()

            data class ZoomImage(val url: String) : Banners()
        }

        sealed class Deals : Action() {
            override val typeClazz: KClass<*> = Deals::class

            data class GetAllDeals(
                val page: Int,
                val search: String,
                val unitCode: String,
                val promoCode: String
            ) :
                Deals()

            data class AddItemToCart(
                val sellerUnitCode: String?,
                val productCode: String,
                val buyingOption: BuyingOption,
                val id: CartIdentifier?,
                val quantity: Double,
                val freeQuantity: Double,
            ) : Deals()

            data class ZoomImage(val url: String) : Deals()
        }

        sealed class Ocr : Action() {
            override val typeClazz: KClass<*> = Ocr::class

            data class ShowUploadBottomSheet(val type: String) : Ocr()

            data class GetOcrImage(val filePath: String) : Ocr()

        }

        sealed class Manufacturers : Action() {
            override val typeClazz: KClass<*> = Manufacturers::class

            data class GetManufacturers(
                val page: Int,
                val search: String,
            ) : Manufacturers()
        }

        sealed class Demo : Action() {
            override val typeClazz: KClass<*> = Demo::class

            object MyDemo : Demo()
            data class OpenVideo(val url: String = "") : Demo()
            object ReleasePlayer : Demo()
        }

        sealed class Rewards : Action() {
            override val typeClazz: KClass<*> = Rewards::class

            object GetRewards : Rewards()
        }
    }


    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object Back : Transition()
        object Refresh : Transition()
        object SignUp : Transition()
        object Otp : Transition()
        object ChangePassword : Transition()
        data class Search(val autoComplete: AutoComplete? = null) : Transition()
        object Dashboard : Transition()
        data class Settings(val showBackIcon: Boolean) : Transition()
        object Profile : Transition()
        object Address : Transition()
        object GstinDetails : Transition()
        object WhatsappPreference : Transition()
        data class Management(val manageUserType: UserType, val search: String = "") : Transition()
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
        data class Offers(val status: OfferStatus) : Transition()
        object CreateOffers : Transition()
        object PoInvoices : Transition()
        object InStore : Transition()
        object InStoreUsers : Transition()
        object InStoreAddUser : Transition()
        data class InStoreCart(
            val unitcode: String,
            val name: String,
            val address: String,
            val phoneNumber: String
        ) : Transition()

        data class Inventory(
            val type: InventoryScope.InventoryType,
            val manufacturer: String = ""
        ) : Transition()

        object Menu : Transition()
        data class Batches(
            val spid: String,
            val batchData: DataSource<List<com.zealsoftsol.medico.data.Batches>?>,
            val selectedBatchData: DataSource<OrderHsnEditScope.SelectedBatchData?>,
            val requiredQty: Double,
        ) : Transition()

        object QrCode : Transition()
        object IOCSeller : Transition()
        object IOCBuyer : Transition()
        object AddEmployee : Transition()
        object Preference : Transition()
        data class Companies(val title: String, val unitCode: String) : Transition()
        object Banners : Transition()
        object Deals : Transition()
        object Ocr : Transition()
        object Manufacturers : Transition()
        object Demo : Transition()
        object Rewards : Transition()
        object OnlineOrders : Transition()
    }
}