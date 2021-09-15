package com.zealsoftsol.medico.screens.auth

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.OutlinedInputField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
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
                color = ConstColors.yellow,
                contentColor = MaterialTheme.colors.background,
            )
        }

        Space(24.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color(0xFFF2F7FA)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.copyright),
                color = MaterialTheme.colors.background,
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
            val formatted = credentialsState.value.phoneNumberOrEmail
            OutlinedInputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.phone_number_or_email),
                text = formatted,
                isValid = true,
                maxLines = 1,
                onValueChange = {
                    scope.updateAuthCredentials(
                        it,
                        credentialsState.value.password
                    )
                },
            )
            Space(12.dp)
            Box(
                contentAlignment = Alignment.CenterEnd,
            ) {
                val isPasswordHidden = remember { mutableStateOf(true) }
                OutlinedInputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.password),
                    text = credentialsState.value.password,
                    isValid = true,
                    visualTransformation = if (isPasswordHidden.value) PasswordVisualTransformation() else VisualTransformation.None,
                    maxLines = 1,
                    onValueChange = {
                        scope.updateAuthCredentials(
                            credentialsState.value.phoneNumberOrEmail,
                            it
                        )
                    },
                )
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = null,
                    tint = if (isPasswordHidden.value) ConstColors.gray else ConstColors.lightBlue,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(42.dp)
                        .clickable(indication = rememberRipple(radius = 15.dp)) {
                            isPasswordHidden.value = !isPasswordHidden.value
                        }
                        .padding(12.dp),
                )
            }
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