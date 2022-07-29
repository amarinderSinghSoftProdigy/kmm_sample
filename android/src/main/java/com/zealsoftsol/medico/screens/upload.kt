package com.zealsoftsol.medico.screens

import android.util.Base64
import com.zealsoftsol.medico.core.data.FileType
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import java.io.File

inline fun BottomSheet.UploadDocuments.handleFileUpload(file: File) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    if (isSeasonBoy)
        uploadAadhaar(base64)
    else
        uploadDrugLicense(base64, FileType.fromExtension(file.extension))
}

inline fun BottomSheet.UploadDocuments.handleUpload(
    file: File,
    type: String,
) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    uploadDocument(
        base64,
        FileType.fromExtension(file.extension),
        type,
        path = file.absolutePath,
    )
}