package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.data.TapMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TapModeHelper(private val coroutineScope: CoroutineScope) {

    private var job: Job? = null

    fun action(mode: TapMode, onAction: () -> Boolean) {
        when (mode) {
            TapMode.CLICK -> onAction()
            TapMode.LONG_PRESS -> {
                job = coroutineScope.launch {
                    var doRun = true
                    while (isActive && doRun) {
                        delay(100)
                        doRun = onAction()
                    }
                }
            }
            TapMode.RELEASE -> {
                job?.cancel()
            }
        }
    }
}