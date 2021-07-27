package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.screens.common.BasicScreen
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputField
import java.text.SimpleDateFormat

@Composable
fun AuthPhoneNumberInputScreen(scope: OtpScope.PhoneNumberInput) {
    val phoneState = scope.phoneNumber.flow.collectAsState()
    BasicScreen(
        subtitle = stringResource(id = R.string.reset_password_hint),
        body = {
            PhoneFormatInputField(
                hint = stringResource(id = R.string.phone_number),
                text = phoneState.value,
                onValueChange = { scope.changePhoneNumber(it) },
            ) || phoneState.value.isEmpty()
        },
        buttonText = stringResource(id = R.string.get_code),
        onButtonClick = { scope.sendOtp(phoneState.value.filter { it.isDigit() }) },
    )
}

@Composable
fun AuthAwaitVerificationScreen(
    scope: OtpScope.AwaitVerification,
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("mm:ss", context.resources.configuration.locale) }
    val code = remember { mutableStateOf("") }
    val attempts = scope.attemptsLeft.flow.collectAsState()
    BasicScreen(
        subtitle = "${stringResource(id = R.string.verification_code_sent_hint)} ${scope.phoneNumber}",
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
                    text = "${attempts.value} ${stringResource(id = R.string.attempt_left)}",
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
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        code.value = it
                    }
                },
            )
            code.value.isNotEmpty()
        },
        buttonText = stringResource(id = R.string.submit),
        onButtonClick = { scope.submitOtp(code.value) },
        footer = {
            val isResendActive = scope.resendActive.flow.collectAsState()
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp)
                    .run {
                        if (isResendActive.value) clickable(onClick = { scope.resendOtp() }) else this
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.didnt_get_code),
                    style = MaterialTheme.typography.body2,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(id = R.string.resend),
                    style = MaterialTheme.typography.subtitle2,
                    color = if (isResendActive.value) ConstColors.lightBlue else ConstColors.gray,
                )
            }
        }
    )
}