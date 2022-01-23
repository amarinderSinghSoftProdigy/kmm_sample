package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.HelpData
import com.zealsoftsol.medico.data.HelpType

open class HelpScope(val helpData: HelpData) : Scope.Child.TabBar() {
    override val isRoot: Boolean = false

    class TandC(helpData: HelpData) : HelpScope(helpData) {
        var tabs: ArrayList<Tab>? = null
        val activeTab: DataSource<Tab?> = DataSource(null)
        val loadUrl: DataSource<String> = DataSource("")

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
            return TabBarInfo.OnlyBackHeader("")
        }

        init {
            tabs = ArrayList()
            tabs?.add(Tab.TERMS_AND_CONDITIONS)
            tabs?.add(Tab.PRIVACY_POLICY)
            activeTab.value = Tab.TERMS_AND_CONDITIONS
            loadUrl.value = "tos"
        }

        fun selectTab(tab: Tab) {
            activeTab.value = tab
            EventCollector.sendEvent(Event.Action.Help.ChangeTab(tab.stringId))
        }
    }

    class ContactUs(helpData: HelpData) : HelpScope(helpData) {
        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
            return TabBarInfo.OnlyBackHeader("")
        }
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return TabBarInfo.OnlyBackHeader("")
    }

    enum class Tab(val stringId: String, val orderType: HelpType) {
        TERMS_AND_CONDITIONS("tos", HelpType.TERMS_AND_CONDITIONS),
        PRIVACY_POLICY("privacy_policy", HelpType.PRIVACY_POLICY),
    }
}