package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo

class TabBarScope(
    childScope: Scope.Child.TabBar,
    private val initialTabBarInfo: TabBarInfo,
    private val initialNavigationSection: NavigationSection?,
) : Scope.Host(), CommonScope.CanGoBack {

    val tabBar: DataSource<TabBarInfo> =
        DataSource(childScope.overrideParentTabBarInfo(initialTabBarInfo) ?: initialTabBarInfo)
    val navigationSection: DataSource<NavigationSection?> = DataSource(initialNavigationSection)
    val childScope: DataSource<Child.TabBar> = DataSource(childScope)

    internal fun setChildScope(child: Child.TabBar, childCount: Int) {
        tabBar.value = child.overrideParentTabBarInfo(initialTabBarInfo) ?: initialTabBarInfo
        if (childCount > 1) tabBar.value.withBackIcon()?.let {
            tabBar.value = it
        }
        navigationSection.value = if (tabBar.value.icon != ScopeIcon.HAMBURGER) {
            null
        } else {
            initialNavigationSection
        }
        childScope.value = child
    }
}