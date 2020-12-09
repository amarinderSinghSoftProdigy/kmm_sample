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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CanGoBack
import com.zealsoftsol.medico.core.mvi.scope.EnterNewPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.screens.BasicTabBar
import com.zealsoftsol.medico.screens.InputField
import com.zealsoftsol.medico.screens.InputWithError
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.PhoneFormatInputField
import com.zealsoftsol.medico.screens.showAlert
import com.zealsoftsol.medico.screens.showErrorAlert
import java.text.SimpleDateFormat

@Composable
private fun BasicAuthRestoreScreen(
    title: String,
    subtitle: String,
    back: CanGoBack,
    body: @Composable ColumnScope.() -> Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    footer: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colors.primary)
    ) {
        BasicTabBar(back = back, title = title)
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
fun AuthPhoneNumberInputScreen(scope: OtpScope.PhoneNumberInput) {
    val phoneState = scope.phoneNumber.flow.collectAsState()
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.password_reset),
        subtitle = stringResource(id = R.string.reset_password_hint),
        back = scope,
        body = {
            PhoneFormatInputField(
                hint = stringResource(id = R.string.phone_number),
                text = phoneState.value,
                onValueChange = { scope.changePhoneNumber(it) },
            )
        },
        buttonText = stringResource(id = R.string.get_code),
        onButtonClick = { scope.sendOtp(phoneState.value.filter { it.isDigit() }) },
    )
    scope.showErrorAlert()
}

@Composable
fun AuthAwaitVerificationScreen(
    scope: OtpScope.AwaitVerification,
    dateFormat: SimpleDateFormat,
) {
    val code = remember { mutableStateOf("") }
    val attempts = scope.attemptsLeft.flow.collectAsState()
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.phone_verification),
        subtitle = "${stringResource(id = R.string.verification_code_sent_hint)} ${scope.phoneNumber}",
        back = scope,
        body = {
            val timer = scope.resendTimer.flow.collectAsState()
            if (timer.value > 0 && attempts.value > 0) {
                Text(
                    text = dateFormat.format(timer.value),
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
            code.value.isNotEmpty() && attempts.value > 0
        },
        buttonText = stringResource(id = R.string.submit),
        onButtonClick = { scope.submitOtp(code.value) },
        footer = {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp)
                    .clickable(onClick = { scope.resendOtp() })
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
    scope.showErrorAlert()
}

@Composable
fun AuthEnterNewPasswordScreen(scope: EnterNewPasswordScope) {
    val password1 = remember { mutableStateOf("") }
    val password2 = remember { mutableStateOf("") }
    val validation = scope.passwordValidation.flow.collectAsState()
    BasicAuthRestoreScreen(
        title = stringResource(id = R.string.new_password),
        subtitle = "",
        back = scope,
        body = {
            PasswordFormatInputField(
                hint = stringResource(id = R.string.new_password),
                text = password1.value,
                onValueChange = { password1.value = it },
            )
            Spacer(modifier = Modifier.size(12.dp))
            val isValid = password2.value.isEmpty() || (password1.value == password2.value)
            val errorText = validation.value?.password ?: run {
                if (!isValid) stringResource(id = R.string.password_doesnt_match) else null
            }
            InputWithError(errorText) {
                PasswordFormatInputField(
                    hint = stringResource(id = R.string.new_password_repeat),
                    text = password2.value,
                    isValid = isValid,
                    onValueChange = { password2.value = it },
                )
            }
            password1.value.isNotEmpty() && password1.value == password2.value
        },
        buttonText = stringResource(id = R.string.confirm),
        onButtonClick = { scope.changePassword(password2.value) }
    )
    scope.showAlert(onDismiss = { scope.finishResetPasswordFlow() })
}