package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
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
            event.orderId,
            event.orderEntryId,
            event.servedQty,
            event.freeQty,
            event.price,
            event.batchNo,
            event.expiryDate
        )
        is Event.Action.OrderHsn.Load -> load(event.isFirstLoad)
        is Event.Action.OrderHsn.Search -> search(event.value)
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
        expiryDate: String
    ) {
        navigator.withScope<OrderHsnEditScope> {
            withProgress {
                networkOrdersScope.saveNewOrderQty(
                    OrderNewQtyRequest(
                        orderId = orderId,
                        orderEntryId = orderEntryId,
                        unitCode = userRepo.requireUser().unitCode,
                        servedQty = servedQty,
                        freeQty = freeQty,
                        price = price,
                        batchNo = batchNo,
                        expiryDate = expiryDate,
                    )
                )
            }.onSuccess { body ->
                log(body.toString())
            }.onError {
                log(it.body)
            }
        }
    }

}