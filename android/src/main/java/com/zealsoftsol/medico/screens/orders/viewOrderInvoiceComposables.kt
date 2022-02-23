package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderInvoiceScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun ViewOrderInvoiceScreen(scope: ViewOrderInvoiceScope) {

    val order = scope.orderTax.flow.collectAsState()
    val b2bData = scope.b2bData.flow.collectAsState()
    val entries = scope.entries.flow.collectAsState()
    val declineReasons = scope.declineReason.flow.collectAsState()
    val openDialog = scope.showAlert.flow.collectAsState()
    val selectedId = scope.selectedId.flow.collectAsState()

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
                            Space(5.dp)
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
                        }
                    }
                    Space(8.dp)

                    Divider(Modifier.padding(horizontal = 16.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        b2bData.value?.let { _ ->
                            Space(16.dp)
                            entries.value.forEachIndexed { _, it ->
                                OrderInvoiceEntryItem(
                                    selectedId = selectedId.value,
                                    entry = it,
                                    onClick = {
                                        scope.changeSelectedItem(it.id)
                                        scope.openBottomSheet(
                                            orderDetails = it,
                                            orderTaxDetails = order.value?.info,
                                            if (declineReasons.value.isNotEmpty()) {
                                                declineReasons.value[0].code
                                            } else {
                                                ""
                                            },
                                            scope
                                        )
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
                            text = stringResource(R.string.confirm),
                            isEnabled = true,
                            onClick = {
                                scope.confirm(
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
private fun OrderInvoiceEntryItem(
    entry: OrderEntry,
    onClick: () -> Unit,
    selectedId: String = ""
) {
    Surface(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = when (selectedId) {
            entry.id -> BorderStroke(
                1.dp,
                ConstColors.gray,
            )
            else -> null
        },
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
            ) {
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
}