package com.zealsoftsol.medico

import android.app.Application
import com.zealsoftsol.medico.core.UiLink
import com.zealsoftsol.medico.core.extensions.Logger
import org.kodein.di.DI
import org.kodein.di.DIAware

class MedicoApp : Application(), DIAware {

    override lateinit var di: DI

    override fun onCreate() {
        super.onCreate()
        val (di, nav) = UiLink.appStart(
            context = this,
            useMocks = false,
            loggerLevel = if (BuildConfig.DEBUG) Logger.Level.LOG else Logger.Level.NONE,
        )
        this.di = di
    }
}