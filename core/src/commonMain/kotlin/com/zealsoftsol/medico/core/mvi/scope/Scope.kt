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
    val storage by lazy { Storage() }

    sealed class Child : Scope() {
        internal abstract val parentScopeId: KClass<*>

        abstract class TabBar(internal val tabBarInfo: TabBarInfo) : Child() {
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
            private val navigationSectionValue: NavigationSection?,
        ) : Host(), CommonScope.CanGoBack {
            val tabBar: DataSource<TabBarInfo> = DataSource(childScope.tabBarInfo)
            val navigationSection: DataSource<NavigationSection?> =
                DataSource(navigationSectionValue)
            val childScope: DataSource<Child.TabBar> = DataSource(childScope)

            internal fun setChildScope(child: Child.TabBar) {
                tabBar.value = child.tabBarInfo
                navigationSection.value = if (child.tabBarInfo.icon != ScopeIcon.HAMBURGER) {
                    null
                } else {
                    navigationSectionValue
                }
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
    abstract val icon: ScopeIcon

    data class Simple(override val icon: ScopeIcon, val titleId: String?) :
        TabBarInfo()

    data class Search(
        override val icon: ScopeIcon,
        val cartItems: DataSource<Int> = DataSource(0)
    ) :
        TabBarInfo() {

        fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search)
    }
}

internal object StartScope : Scope.Host.Regular()

interface Scopable

class Storage {
    private val map = hashMapOf<String, Any>()

    fun save(key: String, value: Any) {
        map[key] = value
    }

    fun restore(key: String) = map[key]

    fun clear(key: String) = map.remove(key)
}
