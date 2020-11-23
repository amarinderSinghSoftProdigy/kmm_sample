package com.zealsoftsol.medico.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.Scope
import com.zealsoftsol.medico.utils.PhoneNumberFormatter
import kotlinx.coroutines.Deferred

@Composable
fun TabBar(color: Color = Color(0xffD9EDF9), content: @Composable () -> Unit) {
    Surface(color = color, modifier = Modifier.fillMaxWidth().height(56.dp)) {
        content()
    }
}

@Composable
fun MedicoButton(text: String, isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonConstants.defaultButtonColors(
            backgroundColor = ConstColors.yellow,
            disabledBackgroundColor = Color.LightGray,
            contentColor = MaterialTheme.colors.onPrimary,
            disabledContentColor = MaterialTheme.colors.onPrimary,
        ),
        enabled = isEnabled,
        shape = RoundedCornerShape(2.dp),
        elevation = ButtonConstants.defaultElevation(),
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun IndefiniteProgressBar() {
    Dialog(onDismissRequest = {}) { CircularProgressIndicator() }
}

@Composable
fun ErrorDialog(titleRes: Int, textRes: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = titleRes),
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            if (textRes != 0) {
                Text(
                    text = stringResource(id = textRes),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonConstants.defaultTextButtonColors(contentColor = ConstColors.lightBlue),
                elevation = ButtonConstants.defaultElevation(0.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    )
}

@Composable
inline fun <T : Scope> T.showError(titleRes: Int, textRes: Int, case: T.() -> Boolean) {
    val state = withState(event = case)
    if (state.value) ErrorDialog(
        titleRes = titleRes,
        textRes = textRes,
        onDismiss = {
            state.value = false
        },
    )
}

@Composable
inline fun <T : Scope> T.withState(event: T.() -> Boolean): MutableState<Boolean> {
    return remember(this) { mutableStateOf(event()) }
}

@Composable
fun PhoneFormatInputField(hint: String, text: String, onValueChange: (String) -> Unit): Boolean {
    val formatter =
        remember { PhoneNumberFormatter(if (BuildConfig.DEBUG) "RU-ru" else "IN-in") }
    val formatted = formatter.verifyNumber(text)
    val isValid = formatted != null
    InputField(
        hint = hint,
        text = formatted ?: text,
        isValid = isValid,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        onValueChange = onValueChange,
    )
    formatted?.let(onValueChange)
    return isValid
}

@Composable
fun PhoneOrEmailFormatInputField(hint: String, text: String, isPhoneNumber: Boolean, onValueChange: (String) -> Unit) {
    val formatter =
        remember { PhoneNumberFormatter(if (BuildConfig.DEBUG) "RU-ru" else "IN-in") }
    val formatted = if (isPhoneNumber) formatter.verifyNumber(text) else null
    InputField(
        hint = hint,
        text = formatted ?: text,
        isValid = if (isPhoneNumber) formatted != null else true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = if (isPhoneNumber) KeyboardType.Phone else KeyboardType.Email),
        onValueChange = onValueChange,
    )
    formatted?.let(onValueChange)
}

@Composable
fun PasswordFormatInputField(hint: String, text: String, isValid: Boolean = true, onValueChange: (String) -> Unit) {
    InputField(
        hint = hint,
        text = text,
        isValid = isValid,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = onValueChange
    )
}

@Composable
fun InputField(
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = text,
        label = {
            Text(
                text = hint,
                style = TextStyle.Default
                    .copy(color = when {
                        text.isEmpty() -> ConstColors.gray
                        !isValid -> MaterialTheme.colors.error
                        else -> ConstColors.lightBlue
                    })
            )
        },
        isErrorValue = !isValid,
        activeColor = ConstColors.lightBlue,
        backgroundColor = Color.White,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun <T> Deferred<T>.awaitAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    LaunchedEffect(this) {
        state.value = await()
    }
    return state
}