package com.zealsoftsol.medico.screens.batches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.data.BatchesData
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder


@Composable
fun ViewBatchesScreen(scope: BatchesScope) {
    val batchData = scope.batchData.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CoilImageBrands(
            src = R.drawable.ic_acc_place,
            contentScale = ContentScale.FillBounds,
            onError = { ItemPlaceholder() },
            onLoading = { ItemPlaceholder() },
            height = 180.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = if (batchData.value.isNotEmpty()) batchData.value[0].productName else "",
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
                    text = if (batchData.value.isNotEmpty()) batchData.value[0].manufacturerName else "",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (batchData.value.isNotEmpty()) {
                            if (batchData.value[0].totalStock.value > 0) {
                                stringResource(id = R.string.in_stock)
                            } else {
                                stringResource(id = R.string.out_stock)
                            }
                        } else "",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                    CommonRoundedText(
                        text = if (batchData.value.isNotEmpty()) {
                            if (batchData.value[0].totalStock.value > 0) {
                                batchData.value[0].totalStock.formatted
                            } else {
                                "0.0"
                            }
                        } else "0.0",
                        modifier = Modifier.padding(start = 5.dp),
                        backgroundColor = Color.White,
                        borderColor = ConstColors.green,
                        textColor = Color.Black
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .height(30.dp)
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(20.dp),
                color = ConstColors.lightGrey
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_discount_buyx),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 5.dp)
                    )

                    Text(
                        text = "10 + 1 offer",
                        color = Color.Red,
                        fontSize = 14.sp,
                    )
                }
            }

            if (batchData.value.isNotEmpty() && batchData.value[0].batches.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    batchData.value[0].batches.let {
                        itemsIndexed(
                            items = it,
                            key = { pos, item -> pos },
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

/**
 * BatchItem
 */
@Composable
fun BatchesItem(item: Batch, scope: BatchesScope) {
    Card(
        modifier = Modifier
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on category
                }),
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.ptr))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = item.ptr.formatted,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.in_stock))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.stock.formatted,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row {
                    Text(
                        text = stringResource(id = R.string.batch_no),
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.batchNo,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.mrp))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = item.mrp.formatted,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.expiry_))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.expiryDate,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.ptr))
                            append(":")
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.ptr))
                            append(":")
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
            .padding(3.dp)
            .border(
                BorderStroke(0.5.dp, color = borderColor),
                shape = MaterialTheme.shapes.medium
            )
    ) {

        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
        )

    }
}
