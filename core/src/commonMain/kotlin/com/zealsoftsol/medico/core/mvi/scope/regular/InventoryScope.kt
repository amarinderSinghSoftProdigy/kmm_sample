package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.ManufacturerData
import com.zealsoftsol.medico.data.OnlineStatusData
import com.zealsoftsol.medico.data.ProductsData
import com.zealsoftsol.medico.data.StockExpiredData
import com.zealsoftsol.medico.data.StocksStatusData

class InventoryScope : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("")

    private var mCurrentPage = 0
    val mCurrentManufacturerName = DataSource("")
    val stockStatusData: DataSource<StocksStatusData?> = DataSource(null)
    val onlineStatusData: DataSource<OnlineStatusData?> = DataSource(null)
    val stockExpiredData: DataSource<StockExpiredData?> = DataSource(null)
    val manufacturersList: DataSource<List<ManufacturerData>> = DataSource(emptyList())
    val productsList: DataSource<MutableList<ProductsData>> = DataSource(mutableListOf())

    var totalProducts = 0
    private var mManufacturerCode = ""

    init {
        getInventory(true)
    }

    /**
     * update manufacturer list, name and check first item during initial load of items
     */
    fun updateManufacturersList(list: List<ManufacturerData>) {
        if (manufacturersList.value.isEmpty() && list.isNotEmpty()) {
            mCurrentManufacturerName.value = list[0].name
            mManufacturerCode = list[0].code
            manufacturersList.value = list
            manufacturersList.value[0].isChecked = true
        }
    }

    /**
     * update products list from server
     */
    fun updateProductsList(list: List<ProductsData>) {
        if (productsList.value.isEmpty()) {
            productsList.value = list as MutableList<ProductsData>
        } else {
            productsList.value.addAll(list)
        }
    }

    /**
     * start search for a product
     */
    fun startSearch(search: String?){
        productsList.value.clear()
        if(search.isNullOrEmpty()){
            getInventory(true)
        }else {
            EventCollector.sendEvent(
                Event.Action.Inventory.GetInventory(
                    page = 0,
                    search = search,
                    manufacturer = mManufacturerCode
                )
            )
        }
    }

    /**
     * update current manufacturer and get new results
     */
    fun updateManufacturer(manufacturerName: String, manufacturerCode: String) {
        productsList.value.clear()
        mCurrentManufacturerName.value = manufacturerName
        mManufacturerCode = manufacturerCode
        getInventory(true)
    }

    /**
     * get current Inventory of stockist
     */
    fun getInventory(isFirstLoad: Boolean = false, search: String? = null) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1
        EventCollector.sendEvent(
            Event.Action.Inventory.GetInventory(
                page = mCurrentPage,
                search = search,
                manufacturer = mManufacturerCode
            )
        )
    }
}