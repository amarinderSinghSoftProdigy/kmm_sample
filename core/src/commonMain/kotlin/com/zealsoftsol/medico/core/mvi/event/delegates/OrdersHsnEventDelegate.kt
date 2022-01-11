package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.LoadHelper

internal class OrdersHsnEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.OrderHsnEditStore,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.OrderHsn>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.OrderHsn) = when (event) {
        is Event.Action.OrderHsn.SelectHsn -> loadHsn(false)
    }

    private fun loadHsn(isFirstLoad: Boolean) {
        navigator.withScope<ViewOrderScope> {
           /* navigator.setScope(OrderHsnEditScope(orderEntry))
            navigator.scope.value.bottomSheet.value = BottomSheet.ModifyOrderEntry(
                orderEntry,
                isChecked = DataSource(orderEntry in it.checkedEntries.value),
                canEdit = it.canEdit,
            )*/
        }
    }

}