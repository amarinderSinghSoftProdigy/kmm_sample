package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AddInvoice
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.InvContactDetails
import com.zealsoftsol.medico.data.InvListingData
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.data.InvoiceDetails
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.data.UploadResponseData


sealed class IocSellerScope : Scope.Child.TabBar(), CommonScope.UploadDocument {
    override val supportedFileTypes: Array<FileType> = FileType.forProfile()

    val invoiceUpload: DataSource<UploadResponseData> =
        DataSource(UploadResponseData("", "", "", ""))
    val paymentTypes: List<PaymentTypes> = listOf(
        PaymentTypes.CASH_IN_HAND,
        PaymentTypes.PAYTM,
        PaymentTypes.GOOGLE_PAY,
        PaymentTypes.AMAZON_PAY,
        PaymentTypes.PHONE_PE,
        PaymentTypes.BHIM_UPI,
        PaymentTypes.NET_BANKING,
    )

    class InvUserListing : IocSellerScope(), Loadable<InvUserData>, CommonScope.CanGoBack {

        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<InvUserData>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
            TabBarInfo.OnlyBackHeader("")

        fun openCreateIOC() {
            EventCollector.sendEvent(Event.Action.IOC.OpenCreateIOC)
        }

        fun openIOCListing(item: InvUserData) {
            EventCollector.sendEvent(Event.Action.IOC.OpenIOCListing(item))
        }

        fun load(value: String) {
            EventCollector.sendEvent(Event.Action.IOC.LoadUsers(value))
        }

        fun loadItems() = EventCollector.sendEvent(Event.Action.IOC.LoadMoreUsers)

    }

    class InvListing(val item: InvUserData) : IocSellerScope(), CommonScope.CanGoBack {
        override val isRoot: Boolean = false
        val items: DataSource<List<BuyerDetailsData>> = DataSource(emptyList())
        val data: DataSource<InvListingData?> = DataSource(null)

        init {
            EventCollector.sendEvent(Event.Action.IOC.LoadInvListing(item.unitCode))
        }

        fun openIOCDetails(item: BuyerDetailsData) {
            EventCollector.sendEvent(Event.Action.IOC.OpenIOCDetails(item))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item.tradeName)
        }
    }

    class InvDetails(val item: BuyerDetailsData) : IocSellerScope() {
        override val isRoot: Boolean = false
        val data: DataSource<InvoiceDetails?> = DataSource(null)
        val items: DataSource<List<InvContactDetails>> = DataSource(emptyList())

        init {
            loadData(item.invoiceId)
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item.tradeName)
        }


        private fun loadData(invoiceId: String) {
            EventCollector.sendEvent(Event.Action.IOC.LoadInvDetails(invoiceId))
        }

    }

    class IOCListing : IocSellerScope(), Loadable<RetailerData> {
        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<RetailerData>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        val selectedIndex: DataSource<Int> = DataSource(-1)
        override val searchText: DataSource<String> = DataSource("")

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = "create_debt")
        }

        fun updateIndex(index: Int) {
            selectedIndex.value = index
        }

        fun selectItem(item: RetailerData) =
            EventCollector.sendEvent(Event.Action.IOC.Select(item))

        fun search(value: String) {
            updateIndex(-1)
            EventCollector.sendEvent(Event.Action.IOC.Search(value))
        }

        fun loadItems() = EventCollector.sendEvent(Event.Action.IOC.LoadMoreProducts)

    }

    class IOCCreate(val item: RetailerData) : IocSellerScope(), CommonScope.CanGoBack {

        val dialogMessage: DataSource<String> = DataSource("")
        val showAlert: DataSource<Boolean> = DataSource(false)
        val enableButton: DataSource<Boolean> = DataSource(false)
        val invoiceNum: DataSource<String> = DataSource("")
        val invoiceDate: DataSource<String> = DataSource("")
        private val invoiceDateMili: DataSource<Long> = DataSource(0)
        val totalAmount: DataSource<String> = DataSource("")
        val outstandingAmount: DataSource<String> = DataSource("")
        val outstandingDiffAmount: DataSource<String> = DataSource("")


        fun updateInvoiceNum(data: String) {
            invoiceNum.value = data
            validate()
        }

        fun updateInvoiceDate(data: String, mili: Long) {
            invoiceDate.value = data
            invoiceDateMili.value = mili
            validate()
        }

        fun updateTotalAmount(data: String) {
            if (data == "0" || data == "0.0") {
                totalAmount.value = ""
            } else {
                totalAmount.value = data
            }
            validate()
        }

        fun updateOutstandingAmount(data: String) {
            outstandingAmount.value = data
            validate()
        }

        fun validate() {
            if (totalAmount.value.isNotEmpty() && outstandingAmount.value.isNotEmpty() &&
                totalAmount.value.toDouble() > outstandingAmount.value.toDouble()
            ) {
                outstandingDiffAmount.value =
                    (totalAmount.value.toDouble() - outstandingAmount.value.toDouble()).toString()
            } else {
                outstandingDiffAmount.value = ""
            }
            enableButton.value = (invoiceNum.value.isNotEmpty() && invoiceDate.value.isNotEmpty()
                    && totalAmount.value.isNotEmpty() && outstandingAmount.value.isNotEmpty()
                    && invoiceUpload.value.cdnUrl.isNotEmpty()) &&
                    totalAmount.value.toDouble() > outstandingAmount.value.toDouble()
        }

        fun changeAlertScope(enable: Boolean) {
            showAlert.value = enable
        }

        fun addInvoice() {
            val addRequest = AddInvoice(
                item.unitCode,
                invoiceNum.value,
                invoiceDateMili.value,
                totalAmount.value.toDouble(),
                outstandingAmount.value.toDouble(),
                invoiceUpload.value.cdnUrl,
                invoiceUpload.value.id,
                invoiceUpload.value.documentType
            )
            EventCollector.sendEvent(Event.Action.IOC.SubmitInvoice(addRequest))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item.tradeName)
        }

    }

    fun previewImage(item: String) =
        EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(item))

    fun openEditInvoice(item: BuyerDetailsData, sellerScope: IocSellerScope) {
        EventCollector.sendEvent(Event.Action.IOC.OpenEditIOCBottomSheet(item, sellerScope))
    }
    fun openRetailerDetails(item: String) =
        EventCollector.sendEvent(Event.Action.IOCBuyer.ShowRetailerDetails(item))

    enum class PaymentTypes(val stringId: String, val type: String) {
        CASH_IN_HAND("cash_in_hand", "CASH_IN_HAND"),
        GOOGLE_PAY("g_pay", "GOOGLE_PAY"),
        AMAZON_PAY("amazon_pay", "AMAZON_PAY"),
        PHONE_PE("phone_pe", "PHONE_PE"),
        BHIM_UPI("upi", "BHIM_UPI"),
        PAYTM("paytm", "PAYTM"),
        NET_BANKING("net_banking", "NET_BANKING");
    }

}