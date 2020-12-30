package com.zealsoftsol.medico.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.AmbientContext
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
import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.CanGoBack
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithErrors
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithNotifications
import com.zealsoftsol.medico.utils.PhoneNumberFormatter
import kotlinx.coroutines.Deferred

@Composable
fun BasicTabBar(back: CanGoBack?, title: String) {
    TabBar {
        Row {
            if (back != null) {
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_arrow_back),
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
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ConstColors.yellow,
            disabledBackgroundColor = Color.LightGray,
            contentColor = MaterialTheme.colors.onPrimary,
            disabledContentColor = MaterialTheme.colors.onPrimary,
        ),
        enabled = isEnabled,
        shape = RoundedCornerShape(2.dp),
        elevation = ButtonDefaults.elevation(),
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
private fun ErrorDialog(title: String, text: String = "", onDismiss: () -> Unit) {
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
                colors = ButtonDefaults.textButtonColors(contentColor = ConstColors.lightBlue),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
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
fun <T : WithErrors> T.showErrorAlert() {
    val errorCode = errors.flow.collectAsState()
    errorCode.value?.let {
        val titleResourceId = AmbientContext.current.runCatching {
            resources.getIdentifier(it.title, "string", packageName)
        }.getOrNull() ?: 0
        val bodyResourceId = AmbientContext.current.runCatching {
            resources.getIdentifier(it.body, "string", packageName)
        }.getOrNull() ?: 0
        ErrorDialog(
            title = stringResource(id = titleResourceId),
            text = stringResource(id = bodyResourceId),
            onDismiss = { dismissError() }
        )
    }
}

@Composable
fun <T : WithNotifications> T.showAlert(onDismiss: () -> Unit = { dismissNotification() }) {
    val notification = notifications.flow.collectAsState()
    notification.value?.let {
        val titleResourceId = AmbientContext.current.runCatching {
            resources.getIdentifier(it.title, "string", packageName)
        }.getOrNull() ?: 0
        val bodyResourceId = AmbientContext.current.runCatching {
            resources.getIdentifier(it.body, "string", packageName)
        }.getOrNull() ?: 0
        ErrorDialog(
            title = stringResource(id = titleResourceId),
            text = stringResource(id = bodyResourceId),
            onDismiss = onDismiss
        )
    }
}

@Composable
fun stringResourceByName(name: String): String {
    return stringResource(id = AmbientContext.current.runCatching {
        resources.getIdentifier(name, "string", packageName)
    }.getOrNull() ?: 0)
}

@Composable
fun PhoneFormatInputField(
    hint: String,
    text: String,
    onValueChange: (String) -> Unit,
): Boolean {
    val countryCode = when {
        BuildConfig.FLAVOR == "dev" && BuildConfig.DEBUG && BuildConfig.ANDROID_DEV -> "RU" // devDebug
        BuildConfig.FLAVOR == "prod" && !BuildConfig.DEBUG -> "IN" // prodRelease
        else -> AmbientConfiguration.current.locale.country
    }
    val formatter = remember { PhoneNumberFormatter(countryCode) }
    val formatted = formatter.verifyNumber(text)
    val isValid = formatted != null || text.isEmpty()
    InputField(
        hint = hint,
        text = formatted ?: text,
        isValid = isValid,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        maxLines = 1,
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
    val countryCode = when {
        BuildConfig.FLAVOR == "dev" && BuildConfig.DEBUG && BuildConfig.ANDROID_DEV -> "RU" // devDebug
        BuildConfig.FLAVOR == "prod" && !BuildConfig.DEBUG -> "IN" // prodRelease
        else -> AmbientConfiguration.current.locale.country
    }
    val formatter = remember { PhoneNumberFormatter(countryCode) }
    val formatted = if (isPhoneNumber) formatter.verifyNumber(text) else null
    InputField(
        hint = hint,
        text = formatted ?: text,
        isValid = if (isPhoneNumber) formatted != null || text.isEmpty() else true,
        maxLines = 1,
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
        maxLines = 1,
        onValueChange = onValueChange,
    )
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    isValid: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
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
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
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
fun Separator(modifier: Modifier = Modifier, padding: Dp = 16.dp) {
    Box(
        modifier = modifier.fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = padding)
            .background(ConstColors.gray)
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

data class BottomSheetCell(val stringId: Int, val iconAsset: ImageVector)

@Composable
fun BottomSheet(
    title: String,
    cells: List<BottomSheetCell>,
    onClick: (BottomSheetCell) -> Unit,
    isShowingBottomSheet: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    if (isShowingBottomSheet.value) Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .clickable(indication = null) { isShowingBottomSheet.value = false }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column {
                Row(
                    modifier = Modifier.height(52.dp)
                        .padding(start = 18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        color = ConstColors.gray,
                    )
                }
                cells.forEach {
                    Row(
                        modifier = Modifier.height(48.dp)
                            .fillMaxWidth()
                            .clickable { onClick(it) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = it.iconAsset,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                        Text(
                            text = stringResource(id = it.stringId),
                            color = ConstColors.gray,
                        )
                    }
                }
            }
        }
    }
}