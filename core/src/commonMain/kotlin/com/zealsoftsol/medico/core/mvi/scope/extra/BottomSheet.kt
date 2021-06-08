package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.TapMode

sealed class BottomSheet {

    class UploadDocuments(
        val supportedFileTypes: Array<FileType>,
        val isSeasonBoy: Boolean,
    ) : BottomSheet() {

        fun uploadAadhaar(base64: String): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }
        }

        fun uploadDrugLicense(base64: String, fileType: FileType): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(
                        base64,
                        fileType
                    )
                )
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }
        }

        private fun sizeInBytes(base64: String): Int =
            (base64.length * 3 / 4) - base64.takeLast(2).count { it == '=' }

        companion object {
            private const val MAX_FILE_SIZE = 1_000_000
        }
    }

    class PreviewManagementItem(
        val entityInfo: EntityInfo,
        val isSeasonBoy: Boolean,
        val canSubscribe: Boolean,
    ) : BottomSheet() {

        fun subscribe() =
            EventCollector.sendEvent(Event.Action.Management.RequestSubscribe(entityInfo))
    }

    class ModifyOrderEntry(
        val orderEntry: OrderEntry,
        val canEdit: Boolean,
        val isChecked: DataSource<Boolean>,
    ) : BottomSheet() {

        val quantity = DataSource(orderEntry.servedQty.value.toInt())

        fun inc(tapMode: TapMode) {
            if (tapMode != TapMode.CLICK) return
            quantity.value = quantity.value + 1
        }

        fun dec(tapMode: TapMode) {
            if (tapMode != TapMode.CLICK) return
            quantity.value = (quantity.value - 1).coerceAtLeast(0)
        }

        fun toggleCheck() {
            if (EventCollector.sendEvent(Event.Action.Orders.ToggleCheckEntry(orderEntry))) {
                isChecked.value = !isChecked.value
            }
        }

        fun save() =
            EventCollector.sendEvent(Event.Action.Orders.SaveEntryQty(orderEntry, quantity.value))
    }
}