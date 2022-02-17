package com.zealsoftsol.medico.screens.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithNotifications
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.screens.Notification
import kotlinx.coroutines.Deferred
import java.io.File
import java.util.Locale

@Composable
fun TabBar(
    color: Color = Color.White,
    isNewDesign: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (!isNewDesign) color else ConstColors.newDesignGray),
            contentAlignment = Alignment.CenterStart,
        ) {
            content()
        }
        Divider(color = ConstColors.lightBlue.copy(alpha = 0.5f), thickness = (0.7).dp)
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
    txtColor: Color = MaterialTheme.colors.background,
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
            color = txtColor
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
fun DataWithLabel(label: Int, data: String, modifier: Modifier = Modifier, size: TextUnit) {
    Row(modifier = modifier) {
        Text(
            text = "${stringResource(id = label)}:",
            fontSize = size,
            color = ConstColors.gray,
        )
        Space(4.dp)
        Text(
            text = data,
            fontSize = size,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colors.background,
        )
    }
}

@Composable
fun SingleTextLabel(
    data: String,
    color: Color = MaterialTheme.colors.background
) {
    Text(
        text = data,
        fontSize = 12.sp,
        fontWeight = FontWeight.W600,
        color = color,
    )
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
    onFocus: (() -> Unit)? = null,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    formattingRule: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    showThinDivider: Boolean = false,
    textStyle: TextStyle? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val color = when {
        isError -> ConstColors.red
        isEnabled -> MaterialTheme.colors.background
        else -> ConstColors.gray.copy(alpha = 0.8f)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                color = ConstColors.gray,
            )
            Space(16.dp)
            val wasQty = remember {
                mutableStateOf(
                    if (qty.split(".").lastOrNull() == "0") qty.split(".").first() else qty
                )
            }

            val style = textStyle ?: TextStyle(
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.W700,
                textAlign = TextAlign.End,
            )

            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent { if (it.isFocused) onFocus?.invoke() },
                value = TextFieldValue(wasQty.value, selection = TextRange(wasQty.value.length)),
                onValueChange = {
                    if (formattingRule) {
                        val split = it.text.replace(",", ".").split(".")
                        val beforeDot = split[0]
                        val afterDot = split.getOrNull(1)
                        var modBefore = beforeDot.toIntOrNull() ?: 0
                        val modAfter = when (afterDot?.length) {
                            0 -> "."
                            in 1..Int.MAX_VALUE -> when (afterDot!!.take(1).toIntOrNull()) {
                                0 -> ".0"
                                in 1..4 -> ".0"
                                5 -> ".5"
                                in 6..9 -> {
                                    modBefore++
                                    ".0"
                                }
                                null -> ""
                                else -> throw UnsupportedOperationException("cant be that")
                            }
                            null -> ""
                            else -> throw UnsupportedOperationException("cant be that")
                        }
                        wasQty.value = "$modBefore$modAfter"
                        onChange("$modBefore$modAfter")
                    } else {
                        onChange(it.text)
                    }
                },
                keyboardOptions = keyboardOptions,
                maxLines = 1,
                singleLine = true,
                readOnly = !isEnabled,
                enabled = isEnabled,
                textStyle = style
            )
        }
        Space(4.dp)
        if (showThinDivider) {
            Divider()
        } else {
            Canvas(
                modifier = Modifier
                    .height((1.5).dp)
                    .fillMaxWidth()
            ) {
                drawRect(color)
            }
        }
    }
}

@Composable
fun TextLabel(
    value: String,
    src: Int = 0
) {
    if (value.isNotEmpty())
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (src != 0) {
                    Image(
                        painter = painterResource(src),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Space(dp = 8.dp)
                }

                val textValue = if (src == R.drawable.ic_verify_password) {
                    var check = ""
                    repeat(value.length) {
                        check = "$check*"
                    }
                    check
                } else {
                    value
                }

                Text(
                    text = textValue,
                    fontSize = 14.sp,
                    color = ConstColors.gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(0.8f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_verified),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
            Space(4.dp)
            Divider(thickness = 0.5.dp)
            Space(16.dp)
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageLabel(
    value: String,
    onClick: () -> Unit
) {
    if (value.isNotEmpty())
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(onClick = onClick, shape = MaterialTheme.shapes.large) {
                    val cacheFile = File(value)
                    CoilImage(
                        src = cacheFile,
                        modifier = Modifier
                            .width(130.dp)
                            .height(80.dp),
                        onError = { Placeholder(R.drawable.ic_img_placeholder) },
                        onLoading = { Placeholder(R.drawable.ic_img_placeholder) },
                        isCrossFadeEnabled = false
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_verified),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
            Space(16.dp)
        }
}

/**
 * @param scope current scope to get the current and updated state of views
 */

@Composable
fun ShowAlert(message: String, onClick: () -> Unit) {
    MaterialTheme {

        AlertDialog(
            onDismissRequest = onClick,
            text = {
                Text(message)
            },
            confirmButton = {
                Button(
                    onClick = onClick
                ) {
                    Text(stringResource(id = R.string.okay))
                }
            }
        )
    }
}

@Composable
fun ShowToastGlobal(msg: String) {
    val context = LocalContext.current
    Column(
        content = {
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
}

fun checkIfPermissionGranted(context: Context, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED)
}

fun shouldShowPermissionRationale(context: Context, permission: String): Boolean {
    val activity = context as Activity?
    if (activity == null)
        Log.d("Permission", "Activity is null")

    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity!!,
        permission
    )
}

