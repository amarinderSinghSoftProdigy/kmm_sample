package com.zealsoftsol.medico.screens.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.zealsoftsol.medico.AppTheme
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class AuthRestoreActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()
    private val authViewModel by instance<AuthViewModelFacade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
            }
        }
    }
}