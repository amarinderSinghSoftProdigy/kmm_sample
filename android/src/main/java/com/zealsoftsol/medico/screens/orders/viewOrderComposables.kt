package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.management.GeoLocation


@Composable
fun ViewOrderScreen(scope: ViewOrderScope, callUpdateAPI: Boolean) {

    if (callUpdateAPI) {
        scope.updateData()
    }

    val order = scope.order.flow.collectAsState()
    val b2bData = scope.b2bData.flow.collectAsState()
    val activity = LocalContext.current as MainActivity
    val openDeclineReasonBottomSheet =
        scope.showDeclineReasonsBottomSheet.flow.collectAsState().value
    val entries = scope.entries.flow.collectAsState()
    val declineReasons = scope.declineReason.flow.collectAsState()
    val checkedEntries = scope.checkedEntries.flow.collectAsState()
    val openDialog = scope.showAlert.flow.collectAsState()

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
                    ShowAlert { scope.changeAlertScope(false) }
                Column {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color.White)
                            .padding(horizontal = 16.dp),
                    ) {
                        Space(10.dp)
                        Surface(
                            elevation = 5.dp, shape = MaterialTheme.shapes.medium,
                        ) {
                            FoldableItem(
                                headerBackground = Color.White,
                                expanded = false,
                                header = {
                                    Space(12.dp)
                                    Row(
                                        modifier = Modifier
                                            .weight(.8f)
                                            .height(35.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_retailer),
                                            contentDescription = null,
                                            tint = ConstColors.green,
                                        )
                                        Space(8.dp)
                                        Text(
                                            text = orderTaxValue.tradeName,
                                            color = Color.Black,
                                            fontWeight = FontWeight.W800,
                                            fontSize = 16.sp,
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
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_arrow_right),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .rotate(90f),
                                        )
                                    }
                                },
                                childItems = listOf(orderTaxValue.info),
                                item = { _, _ ->
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        b2bData.value?.let { b2bDataValue ->
                                            Text(
                                                text = b2bDataValue.addressData.address,
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
                                                    location = b2bDataValue.addressData.fullAddress(),
                                                    textSize = 12.sp,
                                                    tint = MaterialTheme.colors.background
                                                )
                                                Space(8.dp)
                                                ClickableText(
                                                    text = AnnotatedString(b2bDataValue.phoneNumber),
                                                    style = TextStyle(
                                                        color = MaterialTheme.colors.background,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.W400,
                                                    ),
                                                    onClick = { activity.openDialer(b2bDataValue.phoneNumber) },
                                                )
                                            }
                                            Space(8.dp)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = b2bDataValue.gstin
                                                        ?: b2bDataValue.panNumber,
                                                    color = MaterialTheme.colors.background,
                                                    fontWeight = FontWeight.W400,
                                                    fontSize = 12.sp,
                                                )
                                                Space(8.dp)
                                                if (b2bDataValue.gstin != null) {
                                                    Text(
                                                        text = b2bDataValue.panNumber,
                                                        color = MaterialTheme.colors.background,
                                                        fontWeight = FontWeight.W400,
                                                        fontSize = 12.sp,
                                                    )
                                                }
                                            }
                                            Space(8.dp)
                                            Text(
                                                text = "${stringResource(id = R.string.dl_one)}: ${b2bDataValue.drugLicenseNo1}",
                                                color = MaterialTheme.colors.background,
                                                fontWeight = FontWeight.W400,
                                                fontSize = 12.sp,
                                            )
                                            Space(8.dp)
                                            Text(
                                                text = "${stringResource(id = R.string.dl_two)}: ${b2bDataValue.drugLicenseNo2}",
                                                color = MaterialTheme.colors.background,
                                                fontWeight = FontWeight.W400,
                                                fontSize = 12.sp,
                                            )
                                        }
                                    }
                                }
                            )
                        }
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
                                    append(orderTaxValue.info.id)
                                    addStyle(
                                        SpanStyle(fontWeight = FontWeight.W600),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = Color.Black,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.type))
                                    append(": ")
                                    val startIndex = length
                                    append(orderTaxValue.info.paymentMethod.serverValue)
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
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.status))
                                    append(": ")
                                    val startIndex = length
                                    append(orderTaxValue.info.status.toString())
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
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = orderTaxValue.info.date,
                                color = Color.Gray,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            Text(
                                text = orderTaxValue.info.time,
                                color = Color.Gray,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                            )
                            Space(5.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.discount))
                                    append(": ")
                                    val startIndex = length
                                    append(orderTaxValue.info.discount?.formatted ?: "")
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
                        }
                    }
                    Space(8.dp)
                    Divider(Modifier.padding(horizontal = 16.dp))

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        b2bData.value?.let { b2BDataValue ->
                            Space(16.dp)
                            entries.value.forEachIndexed { index, it ->
                                OrderEntryItem(
                                    showDetails = true,
                                    canEdit = scope.canEdit,
                                    entry = it,
                                    isChecked = it in checkedEntries.value,
                                    onChecked = { _ -> scope.toggleCheck(it) },
                                    onClick = {
                                        scope.selectEntry(
                                            taxType = orderTaxValue.info.taxType!!,
                                            retailerName = b2BDataValue.tradeName,
                                            canEditOrderEntry = scope.canEdit,
                                            declineReason = declineReasons.value,
                                            entry = entries.value,
                                            index = index
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
                    if (scope.canEdit) {
                        val actions = scope.actions.flow.collectAsState()
                        Row(modifier = Modifier.fillMaxWidth()) {
                            actions.value.forEachIndexed { index, action ->
                                MedicoButton(
                                    modifier = Modifier.weight(action.weight),
                                    text = stringResourceByName(action.stringId),
                                    isEnabled = true,
                                    color = Color(action.bgColorHex.toColorInt()),
                                    contentColor = Color(action.textColorHex.toColorInt()),
                                    onClick = { scope.acceptAction(action) },
                                )
                                if (index != actions.value.lastIndex) {
                                    Space(16.dp)
                                }
                            }
                        }
                    }
                    Space(10.dp)
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        MedicoButton(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(10.dp)
//                                .height(40.dp),
//                            text = stringResource(id = R.string.decline_all),
//                            onClick = {
//                                scope.manageDeclineBottomSheetVisibility(true)
//                            },
//                            color = ConstColors.red,
//                            contentColor = Color.White,
//                            isEnabled = true,
//                        )
//
//                        MedicoButton(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(10.dp)
//                                .height(40.dp),
//                            text = stringResource(id = R.string.accept_all),
//                            onClick = { scope.takeActionOnOrderEntries(ViewOrderScope.Action.ACCEPT_ALL) },
//                            isEnabled = true,
//                            txtColor = MaterialTheme.colors.background,
//                        )
//                    }
//                    Space(10.dp)
                }
            }
        }
        if (openDeclineReasonBottomSheet)
            DeclineReasonBottomSheet(scope)
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
                        colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
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
                    Column(modifier = Modifier.weight(.6f)) {
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
                    }
                    Column(
                        modifier = Modifier
                            .weight(.35f)
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

                    }
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
            if (entry.hsnCode.isEmpty() || entry.price.value == 0.0 || entry.servedQty.value == 0.0) {
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                }

            }
        }

    }
}

@Composable
fun DeclineReasonBottomSheet(scope: ViewOrderScope) {
    val declineReason = scope.declineReason.flow.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(indication = NoOpIndication) {
                    scope.manageDeclineBottomSheetVisibility(false)
                })
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(indication = null) { /* intercept touches */ }
                .align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color.White),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .padding(end = 12.dp)
                        .clickable {
                            scope.manageDeclineBottomSheetVisibility(false)
                        }
                )
                Divider()
                Text(
                    text = stringResource(id = R.string.decline_reason),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )


                LazyColumn(
                    contentPadding = PaddingValues(start = 3.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .heightIn(0.dp, 380.dp) //mention max height here
                        .fillMaxWidth(),
                ) {
                    itemsIndexed(
                        items = declineReason,
                        key = { index, _ -> index },
                        itemContent = { _, item ->
                            Divider()
                            Column(
                                modifier = Modifier
                                    .height(40.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.updateDeclineReason(item.code)
                                        scope.manageDeclineBottomSheetVisibility(false)
                                    },
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.name,
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                        },
                    )
                }
                Divider()
                Space(10.dp)
            }
        }
    }
}