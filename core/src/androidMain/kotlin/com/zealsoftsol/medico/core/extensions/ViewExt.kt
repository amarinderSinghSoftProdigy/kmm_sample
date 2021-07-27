@file:Suppress("NOTHING_TO_INLINE")
package com.zealsoftsol.medico.core.extensions

import android.app.Activity
import android.content.Context
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.StringRes

inline val Activity.keyboardHeight: Int
    get() {
        val insets = window.decorView.rootWindowInsets
        return insets.systemWindowInsetBottom - insets.stableInsetBottom
    }
inline val Activity.windowInsets: WindowInsets
    get() = window.decorView.rootWindowInsets
inline val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels
inline val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels
inline val Context.density: Float
    get() = resources.displayMetrics.density

inline fun Context.hasPermissions(vararg permssions: String) =
    if (permssions.isEmpty()) false else permssions.map {
        checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }.reduce { acc, b -> acc && b }

inline fun Context.toast(message: CharSequence, length: Int = Toast.LENGTH_SHORT): Toast = Toast
    .makeText(this, message, length)
    .apply {
        show()
    }

inline fun Context.toast(@StringRes res: Int, length: Int = Toast.LENGTH_SHORT): Toast = Toast
    .makeText(this, resources.getString(res), length)
    .apply {
        show()
    }