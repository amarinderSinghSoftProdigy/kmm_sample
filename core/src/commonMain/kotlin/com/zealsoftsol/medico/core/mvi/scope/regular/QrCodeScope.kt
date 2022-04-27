package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo

class QrCodeScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("qr_code")

    init {
        EventCollector.sendEvent(Event.Action.QrCode.GetQrCode)
    }

    val qrCode: DataSource<String> = DataSource("")
    val qrCodeImage: DataSource<String> = DataSource("")

    fun regenerateQrCode() {
        EventCollector.sendEvent(Event.Action.QrCode.RegenerateQrCode(qrCode.value))
    }
}