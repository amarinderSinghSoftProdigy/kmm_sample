package com.zealsoftsol.medico.screens.orders

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun ConfirmOrderScreen(scope: ConfirmOrderScope) {
    val order = scope.order.flow.collectAsState()
    val activeTab = scope.activeTab.flow.collectAsState()
    val entries = scope.entries.flow.collectAsState()
    val checkedEntries = scope.checkedEntries.flow.collectAsState()
    val openDeclineReasonBottomSheet =
        scope.showDeclineReasonsBottomSheet.flow.collectAsState().value
    val openDialog = scope.showAlert.flow.collectAsState()

    Box {
        order.value?.let { orderTaxValue ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
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

                            Space(5.dp)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.type))
                                        append(": ")
                                        val startIndex = length
                                        append(orderTaxValue.info.paymentMethod.name)
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
                            }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.discount))
                                        append(": ")
                                        val startIndex = length
                                        append(orderTaxValue.info.discount?.formatted ?: "0.0")
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
                    }
                    Space(10.dp)
                    /*Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.action_confirmation),
                        color = MaterialTheme.colors.background,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                    )*/
                    Divider()
                    Space(10.dp)
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Space(8.dp)
                        entries.value.forEach {
                            if(scope.ifIsDeclinedEntry(it)){
                                it.status = OrderEntry.Status.DECLINED
                            }else{
                                it.status = OrderEntry.Status.ACCEPTED
                            }
                            OrderEntryItem(
                                canEdit = false,
                                entry = it,
                                isChecked = it in checkedEntries.value,
                                onChecked = { _ -> scope.toggleCheck(it) },
                                onClick = { },
                                isConfirmOrderScope = true,
                            )
                        }
                        Space(8.dp)
                    }
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    order.value?.info?.total?.formattedPrice?.let {
                        OrderTotal(it)
                    }
                    Space(16.dp)
                    val actions = scope.actions.flow.collectAsState()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        actions.value.forEachIndexed { index, action ->
                            MedicoButton(
                                modifier = Modifier.weight(action.weight),
                                text = stringResourceByName(action.stringId),
                                txtColor = if (action.stringId == ConfirmOrderScope.Action.PREVIEW.stringId) MaterialTheme.colors.background else Color.White,
                                isEnabled = true,
                                color = Color(action.bgColorHex.toColorInt()),
                                contentColor = Color(action.textColorHex.toColorInt()),
                                onClick = {
                                    when (action) {
                                        ConfirmOrderScope.Action.ACCEPT -> run {
                                            for (entry in checkedEntries.value) {
                                                if (checkOrderEntryValidation(entry)) {
                                                    scope.changeAlertScope(true)
                                                    return@run
                                                }
                                            }
                                            scope.acceptAction(action)
                                        }
                                        ConfirmOrderScope.Action.REJECT -> {
                                            scope.acceptAction(action)
                                        }
                                        ConfirmOrderScope.Action.PREVIEW -> {
                                            scope.acceptAction(action)
                                        }
                                        ConfirmOrderScope.Action.CONFIRM -> {
                                            scope.acceptAction(action)
                                        }
                                    }
                                },
                            )
                            if (index != actions.value.lastIndex) {
                                Space(16.dp)
                            }
                        }
                    }
                    Space(10.dp)
                }
            }
            if (openDeclineReasonBottomSheet)
                DeclineReasonBottomSheet(scope)
            if (openDialog.value)
                ShowAlert(stringResource(id = R.string.warning_order_entry_validation)) {
                    scope.changeAlertScope(
                        false
                    )
                }
        }
    }
}

@Composable
fun DeclineReasonBottomSheet(scope: ConfirmOrderScope) {
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
                        .size(45.dp)
                        .padding(15.dp)
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