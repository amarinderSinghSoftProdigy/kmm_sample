package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType

sealed class BottomSheet {

    class UploadDocuments(
        val supportedFileTypes: Array<FileType>,
        val isSeasonBoy: Boolean,
    ) : BottomSheet() {

        fun uploadAadhaar(base64: String) =
            EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))

        fun uploadDrugLicense(base64: String, fileType: FileType) =
            EventCollector.sendEvent(Event.Action.Registration.UploadDrugLicense(base64, fileType))
    }

    class PreviewManagementItem(val entityInfo: EntityInfo) : BottomSheet() {
        fun subscribe() =
            EventCollector.sendEvent(Event.Action.Management.RequestSubscribe(entityInfo))
    }
}