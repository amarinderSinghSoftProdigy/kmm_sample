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
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.data.UploadResponseData


sealed class IocScope : Scope.Child.TabBar(), CommonScope.CanGoBack, CommonScope.UploadDocument {
    override val supportedFileTypes: Array<FileType> = FileType.forProfile()

    val invoiceUpload: DataSource<UploadResponseData> =
        DataSource(UploadResponseData("", "", "", ""))

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

    class IOCCreate(val item: RetailerData) : IocScope() {

        val invoiceNum: DataSource<String> = DataSource("")
        val invoiceDate: DataSource<String> = DataSource("")
        private val invoiceDateMili: DataSource<Long> = DataSource(0)
        val totalAmount: DataSource<String> = DataSource("")
        val outstandingAmount: DataSource<String> = DataSource("")


        fun updateInvoiceNum(data: String) {
            invoiceNum.value = data
        }

        fun updateInvoiceDate(data: String, mili: Long) {
            invoiceDate.value = data
            invoiceDateMili.value = mili
        }

        fun updateTotalAmount(data: String) {
            if (data == "0" || data == "0.0") {
                totalAmount.value = ""
                return
            }
            totalAmount.value = data
        }

        fun updateOutstandingAmount(data: String) {
            outstandingAmount.value = data
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

        fun previewImage(item: String) =
            EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(item))

    }
}