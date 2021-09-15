package com.zealsoftsol.medico.screens.common

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithNotifications
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.screens.Notification
import kotlinx.coroutines.Deferred
import java.util.Locale

@Composable
fun TabBar(
    color: Color = MaterialTheme.colors.secondary,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color),
        contentAlignment = Alignment.CenterStart,
    ) {
        content()
    }
}

@Composable
fun ShimmerItem(padding: PaddingValues = PaddingValues(0.dp)) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = ConstColors.gray.copy(alpha = 0.1f),
        targetValue = ConstColors.gray.copy(alpha = 0.4f),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .padding(padding)
            .background(color, RoundedCornerShape(50))
            .fillMaxWidth()
            .height(24.dp)
    )
}

@Composable
fun MedicoButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean,
    color: Color = ConstColors.yellow,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    textSize: TextUnit = 15.sp,
    height: Dp = 48.dp,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            disabledBackgroundColor = Color.LightGray,
            contentColor = contentColor,
            disabledContentColor = contentColor,
        ),
        border = border,
        enabled = isEnabled,
        shape = MaterialTheme.shapes.medium,
        elevation = elevation,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Text(
            text = text,
            fontSize = textSize,
            fontWeight = FontWeight.W700,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun MedicoRoundButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    color: Color = ConstColors.yellow,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = null,
    textSize: TextUnit = 15.sp,
    height: Dp = 38.dp,
    wrapTextSize: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            disabledBackgroundColor = Color.LightGray,
            contentColor = contentColor,
            disabledContentColor = contentColor,
        ),
        border = border,
        enabled = isEnabled,
        shape = RoundedCornerShape(50),
        elevation = elevation,
        contentPadding = if (wrapTextSize) PaddingValues(
            vertical = 8.dp,
            horizontal = 32.dp
        ) else ButtonDefaults.ContentPadding,
        modifier = modifier
            .composed {
                if (wrapTextSize) {
                    wrapContentWidth(unbounded = true)
                } else {
                    fillMaxWidth()
                }
            }
            .height(height),
    ) {
        Text(
            text = text,
            fontSize = textSize,
            fontWeight = FontWeight.W700,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun MedicoSmallButton(
    modifier: Modifier = Modifier,
    widthModifier: Modifier.() -> Modifier = { wrapContentWidth(unbounded = true) },
    text: String,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    isEnabled: Boolean = true,
    enabledColor: Color = ConstColors.yellow,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = enabledColor,
            disabledBackgroundColor = Color.LightGray,
            contentColor = contentColor,
            disabledContentColor = MaterialTheme.colors.onPrimary,
        ),
        contentPadding = contentPadding,
        enabled = isEnabled,
        shape = RoundedCornerShape(5.dp),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        modifier = modifier.widthModifier(),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun IndefiniteProgressBar() {
    Dialog(onDismissRequest = {}) { CircularProgressIndicator() }
}

@Composable
private fun SimpleDialog(
    title: String,
    text: String?,
    onDismiss: () -> Unit,
    canDismissOnTapOutside: Boolean,
) {
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
            if (!text.isNullOrEmpty()) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        },
        confirmButton = {
            AlertButton(onDismiss, stringResource(id = R.string.okay))
        },
        properties = DialogProperties(
            dismissOnBackPress = canDismissOnTapOutside,
            dismissOnClickOutside = canDismissOnTapOutside,
        )
    )
}

@Composable
fun AlertButton(
    onClick: () -> Unit,
    text: String,
    isEnabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = ConstColors.lightBlue),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        enabled = isEnabled,
    ) {
        Text(
            text = text.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.subtitle2,
        )
    }
}

@Composable
fun Scope.Host.showErrorAlert() {
    val errorCode = alertError.flow.collectAsState()
    errorCode.value?.let {
        val titleResourceId = LocalContext.current.runCatching {
            resources.getIdentifier(it.title, "string", packageName)
        }.getOrNull() ?: R.string.E0000
        val bodyResourceId = LocalContext.current.runCatching {
            resources.getIdentifier(it.body, "string", packageName)
        }.getOrNull() ?: R.string.something_went_wrong
        SimpleDialog(
            title = stringResource(id = titleResourceId),
            text = stringResource(id = bodyResourceId),
            canDismissOnTapOutside = true,
            onDismiss = { dismissAlertError() }
        )
    }
}

@Composable
fun <T : WithNotifications> T.showNotificationAlert() {
    val notification = notifications.flow.collectAsState()
    notification.value?.let {
        val titleResourceId = LocalContext.current.runCatching {
            resources.getIdentifier(it.title, "string", packageName)
        }.getOrNull() ?: 0
        val bodyResourceId = LocalContext.current.runCatching {
            resources.getIdentifier(it.body, "string", packageName)
        }.getOrNull() ?: 0
        if (it.isSimple) {
            SimpleDialog(
                title = stringResource(id = titleResourceId),
                text = if (bodyResourceId != 0) stringResource(id = bodyResourceId) else null,
                canDismissOnTapOutside = it.isDismissible,
                onDismiss = { dismissNotification() },
            )
        } else {
            Notification(
                title = if (titleResourceId != 0) stringResource(id = titleResourceId) else null,
                titleRes = titleResourceId,
                onDismiss = { dismissNotification() },
                notification = it,
            )
        }
    }
}

@Composable
fun stringResourceByName(name: String): String {
    return LocalContext.current.runCatching {
        resources.getIdentifier(name, "string", packageName)
    }.getOrNull()?.let { stringResource(id = it) } ?: name
}

@Composable
fun NavigationCell(
    icon: Painter,
    text: String,
    color: Color = MaterialTheme.colors.onPrimary,
    clickIndication: Indication? = LocalIndication.current,
    label: @Composable (RowScope.() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = clickIndication,
                onClick = onClick,
            )
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon,
            tint = color,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 18.dp)
                .size(22.dp),
        )
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
            color = color,
            modifier = Modifier.padding(start = 24.dp),
        )
        label?.invoke(this)
    }
}

@Composable
fun Space(dp: Dp) {
    Spacer(modifier = Modifier.size(dp))
}

@Composable
fun Separator(modifier: Modifier = Modifier, padding: Dp = 16.dp) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = padding)
            .background(ConstColors.gray.copy(alpha = .5f))
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

@Composable
fun BasicScreen(
    subtitle: String? = null,
    body: @Composable ColumnScope.() -> Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    footer: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!subtitle.isNullOrEmpty()) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(color = Color.White)
            ) {
                it.invoke(this)
            }
        }
    }
}

object NoOpIndication : Indication {

    private class NoOpIndicationInstance : IndicationInstance {

        override fun ContentDrawScope.drawIndication() {
            drawContent()
//            if (interactionState.contains(Interaction.Pressed)) {
//            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return remember(interactionSource) { NoOpIndicationInstance() }
    }
}

@Composable
fun DataWithLabel(label: Int, data: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = "${stringResource(id = label)}:",
            fontSize = 14.sp,
            color = ConstColors.gray,
        )
        Space(4.dp)
        Text(
            text = data,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colors.background,
        )
    }
}

@Composable
fun Dropdown(
    rememberChooseKey: Any?,
    value: String?,
    hint: String,
    dropDownItems: List<String>,
    readOnly: Boolean = false,
    onSelected: (String) -> Unit,
    backgroundColor: Color = Color.White,
    arrowTintColor: Color = ConstColors.gray,
    width: Dp? = null,
) {
    val choosing = remember(rememberChooseKey) { mutableStateOf(false) }
    Box(modifier = width?.let { Modifier.width(it) } ?: Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = backgroundColor)
                .clickable(onClick = {
                    if (dropDownItems.isNotEmpty()) {
                        choosing.value = true
                    }
                })
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = value?.takeIf { it.isNotEmpty() } ?: hint,
                color = Color.Black,
                fontSize = 14.sp,
            )
            if (!readOnly) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = arrowTintColor,
                )
            }
        }
        if (!readOnly) {
            DropdownMenu(
                expanded = choosing.value,
                onDismissRequest = { choosing.value = false },
                content = {
                    dropDownItems.forEach {
                        DropdownMenuItem(
                            onClick = {
                                choosing.value = false
                                onSelected(it)
                            },
                            content = { Text(it) },
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun EditField(
    label: String,
    qty: String,
    onChange: (String) -> Unit,
    isEnabled: Boolean = true,
    formattingRule: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                color = ConstColors.gray,
            )
            Space(16.dp)
            BasicTextField(
                value = qty,
                onValueChange = {
                    if (formattingRule) {
                        if (it.contains(".")) {
                            val (beforeDot, afterDot) = it.split(".")
                            var modBefore = beforeDot.toIntOrNull() ?: 0
                            val newAfter = if (afterDot.length > 1) {
                                afterDot.take(1)
                            } else {
                                afterDot
                            }
                            val modAfter = when (newAfter.toIntOrNull() ?: 0) {
                                0 -> "0"
                                in 1..4 -> "0"
                                5 -> "5"
                                in 6..9 -> {
                                    modBefore++
                                    "0"
                                }
                                else -> throw UnsupportedOperationException("cant be that")
                            }
                            onChange("$modBefore.$modAfter")
                        }
                    } else {
                        onChange(it)
                    }
                },
                keyboardOptions = keyboardOptions,
                maxLines = 1,
                singleLine = true,
                readOnly = !isEnabled,
                textStyle = TextStyle(
                    color = MaterialTheme.colors.background,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700
                )
            )
        }
        Space(4.dp)
        Canvas(
            modifier = Modifier
                .height(1.5.dp)
                .fillMaxWidth()
        ) {
            drawRect(if (isEnabled) ConstColors.lightBlue else ConstColors.gray)
        }
    }
}