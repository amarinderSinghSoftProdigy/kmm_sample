package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.User

class DashboardScope private constructor() :
    Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.HAMBURGER)) {

    companion object {
        fun get(user: User, userDataSource: ReadOnlyDataSource<User>) = Host.TabBar(
            childScope = DashboardScope(),
            navigationSectionValue = NavigationSection(
                userDataSource,
                NavigationOption.default(user.type),
                NavigationOption.footer()
            ),
        )
    }
}