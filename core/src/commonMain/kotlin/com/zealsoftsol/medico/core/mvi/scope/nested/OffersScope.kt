package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.OfferData
import com.zealsoftsol.medico.data.PromotionType

class OffersScope(
    val title: String
) : Scope.Child.TabBar(), CommonScope.CanGoBack, Loadable<OfferData> {

    override val items: DataSource<List<OfferData>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(getCurrentOffers())
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.OnlyBackHeader(title)
    }

    /**
     * get current offers
     */
    private fun getCurrentOffers() =
        Event.Action.Offers.GetOffers

    //Open the dialog for update status
    fun showBottomSheet(promotion: PromotionType?) =
        EventCollector.sendEvent( Event.Action.Offers.ShowBottomSheet(promotion))

}