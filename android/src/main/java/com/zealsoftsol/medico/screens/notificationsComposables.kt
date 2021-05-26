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
import androidx.compose.ui.window.DialogProperties
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.screens.common.AlertButton
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun Notification(title: String?, onDismiss: () -> Unit, notification: ScopeNotification) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = Color.White,
        title =
        if (title != null) {
            {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h6,
                )
            }
        } else {
            null
        },
        text = {
            when (notification) {
                is ManagementScope.ChoosePaymentMethod -> BodyForChoosePaymentMethod(notification)
                is ManagementScope.Congratulations -> Text(
                    text = String.format(
                        stringResource(id = R.string.retailer_added_template),
                        notification.tradeName
                    ),
                    style = MaterialTheme.typography.subtitle1,
                )
                is CartPreviewScope.OrderWithQuotedItems -> Text(
                    text = stringResource(id = R.string.order_with_quote_body),
                    style = MaterialTheme.typography.subtitle1,
                )
                is CartPreviewScope.OrderModified -> Text(
                    text = stringResource(id = R.string.order_modified_body),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        },
        buttons = {
            when (notification) {
                is ManagementScope.ChoosePaymentMethod -> {
                    val isSendEnabled = notification.isSendEnabled.flow.collectAsState()
                    ButtonsForChoosePaymentMethod(
                        isSendEnabled = isSendEnabled.value,
                        onCancel = onDismiss,
                        onNext = { notification.sendRequest() },
                    )
                }
                is ManagementScope.Congratulations -> Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlertButton(
                        onClick = onDismiss,
                        text = stringResource(id = R.string.retailers_list)
                    )
                }
                is CartPreviewScope.OrderWithQuotedItems -> CartNotificationButtons(
                    onDismiss,
                    onContinue = { notification.placeOrder() })
                is CartPreviewScope.OrderModified -> CartNotificationButtons(
                    onDismiss,
                    onContinue = { notification.placeOrder() })
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = notification.isDismissible,
            dismissOnClickOutside = notification.isDismissible,
        )
    )
}

@Composable
private fun BodyForChoosePaymentMethod(notification: ManagementScope.ChoosePaymentMethod) {
    val paymentMethod = notification.paymentMethod.flow.collectAsState()
    val creditDays = notification.creditDays.flow.collectAsState()
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
        if (paymentMethod.value == PaymentMethod.CREDIT) {
            InputField(
                hint = stringResource(id = R.string.no_of_credit_days),
                text = creditDays.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { notification.changeCreditDays(it) },
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
private fun ButtonsForChoosePaymentMethod(
    isSendEnabled: Boolean,
    onCancel: () -> Unit,
    onNext: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        AlertButton(
            onClick = onCancel,
            text = stringResource(id = R.string.cancel),
        )
        Space(8.dp)
        AlertButton(
            onClick = onNext,
            isEnabled = isSendEnabled,
            text = stringResource(id = R.string.send_request),
        )
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
            text = stringResource(id = R.string.cancel)
        )
        AlertButton(
            onClick = onContinue,
            text = stringResource(id = R.string.continue_)
        )
    }
}