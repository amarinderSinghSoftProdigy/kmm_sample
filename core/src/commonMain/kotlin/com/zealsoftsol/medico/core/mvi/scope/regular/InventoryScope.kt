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

class InventoryScope(
    var inventoryType: DataSource<InventoryType> = DataSource(InventoryType.ALL),
    var stockStatus: DataSource<StockStatus> = DataSource(StockStatus.ALL)
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("")

    private var mCurrentPage = 0
    val stockStatusData: DataSource<StocksStatusData?> = DataSource(null)
    val onlineStatusData: DataSource<OnlineStatusData?> = DataSource(null)
    val stockExpiredData: DataSource<StockExpiredData?> = DataSource(null)
    val manufacturersList: DataSource<List<ManufacturerData>> = DataSource(emptyList())
    val productsList: DataSource<MutableList<ProductsData>> = DataSource(mutableListOf())
    val showNoBatchesDialog = DataSource(false)

    var totalProducts = 0
    private var mManufacturerCode = ""

    enum class InventoryType(val title: String) {
        ALL("All"), LIMITED_STOCK("Limited Stock"), IN_STOCK("In Stock"),
        OUT_OF_STOCK("Out Of Stock")
    }

    enum class StockStatus(val title: String) {
        ALL("All"), ONLINE("Online"), OFFLINE("Offline")
    }


    init {
        getInventory(true)
    }

    fun hideBatchesDialog() {
        showNoBatchesDialog.value = false
    }

    /**
     * get batches data for the selected product
     */
    fun getBatchesData(spid: String, productsData: ProductsData) {
        EventCollector.sendEvent(Event.Action.Inventory.GetBatches(spid, productsData))
    }

    /**
     * update manufacturer list, name and check first item during initial load of items
     */
    fun updateManufacturersList(list: List<ManufacturerData>) {
        if (manufacturersList.value.isEmpty() && list.isNotEmpty()) {
            manufacturersList.value = list
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
     * update Inventory Type to API
     */
    fun updateInventoryType(type: InventoryType) {
        inventoryType.value = type
        productsList.value.clear()
        getInventory(true)
    }

    /**
     * update Inventory Status to API
     */
    fun updateInventoryStatus(status: StockStatus) {
        stockStatus.value = status
        productsList.value.clear()
        getInventory(true)
    }

    /**
     * start search for a product
     */
    fun startSearch(search: String?) {
        productsList.value.clear()
        if (search.isNullOrEmpty()) {
            getInventory(true)
        } else {
            EventCollector.sendEvent(
                Event.Action.Inventory.GetInventory(
                    page = 0,
                    search = search,
                    manufacturer = mManufacturerCode,
                    status = stockStatus.value,
                    stockStatus = inventoryType.value
                )
            )
        }
    }

    /**
     * update current manufacturer and get new results
     */
    fun updateManufacturer(manufacturerName: String, manufacturerCode: String) {
        productsList.value.clear()
        mManufacturerCode = manufacturerCode
        getInventory(true)
    }

    /**
     * get current Inventory of stockist
     */
    fun getInventory(
        isFirstLoad: Boolean = false,
        search: String? = null,
    ) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1
        EventCollector.sendEvent(
            Event.Action.Inventory.GetInventory(
                page = mCurrentPage,
                search = search,
                manufacturer = mManufacturerCode,
                status = stockStatus.value,
                stockStatus = inventoryType.value
            )
        )
    }
}