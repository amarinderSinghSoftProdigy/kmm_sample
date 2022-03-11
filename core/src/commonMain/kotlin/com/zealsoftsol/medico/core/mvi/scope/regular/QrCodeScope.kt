package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope

class QrCodeScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    init {
        EventCollector.sendEvent(Event.Action.QrCode.GetQrCode)
    }

    val qrCode: DataSource<String> = DataSource("")
    val qrCodeImage: DataSource<String> = DataSource("")

    fun regenerateQrCode() {
        EventCollector.sendEvent(Event.Action.QrCode.RegenerateQrCode(qrCode.value))
    }
}