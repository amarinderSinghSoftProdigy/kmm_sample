package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.data.FileType

sealed class BottomSheet {

    class UploadDocuments(
        val supportedFileTypes: Array<FileType>,
        val isSeasonBoy: Boolean,
        val type: String,
    ) : BottomSheet() {

        fun uploadAadhaar(base64: String): Boolean {
            return true
            /*return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }*/
        }

        fun uploadDrugLicense(base64: String, fileType: FileType): Boolean {
            return true
            /*return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(
                        base64,
                        fileType
                    )
                )
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }*/
        }


        fun uploadDocument(
            base64: String,
            fileType: FileType,
            type: String,
            path: String,
        ): Boolean {
            return true
            /*return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDocument(
                        size = sizeInBytes(base64).toString(),
                        asBase64 = base64,
                        fileType = fileType,
                        type = type,
                        path = path,
                        registrationStep1
                    )
                )
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }*/
        }


        private fun sizeInBytes(base64: String): Int =
            (base64.length * 3 / 4) - base64.takeLast(2).count { it == '=' }

        companion object {
            private const val MAX_FILE_SIZE = 5_000_000
        }
    }

    data class ViewLargeImage(val url: String, val type: String?) : BottomSheet()

}