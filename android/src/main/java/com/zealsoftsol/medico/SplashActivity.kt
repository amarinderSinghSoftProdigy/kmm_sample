package com.zealsoftsol.medico

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.screens.MainActivity
import com.zealsoftsol.medico.screens.auth.AuthActivity
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class SplashActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()
    private val authViewModel by instance<AuthViewModelFacade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val targetActivity = if (authViewModel.authState.value == AuthState.SUCCESS) {
            MainActivity::class.java
        } else {
            AuthActivity::class.java
        }
        startActivity(Intent(this, targetActivity))
        overridePendingTransition(android.R.anim.fade_in, R.anim.no_op_anim)
        window.decorView.post { finish() }
    }
}
