package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.data.BatchStatusUpdateRequest
import com.zealsoftsol.medico.data.Batches
import com.zealsoftsol.medico.data.ProductsData

class BatchesScope(
    val spid: String,
    val batchData: DataSource<List<Batches>?> = DataSource(emptyList()),
    var selectedBatchData: DataSource<OrderHsnEditScope.SelectedBatchData?>,
    val requiredQty: Double,
    val productsData: ProductsData
) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("view_batches")

    val showErrorAlert = DataSource(false)

    fun updateSuccessAlertVisibility(showAlert: Boolean) {
        this.showErrorAlert.value = showAlert
    }

    /**
     * update selected batch by user
     */
    fun updateData(
        batchNo: String,
        qty: String,
        price: String,
        mrp: String,
        expiry: String,
        hsnCode: String
    ) {
        if (qty.toDouble() >= requiredQty) {
            selectedBatchData.value = OrderHsnEditScope.SelectedBatchData(
                batch = batchNo, quantity = qty,
                ptr = price, mrp = mrp, expiry = expiry, selectedHsnCode = hsnCode
            )
            goBack()
        } else {
            showErrorAlert.value = true
        }
    }

    /**
     * Show edit bottom sheet
     */
    fun showEditBottomSheet(item: Batch) {
        EventCollector.sendEvent(
            Event.Action.Inventory.EditBatch(
                item,
                productsData
            )
        )
    }


    fun updateBatchStatus(item: Batch, status: Boolean) {
        val request = BatchStatusUpdateRequest(
            productCode = productsData.productCode ?: "",
            manufacturerCode = productsData.manufacturerCode ?: "",
            hsnCode = item.hsncode,
            spid = productsData.spid ?: "",
            warehouseCode = productsData.warehouseCode ?: "",
            status = if (status) "ONLINE" else "OFFLINE",
        )
        EventCollector.sendEvent(Event.Action.Inventory.UpdateBatchStatus(request))
    }

    //update data after editing
    fun refresh() {
        EventCollector.sendEvent(Event.Action.Batches.GetBatches(spid, productsData))
    }
}