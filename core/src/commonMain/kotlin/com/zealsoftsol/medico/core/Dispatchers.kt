package com.zealsoftsol.medico.core

import kotlinx.coroutines.CoroutineDispatcher

expect val ktorDispatcher: CoroutineDispatcher
expect val ioDispatcher: CoroutineDispatcher
expect val compatDispatcher: CoroutineDispatcher