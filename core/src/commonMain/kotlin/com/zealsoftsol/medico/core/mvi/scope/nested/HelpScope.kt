package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.HelpType

class HelpScope(val helpData: HelpData) : Scope.Child.TabBar() {
    override val isRoot: Boolean = false

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    enum class Tab(val stringId: String, val orderType: HelpType) {
        TERMS_AND_CONDITIONS("tos", HelpType.TERMS_AND_CONDITIONS),
        PRIVACY_POLICY("privacy_policy", HelpType.PRIVACY_POLICY),
    }
}