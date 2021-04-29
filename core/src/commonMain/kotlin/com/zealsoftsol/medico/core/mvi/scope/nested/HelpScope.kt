package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.HelpData

class HelpScope(val helpData: HelpData) : Scope.Child.TabBar() {
    override val isRoot: Boolean = true
}