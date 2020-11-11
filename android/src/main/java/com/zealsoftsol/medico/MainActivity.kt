package com.zealsoftsol.medico

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class MainActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()
    private val authViewModel by instance<AuthViewModelFacade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
