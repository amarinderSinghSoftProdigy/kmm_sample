package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.regular.QrCodeScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope

internal class QrCodeEventDelegate(
    navigator: Navigator,
    private val qrCodeScope: NetworkScope.QrCodeStore,
) : EventDelegate<Event.Action.QrCode>(navigator) {

    override suspend fun handleEvent(event: Event.Action.QrCode) = when (event) {
        is Event.Action.QrCode.GetQrCode -> getQrCode()
        is Event.Action.QrCode.RegenerateQrCode -> regenerateQrCode(event.qrCode)
    }

    /**
     * regenerate qrCode
     */
    private suspend fun regenerateQrCode(qrCode: String) {
        navigator.withScope<QrCodeScope> {
            val result = withProgress {
                qrCodeScope.regenerateQrCode(qrCode)
            }

            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                if (data != null) {
                    it.qrCode.value = data.qrCode
                    it.qrCodeImage.value = data.qrCodeUrl
                }
            }.onError(navigator)
        }
    }

    /**
     * get qr code from server
     */
    private suspend fun getQrCode() {
        navigator.withScope<QrCodeScope> {
            val result = withProgress {
                qrCodeScope.getQrCode()
            }
            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                if (data != null) {
                    it.qrCode.value = data.qrCode
                    it.qrCodeImage.value = data.qrCodeUrl
                }
            }.onError(navigator)
        }
    }
}