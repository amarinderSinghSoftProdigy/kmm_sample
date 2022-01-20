package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.GeoLocation

@Composable
fun ViewOrderScreen(scope: ViewOrderScope) {
    val order = scope.order.flow.collectAsState()
    val b2bData = scope.b2bData.flow.collectAsState()
    val activity = LocalContext.current as MainActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
            ) {
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
                                text = order.value.tradeName,
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
                    childItems = listOf(order.value.info),
                    item = { value, _ ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = b2bData.value.addressData.address,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W500,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(8.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                GeoLocation(
                                    location = b2bData.value.addressData.fullAddress(),
                                    textSize = 12.sp,
                                    tint = MaterialTheme.colors.background
                                )
                                Space(8.dp)
                                ClickableText(
                                    text = AnnotatedString(b2bData.value.phoneNumber),
                                    style = TextStyle(
                                        color = MaterialTheme.colors.background,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W400,
                                    ),
                                    onClick = { activity.openDialer(b2bData.value.phoneNumber) },
                                )
                            }
                            Space(8.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = b2bData.value.gstin ?: b2bData.value.panNumber,
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                )
                                Space(8.dp)
                                if (b2bData.value.gstin != null) {
                                    Text(
                                        text = b2bData.value.panNumber,
                                        color = MaterialTheme.colors.background,
                                        fontWeight = FontWeight.W400,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                            Space(8.dp)
                            Text(
                                text = "${stringResource(id = R.string.dl_one)}: ${b2bData.value.drugLicenseNo1}",
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                            )
                            Space(8.dp)
                            Text(
                                text = "${stringResource(id = R.string.dl_two)}: ${b2bData.value.drugLicenseNo2}",
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                            )
                        }
                    }
                )
                Space(8.dp)
                OrdersStatus(order.value.info.status)
                Space(8.dp)
            }
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
                            append(stringResource(id = R.string.order_no))
                            append(" ")
                            val startIndex = length
                            append(order.value.info.id)
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
                            append(order.value.info.paymentMethod.serverValue)
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
                Column {
                    Text(
                        text = order.value.info.date,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = order.value.info.time,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                }
            }
            Space(8.dp)
            Divider(Modifier.padding(horizontal = 16.dp))
            val entries = scope.entries.flow.collectAsState()
            val declineReasons = scope.declineReason.flow.collectAsState()
            val checkedEntries = scope.checkedEntries.flow.collectAsState()
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Space(8.dp)
                entries.value.forEachIndexed { index, it ->
                    OrderEntryItem(
                        canEdit = scope.canEdit,
                        entry = it,
                        isChecked = it in checkedEntries.value,
                        onChecked = { _ -> scope.toggleCheck(it) },
                        onClick = {
                            scope.selectEntry(
                                taxType = order.value.info.taxType!!,
                                retailerName = b2bData.value.tradeName,
                                canEditOrderEntry = scope.canEdit,
                                declineReason = declineReasons.value, entry = entries.value,
                                index = index
                            )
                        },
                    )
                }
                Space(8.dp)
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OrderTotal(order.value.info.total.formattedPrice)
//            Space(16.dp)
//            if (scope.canEdit) {
//                val actions = scope.actions.flow.collectAsState()
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    actions.value.forEachIndexed { index, action ->
//                        MedicoButton(
//                            modifier = Modifier.weight(action.weight),
//                            text = stringResourceByName(action.stringId),
//                            isEnabled = true,
//                            color = Color(action.bgColorHex.toColorInt()),
//                            contentColor = Color(action.textColorHex.toColorInt()),
//                            onClick = { scope.acceptAction(action) },
//                        )
//                        if (index != actions.value.lastIndex) {
//                            Space(16.dp)
//                        }
//                    }
//                }
//            }
            Space(10.dp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderEntryItem(
    canEdit: Boolean,
    isChecked: Boolean,
    entry: OrderEntry,
    onChecked: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = when {
            entry.buyingOption == BuyingOption.QUOTE -> BorderStroke(
                2.dp,
                ConstColors.gray.copy(alpha = 0.5f),
            )
            entry.status == OrderEntry.Status.REJECTED -> BorderStroke(
                2.dp,
                ConstColors.red,
            )
            else -> null
        },
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
//            if (canEdit) {
//                Checkbox(
//                    checked = isChecked,
//                    colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
//                    onCheckedChange = onChecked,
//                    modifier = Modifier.align(Alignment.CenterVertically),
//                )
//                Space(8.dp)
//            }
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            append(entry.servedQty.formatted)
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
                        BuyingOption.QUOTE -> {
                            Text(
                                text = stringResource(id = R.string.quoted),
                                color = MaterialTheme.colors.background,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                }
            }
        }
    }
}