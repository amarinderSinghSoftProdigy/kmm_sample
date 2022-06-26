package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.Value
import kotlin.reflect.KClass

sealed class Scope : Scopable {
    internal val queueId: String = this::class.qualifiedName.orEmpty()
    internal abstract val scopeId: KClass<*>
    val storage by lazy { Storage() }

    sealed class Child : Scope() {
        internal abstract val parentScopeId: KClass<*>
        open val isRoot = false

        abstract class TabBar : Child() {
            override val scopeId: KClass<*> = TabBar::class
            override val parentScopeId: KClass<*> = Host::class

            open fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? = null

            open fun goHome() = EventCollector.sendEvent(Event.Transition.Dashboard)

            open fun globalSearch(searchValue: String) {
                val autoComplete = AutoComplete(
                    query = "suggest", details = "",
                    suggestion = searchValue
                )
                EventCollector.sendEvent(Event.Transition.Search(autoComplete))
            }

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
        val titleColor: Long = -1L,
        val notificationItemsCount: ReadOnlyDataSource<Int>? = null,
    ) : TabBarInfo() {

        override fun withBackIcon() = copy(icon = ScopeIcon.BACK)

        fun goToCart() = EventCollector.sendEvent(Event.Transition.Cart)

        fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)
    }

    data class Search(
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER,
        val notificationItemsCount: ReadOnlyDataSource<Int>,
        val cartItemsCount: ReadOnlyDataSource<Int>? = null,
    ) : TabBarInfo() {

        override fun withBackIcon() = copy(icon = ScopeIcon.BACK)

        fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search(null))

        fun goToCart() = EventCollector.sendEvent(Event.Transition.Cart)

        fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)
    }

    data class ActiveSearch(
        val search: DataSource<String>,
        val filtersManufactures: DataSource<List<Value>>,
        val isFilterApplied: DataSource<Boolean> = DataSource(false)
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.NO_ICON

        fun toggleFilter() = EventCollector.sendEvent(Event.Action.Search.ToggleFilter)

        fun openManufacturersFilter() =
            EventCollector.sendEvent(Event.Action.Search.ShowManufacturers(filtersManufactures.value))

        fun searchProduct(input: String, withAutoComplete: Boolean): Boolean {
            return if(input.isNotEmpty()) {
                trimInput(input, search.value) {
                    val event = if (withAutoComplete) {
                        Event.Action.Search.SearchAutoComplete(it)
                    } else {
                        Event.Action.Search.SearchInput(isOneOf = false, search = input)
                    }
                    EventCollector.sendEvent(event)
                }
            }else{
                EventCollector.sendEvent(Event.Action.Search.SearchInput(isOneOf = true, search = ""))
                true
            }
        }
    }

    object NewDesignLogo : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER
    }

    data class NewDesignTitle(val title: String) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER
    }

    data class InStoreProductTitle(val title: String, val address: String, val phone: String) :
        TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
    }

    data class NoIconTitle(
        val title: String, val notificationItemsCount: ReadOnlyDataSource<Int>?,
        val cartItemsCount: ReadOnlyDataSource<Int>? = null,
        val showBackButton: DataSource<Boolean>? = null

    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
        fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)
        fun goToSearch() = EventCollector.sendEvent(Event.Transition.Search())
    }

    data class OnlyBackHeader(
        val title: String, //pass the string resource id
        val cartItemsCount: ReadOnlyDataSource<Int>? = null
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
    }

    data class PlayerBackHeader(
        val title: String, //pass the string resource id
        val event: Event,
        val cartItemsCount: ReadOnlyDataSource<Int>? = null
    ) : TabBarInfo() {
        fun releasePlayer(){
            EventCollector.sendEvent(event)
        }
        override val icon: ScopeIcon = ScopeIcon.BACK
    }

    data class OfferHeader(
        val title: String //pass the string resource id
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
    }

    data class StoreTitle(
        val storeName: String,
        val notificationItemsCount: ReadOnlyDataSource<Int>? = null,
        val cartItemsCount: ReadOnlyDataSource<Int>? = null,
        val showNotifications: Boolean = true,
        val event: Event,
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
        fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)
        fun openBottomSheet() {
            EventCollector.sendEvent(event)
        }
    }

    data class NoHeader(
        val title: String = "" //pass the string resource id
    ) : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.BACK
    }

    data class StoresSearch(
        val search: DataSource<String>,
        val activeFilterIds: DataSource<List<String>>,
        val pagination: Pagination,
        val productSearch: DataSource<String>,
        val store: Store
    ) : TabBarInfo() {

        override val icon: ScopeIcon = ScopeIcon.BACK

        fun startSearch(check: Boolean, search: String? = null) {
            productSearch.value = ""
            EventCollector.sendEvent(
                Event.Action.Search.SearchInput(
                    isOneOf = check,
                    search = search
                )
            )
        }
        fun searchProduct(input: String, withAutoComplete: Boolean, sellerUnitCode: String): Boolean {
            return trimInput(input, productSearch.value) {
                val event = if (withAutoComplete) {
                    Event.Action.Search.SearchAutoComplete(it, sellerUnitCode)
                } else {
                    Event.Action.Search.SearchInput(isOneOf = false, search = input)
                }
                EventCollector.sendEvent(event)
            }
        }

        fun searchProduct(value:String) {
            productSearch.value = value
            searchProduct(
                value,
                withAutoComplete = true,
                store.sellerUnitCode
            )
        }
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
