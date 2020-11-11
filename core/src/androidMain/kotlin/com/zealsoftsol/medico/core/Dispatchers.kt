package com.zealsoftsol.medico.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ktorDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO