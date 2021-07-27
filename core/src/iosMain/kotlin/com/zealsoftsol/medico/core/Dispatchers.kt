package com.zealsoftsol.medico.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ktorDispatcher: CoroutineDispatcher
    get() = Dispatchers.Main

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

actual val compatDispatcher: CoroutineDispatcher
    get() = Dispatchers.Main