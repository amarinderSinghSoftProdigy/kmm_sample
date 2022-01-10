package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

/**
 * @param scope current scope to get the current and updated state of views
 * show the view to update phone number and language for user
 */

@Preview
@Composable
fun OrderHsnEditScreen(scope: OrderHsnEditScope) {
    val canEdit = false//scope.canEdit

    val qty = scope.quantity.flow.collectAsState()
    val freeQty = scope.freeQuantity.flow.collectAsState()
    val ptr = scope.ptr.flow.collectAsState()
    val batch = scope.batch.flow.collectAsState()
    val expiry = scope.expiry.flow.collectAsState()
    val isChecked = scope.isChecked.flow.collectAsState()



    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ShowAlert(scope)
        Space(20.dp)

        Space(12.dp)
        Column {
            Row(modifier = Modifier.padding(end = 30.dp)) {
                Column {
                    Text(
                        text = scope.orderEntry.productName,
                        color = MaterialTheme.colors.background,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(8.dp)
                    Text(
                        text = "${stringResource(id = R.string.batch_no)} ${batch.value}",
                        color = MaterialTheme.colors.background,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(8.dp)
                    Text(
                        text = "${stringResource(id = R.string.expiry)} ${expiry.value}",
                        color = MaterialTheme.colors.background,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(8.dp)
                    Row {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.price))
                                append(": ")
                                val startIndex = length
                                append(scope.orderEntry.price.formatted)
                                addStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colors.background,
                                        fontWeight = FontWeight.W600
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.requested_qty))
                                append(" ")
                                val startIndex = length
                                append(scope.orderEntry.requestedQty.formatted)
                                addStyle(
                                    SpanStyle(
                                        color = ConstColors.lightBlue,
                                        fontWeight = FontWeight.W600
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Space(20.dp)
            Divider()
            Space(20.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                            EditField(
                                label = stringResource(id = R.string.qty),
                                qty = qty.value.toString(),
                                onChange = { scope.updateQuantity(it.toDouble()) },
                                isEnabled = canEdit,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            EditField(
                                label = stringResource(id = R.string.free),
                                qty = freeQty.value.toString(),
                                onChange = { scope.updateFreeQuantity(it.toDouble()) },
                                isEnabled = canEdit,
                            )
                        }
                    }
                }
                Space(8.dp)
                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                            EditField(
                                label = stringResource(id = R.string.ptr),
                                qty = ptr.value,
                                onChange = { scope.updatePtr(it) },
                                isEnabled = canEdit,
                                formattingRule = false,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            EditField(
                                label = stringResource(id = R.string.batch),
                                qty = batch.value,
                                onChange = { scope.updateBatch(it) },
                                isEnabled = canEdit,
                                formattingRule = false,
                                keyboardOptions = KeyboardOptions.Default,
                            )
                        }
                    }
                }
                Space(8.dp)
                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                            EditField(
                                label = stringResource(id = R.string.expiry_),
                                qty = expiry.value,
                                onChange = { scope.updateExpiry(it) },
                                isEnabled = canEdit,
                                formattingRule = false,
                                keyboardOptions = KeyboardOptions.Default,
                            )
                        }
                    }
                }
            }
            Space(20.dp)
            Divider()
            Space(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${stringResource(id = R.string.subtotal)}: ${scope.orderEntry.totalAmount.formatted}",
                    color = MaterialTheme.colors.background,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Space(20.dp)
        MedicoButton(
            text = stringResource(id = R.string.save),
            onClick = { scope.submit() },
            isEnabled = true //hsnCode.value.isNotEmpty() , // submit only when hsn code is added
        )
    }
}


/**
 * @param scope current scope to get the current and updated state of views
 * open language picker for user to select language
 */

@Composable
fun ShowAlert(scope: OrderHsnEditScope) {
    MaterialTheme {
        val openDialog = scope.showAlert.flow.collectAsState()

        if (openDialog.value) {

            AlertDialog(
                onDismissRequest = {
                    scope.changeAlertScope(false)
                },
                text = {
                    Text(stringResource(id = R.string.update_successfull))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.changeAlertScope(false)
                        }) {
                        Text(stringResource(id = R.string.okay))
                    }
                }
            )
        }

    }
}