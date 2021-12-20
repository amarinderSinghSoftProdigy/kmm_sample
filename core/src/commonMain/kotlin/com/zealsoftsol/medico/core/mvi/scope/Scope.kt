package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.ErrorCode
import kotlin.reflect.KClass

sealed class Scope : Scopable {
    internal val queueId: String = this::class.qualifiedName.orEmpty()
    internal abstract val scopeId: KClass<*>
    val storage by lazy { Storage() }

    sealed class Child : Scope() {
        internal abstract val parentScopeId: KClass<*>
        open val isRoot = false

        abstract class TabBar : Child() {
            override val scopeId: KClass<*> = Child.TabBar::class
            override val parentScopeId: KClass<*> = Host::class

            open fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? = null

            open fun goHome() = EventCollector.sendEvent(Event.Transition.Dashboard)
        }
    }

    abstract class Host : Scope() {
        override val scopeId: KClass<*> = Host::class

        val isInProgress: DataSource<Boolean> = DataSource(false)
        val alertError: DataSource<ErrorCode?> = DataSource(null)
        val bottomSheet: DataSource<BottomSheet?> = DataSource(null)

        fun dismissAlertError() {
            alertError.value = null
        }

        fun dismissBottomSheet() {
            bottomSheet.value = null
        }
    }
}

//internal object DetachedScopeId

enum class ScopeIcon {
    NO_ICON, BACK, HAMBURGER;
}

sealed class TabBarInfo {
    abstract val icon: ScopeIcon

    open fun withBackIcon(): TabBarInfo? = null

    data class Simple(
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER,
        val title: StringResource?,
        val cartItemsCount: ReadOnlyDataSource<Int>? = null,
    ) : TabBarInfo() {

        override fun withBackIcon() = copy(icon = ScopeIcon.BACK)

        fun goToCart() = EventCollector.sendEvent(Event.Transition.Cart)
    }

    data class Search(
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER,
        val cartItemsCount: ReadOnlyDataSource<Int>,
    ) : TabBarInfo() {

        override fun withBackIcon() = copy(icon = ScopeIcon.BACK)

        fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search)

        fun goToCart() = EventCollector.sendEvent(Event.Transition.Cart)
    }

    data class ActiveSearch(
        val search: DataSource<String>,
        val activeFilterIds: DataSource<List<String>>
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.NO_ICON

        fun toggleFilter() = EventCollector.sendEvent(Event.Action.Search.ToggleFilter)

        fun searchProduct(input: String, withAutoComplete: Boolean): Boolean {
            return trimInput(input, search.value) {
                val event = if (withAutoComplete) {
                    Event.Action.Search.SearchAutoComplete(it)
                } else {
                    Event.Action.Search.SearchInput(isOneOf = false, search = input)
                }
                EventCollector.sendEvent(event)
            }
        }
    }

    object NewDesignLogo : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER
    }

    data class NewDesignTitle(val title: String) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER
    }

    object InStoreProductTitle : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
    }
}

internal object StartScope : Scope.Host()

interface Scopable

class Storage {
    private val map = hashMapOf<String, Any>()

    fun save(key: String, value: Any) {
        map[key] = value
    }

    fun restore(key: String) = map[key]

    fun clear(key: String) = map.remove(key)
}
