package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.GeoData
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.UploadResponseData

sealed class IocScope : Scope.Child.TabBar(), CommonScope.UploadDocument {
    override val supportedFileTypes: Array<FileType> = FileType.forProfile()

    val invoiceUpload: DataSource<UploadResponseData?> = DataSource(null)

    class IOCListing : IocScope(), Loadable<RetailerData> {
        override val isRoot: Boolean = false
        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<RetailerData>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        val selectedIndex: DataSource<Int> = DataSource(-1)
        override val searchText: DataSource<String> = DataSource("")

        init {
            EventCollector.sendEvent(Event.Action.IOC.Load(searchText.value))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = "create_debt")
        }

        fun updateIndex(index: Int) {
            selectedIndex.value = index
        }

        fun selectItem(item: String) =
            EventCollector.sendEvent(Event.Action.IOC.Select(item))

        fun search(value: String) = EventCollector.sendEvent(Event.Action.IOC.Search(value))

        fun loadItems() = EventCollector.sendEvent(Event.Action.IOC.LoadMoreProducts)

    }

    class IOCCreate(val title: String) : IocScope() {

        val invoiceNum: DataSource<String> = DataSource("")
        val invoiceDate: DataSource<String> = DataSource("")
        val totalAmount: DataSource<String> = DataSource("")
        val outstandingAmount: DataSource<String> = DataSource("")


        fun updateInvoiceNum(data: String) {
            invoiceNum.value = data
        }

        fun updateInvoiceDate(data: String) {
            invoiceDate.value = data
        }

        fun updateTotalAmount(data: String) {
            totalAmount.value = data
        }

        fun updateOutstandingAmount(data: String) {
            outstandingAmount.value = data
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title = title)
        }

        fun previewImage(item: String) =
            EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(item, type = "type"))

    }
}