package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.Batches
import com.zealsoftsol.medico.data.BatchesData
import com.zealsoftsol.medico.data.Filter

class BatchesScope(val spid: String) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    val batchData: DataSource<List<Batches>> = DataSource(emptyList())

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("")

    init {
        getInitData()
    }

    fun getInitData() {
        EventCollector.sendEvent(Event.Action.Batches.GetBatches)
    }
}