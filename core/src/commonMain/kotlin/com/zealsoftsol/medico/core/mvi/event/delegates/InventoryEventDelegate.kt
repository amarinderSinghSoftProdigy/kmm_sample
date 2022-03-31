package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.data.BatchStatusUpdateRequest
import com.zealsoftsol.medico.data.BatchUpdateRequest
import com.zealsoftsol.medico.data.ProductsData

internal class InventoryEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val inventoryScope: NetworkScope.InventoryStore,

    ) : EventDelegate<Event.Action.Inventory>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Inventory) = when (event) {
        is Event.Action.Inventory.GetInventory -> load(
            search = event.search,
            manufacturer = event.manufacturer,
            page = event.page,
            status = event.status,
            stockStatus = event.stockStatus
        )
        is Event.Action.Inventory.GetBatches -> loadBatches(event.spid, event.productsData)
        is Event.Action.Inventory.EditBatch -> openDetails(event.item, event.productsData)
        is Event.Action.Inventory.UpdateBatch -> updateBatch(event.batchData)
        is Event.Action.Inventory.UpdateBatchStatus -> updateBatchStatus(event.batchData)
    }

    private suspend fun updateBatchStatus(spid: BatchStatusUpdateRequest) {
        val user = userRepo.requireUser()
        navigator.withScope<BatchesScope> {
            withProgress {
                inventoryScope.updateBatchStatus(unitCode = user.unitCode, spid)
                    .onSuccess { body ->

                    }.onError(navigator)
            }
        }
    }

    private suspend fun updateBatch(item: BatchUpdateRequest) {
        val user = userRepo.requireUser()
        navigator.withScope<BatchesScope> {
            withProgress {
                inventoryScope.editBatches(
                    unitCode = user.unitCode,
                    item
                )
            }.onSuccess { _ ->
                it.refresh()
            }.onError(navigator)
        }
    }

    /**
     * load edit batches
     */
    private fun openDetails(item: Batch, productsData: ProductsData) {
        navigator.withScope<BatchesScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.EditBatchSheet(
                item,
                productsData,
            )
        }
    }

    /**
     * load batches data from server
     */
    private suspend fun loadBatches(spid: String, productsData: ProductsData) {
        val user = userRepo.requireUser()

        navigator.withScope<InventoryScope> {
            withProgress {
                inventoryScope.getBatches(unitCode = user.unitCode, spid)
                    .onSuccess { body ->
                        if (body.results[0].batches.isNotEmpty()) {
                            setScope(
                                BatchesScope(
                                    spid = spid,
                                    batchData = DataSource(body.results),
                                    requiredQty = 0.0,
                                    selectedBatchData = DataSource(null),
                                    productsData = productsData
                                )
                            )
                        } else {
                            it.showNoBatchesDialog.value = true
                        }
                    }.onError(navigator)
            }
        }
    }

    /**
     * get the Inventory data and pass it to scope
     */
    private suspend fun load(
        search: String?,
        manufacturer: String?,
        page: Int,
        stockStatus: InventoryScope.InventoryType,
        status: InventoryScope.InventoryStatus
    ) {
        navigator.withScope<InventoryScope> {
            withProgress {
                inventoryScope.getInventoryData(
                    unitCode = userRepo.requireUser().unitCode,
                    manufacturer = manufacturer,
                    search = search,
                    page = page,
                    status = status,
                    stockStatus = stockStatus
                )
            }.onSuccess { body ->
                if (search.isNullOrEmpty()) {
                    it.stockStatusData.value = body.stockStatusData
                    it.onlineStatusData.value = body.onlineStatusData
                    it.stockExpiredData.value = body.stockExpiredData
                    it.updateManufacturersList(body.manufacturers)
                    it.updateProductsList(body.results)
                } else {
                    it.updateProductsList(body.results)
                }
                it.totalProducts = body.totalResults
            }.onError(navigator)
        }
    }
}