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

inline fun <T : View> View.bind(@IdRes id: Int): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

inline fun Context.color(@ColorRes res: Int) = ContextCompat.getColor(this, res)

inline fun View.color(@ColorRes res: Int) = context.color(res)

inline fun Context.string(@StringRes res: Int): String =
    safeCall { resources.getString(res) }.orEmpty()

inline fun Context.drawable(resId: Int) = requireNotNull(ContextCompat.getDrawable(this, resId))

inline fun View.string(@StringRes res: Int) = context.string(res)

inline fun View.drawable(@DrawableRes res: Int): Drawable = context.drawable(res)

fun EditText.showKeyboard() {
    requestFocus()
    val imm: InputMethodManager =
        context.getSystemService<InputMethodManager>() as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun EditText.hideKeyboard(): Boolean {
    clearFocus()
    val imm: InputMethodManager =
        context.getSystemService<InputMethodManager>() as InputMethodManager
    return imm.hideSoftInputFromWindow(windowToken, 0)
//    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

@OptIn(ExperimentalContracts::class)
inline fun View.getAttrs(
    stylable: IntArray,
    attrs: AttributeSet?,
    onStyleReady: TypedArray.() -> Unit
) {
    contract {
        callsInPlace(onStyleReady, InvocationKind.EXACTLY_ONCE)
    }
    attrs?.let {
        val styles = context.obtainStyledAttributes(
            it,
            stylable
        )
        try {
            styles.onStyleReady()
        } finally {
            styles.recycle()
        }
    }
}

inline fun ImageView.enable(value: Boolean) {
    isEnabled = value
    alpha = if (value) 1f else .5f
}

inline fun View.globalRect(withPadding: Boolean = false, withStatusBarOffset: Boolean = true) =
    Rect().also {
        getGlobalVisibleRect(it)
//        if (withStatusBarOffset) it.offset(0, -Res.Dimen.statusBarHeight)
        if (withPadding) {
            it.left -= paddingLeft
            it.top -= paddingTop
            it.right += paddingRight
            it.bottom += paddingBottom
        }
    }

inline fun View.hitRect() = Rect().also {
    getHitRect(it)
}

//inline fun <T : View> T.animatePressObservable(
//    factor: Float = 0.85f,
//    config: SpringSet.Config = SpringSet.Config.Base()
//): Pair<SpringAnimation, Observable<Unit>> {
//    val spring = animateSpring(
//        SpringSet.SCALE_XY,
//        1f,
//        config,
//        DynamicAnimation.MIN_VISIBLE_CHANGE_SCALE,
//        false
//    )
//    setOnTouchListener { _, event ->
//        if (isEnabled)
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    playSoundEffect(SoundEffectConstants.CLICK)
//                    spring.animateToFinalPosition(factor)
//                }
//                MotionEvent.ACTION_CANCEL -> spring.animateToFinalPosition(1f)
//                MotionEvent.ACTION_UP -> {
//                    spring.animateToFinalPosition(1f)
////                    if (globalRect(true).contains(event.rawX.toInt(), event.rawY.toInt()))
//                    callOnClick()
//                }
//            }
//        true
//    }
//    return Pair(spring, this.clicks())
//}

inline fun ImageView.crossFadeDrawable(@DrawableRes res: Int) {
    alpha = 0f
    setImageResource(res)
    animate().alpha(1f).setDuration(150).start()
}

//inline fun TextView.crossFadeText(text: String) {
//    val offset = dip(4).toFloat()
//    animate().alpha(0f).translationY(-offset)
//        .setInterpolator(AccelerateDecelerateInterpolator())
//        .setDuration(150)
//        .withEndAction {
//            this.text = text
//            translationY = offset
//            animate().alpha(1f).translationY(0f)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .setDuration(150)
//                .start()
//        }.start()
//}

inline fun ViewPropertyAnimator.scaleXY(value: Float): ViewPropertyAnimator =
    scaleX(value).scaleY(value)

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

inline var View.scaleXY: Float
    get() = scaleX
    set(value) {
        scaleX = value
        scaleY = value
    }

//inline fun <T> Observable<T>.antiSpamFirst(window: Long = 1000): Observable<T> =
//    throttleFirst(window, TimeUnit.MILLISECONDS).observeOn(
//        AndroidSchedulers.mainThread()
//    )
//
//inline fun <T> Observable<T>.antiSpamLast(window: Long = 500): Observable<T> =
//    debounce(window, TimeUnit.MILLISECONDS).observeOn(
//        AndroidSchedulers.mainThread()
//    )

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
    .makeText(this, string(res), length)
    .apply {
        show()
    }

inline fun Context.getThemeAttr(id: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue
}

inline fun Resources.getAttrs(id: Int): AttributeSet = Xml.asAttributeSet(this.getXml(id).apply {
    try {
        next()
        nextTag()
    } catch (e: Exception) {
        e.printStackTrace()
    }
})

inline fun View.getPositionInWindow(): PointF {
    val pos = intArrayOf(0, 0)
    getLocationInWindow(pos)
    return PointF(pos[0].toFloat(), pos[1].toFloat())
}

inline fun @receiver:DrawableRes Int.toUri(context: Context): Uri = Uri.parse(
    ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + context.resources.getResourcePackageName(this)
            + '/' + context.resources.getResourceTypeName(this)
            + '/' + context.resources.getResourceEntryName(this)
)

//inline fun View.drawToBitmap(
//    onlyVisiblePart: Boolean = false,
//    config: Bitmap.Config = Bitmap.Config.ARGB_8888
//): Bitmap {
//    if (!ViewCompat.isLaidOut(this)) {
//        throw IllegalStateException("View needs to be laid out before calling drawToBitmap()")
//    }
//    return if (!onlyVisiblePart) Bitmap.createBitmap(width, height, config).applyCanvas {
//        translate(-scrollX.toFloat(), -scrollY.toFloat())
//        draw(this)
//    }
//    else {
//        val rect = globalRect(withStatusBarOffset = false)
//        val point = getPositionInWindow()
//        Bitmap.createBitmap(rect.width(), rect.height(), config).applyCanvas {
//            translate(-scrollX.toFloat(), -scrollY.toFloat())
//            translate(point.x - rect.left, point.y - rect.top)
//            draw(this)
//        }
//    }
//}