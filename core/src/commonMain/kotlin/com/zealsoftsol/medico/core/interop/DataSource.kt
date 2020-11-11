package com.zealsoftsol.medico.core.interop

expect class DataSource<T>(initialValue: T) {
    var value: T
}