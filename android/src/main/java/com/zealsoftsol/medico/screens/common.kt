package com.zealsoftsol.medico.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.BaseScope
import com.zealsoftsol.medico.core.mvi.scope.CanGoBack
import com.zealsoftsol.medico.utils.PhoneNumberFormatter
import kotlinx.coroutines.Deferred

@Composable
fun BasicTabBar(back: CanGoBack?, title: String) {
    TabBar {
        Row {
            if (back != null) {
                Icon(
                    asset = vectorResource(id = R.drawable.ic_arrow_back),
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .fillMaxHeight()
                        .padding(16.dp)
                        .clickable(
                            indication = null,
                            onClick = { back.goBack() },
                        )
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
            )
        }
    }
}

@Composable
fun TabBar(
    color: Color = MaterialTheme.colors.secondary,
    content: @Composable () -> Unit,
) {
    Surface(
        color = color,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        content()
    }
}

@Composable
fun MedicoButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
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
        modifier = modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun IndefiniteProgressBar() {
    Dialog(onDismissRequest = {}) { CircularProgressIndicator() }
}

@Composable
fun ErrorDialog(title: String, text: String = "", onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = Color.White,
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonConstants.defaultTextButtonColors(contentColor = ConstColors.lightBlue),
                elevation = ButtonConstants.defaultElevation(0.dp, 0.dp, 0.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.okay),
                    style = MaterialTheme.typography.subtitle2,
                )
            }
        }
    )
}

@Composable
inline fun <T : BaseScope> T.showError(title: String, text: String = "", case: T.() -> Boolean) {
    val state = withState(event = case)
    if (state.value) ErrorDialog(
        title = title,
        text = text,
        onDismiss = { state.value = false },
    )
}

@Composable
inline fun <T : BaseScope> T.withState(event: T.() -> Boolean): MutableState<Boolean> {
    return remember(this) { mutableStateOf(event()) }
}

@Composable
fun PhoneFormatInputField(
    hint: String,
    text: String,
    onValueChange: (String) -> Unit,
): Boolean {
    val countryCode =
        "RU"//if (BuildConfig.DEBUG) ConfigurationAmbient.current.locale.country else "IN"
    val formatter = remember { PhoneNumberFormatter(countryCode) }
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
fun PhoneOrEmailFormatInputField(
    hint: String,
    text: String,
    isPhoneNumber: Boolean,
    onValueChange: (String) -> Unit,
) {
    val countryCode =
        "RU"//if (BuildConfig.DEBUG) ConfigurationAmbient.current.locale.country else "IN"
    val formatter = remember { PhoneNumberFormatter(countryCode) }
    val formatted = if (isPhoneNumber) formatter.verifyNumber(text) else null
    InputField(
        hint = hint,
        text = formatted ?: text,
        isValid = if (isPhoneNumber) formatted != null else true,
        onValueChange = onValueChange,
    )
    formatted?.let(onValueChange)
}

@Composable
fun PasswordFormatInputField(
    hint: String,
    text: String,
    isValid: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    InputField(
        hint = hint,
        text = text,
        isValid = isValid,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = onValueChange,
    )
}

@Composable
fun InputField(
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        label = {
            Text(
                text = hint,
                style = TextStyle.Default
                    .copy(
                        color = when {
                            text.isEmpty() -> ConstColors.gray
                            !isValid -> MaterialTheme.colors.error
                            else -> ConstColors.lightBlue
                        }
                    )
            )
        },
        isErrorValue = !isValid,
        activeColor = ConstColors.lightBlue,
        backgroundColor = Color.White,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun InputWithError(errorText: String?, input: @Composable () -> Unit) {
    input()
    errorText?.let {
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = it,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.error,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
fun Space(dp: Dp) {
    Spacer(modifier = Modifier.size(dp))
}

@Composable
fun <T> Deferred<T>.awaitAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    LaunchedEffect(this) {
        state.value = await()
    }
    return state
}