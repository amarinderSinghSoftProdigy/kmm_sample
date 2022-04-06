package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.UserType

class AddEmployeeScope() : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(""))
    }

    val selectedUserType = DataSource<UserType?>(null)

    enum class OptionSelected {
        ADD_EMPLOYEE, VIEW_EMPLOYEE
    }

    fun selectUserType(userType: UserType) {
        selectedUserType.value = userType
    }

}