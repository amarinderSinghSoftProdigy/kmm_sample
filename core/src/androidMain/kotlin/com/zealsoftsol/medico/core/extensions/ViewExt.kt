@file:Suppress("NOTHING_TO_INLINE")
package com.zealsoftsol.medico.core.extensions

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.WindowInsets
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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