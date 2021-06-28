package com.zealsoftsol.medico

import android.app.Application
import com.zealsoftsol.medico.core.UiLink
import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.notifications.FirebaseMessaging
import org.kodein.di.DI
import org.kodein.di.DIAware

class MedicoApp : Application(), DIAware {

    override lateinit var di: DI
    lateinit var messaging: FirebaseMessaging

    override fun onCreate() {
        super.onCreate()
        val (di, _, notifications) = UiLink.appStart(
            context = this,
            useMocks = false,
            useNavigatorSafeCasts = !BuildConfig.DEBUG,
            useNetworkInterceptor = BuildConfig.FLAVOR == "dev",
            failOnServerError = BuildConfig.FLAVOR == "dev",
            loggerLevel = if (BuildConfig.FLAVOR == "prod" && !BuildConfig.DEBUG) Logger.Level.NONE else Logger.Level.LOG,
            networkUrl = NetworkClient.BaseUrl.DEV,
        )
        this.messaging = notifications
        this.di = di

        NotificationCenter(this, notifications)
    }
}