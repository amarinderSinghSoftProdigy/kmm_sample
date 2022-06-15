package com.zealsoftsol.medico.screens

import android.util.Base64
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.UserRegistration1
import java.io.File

inline fun BottomSheet.UploadDocuments.handleFileUpload(file: File) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    if (isSeasonBoy)
        uploadAadhaar(base64)
    else
        uploadDrugLicense(base64, FileType.fromExtension(file.extension))
}

inline fun BottomSheet.UploadProfileData.handleProfileUpload(file: File, type: String) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    uploadProfile(base64, FileType.fromExtension(file.extension), type)
}

inline fun BottomSheet.GetOcrImageData.handleOcrImage(file: File, type: String) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    handleOcrImage(base64,file, FileType.fromExtension(file.extension), type)
}

inline fun BottomSheet.UploadInvoiceData.handleInvoiceUpload(file: File, type: String) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    uploadInvoice(base64, FileType.fromExtension(file.extension), type)
}

inline fun BottomSheet.UploadDocuments.handleUpload(
    file: File,
    type: String,
    registrationStep1: UserRegistration1
) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    uploadDocument(
        base64,
        FileType.fromExtension(file.extension),
        type,
        path = file.absolutePath,
        registrationStep1
    )
}