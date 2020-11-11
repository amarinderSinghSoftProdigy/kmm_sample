@file:Suppress("NOTHING_TO_INLINE")

package com.zealsoftsol.medico.core.extensions

import kotlinx.coroutines.delay
import kotlin.reflect.KProperty

inline fun <V1, V2> ifNotNull(value1: V1?, value2: V2?, block: (V1, V2) -> Unit) {
    if (value1 != null && value2 != null) {
        block(value1, value2)
    } else {
//        "some values were null $value1, $value2".warn
    }
}

inline fun <V1, V2, V3> ifNotNull(
    value1: V1?,
    value2: V2?,
    value3: V3?,
    block: (V1, V2, V3) -> Unit
) {
    if (value1 != null && value2 != null && value3 != null) {
        block(value1, value2, value3)
    } else {
//        "some values were null $value1, $value2, $value3".warn
    }
}

inline fun <V1, V2, V3, V4> ifNotNull(
    value1: V1?,
    value2: V2?,
    value3: V3?,
    value4: V4?,
    block: (V1, V2, V3, V4) -> Unit
) {
    if (value1 != null && value2 != null && value3 != null && value4 != null) {
        block(value1, value2, value3, value4)
    } else {
//        "some values were null $value1, $value2, $value3, $value4".warn
    }
}

inline fun <V1, V2, V3, V4, V5> ifNotNull(
    value1: V1?,
    value2: V2?,
    value3: V3?,
    value4: V4?,
    value5: V5?,
    block: (V1, V2, V3, V4, V5) -> Unit
) {
    if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null) {
        block(value1, value2, value3, value4, value5)
    } else {
//        "some values were null $value1, $value2, $value3, $value4, $value5".warn
    }
}

inline fun <T> firstNotNull(initLazy: Boolean = true, noinline initializer: () -> T?) =
    FirstNotNull(initLazy, initializer)

class FirstNotNull<T>(initLazy: Boolean = true, private val initializer: () -> T?) {
    private var value: T? = if (initLazy) null else initializer()

    operator fun getValue(thisRef: Any, prop: KProperty<*>): T? {
        if (value == null)
            value = initializer()
        return value
    }
}

inline fun Any.exhaustive(): Nothing =
    throw UnsupportedOperationException("reached exhaustive branch of if statement in ${this::class}")

inline fun Any.stub(): Nothing =
    throw UnsupportedOperationException("trying to access stub property in ${this::class}")

inline fun <T> Boolean.ifTrue(body: () -> T): T? {
    return if (this)
        body()
    else
        null
}

inline fun <T> Boolean.ifFalse(body: () -> T): T? {
    return if (!this)
        body()
    else
        null
}

inline fun <T> Any.safeCall(block: () -> T) = try {
    block()
} catch (e: Exception) {
    "safe call caught exception $e in ${this::class.simpleName}".warnIt()
    e.printStackTrace()
    null
}

sealed class Interval(protected val value: Long) {
    open fun getInterval(): Long = value
    class Linear(value: Long) : Interval(value)
    class Progressive(startValue: Long) : Interval(startValue) {
        private var count = 1
        override fun getInterval(): Long {
            return value * count++
        }
    }
}

suspend fun <T> getEventually(interval: Interval, block: suspend () -> T?): T {
    var item = Unit.safeCall { block() }
    while (item == null) {
        item = Unit.safeCall { block() }
        delay(interval.getInterval())
    }
    return item
}