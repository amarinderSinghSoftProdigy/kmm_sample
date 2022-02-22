package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderInvoiceScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun ViewOrderInvoiceScreen(scope: ViewOrderInvoiceScope) {

    if (scope.canEdit)
        remember { scope.updateData() }

    val order = scope.orderTax.flow.collectAsState()
    val b2bData = scope.b2bData.flow.collectAsState()
    val entries = scope.entries.flow.collectAsState()
    val declineReasons = scope.declineReason.flow.collectAsState()
    val checkedEntries = scope.checkedEntries.flow.collectAsState()
    val openDialog = scope.showAlert.flow.collectAsState()
    val openPaymentView = scope.showPaymentTypeOption.flow.collectAsState()
    val openEditDiscountView = scope.showEditDiscountOption.flow.collectAsState()
    val paymentMethod = scope.paymentType.flow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        order.value?.let { orderTaxValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                if (openDialog.value)
                    ShowAlert(stringResource(id = R.string.warning_order_entry_validation)) {
                        scope.changeAlertScope(
                            false
                        )
                    }
                Column {
                    Space(8.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.invoice_no))
                                    append(" ")
                                    val startIndex = length
                                    append(orderTaxValue.info.orderId)
                                    addStyle(
                                        SpanStyle(fontWeight = FontWeight.W600),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)

                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.type))
                                    append(": ")
                                    val startIndex = length
                                    append(order.value?.info?.paymentMethod ?: "")
                                    addStyle(
                                        SpanStyle(
                                            color = ConstColors.green,
                                            fontWeight = FontWeight.W600
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )

                            /*Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.status))
                                    append(": ")
                                    val startIndex = length
                                    append(orderTaxValue.info.orderStatus)
                                    addStyle(
                                        SpanStyle(
                                            color = Color.Black,
                                            fontWeight = FontWeight.W600
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = Color.Black,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                            )*/

                            Space(5.dp)

                            /*Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    if (scope.canEdit) {
                                        scope.showEditDiscountOption(false)
                                        scope.showPaymentOptions(!openPaymentView.value)
                                    }
                                }) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.type))
                                        append(": ")
                                        val startIndex = length
                                        append(paymentMethod.value)
                                        addStyle(
                                            SpanStyle(
                                                color = ConstColors.green,
                                                fontWeight = FontWeight.W600
                                            ),
                                            startIndex,
                                            length,
                                        )
                                    },
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                )
                            }*/
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = orderTaxValue.info.orderDate,
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            Text(
                                text = orderTaxValue.info.orderTime,
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            /*Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    if (scope.canEdit) {
                                        scope.showPaymentOptions(false)
                                        scope.showEditDiscountOption(!openEditDiscountView.value)
                                    }
                                }) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.discount))
                                        append(": ")
                                        val startIndex = length
                                        append(orderTaxValue.info.discount.formatted)
                                        addStyle(
                                            SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.W600
                                            ),
                                            startIndex,
                                            length,
                                        )
                                    },
                                    color = Color.Black,
                                    fontWeight = FontWeight.W600,
                                    fontSize = 16.sp,
                                )
                            }*/
                        }
                    }
                    Space(8.dp)
                    if (openPaymentView.value) {
                        ShowPaymentDropDown(scope = scope)
                    }
                    if (openEditDiscountView.value) {
                        ShowEditDiscountDropDown(scope = scope,
                            onChange = {
                                scope.updateDiscountValue(it)
                            })
                    }
                    Divider(Modifier.padding(horizontal = 16.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        b2bData.value?.let { b2BDataValue ->
                            Space(16.dp)
                            entries.value.forEachIndexed { index, it ->
                                OrderInvoiceEntryItem(
                                    showDetails = true,
                                    canEdit = scope.canEdit,
                                    entry = it,
                                    isChecked = it in checkedEntries.value,
                                    onChecked = { _ -> scope.toggleCheck(it) },
                                    onClick = {
                                        /*scope.selectEntry(
                                            taxType = orderTaxValue.info.taxType!!,
                                            retailerName = b2BDataValue.tradeName,
                                            canEditOrderEntry = scope.canEdit,
                                            declineReason = declineReasons.value,
                                            entry = entries.value,
                                            index = index
                                        )*/
                                    },
                                )
                                Space(8.dp)
                            }
                            Space(8.dp)
                        }
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OrderTotal(orderTaxValue.info.total.formattedPrice)
                    Space(16.dp)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MedicoButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.preview),
                            isEnabled = true,
                            onClick = {
                                scope.openBottomSheet(
                                    order.value?.info,
                                    if (declineReasons.value.isNotEmpty()) {
                                        declineReasons.value[0].code
                                    } else {
                                        ""
                                    }
                                )
                            },
                        )
                    }
                    Space(10.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowPaymentDropDown(
    scope: ViewOrderInvoiceScope
) {
    Surface(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.cash),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable {
                        scope.submitPaymentValue(PaymentMethod.CASH)
                        scope.showPaymentOptions(false)
                    }
            )
            Divider()
            Text(
                text = stringResource(id = R.string.credit),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable {
                        scope.submitPaymentValue(PaymentMethod.CREDIT)
                        scope.showPaymentOptions(false)
                    }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowEditDiscountDropDown(
    scope: ViewOrderInvoiceScope,
    onChange: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val discountValue = scope.discountValue.flow.collectAsState()

    Surface(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
    ) {
        val textStyle = TextStyle(
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Start,
        )

        Column {
            Text(
                text = stringResource(id = R.string.edit_discount),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        value = discountValue.value.toString(),
                        onValueChange = {
                            if (it.toDoubleOrNull() != null && it.length < 6) {
                                if (it.toDouble() <= 100)
                                    onChange(it)
                            } else {
                                onChange("0")
                            }
                        },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        enabled = true,
                        textStyle = textStyle
                    )
                    Divider(modifier = Modifier.padding(start = 16.dp), thickness = 1.dp)
                }

                Box(modifier = Modifier.weight(3f)) {}

                MedicoButton(
                    modifier = Modifier
                        .weight(3f)
                        .height(45.dp)
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp),
                    text = stringResource(id = R.string.save),
                    isEnabled = true,
                    txtColor = Color.Black
                ) {
                    scope.showEditDiscountOption(false)
                    scope.submitDiscountValue()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderInvoiceEntryItem(
    canEdit: Boolean,
    isChecked: Boolean,
    entry: OrderEntry,
    onChecked: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit,
    showDetails: Boolean = false
) {
    Surface(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = when {
            entry.buyingOption == BuyingOption.QUOTE -> BorderStroke(
                1.dp,
                ConstColors.gray.copy(alpha = 0.5f),
            )
            entry.status == OrderEntry.Status.REJECTED || entry.status == OrderEntry.Status.DECLINED -> BorderStroke(
                1.dp,
                ConstColors.red,
            )
            else -> null
        },
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
            ) {
                if (canEdit) {
                    Checkbox(
                        checked = isChecked,
                        colors = CheckboxDefaults.colors(checkedColor = ConstColors.green),
                        onCheckedChange = onChecked,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    Space(8.dp)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(.55f)) {
                        Text(
                            text = entry.productName,
                            color = MaterialTheme.colors.background,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.ptr))
                                append(": ")
                                val startIndex = length
                                append(entry.price.formatted)
                                val nextIndex = length
                                addStyle(
                                    SpanStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.W500
                                    ),
                                    startIndex,
                                    length,
                                )
                                append("*")
                                addStyle(
                                    SpanStyle(
                                        color = ConstColors.lightBlue,
                                        fontWeight = FontWeight.W500
                                    ),
                                    nextIndex,
                                    length,
                                )
                            },
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.gst))
                                append(": ")
                                val startIndex = length
                                append(entry.sgstTax.percent.formatted)
                                val nextIndex = length
                                addStyle(
                                    SpanStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.W500
                                    ),
                                    startIndex,
                                    length,
                                )
                                addStyle(
                                    SpanStyle(
                                        color = ConstColors.lightBlue,
                                        fontWeight = FontWeight.W500
                                    ),
                                    nextIndex,
                                    length,
                                )
                            },
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )


                    }
                    Column(
                        modifier = Modifier
                            .weight(.45f)
                            .padding(end = 10.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.qty))
                                append(": ")
                                val startIndex = length
                                append(entry.servedQty.formatted)
                                addStyle(
                                    SpanStyle(
                                        color = ConstColors.green,
                                        fontWeight = FontWeight.W500
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        when (entry.buyingOption) {
                            BuyingOption.BUY -> {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.subtotal))
                                        append(": ")
                                        val startIndex = length
                                        append(entry.totalAmount.formatted)
                                        addStyle(
                                            SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.W500
                                            ),
                                            startIndex,
                                            length,
                                        )
                                    },
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            BuyingOption.QUOTE -> {
                                Text(
                                    text = stringResource(id = R.string.quoted),
                                    color = MaterialTheme.colors.background,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Space(8.dp)
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.discount))
                                append(": ")
                                val startIndex = length
                                append(entry.discount.formatted)
                                addStyle(
                                    SpanStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.W500
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (canEdit) {
                        if (showDetails) {
                            Image(
                                modifier = Modifier
                                    .weight(.05f),
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = null
                            )
                        }
                    }
                }

            }
            if (canEdit) {
                if (checkOrderEntryValidation(entry)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(8.dp), onDraw = {
                                drawCircle(color = Color.Red)
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                            text = stringResource(id = R.string.please_add),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }

                }
            }
        }

    }
}