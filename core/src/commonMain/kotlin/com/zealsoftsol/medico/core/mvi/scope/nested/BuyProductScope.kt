package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.DetachedScopeId
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import kotlin.reflect.KClass

class BuyProductScope private constructor(
    val product: ProductSearch,
    val sellersInfo: List<SellerInfo>,
    val quantities: DataSource<Map<SellerInfo, Int>> = DataSource(mapOf()),
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.BACK)),
    CommonScope.CanGoBack {
    override val scopeId: KClass<*> = DetachedScopeId::class

    fun inc(sellerInfo: SellerInfo) {
        val count = quantities.value[sellerInfo] ?: 0
        quantities.value = quantities.value.toMutableMap().also {
            it[sellerInfo] = count + 1
        }
    }

    fun dec(sellerInfo: SellerInfo) {
        quantities.value = quantities.value
            .mapValues { (info, count) ->
                if (sellerInfo == info) (count - 1).coerceAtLeast(0) else count
            }
    }

    fun addToCart(sellerInfo: SellerInfo) {
        // TODO not implemented
    }

    companion object {
        fun get(
            product: ProductSearch,
            sellersInfo: List<SellerInfo>,
        ): Host.TabBar {
            return Host.TabBar(
                childScope = BuyProductScope(
                    product,
                    sellersInfo,
                ),
                navigationSectionValue = null,
            )
        }
    }
}