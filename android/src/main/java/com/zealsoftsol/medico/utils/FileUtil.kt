package com.zealsoftsol.medico.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

object FileUtil {

    fun createGetContentIntent(): Intent {
        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.type = "*/*"
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }

    fun getTempFile(context: Context, uri: Uri): File {
        val tempFile = File.createTempFile(uri.hashCode().toString(), "temp")
        runCatching {
            context.contentResolver.openInputStream(uri)!!.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return tempFile
    }

}