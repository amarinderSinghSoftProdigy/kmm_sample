package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.data.ErrorCode
import kotlin.reflect.KClass

sealed class Scope : Scopable {
    internal val queueId: String = this::class.qualifiedName.orEmpty()
    internal abstract val scopeId: KClass<*>

    sealed class Child : Scope() {
        internal abstract val parentScopeId: KClass<*>

        abstract class TabBar(internal val icon: ScopeIcon, internal val titleId: String?) :
            Child() {
            override val scopeId: KClass<*> = Child.TabBar::class
            override val parentScopeId: KClass<*> = Host::class
        }
    }

    sealed class Host : Scope() {
        val isInProgress: DataSource<Boolean> = DataSource(false)
        override val scopeId: KClass<*> = Host::class
        val alertError: DataSource<ErrorCode?> = DataSource(null)
        val bottomSheet: DataSource<BottomSheet?> = DataSource(null)

        fun dismissAlertError() {
            alertError.value = null
        }

        fun dismissBottomSheet() {
            bottomSheet.value = null
        }

        abstract class Regular : Host()

        open class TabBar(
            childScope: Child.TabBar,
            tabBarInfo: TabBarInfo,
            val navigationSection: NavigationSection?,
        ) : Host(), CommonScope.CanGoBack {
            val tabBar: DataSource<TabBarInfo> =
                DataSource(tabBarInfo.apply(childScope.icon, childScope.titleId))
            val childScope: DataSource<Child.TabBar> = DataSource(childScope)

            internal fun setChildScope(child: Child.TabBar) {
                tabBar.value = tabBar.value.apply(child.icon, child.titleId)
                childScope.value = child
            }
        }
    }
}

internal object DetachedScopeId

enum class ScopeIcon {
    NO_ICON, BACK, HAMBURGER;
}

sealed class TabBarInfo {

    abstract fun apply(icon: ScopeIcon, titleId: String?): TabBarInfo

    data class Simple(val icon: ScopeIcon = ScopeIcon.NO_ICON, val titleId: String? = null) :
        TabBarInfo() {
        override fun apply(icon: ScopeIcon, titleId: String?): TabBarInfo =
            copy(icon = icon, titleId = titleId)
    }

    data class Search(val icon: ScopeIcon = ScopeIcon.NO_ICON, val cartItems: Int = 0) :
        TabBarInfo() {
        override fun apply(icon: ScopeIcon, titleId: String?): TabBarInfo = copy(icon = icon)

        fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search)
    }
}

internal object StartScope : Scope.Host.Regular()

interface Scopable

