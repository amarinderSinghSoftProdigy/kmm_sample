package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.Scope
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.screens.InputField
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.PhoneFormatInputField
import com.zealsoftsol.medico.screens.TabBar
import java.text.SimpleDateFormat

@Composable
private fun BasicAuthRestoreScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    body: @Composable ColumnScope.() -> Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    footer: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colors.primary)
    ) {
        TabBar {
            Row {
                Icon(
                    asset = vectorResource(id = R.drawable.ic_arrow_back),
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(16.dp)
                        .clickable(
                            indication = null,
                            onClick = onBack,
                        )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                )
            }
        }
        Column(modifier = Modifier.align(Alignment.Center).padding(16.dp)) {
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    color = ConstColors.gray,
                    modifier = Modifier.padding(horizontal = 60.dp),
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            val isButtonEnabled = body()
            Spacer(modifier = Modifier.size(12.dp))
            MedicoButton(
                text = buttonText,
                onClick = onButtonClick,
                isEnabled = isButtonEnabled,
            )
        }
        footer?.let {
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(color = Color.White)
            ) {
                it.invoke(this)
            }
        }
    }
}

@Composable
fun AuthPhoneNumberInputScreen(authViewModel: AuthViewModel, scope: Scope.ForgetPassword.PhoneNumberInput) {
    val phoneState = remember { mutableStateOf("") }
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.password_reset),
        subtitle = stringResource(id = R.string.reset_password_hint),
        onBack = { scope.goBack() },
        body = {
            PhoneFormatInputField(
                hint = stringResource(id = R.string.phone_number),
                text = phoneState.value,
                onValueChange = { phoneState.value = it },
            )
        },
        buttonText = stringResource(id = R.string.get_code),
        onButtonClick = { authViewModel.sendOtp(phoneState.value) }
    )
    if (scope.success.isFalse) ContextAmbient.current.toast(R.string.something_went_wrong)
}

@Composable
fun AuthAwaitVerificationScreen(
    authViewModel: AuthViewModel,
    scope: Scope.ForgetPassword.AwaitVerification,
    dateFormat: SimpleDateFormat,
) {
    val code = remember { mutableStateOf("") }
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.phone_verification),
        subtitle = "${stringResource(id = R.string.verification_code_sent_hint)} ${scope.phoneNumber}",
        onBack = { scope.goBack() },
        body = {
            if (scope.timeBeforeResend > 0 && scope.attemptsLeft > 0) {
                Text(
                    text = dateFormat.format(scope.timeBeforeResend),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "${scope.attemptsLeft} ${stringResource(id = R.string.attempt_left)}",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.W600,
                    color = ConstColors.lightBlue,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            InputField(
                hint = stringResource(id = R.string.verification_code),
                text = code.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { code.value = it },
            )
            code.value.isNotEmpty() && scope.attemptsLeft > 0
        },
        buttonText = stringResource(id = R.string.submit),
        onButtonClick = { authViewModel.submitOtp(code.value) },
        footer = {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp)
                    .clickable(onClick = { authViewModel.resendOtp() })
            ) {
                Text(
                    text = stringResource(id = R.string.didnt_get_code),
                    style = MaterialTheme.typography.body2,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(id = R.string.resend),
                    style = MaterialTheme.typography.subtitle2,
                    color = ConstColors.lightBlue,
                )
            }
        }
    )
    val showDialog = remember(scope.attemptsLeft) { mutableStateOf(scope.codeValidity.isFalse) }
    if (showDialog.value) AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(
                text = stringResource(id = R.string.wrong_code),
                style = MaterialTheme.typography.h6,
            )
        },
        confirmButton = {
            Button(
                onClick = { showDialog.value = false },
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
    when (scope.resendSuccess.value) {
        true -> ContextAmbient.current.toast("code sent")
        false -> ContextAmbient.current.toast(R.string.something_went_wrong)
    }
    if (scope.resendSuccess.isFalse) ContextAmbient.current.toast(R.string.something_went_wrong)
}

@Composable
fun AuthEnterNewPasswordScreen(authViewModel: AuthViewModel, scope: Scope.ForgetPassword.EnterNewPassword) {
    val password1 = remember { mutableStateOf("") }
    val password2 = remember { mutableStateOf("") }
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.new_password),
        subtitle = "",
        onBack = { scope.goBack() },
        body = {
            PasswordFormatInputField(
                hint = stringResource(id = R.string.new_password),
                text = password1.value,
                onValueChange = { password1.value = it },
            )
            Spacer(modifier = Modifier.size(12.dp))
            val isValid = password2.value.isEmpty() || (password1.value == password2.value)
            PasswordFormatInputField(
                hint = stringResource(id = R.string.new_password_repeat),
                text = password2.value,
                isValid = isValid,
                onValueChange = { password2.value = it },
            )
            if (!isValid) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(id = R.string.password_doesnt_match),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            password1.value.isNotEmpty() && password1.value == password2.value
        },
        buttonText = stringResource(id = R.string.confirm),
        onButtonClick = { authViewModel.changePassword(password2.value) }
    )
    if (scope.success.isFalse) ContextAmbient.current.toast(R.string.something_went_wrong)
}