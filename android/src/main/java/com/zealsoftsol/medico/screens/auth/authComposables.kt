package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.PhoneOrEmailFormatInputField
import com.zealsoftsol.medico.screens.TabBar
import com.zealsoftsol.medico.screens.showError

@Composable
fun AuthScreen(scope: LogInScope) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = ColorPainter(MaterialTheme.colors.background),
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
                            0f to MaterialTheme.colors.background.copy(alpha = 0f),
                            1f to MaterialTheme.colors.background,
                            startY = 0f,
                            endY = ContextAmbient.current.screenWidth / 1.0925f
                        )
                    )
            )
            Image(
                painter = ColorPainter(Color(0, 132, 212, 178)),
                modifier = Modifier.constrainAs(solid) {
                    centerTo(parent)
                }.fillMaxSize()
            )
        }
        TabBar(color = Color.White) {
            Box(modifier = Modifier.padding(vertical = 13.dp, horizontal = 24.dp)) {
                Image(
                    asset = imageResource(id = R.drawable.medico_logo),
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
        }
        AuthTab(Modifier.align(Alignment.Center), scope)

        scope.showError(
            title = stringResource(id = R.string.error_log_in_title),
            text = stringResource(id = R.string.error_log_in_text),
            case = { success.isFalse },
        )
    }
}

@Composable
fun AuthTab(modifier: Modifier, scope: LogInScope) {
    val credentialsState = scope.credentials.flow.collectAsState()
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colors.primary)
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.log_in),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h5,
        )
        Spacer(modifier = Modifier.size(12.dp))

        PhoneOrEmailFormatInputField(
            hint = stringResource(id = R.string.phone_number_or_email),
            text = credentialsState.value.phoneNumberOrEmail,
            isPhoneNumber = credentialsState.value.type == AuthCredentials.Type.PHONE,
        ) {
            scope.updateAuthCredentials(it, credentialsState.value.password)
        }
        Spacer(modifier = Modifier.size(12.dp))
        PasswordFormatInputField(
            hint = stringResource(id = R.string.password),
            text = credentialsState.value.password,
        ) {
            scope.updateAuthCredentials(credentialsState.value.phoneNumberOrEmail, it)
        }
        Text(
            text = stringResource(id = R.string.forgot_password),
            color = ConstColors.lightBlue,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                scope.goToForgetPassword()
            })
        )
        MedicoButton(text = stringResource(id = R.string.log_in), isEnabled = true) {
            scope.tryLogIn()
        }
        val string = AnnotatedString.Builder(stringResource(id = R.string.sign_up_to_medico)).apply {
            addStyle(SpanStyle(fontWeight = FontWeight.W700), 0, stringResource(id = R.string.sign_up).length)
        }.toAnnotatedString()
        Text(
            text = string,
            color = ConstColors.lightBlue,
            style = MaterialTheme.typography.body2,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                scope.goToSignUp()
            }),
        )
    }
}