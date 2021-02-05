package com.zealsoftsol.medico.core.mvi.scenario

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.instance

abstract class Scenario {

    private val nav = directDI.instance<Navigator>()
    protected var defaultPauseDuration = environment.requireMocks().delay + 500L

    protected val longDelay: Long = 5000L

    fun start(block: suspend Navigator.() -> Unit) {
        nav.dropScope(strategy = Navigator.DropStrategy.All, updateDataSource = false)
        GlobalScope.launch(Dispatchers.Main) { nav.block() }
    }

    protected suspend fun pause(time: Long = defaultPauseDuration) = delay(time)
}