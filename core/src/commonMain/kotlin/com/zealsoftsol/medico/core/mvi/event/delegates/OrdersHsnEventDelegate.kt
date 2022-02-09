package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.regular.OrderHsnEditScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.SearchDataItem

internal class OrdersHsnEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.OrderHsnEditStore,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.OrderHsn>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.OrderHsn) = when (event) {
        is Event.Action.OrderHsn.SaveOrderEntry -> saveEntry(
            orderId = event.orderId,
            orderEntryId = event.orderEntryId,
            servedQty = event.servedQty,
            freeQty = event.freeQty,
            price = event.price,
            batchNo = event.batchNo,
            expiryDate = event.expiryDate,
            mrp = event.mrp,
            hsnCode = event.hsnCode,
            discount = event.discount
        )
        is Event.Action.OrderHsn.Load -> load(event.isFirstLoad)
        is Event.Action.OrderHsn.Search -> search(event.value)
        is Event.Action.OrderHsn.RejectOrderEntry -> rejectOrderEntry(
            orderEntryId = event.orderEntryId,
            spid = event.spid,
            reasonCode = event.reasonCode
        )
        is Event.Action.OrderHsn.AcceptOrderEntry -> acceptOrderEntry(
            orderEntryId = event.orderEntryId,
            spid = event.spid,
        )
        is Event.Action.OrderHsn.GetBatches -> loadBatches()

    }

    /**
     * load batches data from server
     */
    private suspend fun loadBatches() {
        val user = userRepo.requireUser()

        navigator.withScope<OrderHsnEditScope> {
            networkOrdersScope.getBatches(unitCode = user.unitCode, it.orderEntry.value.spid)
                .onSuccess { body ->
                    it.batchData.value = body.results
                }.onError(navigator)
        }
    }

    /**
     * get available hsn codes from server
     */
    private suspend fun load(isFirstLoad: Boolean) {
        loadHelper.load<OrderHsnEditScope, SearchDataItem>(isFirstLoad = isFirstLoad) {
            networkOrdersScope.getHsnCodes(
                search = searchText.value,
                pagination = pagination
            ).getBodyOrNull()
        }
    }

    /**
     * search for hsn codes from server
     */
    private suspend fun search(value: String) {
        loadHelper.search<OrderHsnEditScope, SearchDataItem>(searchValue = value) {
            networkOrdersScope.getHsnCodes(
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    /**
     * save an order entry to backend
     */
    private suspend fun saveEntry(
        orderId: String,
        orderEntryId: String,
        servedQty: Double,
        freeQty: Double,
        price: Double,
        batchNo: String,
        expiryDate: String,
        hsnCode: String,
        mrp: Double,
        discount: Double,
    ) {
        navigator.withScope<OrderHsnEditScope> {
            withProgress {
                networkOrdersScope.saveNewOrder(
                    OrderNewQtyRequest(
                        orderId = orderId,
                        orderEntryId = orderEntryId,
                        unitCode = userRepo.requireUser().unitCode,
                        servedQty = servedQty,
                        freeQty = freeQty,
                        price = price,
                        batchNo = batchNo,
                        expiryDate = expiryDate,
                        hsnCode = hsnCode,
                        mrp = mrp,
                        discount = discount
                    )
                )
            }.onSuccess { body ->
                it.updateOrderEntriesFromServer(body.entries)
                it.changeAlertScope(true)
            }.onError {
                log(it.body)
            }
        }
    }

    /**
     * reject an order entry
     */
    private suspend fun rejectOrderEntry(
        orderEntryId: String,
        spid: String,
        reasonCode: String
    ) {
        navigator.withScope<OrderHsnEditScope> {
            withProgress {
                networkOrdersScope.rejectEntry(
                    orderEntryId = orderEntryId,
                    spid = spid,
                    reasonCode = reasonCode
                )
            }.onSuccess { body ->
                it.updateOrderEntriesFromServer(body.entries)
                it.changeAlertScope(true)
            }.onError {
                log(it.body)
            }
        }
    }

    /**
     * accept an order entry
     */
    private suspend fun acceptOrderEntry(
        orderEntryId: String,
        spid: String,
    ) {
        navigator.withScope<OrderHsnEditScope> {
            withProgress {
                networkOrdersScope.acceptEntry(
                    orderEntryId = orderEntryId,
                    spid = spid,
                )
            }.onSuccess { body ->
                it.updateOrderEntriesFromServer(body.entries)
                it.changeAlertScope(true)
            }.onError {
                log(it.body)
            }
        }
    }

}