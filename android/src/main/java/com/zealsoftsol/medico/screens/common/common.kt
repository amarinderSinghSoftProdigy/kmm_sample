package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithNotifications
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.screens.Notification
import com.zealsoftsol.medico.utils.PhoneNumberFormatter
import kotlinx.coroutines.Deferred

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
    color: Color = ConstColors.yellow,
    contentColor: Color = MaterialTheme.colors.onPrimary,
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
        enabled = isEnabled,
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.elevation(),
        modifier = modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun MedicoSmallButton(
    modifier: Modifier = Modifier,
    widthModifier: Modifier.() -> Modifier = { wrapContentWidth(unbounded = true) },
    text: String,
    isEnabled: Boolean = true,
    enabledColor: Color = ConstColors.yellow,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = enabledColor,
            disabledBackgroundColor = Color.LightGray,
            contentColor = MaterialTheme.colors.onPrimary,
            disabledContentColor = MaterialTheme.colors.onPrimary,
        ),
        enabled = isEnabled,
        shape = RoundedCornerShape(5.dp),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        modifier = modifier.widthModifier().height(32.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
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
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = ConstColors.lightBlue),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
    ) {
        Text(
            text = text.toUpperCase(),
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
        }.getOrNull() ?: 0
        val bodyResourceId = LocalContext.current.runCatching {
            resources.getIdentifier(it.body, "string", packageName)
        }.getOrNull() ?: 0
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
                title = stringResource(id = titleResourceId),
                onDismiss = { dismissNotification() },
                notification = it,
            )
        }
    }
}

@Composable
fun stringResourceByName(name: String): String {
    return stringResource(id = LocalContext.current.runCatching {
        resources.getIdentifier(name, "string", packageName)
    }.getOrNull() ?: 0)
}

@Composable
fun rememberPhoneNumberFormatter() = getCountryCode().let {
    remember { PhoneNumberFormatter(it) }
}

@Composable
private fun getCountryCode(): String {
    return when {
        BuildConfig.FLAVOR == "dev" && BuildConfig.DEBUG && BuildConfig.ANDROID_DEV -> "RU" // devDebug
        BuildConfig.FLAVOR == "prod" && !BuildConfig.DEBUG -> "IN" // prodRelease
        else -> LocalConfiguration.current.locale.country
    }
}

@Composable
fun NavigationCell(
    icon: Painter,
    text: String,
    color: Color = MaterialTheme.colors.onPrimary,
    clickIndication: Indication? = LocalIndication.current,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.padding(start = 18.dp).size(22.dp),
        )
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
            color = color,
            modifier = Modifier.padding(start = 32.dp),
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

@Composable
fun BasicScreen(
    subtitle: String? = null,
    body: @Composable ColumnScope.() -> Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    footer: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center).padding(16.dp),
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
    modifier: Modifier = Modifier,
    rememberChooseKey: Any?,
    value: String,
    dropDownItems: List<String>,
    onSelected: (String) -> Unit,
) {
    val choosing = remember(rememberChooseKey) { mutableStateOf(false) }
    Row(
        modifier = modifier
            .background(color = Color.White)
            .clickable(onClick = {
                if (dropDownItems.isNotEmpty()) {
                    choosing.value = true
                }
            })
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = value,
            color = Color.Black,
            fontSize = 14.sp,
        )
        Icon(
            modifier = Modifier.align(Alignment.CenterVertically),
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = ConstColors.gray,
        )
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