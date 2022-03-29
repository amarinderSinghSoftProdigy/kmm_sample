package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.FormattedData
import com.zealsoftsol.medico.data.InvContactDetails
import com.zealsoftsol.medico.data.InvListingData
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.data.InvoiceDetails
import com.zealsoftsol.medico.data.SubmitPaymentRequest


sealed class IocBuyerScope : Scope.Child.TabBar(), CommonScope.UploadDocument {
    override val supportedFileTypes: Array<FileType> = FileType.forProfile()

    val paymentTypes: List<PaymentTypes> = listOf(
        PaymentTypes.CASH_IN_HAND,
        PaymentTypes.PAYTM,
        PaymentTypes.GOOGLE_PAY,
        PaymentTypes.AMAZON_PAY,
        PaymentTypes.PHONE_PE,
        PaymentTypes.BHIM_UPI,
        PaymentTypes.NET_BANKING,
    )
    val paymentTypesCash: List<PaymentTypes> = listOf(
        PaymentTypes.CASH_IN_HAND,
    )

    class InvUserListing : IocBuyerScope(), Loadable<InvUserData>, CommonScope.CanGoBack {

        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<InvUserData>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")
        val slider: DataSource<Float> = DataSource(0f)
        val total: DataSource<FormattedData<Double>?> = DataSource(null)
        val paid: DataSource<FormattedData<Double>?> = DataSource(null)
        val outstand: DataSource<FormattedData<Double>?> = DataSource(null)

        fun getSliderPosition() {
            slider.value = ((((total.value?.value ?: 0.0) - (outstand.value?.value
                ?: 0.0)) / (total.value?.value
                ?: 1.0)) * 100).toFloat()
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
            TabBarInfo.OnlyBackHeader("")


        fun openIOCListing(item: InvUserData) {
            EventCollector.sendEvent(Event.Action.IOCBuyer.OpenIOCListing(item))
        }

        fun load(value: String) {
            EventCollector.sendEvent(Event.Action.IOCBuyer.LoadUsers(value))
        }

        fun loadItems() {
            EventCollector.sendEvent(Event.Action.IOCBuyer.LoadMoreUsers)
        }

    }

    class InvListing(val item: InvUserData) : IocBuyerScope(), CommonScope.CanGoBack {
        override val isRoot: Boolean = false
        val items: DataSource<List<BuyerDetailsData>> = DataSource(emptyList())
        val data: DataSource<InvListingData?> = DataSource(null)
        val slider: DataSource<Float> = DataSource(0f)

        init {
            EventCollector.sendEvent(Event.Action.IOCBuyer.LoadInvListing(item.unitCode))
        }

        fun getSliderPosition() {
            slider.value =
                ((((data.value?.totalAmount?.value ?: 0.0) - (data.value?.outstandingAmount?.value
                    ?: 0.0)) / (data.value?.totalAmount?.value ?: 1.0)) * 100).toFloat()
        }

        fun openIOCDetails(item: BuyerDetailsData) {
            EventCollector.sendEvent(Event.Action.IOCBuyer.OpenIOCDetails(item))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item.tradeName)
        }
    }

    class InvDetails(val item: BuyerDetailsData) : IocBuyerScope() {
        override val isRoot: Boolean = false
        val data: DataSource<InvoiceDetails?> = DataSource(null)
        val items: DataSource<List<InvContactDetails>> = DataSource(emptyList())

        init {
            loadData(item.invoiceId)
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item.tradeName)
        }

        fun openPaymentMethod(item: BuyerDetailsData) {
            EventCollector.sendEvent(Event.Action.IOCBuyer.OpenPaymentMethod(item))
        }

        private fun loadData(invoiceId: String) {
            EventCollector.sendEvent(Event.Action.IOCBuyer.LoadInvDetails(invoiceId))
        }

    }


    class IOCPaymentMethod(val item: BuyerDetailsData) : IocBuyerScope(), CommonScope.CanGoBack {
        val items: DataSource<List<PaymentTypes>> = DataSource(emptyList())
        val selected: DataSource<Int> = DataSource(-1)

        init {
            items.value = paymentTypesCash
        }

        fun openPayNow(item: BuyerDetailsData, index: Int, type: PaymentTypes) {
            selected.value = index
            EventCollector.sendEvent(Event.Action.IOCBuyer.OpenPayNow(item, type))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = "payment_method_")
        }
    }

    class IOCPayNow(val item: BuyerDetailsData, val method: PaymentTypes) : IocBuyerScope(),
        CommonScope.CanGoBack {

        val enableButton: DataSource<Boolean> = DataSource(false)
        val lineManName: DataSource<String> = DataSource("")
        val mobileNumber: DataSource<String> = DataSource("")
        val totalAmount: DataSource<String> = DataSource("")

        fun updateLineManName(data: String) {
            lineManName.value = data
            validate()
        }

        fun updatePhoneNumber(data: String) {
            mobileNumber.value = data
            validate()
        }

        fun validPhone(phone: String): Boolean {
            if (phone.isEmpty()) {
                return true
            }
            return phone.length == 10
        }

        fun updateTotalAmount(data: String) {
            if (data == "0" || data == "0.0") {
                totalAmount.value = ""
            } else {
                totalAmount.value = data
            }
            validate()
        }

        fun submitPayment(phoneNumber: String) {
            val request = SubmitPaymentRequest(
                method.type, item.invoiceId,
                item.unitCode, lineManName.value,
                totalAmount.value.toDouble(),
                phoneNumber, ""
            )
            EventCollector.sendEvent(Event.Action.IOCBuyer.SubmitPayment(request))
        }


        private fun validate() {
            enableButton.value = totalAmount.value.isNotEmpty()
                    && lineManName.value.isNotEmpty()
                    && validPhone(mobileNumber.value)
                    && mobileNumber.value.isNotEmpty()
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = method.stringId)
        }

        fun startOtp(phoneNumber: String) =
            EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
    }

    fun previewImage(item: String) =
        EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(item))

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