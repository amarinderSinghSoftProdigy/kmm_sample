package com.zealsoftsol.medico.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.screens.common.AlertButton
import com.zealsoftsol.medico.screens.common.Space

@ExperimentalMaterialApi
@Composable
fun Notification(
    title: String?,
    titleRes: Int,
    onDismiss: () -> Unit,
    notification: ScopeNotification,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = Color.White,
        title =
        if (title != null) {
            {
                /*val string = when (notification) {
                    is ManagementScope.ChoosePaymentMethod -> ""
                    else -> title
                }*/
                Text(
                    text = title,
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h6,
                    fontSize = 16.sp
                )
            }
        } else {
            null
        },
        text = {
            when (notification) {

            }
        },
        buttons = {
            when (notification) {

            }
        },
        properties = DialogProperties(
            dismissOnBackPress = notification.isDismissible,
            dismissOnClickOutside = notification.isDismissible,
        )
    )
}


@ExperimentalMaterialApi
@Composable
private fun ButtonsForChoosePaymentMethod(
    isSendEnabled: Boolean,
    onCancel: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.weight(0.5f),
            shape = RoundedCornerShape(5.dp),
            color = ConstColors.ltgray,
            onClick = onCancel
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colors.background,
                )
            }
            /*AlertButton(
                onClick = onCancel,
                text = stringResource(id = R.string.cancel),
            )*/
        }
        Space(16.dp)
        Surface(
            modifier = Modifier.weight(0.5f),
            shape = RoundedCornerShape(5.dp), enabled = isSendEnabled,
            color = if (isSendEnabled) ConstColors.yellow else ConstColors.yellow.copy(alpha = 0.5f),
            onClick = onNext
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    color = MaterialTheme.colors.background,
                )
            }

        }
        /*AlertButton(
            onClick = onNext,
            isEnabled = isSendEnabled,
            text = stringResource(id = R.string.send_request),
        )*/
    }
}

@Composable
private fun CartNotificationButtons(onCancel: () -> Unit, onContinue: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        AlertButton(
            onClick = onCancel,
            text = stringResource(id = R.string.no)
        )
        AlertButton(
            onClick = onContinue,
            text = stringResource(id = R.string.yes)
        )
    }
}