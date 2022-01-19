package com.zealsoftsol.medico.screens.orders

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.SearchDataItem
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joda.time.DateTime

/**
 * @param scope current scope to get the current and updated state of views
 * show the view to update phone number and language for user
 */

@Composable
fun OrderHsnEditScreen(scope: OrderHsnEditScope) {

    val orderEntry = scope.orderEntry.flow.collectAsState().value
    val selectedIndex = scope.selectedIndex.flow.collectAsState().value
    val openHsnBottomSheet = scope.showHsnBottomSheet.flow.collectAsState().value
    val openWarningBottomSheet = scope.showWarningBottomSheet.flow.collectAsState().value
    val openDeclineReasonBottomSheet =
        scope.showDeclineReasonsBottomSheet.flow.collectAsState().value
    val selectedHsnCode = scope.selectedHsnCode.flow.collectAsState().value
    val expiryDate = scope.expiry.flow.collectAsState().value
    val servedQty = scope.quantity.flow.collectAsState().value
    val freeQty = scope.freeQuantity.flow.collectAsState().value
    val mrp = scope.mrp.flow.collectAsState().value
    val price = scope.ptr.flow.collectAsState().value
    val batchNo = scope.batch.flow.collectAsState().value
    val discount = scope.discount.flow.collectAsState().value
    val canEditOrderEntry = scope.canEditOrderEntry
    val context = LocalContext.current

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
                        text = "${stringResource(id = R.string.line_item)} ${selectedIndex + 1}",
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
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = stringResource(id = R.string.batch_no),
                                color = ConstColors.gray,
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
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)
                            Text(
                                text = stringResource(id = R.string.expiry),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(10.dp)

                            Text(
                                text = stringResource(id = R.string.requested_qty),
                                color = ConstColors.gray,
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
                                text = stringResource(id = R.string.status),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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
                                color = ConstColors.gray,
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
                                color = ConstColors.gray,
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
                                color = ConstColors.gray,
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
                                text = orderEntry.status.toString(),
                                color = if (orderEntry.status == OrderEntry.Status.ACCEPTED)
                                    Color.Green else Color.Red,
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
                                text = orderEntry.discount.formatted,
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
                if (canEditOrderEntry){ //only allow changing hsn code if order is editable
                    Divider()
                    Space(10.dp)
                    Column {
                        Text(
                            text = stringResource(id = R.string.hsn_code),
                            color = ConstColors.gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 4.dp, end = 10.dp)
                            ) {
                                Text(
                                    text = selectedHsnCode,
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.W700
                                )
                                Divider(color = Color.Black)
                            }
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                OpenHsnScreen {
                                    scope.manageBottomSheetVisibility(!openHsnBottomSheet)
                                }
                            }
                        }
                    }
                    Space(10.dp)
                    if (selectedHsnCode.isEmpty())
                        HsnErrorText()
                    Space(10.dp)
                }
            }
            Divider()
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
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(id = R.string.batch_no),
                                    color = ConstColors.gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                EditText(
                                    canEditOrderEntry,
                                    value = batchNo,
                                    onChange = {
                                        scope.updateBatch(it)
                                    },
                                    keyboardOptions = KeyboardOptions.Default,
                                )
                            }
                            Space(5.dp)
                            Divider()
                        }

                    }
                }
                Space(10.dp)
                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.ptr),
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    EditText(canEditOrderEntry,
                                        value = price.toString(), onChange = {
                                            scope.updatePtr(it.toDouble())
                                        }
                                    )

                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.mrp),
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    EditText(canEditOrderEntry,
                                        value = mrp.toString(), onChange = {
                                            scope.updateMrp(it.toDouble())
                                        }
                                    )
                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                    }
                }
                Space(10.dp)
                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.qty),
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    EditText(canEditOrderEntry,
                                        value = servedQty.toString(), onChange = {
                                            scope.updateQuantity(it.toDouble())
                                        }
                                    )
                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.free),
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    EditText(canEditOrderEntry,
                                        value = freeQty.toString(), onChange = {
                                            scope.updateFreeQuantity(it.toDouble())
                                        }
                                    )
                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                    }
                }
                Space(10.dp)

                Box {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier
                            .width(maxWidth / 2 - 8.dp)
                            .align(Alignment.BottomStart)
                            .clickable {
                                if (canEditOrderEntry) { //only allow date picker if order an be edited
                                    val now = DateTime.now()
                                    val dialog = DatePickerDialog(
                                        context,
                                        { _, year, month, _ ->
                                            scope.updateExpiry("${month + 1}/${year}")
                                        },
                                        now.year,
                                        now.monthOfYear - 1,
                                        now.dayOfMonth,
                                    )
                                    dialog.show()
                                }
                            }) {

                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.expiry),
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Text(
                                        text = expiryDate,
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.W700
                                    )
                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2 - 8.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${stringResource(id = R.string.discount)}%",
                                        color = ConstColors.gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    EditText(canEditOrderEntry,
                                        value = discount.toString(), onChange = {
                                            scope.updateDiscount(it.toDouble())
                                        }
                                    )
                                }
                                Space(5.dp)
                                Divider()
                            }
                        }
                    }
                }
            }
            Space(20.dp)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .width(maxWidth)
                        .align(Alignment.BottomEnd)
                ) {

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.sub_total))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = orderEntry.totalAmount.formatted,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
            Space(5.dp)
            Divider()
            Space(10.dp)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .width(maxWidth)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.cgst))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${orderEntry.cgstTax.amount.formatted}(${orderEntry.cgstTax.percent.formatted})",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
            Space(5.dp)
            Divider()
            Space(10.dp)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .width(maxWidth)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        buildAnnotatedString {
                            append(stringResource(id = R.string.sgst))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${orderEntry.sgstTax.amount.formatted}(${orderEntry.sgstTax.percent.formatted})",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                }
            }
            Space(5.dp)
            Divider()
            Space(10.dp)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .width(maxWidth)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        buildAnnotatedString {
                            append(stringResource(id = R.string.igst))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${orderEntry.igstTax.amount.formatted}(${orderEntry.igstTax.percent.formatted})",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
            Space(5.dp)
            Divider()
            Space(10.dp)
            if (canEditOrderEntry) // only allow changing status if order entry is editable i.e New
                Row(modifier = Modifier.fillMaxSize()) {

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.not_available),
                        onClick = {
                            scope.manageWarningBottomSheetVisibility(!openWarningBottomSheet)
                        },
                        color = ConstColors.gray,
                        contentColor = Color.White,
                        isEnabled = true
                    )

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.save),
                        onClick = { scope.saveEntry() },
                        isEnabled = mrp != 0.0 && price != 0.0 // only allow submit if mrp and proce is entered
                    )
                }
            Space(10.dp)
        }
        if (openHsnBottomSheet)
            HsnCodeSheet(scope)
        if (openWarningBottomSheet)
            WarningProductNotAvailable(scope)
        if (openDeclineReasonBottomSheet)
            DeclineReasonBottomSheet(scope)
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
            .height(50.dp),
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
                color = ConstColors.gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun HsnErrorText() {
    Text(
        text = stringResource(id = R.string.hsn_code_error),
        color = ConstColors.red,
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


/**
 * @param scope current scope to get the current and updated state of views
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


/**
 * Bottom sheet for displaying HSN codes for purchase orders
 */
@Composable
private fun HsnCodeSheet(
    scope: OrderHsnEditScope,
) {
    val items = scope.items.flow.collectAsState()

    val selectedHsnCode = remember { mutableStateOf("") }
    val searchTerm = remember { mutableStateOf("") }
    var queryTextChangedJob: Job? = null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(indication = NoOpIndication) {
                    scope.manageBottomSheetVisibility(false)
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
                        .padding(end = 10.dp)
                        .clickable {
                            scope.manageBottomSheetVisibility(false)
                        }
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                        .border(
                            border = BorderStroke(1.dp, color = Color.White),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    elevation = 5.dp
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                border = BorderStroke(1.dp, color = Color.LightGray),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        value = searchTerm.value,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            textColor = Color.Black,
                            placeholderColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            searchTerm.value = it

                            queryTextChangedJob?.cancel()

                            queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                scope.search(it)
                            }
                        },
                        label = {
                            Text(
                                stringResource(id = R.string.search_hsn_code),
                                color = Color.Black
                            )
                        }
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = stringResource(id = R.string.hsn_code),
                        color = ConstColors.green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.rate),
                        color = ConstColors.green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.effective_date),
                        color = ConstColors.green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))
                LazyColumn(
                    contentPadding = PaddingValues(start = 3.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .heightIn(0.dp, 380.dp) //mention max height here
                        .fillMaxWidth(),
                ) {
                    itemsIndexed(
                        items = items.value,
                        key = { index, _ -> index },
                        itemContent = { index, item ->
                            SingleHsnItem(item) { checked ->
                                items.value.forEachIndexed { ind, it ->
                                    if (checked && it.checked) {
                                        items.value[ind].checked = false
                                    }
                                }
                                items.value[index].checked = true
                                selectedHsnCode.value = items.value[index].hsncode
                            }

                            if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.getHsnCodes(false)
                            }
                        },
                    )
                }

                MedicoButton(text = stringResource(id = R.string.select),
                    isEnabled = selectedHsnCode.value.isNotEmpty(),
                    modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp)
                        .height(40.dp),
                    onClick = {
                        scope.updateHsnCode(selectedHsnCode.value)
                        scope.manageBottomSheetVisibility(false)
                    })
            }
        }
    }

}

/**
 * represents each single entity in the the hsn list codes in bottom sheet
 */
@Composable
private fun SingleHsnItem(item: SearchDataItem, onCheckedChange: ((Boolean) -> Unit)) {
    Column {
        Row(
            modifier = Modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            ) {
                Checkbox(
                    colors = CheckboxDefaults.colors(ConstColors.green),
                    checked = item.checked,
                    onCheckedChange = onCheckedChange
                )

                Text(
                    text = item.hsncode,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }

            Text(
                text = item.rate.formattedValue,
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600
            )
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            ) {}
        }
        Divider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))
    }
}

/**
 * Bottom sheet for displaying warning alert if stockist chooses to mark order entry as not available
 */
@Composable
private fun WarningProductNotAvailable(
    scope: OrderHsnEditScope,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(indication = NoOpIndication) {
                    scope.manageWarningBottomSheetVisibility(false)
                })
        Surface(
            modifier = Modifier
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
                        .padding(end = 10.dp)
                        .clickable {
                            scope.manageWarningBottomSheetVisibility(false)
                        }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = stringResource(id = R.string.unavailable_warning),
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.okay),
                        onClick = {
                            scope.manageWarningBottomSheetVisibility(false)
                            scope.manageDeclineBottomSheetVisibility(true)
                        },
                        color = ConstColors.gray,
                        contentColor = Color.White,
                        isEnabled = true
                    )

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.cancel),
                        onClick = {
                            scope.manageWarningBottomSheetVisibility(false)
                        },
                        isEnabled = true
                    )
                }
            }
        }
    }
}

@Composable
fun EditText(
    canEdit: Boolean,
    value: String,
    onChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
) {

    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp,
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.End,
    )

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = {
            onChange(it)
        },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        enabled = canEdit,
        textStyle = textStyle
    )
}


@Composable
fun DeclineReasonBottomSheet(scope: OrderHsnEditScope) {
    val declineReason = scope.declineReason
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(indication = NoOpIndication) {
                    scope.manageBottomSheetVisibility(false)
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
                            Text(
                                text = item.name,
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        scope.updateDeclineReason(item.code)
                                        scope.manageDeclineBottomSheetVisibility(false)
                                    }
                            )
                        },
                    )
                }
                Divider()
                Space(10.dp)
            }
        }
    }
}