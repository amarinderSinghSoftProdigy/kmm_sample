package com.zealsoftsol.medico

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
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
        setContent {
            AppTheme {
                val text = authViewModel.testData.flow.collectAsState()
                Text(text = text.value.test, Modifier.clickable(onClick = {
                    authViewModel.asyncTest()
                }))
            }
        }
    }
}
