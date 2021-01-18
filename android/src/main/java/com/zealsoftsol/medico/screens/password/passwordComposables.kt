package com.zealsoftsol.medico.screens.password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.screens.BasicScreen
import com.zealsoftsol.medico.screens.InputWithError
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.showNotificationAlert

@Composable
fun VerifyCurrentPasswordScreen(scope: PasswordScope.VerifyCurrent) {
    val password = scope.password.flow.collectAsState()
    val validation = scope.passwordValidation.flow.collectAsState()
    BasicScreen(
        subtitle = stringResource(id = R.string.change_password_hint),
        body = {
            InputWithError(validation.value?.password) {
                PasswordFormatInputField(
                    hint = stringResource(id = R.string.current_password),
                    text = password.value,
                    onValueChange = { scope.changePassword(it) },
                )
            }
            password.value.isNotEmpty()
        },
        buttonText = stringResource(id = R.string.confirm),
        onButtonClick = { scope.submit() }
    )
}

@Composable
fun EnterNewPasswordScreen(scope: PasswordScope.EnterNew) {
    val password1 = scope.password.flow.collectAsState()
    val password2 = scope.confirmPassword.flow.collectAsState()
    val validation = scope.passwordValidation.flow.collectAsState()
    BasicScreen(
        subtitle = stringResource(id = R.string.new_password_hint),
        body = {
            PasswordFormatInputField(
                hint = stringResource(id = R.string.new_password),
                text = password1.value,
                onValueChange = { scope.changePassword(it) },
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
                    onValueChange = { scope.changeConfirmPassword(it) },
                )
            }
            password1.value.isNotEmpty() && password1.value == password2.value
        },
        buttonText = stringResource(id = R.string.confirm),
        onButtonClick = { scope.submit() }
    )
    scope.showNotificationAlert(onDismiss = { scope.finishPasswordFlow() })
}