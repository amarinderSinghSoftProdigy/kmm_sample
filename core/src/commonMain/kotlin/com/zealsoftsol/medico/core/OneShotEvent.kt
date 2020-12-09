package com.zealsoftsol.medico.core

@Deprecated("for auth scope only")
open class OneShotEvent<T> internal constructor(
    private val internalValue: T,
    private var wasAccessed: Boolean = false
) {

    fun debug(): T = internalValue

    val value: T?
        get() {
            if (wasAccessed) return null
            wasAccessed = true
            return internalValue
        }
}

@Deprecated("for auth scope only")
class BooleanEvent private constructor(isTrue: Boolean, wasAccessed: Boolean = false) :
    OneShotEvent<Boolean>(isTrue, wasAccessed) {

    inline val isTrue: Boolean
        get() = value == true
    inline val isFalse: Boolean
        get() = value == false

    companion object {
        internal val `true`: BooleanEvent
            get() = BooleanEvent(true)
        internal val `false`: BooleanEvent
            get() = BooleanEvent(false)
        internal val none: BooleanEvent
            get() = BooleanEvent(false, true)

        internal inline fun of(value: Boolean) = if (value) `true` else `false`
    }
}