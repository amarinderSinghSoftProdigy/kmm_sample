package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.BannerItemData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.UserType

class BannersScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("offers")

    val bannersList = DataSource<MutableList<BannerItemData>>(mutableListOf())
    val showToast = DataSource(false)
    var qty: String = ""
    var freeQty: String = ""
    var productName: String = ""

    var totalItems = 0
    private var mCurrentPage = 0

    //get list of all banners
    init {
        getBanners(true)
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
            Event.Action.Banners.AddItemToCart(
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
    fun getBanners(
        isFirstLoad: Boolean = false,
        search: String = "",
    ) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1

        EventCollector.sendEvent(
            Event.Action.Banners.GetAllBanners(
                page = mCurrentPage,
                search = search,
            )
        )
    }

    /**
     * start search for a banner
     */
    fun startSearch(search: String?) {
        bannersList.value.clear()
        if (search.isNullOrEmpty()) {
            getBanners(true)
        } else {
            mCurrentPage = 0
            EventCollector.sendEvent(
                Event.Action.Banners.GetAllBanners(
                    page = 0,
                    search = search,
                )
            )
        }
    }


    /**
     * update current manufacturer and get new results
     */
    fun updateBanners(list: List<BannerItemData>) {
        if (bannersList.value.isEmpty()) {
            bannersList.value = list as MutableList<BannerItemData>
        } else {
            bannersList.value.addAll(list)
        }
    }

    /**
     * zoom selected image
     */
    fun zoomImage(url: String) = EventCollector.sendEvent(Event.Action.Banners.ZoomImage(url))

    /**
     * move to stockist screen and connect
     */
    fun moveToStockist(tradeName: String) =
        EventCollector.sendEvent(Event.Transition.Management(UserType.STOCKIST, tradeName))
}