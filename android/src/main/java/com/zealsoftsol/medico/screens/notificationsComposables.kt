package com.zealsoftsol.medico.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AndroidDialogProperties
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PreviewUserScope
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.screens.common.AlertButton
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun Notification(title: String, onDismiss: () -> Unit, notification: ScopeNotification) {
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
            when (notification) {
                is ManagementScope.ChoosePaymentMethod -> BodyForChoosePaymentMethod(notification)
                is ManagementScope.ChooseNumberOfDays -> BodyForChooseNumberOfDays(notification)
                is PreviewUserScope.Congratulations -> Text(
                    text = String.format(
                        stringResource(id = R.string.retailer_added_template),
                        notification.tradeName
                    ),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        },
        buttons = {
            when (notification) {
                is ManagementScope.ChoosePaymentMethod -> ButtonsForChoosePaymentMethod(
                    onCancel = onDismiss,
                    onNext = { notification.sendRequest() },
                )
                is ManagementScope.ChooseNumberOfDays -> Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlertButton(
                        onClick = { notification.save() },
                        text = stringResource(id = R.string.save),
                    )
                }
                is PreviewUserScope.Congratulations -> Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlertButton(
                        onClick = onDismiss,
                        text = stringResource(id = R.string.retailers_list)
                    )
                }
            }
        },
        properties = AndroidDialogProperties(
            dismissOnBackPress = notification.isDismissible,
            dismissOnClickOutside = notification.isDismissible,
        )
    )
}

@Composable
private fun BodyForChoosePaymentMethod(notification: ManagementScope.ChoosePaymentMethod) {
    val paymentMethod = notification.paymentMethod.flow.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = paymentMethod.value == PaymentMethod.CREDIT,
                onClick = { notification.changePaymentMethod(PaymentMethod.CREDIT) },
                colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
            )
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.credit),
                color = Color.Black,
            )
        }
        Space(16.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = paymentMethod.value == PaymentMethod.CASH,
                onClick = { notification.changePaymentMethod(PaymentMethod.CASH) },
                colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
            )
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.cash),
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun BodyForChooseNumberOfDays(notification: ManagementScope.ChooseNumberOfDays) {
    val days = notification.days.flow.collectAsState()
    InputField(
        hint = "",
        text = days.value.toString().log("days"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { newValue ->
            newValue.toIntOrNull()?.let {
                notification.changeDays(it)
            }
        },
    )
}

@Composable
private fun ButtonsForChoosePaymentMethod(onCancel: () -> Unit, onNext: () -> Unit) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        AlertButton(
            onClick = onCancel,
            text = stringResource(id = R.string.cancel),
        )
        Space(8.dp)
        AlertButton(
            onClick = onNext,
            text = stringResource(id = R.string.send_request),
        )
    }
}