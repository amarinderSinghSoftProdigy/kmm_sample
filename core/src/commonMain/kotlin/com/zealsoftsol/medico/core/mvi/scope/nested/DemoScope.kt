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

open class DemoScope : Scope.Child.TabBar() {

    class DemoListing : DemoScope() {
        var demoData: DataSource<List<DemoResponse>> = DataSource(emptyList())

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
            TabBarInfo.OnlyBackHeader("Demos")

        init {
            EventCollector.sendEvent(Event.Action.Demo.MyDemo)
        }

        fun openVideo(url: String) {
            EventCollector.sendEvent(Event.Action.Demo.OpenVideo(url))
        }
    }

    class DemoPlayer(url: String) : DemoScope() {
        var demoUrl: DataSource<String> = DataSource(url)

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
            TabBarInfo.OnlyBackHeader("Video")
    }

}