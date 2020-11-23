package com.zealsoftsol.medico.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.AppTheme
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.Scope
import com.zealsoftsol.medico.core.UiNavigator
import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.screens.auth.AuthAwaitVerificationScreen
import com.zealsoftsol.medico.screens.auth.AuthEnterNewPasswordScreen
import com.zealsoftsol.medico.screens.auth.AuthPhoneNumberInputScreen
import com.zealsoftsol.medico.screens.auth.AuthScreen
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()
    private val authViewModel by instance<AuthViewModel>()
    private val navigator by instance<UiNavigator>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val currentScope = navigator.scope.flow.collectAsState()
                when (val scope = currentScope.value) {
                    is Scope.LogIn -> AuthScreen(
                        authViewModel = authViewModel,
                        scope = scope,
                    )
                    is Scope.ForgetPassword.PhoneNumberInput -> AuthPhoneNumberInputScreen(
                        authViewModel = authViewModel,
                        scope = scope,
                    )
                    is Scope.ForgetPassword.AwaitVerification -> AuthAwaitVerificationScreen(
                        authViewModel = authViewModel,
                        scope = scope,
                        dateFormat = dateFormat
                    )
                    is Scope.ForgetPassword.EnterNewPassword -> AuthEnterNewPasswordScreen(
                        authViewModel = authViewModel,
                        scope = scope,
                    )
                    is Scope.Main -> MainView(authViewModel = authViewModel)
                }
                if (currentScope.value.isInProgress) IndefiniteProgressBar()
            }
        }
    }

    override fun onBackPressed() {
        if (!navigator.handleBack())
            super.onBackPressed()
    }
}

@Composable
fun MainView(authViewModel: AuthViewModel) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                        .background(ConstColors.gray)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = { authViewModel.logOut() })
                        .padding(vertical = 12.dp)
                ) {
                    Icon(
                        asset = vectorResource(id = R.drawable.ic_exit),
                        modifier = Modifier.padding(start = 18.dp),
                    )
                    BasicText(
                        text = stringResource(R.string.log_out),
                        style = MaterialTheme.typography.body2.copy(color = ConstColors.gray),
                        modifier = Modifier.padding(start = 32.dp),
                    )
                }
            }
        },
        topBar = {
            TabBar {
                Row {
                    Icon(
                        asset = vectorResource(id = R.drawable.ic_menu),
                        modifier = Modifier.align(Alignment.CenterVertically)
                            .padding(16.dp)
                            .clickable(onClick = { scaffoldState.drawerState.open() })
                    )
                }
            }
        },
        bodyContent = { },
    )
}
