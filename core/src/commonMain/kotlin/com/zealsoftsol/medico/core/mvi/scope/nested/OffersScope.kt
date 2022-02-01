package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.Manufacturer
import com.zealsoftsol.medico.data.PromotionStatusData
import com.zealsoftsol.medico.data.PromotionType
import com.zealsoftsol.medico.data.Promotions

class OffersScope(
    val title: String
) : Scope.Child.TabBar(), CommonScope.CanGoBack {
    val statuses: DataSource<List<PromotionStatusData>> = DataSource(emptyList())
    val manufacturer: DataSource<List<Manufacturer>> = DataSource(emptyList())
    val items: DataSource<List<Promotions>> = DataSource(emptyList())
    val totalItems: DataSource<Int> = DataSource(0)
    val searchText: DataSource<String> = DataSource("")
    val pagination: Pagination = Pagination()

    init {
        startSearch()
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.OnlyBackHeader(title)
    }

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Offers.LoadMoreProducts)

    fun startSearch() {
        EventCollector.sendEvent(Event.Action.Offers.GetOffers())
    }

    //Open the dialog for update status
    fun showBottomSheet(promotion: PromotionType?,name:String) =
        EventCollector.sendEvent(Event.Action.Offers.ShowBottomSheet(promotion,name))

}