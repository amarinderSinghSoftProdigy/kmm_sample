package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.screenHeight
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.core.viewmodel.mock.MockAuthViewModel
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.TabBar

@Composable
fun AuthScreen(authViewModel: AuthViewModelFacade) {
    Box {
        Image(
            asset = imageResource(id = R.drawable.auth_logo),
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
        )
        Image(
            painter = ColorPainter(Color(0,132,212,178)),
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = ColorPainter(Color.Transparent),
            modifier = Modifier.fillMaxSize()
                .background(
                    VerticalGradient(
                        0f to Color(0xff003657),
                        1f to Color(0x00003657),
                        startY = ContextAmbient.current.screenHeight.toFloat(),
                        endY = 0f
                    )
                )
        )
        TabBar {
            Box(modifier = Modifier.padding(vertical = 13.dp, horizontal = 24.dp)) {
                Image(asset = imageResource(id = R.drawable.medico_logo), modifier = Modifier.align(
                    Alignment.CenterStart))
            }
        }
        AuthTab(authViewModel = authViewModel, Modifier.align(Alignment.BottomCenter))
        val authState = authViewModel.state.flow.collectAsState()
        when (authState.value) {
            AuthState.ERROR -> AlertDialog(
                onDismissRequest = { authViewModel.clearState() },
                title = {
                    Text(
                        text = "Log in error",
                        style = MaterialTheme.typography.h6
                    )
                },
                text = {
                    Text(
                        text = "Log in or password is wrong. Please try again or restore your password",
                        style = MaterialTheme.typography.subtitle1
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { authViewModel.clearState() },
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp
                    ) {
                        Text(
                            text = "OKAY",
                            style = MaterialTheme.typography.subtitle2.copy(color = ConstColors.lightBlue)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun AuthTab(authViewModel: AuthViewModelFacade, modifier: Modifier) {
    val credentialsState = authViewModel.credentials.flow.collectAsState()
    Column(modifier = modifier.fillMaxWidth()
        .padding(12.dp)
        .background(MaterialTheme.colors.primary)
        .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.log_in),
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
        )
        InputField(
            hint = stringResource(id = R.string.phone_number_or_email),
            text = credentialsState.value.phoneNumberOrEmail
        ) {
            authViewModel.updateAuthCredentials(credentialsState.value.copy(phoneNumberOrEmail = it))
        }
        InputField(
            hint = stringResource(id = R.string.password),
            text = credentialsState.value.password,
            hideCharacters = true
        ) {
            authViewModel.updateAuthCredentials(credentialsState.value.copy(password = it))
        }
        val context = ContextAmbient.current
        Text(
            text = stringResource(id = R.string.forgot_password),
            style = MaterialTheme.typography.body2.copy(color = ConstColors.lightBlue),
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                context.startActivity(Intent(context, AuthRestoreActivity::class.java))
            })
        )
        MedicoButton(text = stringResource(id = R.string.log_in)) {
            authViewModel.tryLogIn()
        }
        Text(
            text = stringResource(id = R.string.sign_up_to_medico),
            style = MaterialTheme.typography.body2.copy(color = ConstColors.lightBlue),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                context.startActivity(Intent(context, SignUpActivity::class.java))
            })
        )
    }
}

@Composable
fun InputField(hint: String, text: String, hideCharacters: Boolean = false, onValueChange: (String) -> Unit) {
    Spacer(modifier = Modifier.size(12.dp))
    TextField(
        value = text,
        label = { Text(text = hint) },
        activeColor = ConstColors.lightBlue,
//        inactiveColor = Color(0xff8E8E93),
        backgroundColor = Color.White,
        onValueChange = onValueChange,
        visualTransformation = if (hideCharacters) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun PreviewAuthScreen() {
    AuthScreen(authViewModel = MockAuthViewModel())
}