package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors

@Composable
fun PasswordFormatInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    autoScrollOnFocus: ScrollState? = null,
    onValueChange: (String) -> Unit,
) {
    InputField(
        modifier = modifier,
        hint = hint,
        text = text,
        isValid = isValid,
        autoScrollOnFocus = autoScrollOnFocus,
        visualTransformation = PasswordVisualTransformation(),
        maxLines = 1,
        onValueChange = onValueChange,
    )
}

@Composable
fun PhoneFormatInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    autoScrollOnFocus: ScrollState? = null,
    onValueChange: (String) -> Unit,
): Boolean {
    val formatter = rememberPhoneNumberFormatter()
    val formatted = formatter.verifyNumber(text)
    val isValid = formatted != null || text.isEmpty()
    InputField(
        modifier = modifier,
        hint = hint,
        text = formatted ?: text,
        isValid = isValid,
        autoScrollOnFocus = autoScrollOnFocus,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        maxLines = 1,
        onValueChange = onValueChange,
    )
    formatted?.let(onValueChange)
    return isValid
}

@Composable
fun PhoneOrEmailFormatInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isPhoneNumber: Boolean,
    autoScrollOnFocus: ScrollState? = null,
    onValueChange: (String) -> Unit,
) {
    val formatter = rememberPhoneNumberFormatter()
    val formatted = if (isPhoneNumber) formatter.verifyNumber(text) else null
    InputField(
        modifier = modifier,
        hint = hint,
        text = formatted ?: text,
        autoScrollOnFocus = autoScrollOnFocus,
        isValid = if (isPhoneNumber) formatted != null || text.isEmpty() else true,
        maxLines = 1,
        onValueChange = onValueChange,
    )
    formatted?.let(onValueChange)
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    autoScrollOnFocus: ScrollState? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    var bounds: Rect? = null
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
        singleLine = maxLines == 1,
        maxLines = maxLines,
        modifier = modifier.run {
            autoScrollOnFocus?.let { ss ->
                onGloballyPositioned { bounds = it.boundsInParent }
                    .onFocusEvent { state ->
                        if (state == FocusState.Active) {
                            bounds?.let { ss.smoothScrollTo(it.top) }
                        }
                    }
            } ?: this
        }.fillMaxWidth(),
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
fun ReadOnlyField(text: String, labelId: Int) {
    Text(
        text = if (text.isEmpty()) stringResource(id = labelId) else text,
        color = if (text.isEmpty()) ConstColors.gray else Color.Black,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(vertical = 20.dp, horizontal = 16.dp),
    )
}