package com.zealsoftsol.medico.core.utils

inline fun trimInput(newValue: String, oldValue: String, finalValue: (String) -> Unit): Boolean {
    return if (newValue.isNotBlank() || oldValue.isNotBlank()) {
        finalValue(newValue.trimNewLine())
        true
    } else {
        false
    }
}

inline fun String.trimNewLine() = trimEnd { it == '\n' }