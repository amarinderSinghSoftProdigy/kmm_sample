package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.DemoClass
import com.zealsoftsol.medico.data.DemoResponse
import com.zealsoftsol.medico.data.EmployeeData

class DemoScope : Scope.Child.TabBar() {

    var demoData: DataSource<List<DemoResponse>> = DataSource(emptyList())

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("Demos")

    init {
        EventCollector.sendEvent(Event.Action.Demo.MyDemo)
    }

}