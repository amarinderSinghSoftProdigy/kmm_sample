package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.InvoicesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceEntry

internal class InvoicesEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.Orders,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Invoices>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Invoices) = when (event) {
        is Event.Action.Invoices.Load -> loadInvoices(event.isFirstLoad)
        is Event.Action.Invoices.Search -> searchInvoices(event.value)
        is Event.Action.Invoices.Select -> selectInvoice(event.invoiceId, event.isPoInvoice)
        is Event.Action.Invoices.Download -> download()
        is Event.Action.Invoices.ShowTaxInfo -> showTaxInfo()
        is Event.Action.Invoices.ShowTaxFor -> showTaxFor(event.invoiceEntry)
    }

    private suspend fun loadInvoices(isFirstLoad: Boolean) {
        loadHelper.load<InvoicesScope, Invoice>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkOrdersScope.getInvoices(
                isPoInvoice = isPoInvoice,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchInvoices(search: String) {
        loadHelper.search<InvoicesScope, Invoice>(searchValue = search) {
            val user = userRepo.requireUser()
            networkOrdersScope.getInvoices(
                isPoInvoice = isPoInvoice,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun selectInvoice(invoiceId: String, isPoInvoice: Boolean) {
        navigator.withScope<Scopable> {
            withProgress {
                networkOrdersScope.getInvoice(
                    isPoInvoice,
                    userRepo.requireUser().unitCode,
                    invoiceId
                )
            }.onSuccess { body ->
                setScope(
                    ViewInvoiceScope(
                        DataSource(body.taxInfo),
                        DataSource(if (isPoInvoice) body.buyerData else body.sellerData),
                        DataSource(body.invoiceEntries),
                    )
                )
            }.onError(navigator)
        }
    }

    private suspend fun download() {
        navigator.withScope<ViewInvoiceScope> {

        }
    }

    private fun showTaxInfo() {
        navigator.withScope<ViewInvoiceScope> {
            navigator.scope.value.bottomSheet.value = BottomSheet.ViewTaxInfo(it.taxInfo.value)
        }
    }

    private fun showTaxFor(invoiceEntry: InvoiceEntry) {
        navigator.withScope<ViewInvoiceScope> {
            navigator.scope.value.bottomSheet.value = BottomSheet.ViewItemTax(invoiceEntry)
        }
    }
}