package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.EditFieldCustom
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

/**
 * @param scope current scope to get the current and updated state of views
 * show the view to update phone number and language for user
 */

@Composable
fun OrderHsnEditScreen(scope: OrderHsnEditScope) {
    val canEdit = true//scope.canEdit

    val hsn = scope.hsnCode.flow.collectAsState()
    val qty = scope.quantity.flow.collectAsState()
    val freeQty = scope.freeQuantity.flow.collectAsState()
    val ptr = scope.ptr.flow.collectAsState()
    val batch = scope.batch.flow.collectAsState()
    val expiry = scope.expiry.flow.collectAsState()
    val isChecked = scope.isChecked.flow.collectAsState()



    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ShowAlert(scope)
        
        Space(12.dp)
        Column {
            Row(modifier = Modifier.padding(end = 30.dp)) {
                Column {
                    Text(
                        text = scope.orderEntry.productName,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(8.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.batch_no))
                            append(": ")
                            val startIndex = length
                            append(batch.value)
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2 - 8.dp)
                            ) {

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
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2 - 8.dp)
                            ) {
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

                            }
                        }
                    }
                    Space(8.dp)
                    Text(
                        text = "${stringResource(id = R.string.expiry)} ${expiry.value}",
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
                }
            }
            Space(20.dp)
            Divider()
            Space(8.dp)
            Column {
                Text(
                    text = stringResource(id = R.string.hsn_code),
                    color = ConstColors.gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(8.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                        ) {
                            /*BasicTextField(
                                value = hsn.value,
                                onValueChange = { scope.updateBatch(it) })*/
                            EditFieldCustom(
                                label = "",
                                qty = hsn.value,
                                onChange = { scope.updateHsnCode(it) },
                                isEnabled = false,
                                formattingRule = false,
                                keyboardOptions = KeyboardOptions.Default,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.CenterEnd)
                        ) {
                            OpenHsnScreen { scope.selectEntry() }
                        }
                    }
                }
                Space(8.dp)
                HsnErrorText(hsn.value.isEmpty())
                Space(8.dp)
            }

            Space(20.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(maxWidth)
                            .align(Alignment.BottomEnd)
                    ) {
                        EditField(
                            label = stringResource(id = R.string.batch_no),
                            qty = batch.value,
                            onChange = { scope.updateBatch(it) },
                            isEnabled = canEdit,
                            formattingRule = false,
                            keyboardOptions = KeyboardOptions.Default,
                        )
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
                                label = stringResource(id = R.string.mrp),
                                qty = scope.orderEntry.price.value.toString(),
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
                                label = stringResource(id = R.string.expiry_),
                                qty = expiry.value,
                                onChange = { scope.updateExpiry(it) },
                                isEnabled = canEdit,
                                formattingRule = false,
                                keyboardOptions = KeyboardOptions.Default,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            EditField(
                                label = stringResource(id = R.string.discount),
                                qty = freeQty.value.toString(),
                                onChange = { scope.updateFreeQuantity(it.toDouble()) },
                                isEnabled = canEdit,
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpenHsnScreen(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(
            2.dp,
            ConstColors.gray.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.search_hsn_code),
                color = ConstColors.gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = ConstColors.gray,
                modifier = Modifier.align(CenterVertically),
            )
        }
    }
}

@Composable
fun HsnErrorText(isVisible: Boolean) {
    if (isVisible)
        Text(
            text = stringResource(id = R.string.hsn_code_error),
            color = ConstColors.red,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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