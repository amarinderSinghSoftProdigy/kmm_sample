package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource

sealed class OtpScope(private val titleId: String) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.NoHeader)?.copy(title = titleId)
    }
}