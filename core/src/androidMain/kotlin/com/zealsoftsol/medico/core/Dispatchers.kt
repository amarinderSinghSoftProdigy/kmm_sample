package com.zealsoftsol.medico.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ktorDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val compatDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default