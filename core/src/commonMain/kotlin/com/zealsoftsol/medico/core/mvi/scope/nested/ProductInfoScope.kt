package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.ProductSearch

class ProductInfoScope(
    private val sellerUnitCode: String?,
    val product: ProductSearch,
    val alternativeBrands: List<AlternateProductData>,
    val isDetailsOpened: DataSource<Boolean> = DataSource(false),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    val compositionsString: String
        get() = product.compositions.reduce { acc, s -> "$acc\n$s" }

    fun buy(): Boolean {
        return sellerUnitCode?.let { unitCode ->
            product.sellerInfo?.spid?.let { spid ->
                EventCollector.sendEvent(
                    Event.Action.Cart.AddItem(
                        unitCode,
                        product.code,
                        product.buyingOption!!,
                        CartIdentifier(spid),
                        1,
                    )
                )
            } ?: false
        } ?: EventCollector.sendEvent(
            Event.Action.Product.BuyProduct(
                product.code,
                product.buyingOption!!
            )
        )
    }

    fun toggleDetails() {
        isDetailsOpened.value = !isDetailsOpened.value
    }

    fun selectAlternativeProduct(product: AlternateProductData) =
        EventCollector.sendEvent(Event.Action.Product.SelectAlternative(product))
}