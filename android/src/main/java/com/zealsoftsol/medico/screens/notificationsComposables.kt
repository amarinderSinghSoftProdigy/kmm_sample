package com.zealsoftsol.medico.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.screens.common.AlertButton
import com.zealsoftsol.medico.screens.common.InputField
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
                val string = when (notification) {
                    is ManagementScope.ChoosePaymentMethod -> stringResource(
                        id = titleRes,
                        notification.tradeName
                    )
                    else -> title
                }
                Column {
                    Text(
                        text = string,
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.h6,
                    )
                    Space(dp = 16.dp)
                }
            }
        } else {
            null
        },
        text = {
            when (notification) {
                is ManagementScope.ChoosePaymentMethod -> BodyForChoosePaymentMethod(notification)
                is ManagementScope.Congratulations -> Text(
                    text = stringResource(
                        id = R.string.retailer_added_template,
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
                is ViewOrderScope.ServeQuotedProduct -> Text(
                    text = stringResource(id = R.string.serve_quoted),
                    style = MaterialTheme.typography.subtitle1,
                )
                is ViewOrderScope.RejectAll -> Text(
                    text = stringResource(id = R.string.sure_reject_all),
                    style = MaterialTheme.typography.subtitle1,
                )
                is ConfirmOrderScope.AreYouSure -> Text(
                    text = stringResource(id = R.string.sure_confirm_order),
                    style = MaterialTheme.typography.subtitle1,
                )
                is ViewInvoiceScope.InvoiceDownloading -> Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ConstColors.yellow)
                }
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
                    onContinue = { notification.placeOrder() }
                )
                is CartPreviewScope.OrderModified -> CartNotificationButtons(
                    onDismiss,
                    onContinue = { notification.placeOrder() }
                )
                is ViewOrderScope.ServeQuotedProduct -> CartNotificationButtons(
                    onDismiss,
                    onContinue = { notification.`continue`() }
                )
                is ViewOrderScope.RejectAll -> CartNotificationButtons(
                    onDismiss,
                    onContinue = { notification.`continue`() }
                )
                is ConfirmOrderScope.AreYouSure -> CartNotificationButtons(onDismiss) {
                    notification.confirm()
                }
                is ViewInvoiceScope.InvoiceDownloading -> Unit
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
private fun BodyForChoosePaymentMethod(notification: ManagementScope.ChoosePaymentMethod) {
    val paymentMethod = notification.paymentMethod.flow.collectAsState()
    val creditDays = notification.creditDays.flow.collectAsState()
    notification.log("nptification")
    Row(
        modifier = Modifier
            .height(150.dp)
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = { notification.changePaymentMethod(PaymentMethod.CASH) },
            elevation = 4.dp,
            shape = RoundedCornerShape(5.dp),
            color = Color.White,
            modifier = Modifier.weight(0.5f),
            border = BorderStroke(
                1.dp,
                color = if (paymentMethod.value == PaymentMethod.CASH) ConstColors.yellow else Color.White
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                /* RadioButton(
             selected = paymentMethod.value == PaymentMethod.CASH,
             onClick = { notification.changePaymentMethod(PaymentMethod.CASH) },
             colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
         )*/

                Surface(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(5.dp),
                    color = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                ) {
                    Row(modifier = Modifier.padding(15.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                Space(4.dp)
                Text(
                    text = stringResource(id = R.string.cash),
                    color = Color.Black,
                )
            }
        }
        Space(dp = 16.dp)
        Surface(
            modifier = Modifier.weight(0.5f),
            onClick = { notification.changePaymentMethod(PaymentMethod.CREDIT) },
            elevation = 4.dp,
            shape = RoundedCornerShape(5.dp),
            color = Color.White,
            border = BorderStroke(
                1.dp,
                color = if (paymentMethod.value == PaymentMethod.CREDIT) ConstColors.yellow else Color.White
            ),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    /*RadioButton(
                selected = paymentMethod.value == PaymentMethod.CREDIT,
                onClick = { notification.changePaymentMethod(PaymentMethod.CREDIT) },
                colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
            )*/

                    Surface(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(5.dp),
                        color = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                    ) {
                        Row(modifier = Modifier.padding(15.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = null,
                                tint = ConstColors.gray,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                    Space(4.dp)
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
            }
        }
    }
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
            color = ConstColors.yellow, onClick = onNext
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