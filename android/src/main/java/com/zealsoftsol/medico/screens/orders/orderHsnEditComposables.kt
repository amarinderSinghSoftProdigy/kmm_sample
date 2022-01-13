package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    val orderEntry = scope.orderEntry.flow.collectAsState().value
    val selectedIndex = scope.selectedIndex.flow.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ShowAlert(scope)
            //only display line items when there are multiple order entries
            if (scope.orderEntries.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (selectedIndex > 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_frwd_circle),
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(180f)
                                .clickable {
                                    scope.updateSelectedIndex(selectedIndex - 1)
                                }
                        )
                    } else {
                        Box {}
                    }

                    Text(
                        text = "${stringResource(id = R.string.line_item)}${selectedIndex + 1}",
                        color = ConstColors.green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600
                    )

                    if (selectedIndex < scope.orderEntries.size - 1) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_frwd_circle),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                scope.updateSelectedIndex(selectedIndex + 1)
                            }
                        )
                    } else {
                        Box {}
                    }
                }
            }
            Space(16.dp)
            Column {
                Text(
                    text = orderEntry.productName,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(10.dp)
                Text(
                    text = orderEntry.manufacturerName,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    Row(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Space(10.dp)
                            Text(
                                text = stringResource(id = R.string.hsn_code),
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = stringResource(id = R.string.batch_no),
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.price))
                                    append(":")
                                },
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = stringResource(id = R.string.expiry),
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)

                            Text(
                                text = stringResource(id = R.string.requested_qty),
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Space(10.dp)
                            Text(
                                text = orderEntry.hsnCode,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = orderEntry.batchNo,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = orderEntry.price.formatted,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            orderEntry.expiryDate?.formatted?.let {
                                Text(
                                    text = it,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.W700
                                )
                            }
                            Space(10.dp)
                            Text(
                                text = orderEntry.requestedQty.formatted,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Space(10.dp)
                            Text(
                                text = "",
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = "",
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.mrp))
                                    append(":")
                                },
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.free))
                                    append(":")
                                },
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.discount))
                                    append("%:")
                                },
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Space(10.dp)
                            Text(
                                text = "",
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = "",
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = orderEntry.mrp.formatted,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = orderEntry.freeQty.formatted,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                            Space(10.dp)
                            Text(
                                text = orderEntry.freeQty.formatted,
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                }
                Space(20.dp)
                Divider()
                Space(10.dp)
                Column {
                    Text(
                        text = stringResource(id = R.string.hsn_code),
                        color = ConstColors.txtGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(10.dp)
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
                                    qty = orderEntry.hsnCode,
                                    onChange = {

                                    },
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
                    Space(10.dp)
                    HsnErrorText(orderEntry.hsnCode.isEmpty())
                    Space(10.dp)
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
                                qty = orderEntry.batchNo,
                                onChange = {

                                },
                                isEnabled = canEdit,
                                formattingRule = false,
                                keyboardOptions = KeyboardOptions.Default,
                            )
                        }
                    }
                    Space(10.dp)
                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.ptr),
                                    qty = orderEntry.price.formatted,
                                    onChange = { },
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
                                    qty = orderEntry.price.value.toString(),
                                    onChange = {

                                    },
                                    isEnabled = canEdit,
                                )
                            }
                        }
                    }
                    Space(10.dp)
                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.qty),
                                    qty = orderEntry.servedQty.formatted,
                                    onChange = {

                                    },
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
                                    qty = orderEntry.freeQty.formatted,
                                    onChange = {

                                    },
                                    isEnabled = canEdit,
                                )
                            }
                        }
                    }
                    Space(10.dp)

                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                orderEntry.expiryDate?.formatted?.let {
                                    EditField(
                                        label = stringResource(id = R.string.expiry_),
                                        qty = it,
                                        onChange = {

                                        },
                                        isEnabled = canEdit,
                                        formattingRule = false,
                                        keyboardOptions = KeyboardOptions.Default,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2 - 8.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                EditField(
                                    label = stringResource(id = R.string.discount),
                                    qty = orderEntry.freeQty.formatted,
                                    onChange = {

                                    },
                                    isEnabled = canEdit,
                                )
                            }
                        }
                    }
                }
                Space(20.dp)
                Divider()
                Space(10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${stringResource(id = R.string.subtotal)}: ${orderEntry.totalAmount.formatted}",
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
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpenHsnScreen(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(40.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(
            1.dp,
            ConstColors.green,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.search_hsn_code),
                color = ConstColors.txtGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
