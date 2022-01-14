package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.SearchDataItem

internal class OrdersHsnEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.OrderHsnEditStore,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.OrderHsn>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.OrderHsn) = when (event) {
        is Event.Action.OrderHsn.SelectHsn ->{ }
        is Event.Action.OrderHsn.Load -> getHsnCodes(event.isFirstLoad)
        is Event.Action.OrderHsn.GetSelectedHsnCode ->  getSelectedHsnCode(event.selectedHsnCode)
        is Event.Action.OrderHsn.DisplayHsnCodes -> selectHsn(event.hsnList as ArrayList<SearchDataItem>)
    }

    /**
     * get selected hsn code by user
     */
    private fun getSelectedHsnCode(selectedHsnCode: String) {
        navigator.withScope<OrderHsnEditScope> {
            it.getSelectedHsnCode(selectedHsnCode)
        }
    }

    /**
     * get available hsn codes from server
     */
    private suspend fun getHsnCodes(isFirstLoad: Boolean) {
        loadHelper.load<OrderHsnEditScope, SearchDataItem>(isFirstLoad = isFirstLoad) {
            networkOrdersScope.getHsnCodes(
                pagination = pagination
            ).getBodyOrNull()
        }

      /*  navigator.withScope<OrderHsnEditScope> {
            val result = withProgress {
                userRepo.getHsnCodes()
            }

            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                it.updateDataFromServer(
                    data!!.results as ArrayList<SearchDataItem>,
                )
                selectHsn(data.results as ArrayList<SearchDataItem>)
            }.onError(navigator)
        }*/
    }

    /**
     * open bottom sheet with hsn codes
     * @param hsnList list of hsn codes
     */
    private fun selectHsn(hsnList : ArrayList<SearchDataItem>) {
        navigator.withScope<OrderHsnEditScope> {
            navigator.scope.value.bottomSheet.value = BottomSheet.SelectHsnEntry(
                hsnList,false,
            )
        }
    }

}