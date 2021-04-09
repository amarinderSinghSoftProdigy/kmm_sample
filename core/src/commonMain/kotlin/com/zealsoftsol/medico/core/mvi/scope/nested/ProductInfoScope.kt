package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.DetachedScopeId
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.ProductSearch
import kotlin.reflect.KClass

class ProductInfoScope private constructor(
    val product: ProductSearch,
    private val fromStoresPage: Boolean,
    val alternativeBrands: List<AlternateProductData>,
    val isDetailsOpened: DataSource<Boolean>,
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.BACK)),
    CommonScope.CanGoBack {
    override val scopeId: KClass<*> =
        if (fromStoresPage) Child.TabBar::class else DetachedScopeId::class

    val compositionsString: String
        get() = product.compositions.reduce { acc, s -> "$acc\n$s" }

    fun buy() {
        if (!fromStoresPage) {
            EventCollector.sendEvent(Event.Action.Product.BuyProduct(product.code))
        } else {
            TODO("not implemented")
        }
    }

    fun toggleDetails() {
        isDetailsOpened.value = !isDetailsOpened.value
    }

    fun selectAlternativeProduct(product: AlternateProductData) =
        EventCollector.sendEvent(Event.Action.Product.SelectAlternative(product))

    companion object {
        fun getAsRegular(
            product: ProductSearch,
            fromStoresPage: Boolean,
            alternativeBrands: List<AlternateProductData>,
            isDetailsOpened: DataSource<Boolean> = DataSource(false),
        ): Host.TabBar {
            return Host.TabBar(
                childScope = ProductInfoScope(
                    product,
                    fromStoresPage,
                    alternativeBrands,
                    isDetailsOpened,
                ),
                navigationSectionValue = null,
            )
        }

        fun getAsNested(
            product: ProductSearch,
            fromStoresPage: Boolean,
            alternativeBrands: List<AlternateProductData>,
            isDetailsOpened: DataSource<Boolean> = DataSource(false),
        ) = ProductInfoScope(
            product,
            fromStoresPage,
            alternativeBrands,
            isDetailsOpened,
        )
    }
}