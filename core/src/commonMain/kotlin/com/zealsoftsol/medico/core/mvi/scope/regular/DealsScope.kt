package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.DealsData
import com.zealsoftsol.medico.data.UserType

class DealsScope(val cartItemsCount: ReadOnlyDataSource<Int>) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("offers", cartItemsCount)

    val dealsList = DataSource<MutableList<DealsData>>(mutableListOf())
    val showToast = DataSource(false)
    var qty: String = ""
    var freeQty: String = ""
    var productName: String = ""

    var totalItems = 0
    private var mCurrentPage = 0

    //get list of all deals
    init {
        getDeals(true)
    }

    fun updateAlertVisibility(visibility: Boolean) {
        this.showToast.value = visibility
    }

    fun addToCart(
        sellerUnitCode: String?,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier?,
        quantity: Double,
        freeQuantity: Double,
        prodName: String
    ) {
        this.productName = prodName
        this.freeQty = freeQuantity.toString()
        this.qty = quantity.toString()

        EventCollector.sendEvent(
            Event.Action.Deals.AddItemToCart(
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity,
                freeQuantity
            )
        )
    }

    /**
     * get all available
     */
    fun getDeals(
        isFirstLoad: Boolean = false,
        search: String = "",
    ) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1

        EventCollector.sendEvent(
            Event.Action.Deals.GetAllDeals(
                page = mCurrentPage,
                search = search,
            )
        )
    }

    /**
     * start search for a deal
     */
    fun startSearch(search: String?) {
        dealsList.value.clear()
        if (search.isNullOrEmpty()) {
            getDeals(true)
        } else {
            mCurrentPage = 0
            EventCollector.sendEvent(
                Event.Action.Deals.GetAllDeals(
                    page = 0,
                    search = search,
                )
            )
        }
    }


    /**
     * update current manufacturer and get new results
     */
    fun updateDeals(list: List<DealsData>) {
        if (dealsList.value.isEmpty()) {
            dealsList.value = list as MutableList<DealsData>
        } else {
            dealsList.value.addAll(list)
        }
    }

    /**
     * zoom selected image
     */
    fun zoomImage(url: String) = EventCollector.sendEvent(Event.Action.Deals.ZoomImage(url))

    /**
     * move to stockist screen and connect
     */
    fun moveToStockist(tradeName: String) =
        EventCollector.sendEvent(Event.Transition.Management(UserType.STOCKIST, tradeName))
}