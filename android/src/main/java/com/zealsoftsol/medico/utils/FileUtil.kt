package com.zealsoftsol.medico.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File

object FileUtil {

    fun getTempFile(context: Context, uri: Uri): File? {
        return runCatching {
            val ext = runCatching {
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri))
            }.getOrNull() ?: "temp"
            val tempFile = File.createTempFile("temp", ".$ext")
            context.contentResolver.openInputStream(uri)!!.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (tempFile.length() > 0) tempFile else null
        }.getOrNull()
    }
}