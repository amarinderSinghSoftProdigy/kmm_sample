package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PasswordFormatInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    onValueChange: (String) -> Unit,
    onPositioned: ((LayoutCoordinates) -> Unit)? = null,
    mandatory: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    ) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.onGloballyPositioned { onPositioned?.invoke(it) },
    ) {
        val isPasswordHidden = remember { mutableStateOf(true) }
        InputField(
            modifier = modifier,
            hint = hint,
            text = text,
            isValid = isValid,
            visualTransformation = if (isPasswordHidden.value) PasswordVisualTransformation() else VisualTransformation.None,
            maxLines = 1,
            onValueChange = onValueChange,
            leadingIcon = leadingIcon,
            mandatory = mandatory,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions
        )
        Icon(
            imageVector = Icons.Default.RemoveRedEye,
            contentDescription = null,
            tint = if (isPasswordHidden.value) ConstColors.gray else ConstColors.lightBlue,
            modifier = Modifier
                .size(42.dp)
                .clickable(indication = rememberRipple(radius = 15.dp)) {
                    isPasswordHidden.value = !isPasswordHidden.value
                }
                .padding(12.dp),
        )
    }
}

@Composable
fun PhoneFormatInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    onValueChange: (String) -> Unit,
): Boolean {
    InputWithPrefix("91") {
        InputField(
            modifier = modifier,
            hint = hint,
            text = text,
            isValid = text.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            maxLines = 1,
            onValueChange = onValueChange,
        )
    }
//    text.let(onValueChange)
    return text.isNotEmpty()
}

@Composable
fun PhoneFormatInputFieldForRegister(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    mandatory: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions(),
    ) {
    InputField(
        modifier = modifier,
        hint = hint,
        text = text,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone,imeAction = ImeAction.Done),
        maxLines = 1,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        mandatory = mandatory,
        keyboardActions = keyboardActions,
    )
}
/*
*  keyboardOptions = KeyboardOptions.Default.copy(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Done
                                                    ),
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        scope.resetButton(false)
                                                        scope.selectBatch(false, product = product)
                                                        scope.buy(product = product)
                                                        keyboardController?.hide()
                                                    })
* */
@Composable
fun InputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    mandatory: Boolean = false,
) {
    TextField(
        value = text,//TextFieldValue(text, TextRange(text.length)),
        label = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = hint,
                    style = TextStyle.Default,
                )
                if (mandatory) {
                    Space(dp = 4.dp)
                    Text(text = "*", color = ConstColors.red)
                }
            }
        },
        isError = !isValid,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = ConstColors.lightBlue,
            focusedLabelColor = ConstColors.lightBlue,
            focusedIndicatorColor = ConstColors.lightBlue,
        ),
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardActions = keyboardActions
    )
}

@Composable
fun InputFieldWithCounter(
    limit: Int,
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = text,
            label = {
                Text(
                    text = hint,
                    style = TextStyle.Default,
                )
            },
            isError = !isValid,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = ConstColors.lightBlue,
                focusedLabelColor = ConstColors.lightBlue,
                focusedIndicatorColor = ConstColors.lightBlue,
            ),
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            modifier = modifier.weight(0.8f),
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.2f),
            text = text.length.toString() + "/" + limit.toString(),
            color = ConstColors.gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun OutlinedInputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = text,//TextFieldValue(text, TextRange(text.length)),
        label = {
            Text(
                text = hint,
                style = TextStyle.Default,
            )
        },
        isError = !isValid,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = ConstColors.lightBlue,
            focusedLabelColor = ConstColors.lightBlue,
            focusedIndicatorColor = ConstColors.lightBlue,
        ),
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
    )
}

class RectHolder(var rect: Rect? = null)

fun Modifier.scrollOnFocus(
    scrollState: ScrollState,
    coroutineScope: CoroutineScope,
): Modifier {
    val rectHolder = RectHolder()
    return composed {
        onGloballyPositioned { rectHolder.rect = it.boundsInParent() }
            .onFocusEvent {
                if (it.isFocused) {
                    rectHolder.rect.takeIf { !scrollState.isScrollInProgress }
                        ?.let { coroutineScope.launch { scrollState.animateScrollTo(it.top.toInt()) } }
                }
            }
    }
}

fun Modifier.scrollOnFocus(
    rectHolder: RectHolder,
    scrollState: ScrollState,
    coroutineScope: CoroutineScope,
): Modifier = composed {
    onFocusEvent {
        if (it.isFocused) {
            rectHolder.rect?.takeIf { !scrollState.isScrollInProgress }
                ?.let { coroutineScope.launch { scrollState.animateScrollTo(it.top.toInt()) } }
        }
    }
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
fun InputWithPrefix(prefix: String, modifier: Modifier = Modifier, input: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = prefix,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colors.background,
            modifier = modifier,
        )
        Space(8.dp)
        input()
    }
}

@Composable
fun ReadOnlyField(text: String, labelId: Int) {
    TextField(
        value = text,
        label = {
            Text(
                text = stringResource(id = labelId),
                style = TextStyle.Default,
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = ConstColors.lightBlue,
            disabledLabelColor = if (text.isNotEmpty()) ConstColors.lightBlue else MaterialTheme.colors.onSurface.copy(
                ContentAlpha.medium
            ),
            disabledTextColor = ConstColors.gray,
        ),
        enabled = false,
        onValueChange = { },
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
    )
}

inline fun String.formatIndia() = "91$this"