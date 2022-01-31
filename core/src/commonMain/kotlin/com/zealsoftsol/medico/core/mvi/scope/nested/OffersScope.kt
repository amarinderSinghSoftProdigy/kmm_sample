package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource

class OffersScope(
    val string: String
) : Scope.Child.TabBar(), CommonScope.CanGoBack {


    init {
        EventCollector.sendEvent(getCurrentOffers())
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    /**
     * get current offers
     */
    private fun getCurrentOffers() =
        Event.Action.Offers.GetOffers

}