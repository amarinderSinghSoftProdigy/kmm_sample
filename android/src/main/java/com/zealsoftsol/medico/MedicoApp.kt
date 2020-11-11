package com.zealsoftsol.medico

import android.app.Application
import com.zealsoftsol.medico.core.UiLink
import org.kodein.di.DI
import org.kodein.di.DIAware

class MedicoApp : Application(), DIAware {

    override lateinit var di: DI

    override fun onCreate() {
        super.onCreate()
        val (di) = UiLink.appStart(this)
        this.di = di
    }
}