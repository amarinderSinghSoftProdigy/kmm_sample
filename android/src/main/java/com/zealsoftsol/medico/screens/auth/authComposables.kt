package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.core.viewmodel.mock.MockAuthViewModel
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.screens.IndefiniteProgressBar
import com.zealsoftsol.medico.screens.MainActivity
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.TabBar
import com.zealsoftsol.medico.screens.launchScreen
import com.zealsoftsol.medico.utils.PhoneNumberFormatter

@OptIn(ExperimentalLayout::class)
@Composable
fun AuthScreen(authViewModel: AuthViewModelFacade) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = ColorPainter(Color(0xff003657)),
            modifier = Modifier.fillMaxSize()
        )
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (image, gradient, solid) = createRefs()

            Image(
                asset = imageResource(id = R.drawable.auth_logo),
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }.fillMaxWidth().aspectRatio(1f)
            )
            Image(
                painter = ColorPainter(Color.Transparent),
                modifier = Modifier.constrainAs(gradient) {
                    top.linkTo(parent.top)
                    bottom.linkTo(image.bottom)
                    centerHorizontallyTo(parent)
                }.fillMaxWidth().aspectRatio(1.0925f)
                    .background(
                        VerticalGradient(
                            0f to Color(0x00003657),
                            1f to Color((0xff003657)),
                            startY = 0f,
                            endY = ContextAmbient.current.screenWidth / 1.0925f
                        )
                    )
            )
            Image(
                painter = ColorPainter(Color(0,132,212,178)),
                modifier = Modifier.constrainAs(solid) {
                    centerTo(parent)
                }.fillMaxSize()
            )
        }
        TabBar {
            Box(modifier = Modifier.padding(vertical = 13.dp, horizontal = 24.dp)) {
                Image(asset = imageResource(id = R.drawable.medico_logo), modifier = Modifier.align(
                    Alignment.CenterStart))
            }
        }
        AuthTab(authViewModel = authViewModel, Modifier.align(Alignment.BottomCenter))

        val authState = authViewModel.state.flow.collectAsState()
        when (authState.value) {
            AuthState.IN_PROGRESS -> IndefiniteProgressBar()
            AuthState.SUCCESS -> {
                launchScreen<MainActivity>()
                (ContextAmbient.current as AuthActivity).finish()
            }
            AuthState.ERROR -> AlertDialog(
                onDismissRequest = { authViewModel.clearState() },
                title = {
                    BasicText(
                        text = "Log in error",
                        style = MaterialTheme.typography.h6
                    )
                },
                text = {
                    BasicText(
                        text = "Log in or password is wrong. Please try again or restore your password",
                        style = MaterialTheme.typography.subtitle1
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { authViewModel.clearState() },
                        colors = ButtonConstants.defaultTextButtonColors(contentColor = ConstColors.lightBlue),
                        elevation = ButtonConstants.defaultElevation(0.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text = "OKAY",
                            style = MaterialTheme.typography.subtitle2
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
        BasicText(
            text = stringResource(id = R.string.log_in),
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
        )
        PhoneFormatInputField(
            hint = stringResource(id = R.string.phone_number_or_email),
            text = credentialsState.value.phoneNumberOrEmail,
            useFormat = credentialsState.value.type == AuthCredentials.Type.PHONE
        ) {
            authViewModel.updateAuthCredentials(it, credentialsState.value.password)
        }
        InputField(
            hint = stringResource(id = R.string.password),
            text = credentialsState.value.password,
            hideCharacters = true
        ) {
            authViewModel.updateAuthCredentials(credentialsState.value.phoneNumberOrEmail, it)
        }
        val context = ContextAmbient.current
        BasicText(
            text = stringResource(id = R.string.forgot_password),
            style = MaterialTheme.typography.body2.copy(color = ConstColors.lightBlue),
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                context.startActivity(Intent(context, AuthRestoreActivity::class.java))
            })
        )
        MedicoButton(text = stringResource(id = R.string.log_in)) {
            authViewModel.tryLogIn()
        }
        val string = AnnotatedString.Builder(stringResource(id = R.string.sign_up_to_medico)).apply {
            addStyle(SpanStyle(fontWeight = FontWeight.W700), 0, stringResource(id = R.string.sign_up).length)
        }.toAnnotatedString()
        Text(
            text = string,
            style = MaterialTheme.typography.body2.copy(color = ConstColors.lightBlue),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                context.startActivity(Intent(context, SignUpActivity::class.java))
            })
        )
    }
}

@Composable
fun PhoneFormatInputField(hint: String, text: String, useFormat: Boolean, onValueChange: (String) -> Unit) {
    val context = ContextAmbient.current
    val formatter = remember { PhoneNumberFormatter(context.resources.configuration.locale.toLanguageTag()) }
    InputField(hint = hint, text = if (useFormat) formatter.verifyNumber(text).formattedPhone else text, onValueChange = onValueChange)
}

@Composable
fun InputField(hint: String, text: String, hideCharacters: Boolean = false, onValueChange: (String) -> Unit) {
    Spacer(modifier = Modifier.size(12.dp))
    TextField(
        value = text,
        label = {
            BasicText(
                text = hint,
                style = TextStyle.Default.copy(color = if (text.isEmpty()) ConstColors.gray else ConstColors.lightBlue)
            )
        },
        activeColor = ConstColors.lightBlue,
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