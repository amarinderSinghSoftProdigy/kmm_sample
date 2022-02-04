package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Manufacturer
import com.zealsoftsol.medico.data.OfferProduct
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.PromotionStatusData
import com.zealsoftsol.medico.data.PromotionType
import com.zealsoftsol.medico.data.Promotions

sealed class OffersScope : Scope.Child.TabBar() {
    class ViewOffers(
        private val title: String
    ) : OffersScope(), CommonScope.CanGoBack {
        val productSearch: DataSource<String> = DataSource("")
        val manufacturerSearch: DataSource<ArrayList<String>> = DataSource(ArrayList())
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

        fun openCreateOffer() =
            EventCollector.sendEvent(Event.Transition.CreateOffers)

        fun startSearch() {
            reset()
            EventCollector.sendEvent(Event.Action.Offers.GetOffers())
        }

        fun reset() {
            productSearch.value = ""
            manufacturerSearch.value = ArrayList()
        }

        fun startSearch(search: String) {
            EventCollector.sendEvent(Event.Action.Offers.GetOffers(search = search))
        }

        fun startSearch(query: ArrayList<String>) {
            EventCollector.sendEvent(Event.Action.Offers.GetOffers(query = query))
        }

        //Open the dialog for update status
        fun showBottomSheet(promotion: String, name: String, active: Boolean) =
            EventCollector.sendEvent(Event.Action.Offers.ShowBottomSheet(promotion, name, active))
    }


    class CreateOffer(
        private val title: String
    ) : OffersScope(), CommonScope.CanGoBack {
        val saveClicked: DataSource<Boolean> = DataSource(true)
        val activeTab: DataSource<String> = DataSource("")
        val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList())
        val selectedProduct: DataSource<OfferProduct?> = DataSource(null)
        val productSearch: DataSource<String> = DataSource("")
        val dialogMessage: DataSource<String> = DataSource("")
        val promoTypes: DataSource<List<PromotionType>> = DataSource(emptyList())
        val showAlert: DataSource<Boolean> = DataSource(false)
        val requestData: DataSource<OfferProductRequest?> = DataSource(null)

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.OnlyBackHeader(title)
        }

        init {
            startSearch()
        }

        fun changeAlertScope(enable: Boolean) {
            this.showAlert.value = enable
        }

        fun startSearch() {
            EventCollector.sendEvent(Event.Action.Offers.GetTypes)
        }

        fun searchProduct(search: String) {
            EventCollector.sendEvent(Event.Action.Offers.SearchAutoComplete(search))
        }

        fun selectAutoComplete(autoComplete: AutoComplete) =
            EventCollector.sendEvent(Event.Action.Offers.SelectAutoComplete(autoComplete))

        fun selectTab(name: String) {
            activeTab.value = name
        }

        fun saveData() {
            saveClicked.value = !saveClicked.value
        }

        fun saveData(request: OfferProductRequest) {
            requestData.value = request
        }

        fun saveOffer(request: OfferProductRequest, product: OfferProduct?) {
            request.manufacturerCode = product?.manufacturerCode
            request.productCode = product?.code
            request.spid = product?.spid
            request.isOfferForAllUsers = true
            request.connectedUsers = ArrayList()
            request.discount = 0.0
            request.stock = 0.0
            request.startDate = ""
            request.endDate = ""
            EventCollector.sendEvent(Event.Action.Offers.SaveOffer(request))
        }
    }
}