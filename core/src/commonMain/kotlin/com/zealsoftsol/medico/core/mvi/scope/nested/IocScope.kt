package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AddInvoice
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.InvoiceData
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.data.UploadResponseData


sealed class IocScope : Scope.Child.TabBar(), CommonScope.UploadDocument {
    override val supportedFileTypes: Array<FileType> = FileType.forProfile()

    val invoiceUpload: DataSource<UploadResponseData> =
        DataSource(UploadResponseData("", "", "", ""))

    class InvUserListing : IocScope(), Loadable<String>, CommonScope.CanGoBack {

        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<String>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        init {
            val list = ArrayList<String>()
            list.add("")
            list.add("")
            list.add("")
            list.add("")
            list.add("")
            items.value = list
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
            TabBarInfo.OnlyBackHeader("")

        fun openCreateIOC() {
            EventCollector.sendEvent(Event.Action.IOC.OpenCreateIOC)
        }

        fun openIOCListing(item: String) {
            EventCollector.sendEvent(Event.Action.IOC.OpenIOCListing(item))
        }

        fun selectItem(item: RetailerData) =
            EventCollector.sendEvent(Event.Action.IOC.Select(item))

        fun search(value: String) {
            EventCollector.sendEvent(Event.Action.IOC.Search(value))
        }

        fun loadItems() = EventCollector.sendEvent(Event.Action.IOC.LoadMoreProducts)

    }

    class InvListing(val item: String) : IocScope(), Loadable<String>, CommonScope.CanGoBack {
        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<String>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        init {
            val list = ArrayList<String>()
            list.add("")
            list.add("")
            list.add("")
            list.add("")
            list.add("")
            items.value = list
        }

        fun openIOCDetails(item: String) {
            EventCollector.sendEvent(Event.Action.IOC.OpenIOCDetails(item))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item)
        }
    }

    class InvDetails(val item: String) : IocScope(), Loadable<String> {
        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<String>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        init {
            val list = ArrayList<String>()
            list.add("Cash In Hand")
            list.add("Google Pay")
            list.add("Phone Pay")
            list.add("Amazon Pay")
            list.add("Bhim UPI")
            items.value = list
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = item)
        }

    }

    class IOCListing : IocScope(), Loadable<RetailerData> {
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

    class IOCCreate(val item: RetailerData) : IocScope(), CommonScope.CanGoBack {

        val enableButton: DataSource<Boolean> = DataSource(false)
        val invoiceNum: DataSource<String> = DataSource("")
        val invoiceDate: DataSource<String> = DataSource("")
        private val invoiceDateMili: DataSource<Long> = DataSource(0)
        val totalAmount: DataSource<String> = DataSource("")
        val outstandingAmount: DataSource<String> = DataSource("")


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
            if (data == "0" || data == "0.0") {
                outstandingAmount.value = ""
            } else {
                outstandingAmount.value = data
            }
            validate()
        }

        fun validate() {
            enableButton.value = (invoiceNum.value.isNotEmpty() && invoiceDate.value.isNotEmpty()
                    && totalAmount.value.isNotEmpty() && outstandingAmount.value.isNotEmpty()
                    && invoiceUpload.value.cdnUrl.isNotEmpty())
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

    fun openEditInvoice(item: InvoiceData) {
        EventCollector.sendEvent(Event.Action.IOC.OpenEditIOCBottomSheet(item))
    }

}