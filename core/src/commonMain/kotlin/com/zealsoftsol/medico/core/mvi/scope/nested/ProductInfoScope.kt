package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.DetachedScopeId
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.ProductData
import com.zealsoftsol.medico.data.User
import kotlin.reflect.KClass

class ProductInfoScope private constructor(
    override val user: ReadOnlyDataSource<User>,
    val product: ProductData,
    val alternativeBrands: List<Any>,
    val isDetailsOpened: DataSource<Boolean>,
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.BACK)),
    CommonScope.WithUser,
    CommonScope.CanGoBack {
    override val scopeId: KClass<*> = DetachedScopeId::class

    val compositionsString: String
        get() = product.compositionsData
            .map { "${it.composition.name} ${it.strength.name}" }
            .reduce { acc, s -> "$acc\n$s" }

    fun addToCart() {

    }

    fun toggleDetails() {
        isDetailsOpened.value = !isDetailsOpened.value
    }

    fun selectAlternativeProduct(product: Any) =
        EventCollector.sendEvent(Event.Action.Product.Select(TODO("backend not ready")))

    companion object {
        fun get(
            user: User,
            userDataSource: ReadOnlyDataSource<User>,
            product: ProductData,
            alternativeBrands: List<Any>,
            isDetailsOpened: DataSource<Boolean> = DataSource(false),
        ): Host.TabBar {
            return Host.TabBar(
                childScope = ProductInfoScope(
                    userDataSource,
                    product,
                    alternativeBrands,
                    isDetailsOpened
                ),
                navigationSectionValue = NavigationSection(
                    userDataSource,
                    NavigationOption.default(user.type),
                    NavigationOption.footer()
                ),
            )
        }
    }
}