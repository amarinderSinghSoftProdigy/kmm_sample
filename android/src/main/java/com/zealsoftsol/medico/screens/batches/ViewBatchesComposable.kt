package com.zealsoftsol.medico.screens.batches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert


@Composable
fun ViewBatchesScreen(scope: BatchesScope) {
    val batchData = scope.batchData.flow.collectAsState()
    val showAlert = scope.showSuccessAlert.flow.collectAsState()

    if (batchData.value != null && batchData.value!!.isNotEmpty()) {
        batchData.value?.get(0)?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(it.productCode, CdnUrlProvider.Size.Px123),
                    onError = {
                        ItemPlaceholder()
                    },
                    onLoading = {
                        ItemPlaceholder()
                    },
                    modifier = Modifier.height(180.dp),
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = it.productName,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.manufacturerName,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.in_stock),
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.W500
                            )
                            CommonRoundedText(
                                text = it.totalStock.formatted,
                                modifier = Modifier.padding(start = 5.dp),
                                backgroundColor = Color.White,
                                borderColor = ConstColors.green,
                                textColor = Color.Black
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                    if (it.batches.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            itemsIndexed(
                                items = it.batches,
                                key = { pos, _ -> pos },
                                itemContent = { _, item ->
                                    BatchesItem(item, scope)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAlert.value)
        ShowAlert(message = stringResource(id = R.string.update_successfull)) {
            scope.updateSuccessAlertVisibility(false)
            scope.goBack()
        }
}

/**
 * BatchItem
 */
@Composable
fun BatchesItem(item: Batch, scope: BatchesScope) {
    Surface(
        modifier = Modifier
            .background(Color.White),
        shape = RoundedCornerShape(5.dp),
        elevation = 5.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (!item.promotionData.promotionActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "${stringResource(id = R.string.offer_deals_running)}:",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Card(
                        modifier = Modifier
                            .padding(end = 10.dp),
                        elevation = 3.dp,
                        shape = RoundedCornerShape(5.dp),
                        backgroundColor = ConstColors.red,
                    ) {
                        Text(
                            text = item.promotionData.displayOffer,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = 12.dp,
                                end = 12.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.ptr))
                                append(":")
                            },
                            color = ConstColors.txtGrey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = item.ptr.formatted,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.in_stock))
                                append(":")
                            },
                            color = ConstColors.txtGrey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        CommonRoundedText(
                            text = item.stock.formatted,
                            modifier = Modifier.padding(start = 5.dp),
                            backgroundColor = Color.White,
                            borderColor = ConstColors.green,
                            textColor = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = stringResource(id = R.string.batch_no),
                            color = ConstColors.txtGrey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = item.batchNo,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.mrp))
                                append(":")
                            },
                            color = ConstColors.txtGrey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = item.mrp.formatted,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.expiry_))
                                append(":")
                            },
                            color = ConstColors.txtGrey,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = item.expiryDate,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MedicoButton(
                            text = stringResource(id = R.string.select), isEnabled = true,
                            modifier = Modifier
                                .height(35.dp)
                                .padding(start = 40.dp, end = 0.dp)
                        ) {
                            scope.updateData(
                                batchNo = item.batchNo,
                                expiry = item.expiryDate,
                                mrp = item.mrp.value.toString(),
                                price = item.ptr.value.toString(),
                                hsnCode = item.hsncode,
                                qty = item.stock.value.toString()
                            )

                            scope.updateSuccessAlertVisibility(true)
                        }
                    }
                }
            }
        }
    }
}

/**
 * common rounded textview
 */
@Composable
private fun CommonRoundedText(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(
                BorderStroke(0.5.dp, color = borderColor),
                shape = RoundedCornerShape(5.dp)
            )
    ) {

        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            modifier = Modifier.padding(5.dp)
        )

    }
}
