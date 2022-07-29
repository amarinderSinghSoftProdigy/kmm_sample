package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.data.ErrorCode
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

    object NewDesignLogo : TabBarInfo() {
        override val icon: ScopeIcon = ScopeIcon.HAMBURGER
    }

    data class NoHeader(
        val title: String = "" //pass the string resource id
    ) : TabBarInfo() {
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
