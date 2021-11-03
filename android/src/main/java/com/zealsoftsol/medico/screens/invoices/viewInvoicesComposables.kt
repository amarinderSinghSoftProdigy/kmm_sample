package com.zealsoftsol.medico.screens.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.GeoLocation

@Composable
fun ViewInvoiceScreen(scope: ViewInvoiceScope) {
    val taxInfo = scope.taxInfo.flow.collectAsState()
    val b2bData = scope.b2bData.flow.collectAsState()
    val entries = scope.entries.flow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Space(10.dp)
            FoldableItem(
                expanded = false,
                header = { isExpanded ->
                    Space(12.dp)
                    Row(
                        modifier = Modifier.weight(.8f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_retailer),
                            contentDescription = null,
                            tint = ConstColors.lightBlue,
                        )
                        Space(8.dp)
                        Text(
                            text = b2bData.value.tradeName,
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.W700,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(.2f)
                            .padding(end = 12.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            tint = ConstColors.gray,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                childItems = listOf(""),
                item = { _, _ ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = b2bData.value.addressData.landmark,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W500,
                                fontSize = 12.sp,
                            )
                            Space(8.dp)
                            GeoLocation(
                                location = b2bData.value.addressData.fullAddress(),
                                textSize = 12.sp,
                                tint = MaterialTheme.colors.background
                            )
                        }
                        Space(4.dp)
                        Column {
                            Text(
                                text = b2bData.value.gstin,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                            )
                            Space(8.dp)
                            Text(
                                text = b2bData.value.panNumber,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            )
            Space(16.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.invoice_no))
                            append(" ")
                            val startIndex = length
                            append(taxInfo.value.b2bUnitInvoiceId)
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W700),
                                startIndex,
                                length,
                            )
                        },
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 15.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.type))
                            append(": ")
                            val startIndex = length
                            append(taxInfo.value.paymentMethod.serverValue)
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W700
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 15.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = taxInfo.value.invoiceDate,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = taxInfo.value.invoiceTime,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                }
            }
            Space(8.dp)
            Divider()
            Column {
                Space(8.dp)
                entries.value.forEach {
                    InvoiceEntryItem(it) { scope.viewInvoice(it) }
                }
                Space(8.dp)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(ConstColors.lightBlue, MaterialTheme.shapes.medium)
                .clickable { scope.viewTaxInfo() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.net_payable),
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                )
                Text(
                    text = taxInfo.value.netAmount.formatted,
                    color = Color.White,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                )
            }
            Space(2.dp)
            Text(
                text = taxInfo.value.amountInWords,
                color = Color.White,
                fontWeight = FontWeight.W600,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End),
            )
            Space(4.dp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InvoiceEntryItem(entry: InvoiceEntry, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(.6f)) {
                Text(
                    text = entry.productName,
                    color = MaterialTheme.colors.background,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(4.dp)
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.price))
                        append(": ")
                        val startIndex = length
                        append(entry.price.formatted)
                        val nextIndex = length
                        addStyle(
                            SpanStyle(
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W600
                            ),
                            startIndex,
                            length,
                        )
                        append("*")
                        addStyle(
                            SpanStyle(
                                color = ConstColors.lightBlue,
                                fontWeight = FontWeight.W600
                            ),
                            nextIndex,
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
            Column(
                modifier = Modifier.weight(.4f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.qty))
                        append(": ")
                        val startIndex = length
                        append(entry.quantity.formatted)
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
                Space(4.dp)
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.subtotal))
                        append(": ")
                        val startIndex = length
                        append(entry.totalAmount.formatted)
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
}