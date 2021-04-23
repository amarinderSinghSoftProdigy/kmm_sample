package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneOrEmailFormatInputField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TabBar
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.common.showErrorAlert

@Composable
fun AuthScreen(scope: LogInScope) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = ColorPainter(MaterialTheme.colors.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (image, gradient, solid) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.auth_logo),
                contentDescription = null,
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }.fillMaxWidth().aspectRatio(1f)
            )
            Image(
                painter = ColorPainter(Color.Transparent),
                contentDescription = null,
                modifier = Modifier.constrainAs(gradient) {
                    top.linkTo(parent.top)
                    bottom.linkTo(image.bottom)
                    centerHorizontallyTo(parent)
                }.fillMaxWidth().aspectRatio(1.0925f)
                    .background(
                        Brush.verticalGradient(
                            0f to MaterialTheme.colors.background.copy(alpha = 0f),
                            1f to MaterialTheme.colors.background,
                            startY = 0f,
                            endY = LocalContext.current.screenWidth / 1.0925f,
                            tileMode = TileMode.Clamp
                        )
                    )
            )
            Image(
                painter = ColorPainter(Color(0, 132, 212, 178)),
                contentDescription = null,
                modifier = Modifier.constrainAs(solid) {
                    centerTo(parent)
                }.fillMaxSize()
            )
        }
        TabBar(color = Color.White) {
            Box(modifier = Modifier.padding(vertical = 13.dp, horizontal = 24.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.medico_logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
        }
        AuthTab(Modifier.padding(top = 56.dp).align(Alignment.Center), scope)

        scope.showErrorAlert()
    }
}

@Composable
private fun AuthTab(modifier: Modifier, scope: LogInScope) {
    val credentialsState = scope.credentials.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colors.primary)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Space(24.dp)
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
            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
        ) {
            scope.updateAuthCredentials(it, credentialsState.value.password)
        }
        Spacer(modifier = Modifier.size(12.dp))
        PasswordFormatInputField(
            hint = stringResource(id = R.string.password),
            text = credentialsState.value.password,
            onValueChange = {
                scope.updateAuthCredentials(credentialsState.value.phoneNumberOrEmail, it)
            },
            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
        )
        Text(
            text = stringResource(id = R.string.forgot_password),
            color = ConstColors.lightBlue,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(vertical = 12.dp).clickable(onClick = {
                scope.goToForgetPassword()
            })
        )
        MedicoButton(
            text = stringResource(id = R.string.log_in),
            isEnabled = !credentialsState.value.isEmpty
        ) {
            scope.tryLogIn()
        }
        Space(12.dp)
        MedicoButton(
            text = stringResource(id = R.string.register),
            isEnabled = true,
            color = Color(0xFF0084D4),
            contentColor = Color(0xFFF4F9FD),
        ) {
            scope.goToSignUp()
        }
        Space(24.dp)
    }
}