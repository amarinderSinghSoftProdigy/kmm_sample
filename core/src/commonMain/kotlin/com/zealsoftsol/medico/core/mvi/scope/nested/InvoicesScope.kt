package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.B2BData
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.TaxInfo

class InvoicesScope : Scope.Child.TabBar(), Loadable<Invoice> {

    override val isRoot: Boolean = true

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
        EventCollector.sendEvent(Event.Action.Invoices.Select(item.info.id))

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
) : Scope.Child.TabBar() {

    fun viewTaxInfo() = EventCollector.sendEvent(Event.Action.Invoices.ShowTaxInfo)

    fun viewInvoice(entry: InvoiceEntry) =
        EventCollector.sendEvent(Event.Action.Invoices.ShowTaxFor(entry))

//    fun download() = EventCollector.sendEvent(Event.Action.Invoices.Download)
}