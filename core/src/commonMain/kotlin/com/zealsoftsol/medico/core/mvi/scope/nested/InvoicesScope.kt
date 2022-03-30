package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.B2BData
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.TaxInfo

class InvoicesScope(val isPoInvoice: Boolean, val unreadNotifications: ReadOnlyDataSource<Int>) : Scope.Child.TabBar(), Loadable<Invoice> {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("invoices")


    override val isRoot: Boolean = false

    override val items: DataSource<List<Invoice>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    val isFilterOpened: DataSource<Boolean> = DataSource(false)
    val dateRange: DataSource<DateRange?> = DataSource(null)

    init {
        EventCollector.sendEvent(Event.Action.Invoices.Load(isFirstLoad = true))
    }

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.Invoices.Load(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.Invoices.Search(value))
        } else {
            false
        }
    }

    fun selectItem(item: Invoice) =
        EventCollector.sendEvent(Event.Action.Invoices.Select(item.info.id, isPoInvoice))

    fun setFrom(fromMs: Long) {
        this.dateRange.value = dateRange.value?.copy(fromMs = fromMs) ?: DateRange(fromMs = fromMs)
        EventCollector.sendEvent(Event.Action.Invoices.Load(isFirstLoad = true))
    }

    fun setTo(toMs: Long) {
        this.dateRange.value = dateRange.value?.copy(toMs = toMs) ?: DateRange(toMs = toMs)
        EventCollector.sendEvent(Event.Action.Invoices.Load(isFirstLoad = true))
    }

    fun clearFilters() {
        dateRange.value = null
        EventCollector.sendEvent(Event.Action.Invoices.Load(isFirstLoad = true))
    }

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }
}

class ViewInvoiceScope(
    val taxInfo: DataSource<TaxInfo>,
    val b2bData: DataSource<B2BData>,
    val entries: DataSource<List<InvoiceEntry>>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(), CommonScope.WithNotifications {

    val actions = DataSource(listOf(Action.VIEW_QR, Action.DOWNLOAD_INVOICE))

    fun viewTaxInfo() = EventCollector.sendEvent(Event.Action.Invoices.ShowTaxInfo)

    fun viewInvoice(entry: InvoiceEntry) =
        EventCollector.sendEvent(Event.Action.Invoices.ShowTaxFor(entry))

    fun acceptAction(action: Action, payload: Any? = null) =
        EventCollector.sendEvent(Event.Action.Invoices.ViewInvoiceAction(action, payload))

    fun sendInvoiceDownloadResult(isSuccess: Boolean) {
        notifications.value = InvoiceDownloadResult(isSuccess)
    }

    enum class Action(
        val stringId: String,
        val weight: Float,
        val bgColorHex: String,
        val textColorHex: String = "#003657"
    ) {
        VIEW_QR("view_qr", 0.4f, "#FFD600"),
        DOWNLOAD_INVOICE("download_invoice", 0.6f, "#0084D4", "#FFFFFF"),
    }

    object InvoiceDownloading : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String = "invoice_downloading"
        override val body: String? = null
    }

    data class InvoiceDownloadResult(private val isSuccess: Boolean) : ScopeNotification {
        override val isSimple: Boolean = true
        override val isDismissible: Boolean = true
        override val title: String = "invoice_downloading"
        override val body: String =
            if (isSuccess) "invoice_download_success" else "invoice_download_fail"
    }
}