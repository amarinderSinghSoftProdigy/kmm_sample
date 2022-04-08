package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope

class PreferenceScope(): Scope.Child.TabBar(), CommonScope.CanGoBack {

    val showAlert = DataSource(false)

    fun showBottomSheet(value: Boolean) {
    }

    fun submit(checked: Boolean) {

    }

}
