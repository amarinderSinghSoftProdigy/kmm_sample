package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.ManufacturerData
import com.zealsoftsol.medico.data.UserType

class ManufacturerScope() : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("manufacturers")

    val manufacturers: DataSource<List<ManufacturerData>> = DataSource(emptyList())

    init {
        getManufacturers()
    }

    private fun getManufacturers(
        isFirstLoad: Boolean = false,
        search: String = "",
    ) {
        EventCollector.sendEvent(
            Event.Action.Manufacturers.GetManufacturers(
                page = 1,
                search = search,
                unitCode = "",
            )
        )
    }

    fun moveToStockist(tradeName: String) =
        EventCollector.sendEvent(Event.Transition.Management(UserType.STOCKIST, tradeName))
}