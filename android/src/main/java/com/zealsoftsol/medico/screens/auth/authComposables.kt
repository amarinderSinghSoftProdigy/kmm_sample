package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneOrEmailFormatInputField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus

@Composable
fun AuthScreen(scope: LogInScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(ConstColors.lightBlue),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Space(36.dp)

        AuthTab(scope)

        Space(24.dp)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 36.dp),
        ) {
            Text(
                text = stringResource(id = R.string.new_to_medico),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
            )
            Space(12.dp)
            MedicoButton(
                text = stringResource(id = R.string.create_account),
                isEnabled = true,
                elevation = null,
                onClick = { scope.goToSignUp() },
                color = Color.Transparent,
                contentColor = Color.White,
                border = BorderStroke(2.dp, Color.White),
            )
        }

        Space(24.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.copyright),
                color = ConstColors.lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
            )
        }
    }
}

@Composable
private fun AuthTab(scope: LogInScope) {
    val credentialsState = scope.credentials.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Space(36.dp)
            Image(
                painter = painterResource(id = R.drawable.medico_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
            Space(24.dp)
            PhoneOrEmailFormatInputField(
                hint = stringResource(id = R.string.phone_number_or_email),
                text = credentialsState.value.phoneNumberOrEmail,
                isPhoneNumber = credentialsState.value.type == AuthCredentials.Type.PHONE,
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
            ) {
                scope.updateAuthCredentials(it, credentialsState.value.password)
            }
            Space(12.dp)
            PasswordFormatInputField(
                hint = stringResource(id = R.string.password),
                text = credentialsState.value.password,
                onValueChange = {
                    scope.updateAuthCredentials(credentialsState.value.phoneNumberOrEmail, it)
                },
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
            )
            Space(8.dp)
            Text(
                text = stringResource(id = R.string.forgot_password),
                color = ConstColors.lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { scope.goToForgetPassword() },
            )
            Space(24.dp)
            MedicoButton(
                text = stringResource(id = R.string.log_in),
                isEnabled = true,
                elevation = null,
                onClick = { scope.tryLogIn() },
                color = ConstColors.lightBlue,
                contentColor = Color.White,
            )
            Space(36.dp)
        }
    }
}