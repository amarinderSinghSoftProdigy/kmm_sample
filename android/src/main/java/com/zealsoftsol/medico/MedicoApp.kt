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
            useNavigatorSafeCasts = !BuildConfig.DEBUG, // all release builds
            useNetworkInterceptor = BuildConfig.FLAVOR == "dev", // all dev builds
            crashOnServerError = BuildConfig.FLAVOR == "dev", // all dev builds
            loggerLevel = if (!BuildConfig.DEBUG) Logger.Level.NONE else Logger.Level.LOG,
            networkUrl = when (BuildConfig.FLAVOR) {
                "dev" -> NetworkClient.BaseUrl.DEV
                "stag" -> NetworkClient.BaseUrl.STAG
                "prod" -> NetworkClient.BaseUrl.PROD
                else -> throw UnsupportedOperationException("unknown flavor")
            },
        )
        this.messaging = notifications
        this.di = di

        NotificationCenter(this, notifications)
    }
}