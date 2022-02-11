package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.Batches

class BatchesScope(
    val spid: String,
    val batchData: DataSource<List<Batches>?> = DataSource(emptyList()),
    var selectedBatchData: DataSource<OrderHsnEditScope.SelectedBatchData>
) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("")

    val showSuccessAlert = DataSource(false)

    fun updateSuccessAlertVisibility(showAlert: Boolean){
        this.showSuccessAlert.value = showAlert
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
        selectedBatchData.value = OrderHsnEditScope.SelectedBatchData(
            batch = batchNo, quantity = qty,
            ptr = price, mrp = mrp, expiry = expiry, selectedHsnCode = hsnCode
        )
    }
}