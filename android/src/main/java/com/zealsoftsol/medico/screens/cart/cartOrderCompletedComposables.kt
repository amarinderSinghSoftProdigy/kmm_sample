package com.zealsoftsol.medico.screens.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
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
import com.zealsoftsol.medico.core.mvi.scope.nested.CartOrderCompletedScope
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.SellerOrder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun CartOrderCompletedScreen(scope: CartOrderCompletedScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Space(24.dp)
            OrderPlacedTile(scope.order)
            Space(14.dp)
            scope.order.sellersOrder.forEach {
                OrderItem(it)
                Space(12.dp)
            }

        }

        Column {
            OrderTotal(scope.total.formattedPrice)
            Space(dp = 16.dp)
            MedicoButton(
                text = stringResource(id = R.string.orders),
                isEnabled = true,
                onClick = { scope.goToOrders() },
            )
            Space(16.dp)
        }
    }
}

@Composable
private fun OrderPlacedTile(
    order: CartSubmitResponse,
) {
    /*Surface(
        shape = MaterialTheme.shapes.medium,
        color = ConstColors.green.copy(alpha = .06f),
        border = BorderStroke(2.dp, ConstColors.green)
    ) {*/
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_order_placed),
            contentDescription = null,
        )
        Space(16.dp)

        Text(
            text = stringResource(id = R.string.order_success_old),
            color = ConstColors.lightGreen,
            fontWeight = FontWeight.W800,
            fontSize = 16.sp,
        )
        Space(4.dp)
        Text(
            text = stringResource(id = R.string.order_email_confirmation),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
        Space(4.dp)
        val tradeNames = order.sellersOrder.map { it.tradeName }.toList().joinToString(",")
        Text(
            text = tradeNames,
            color = ConstColors.lightGreen,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )

        /*Row {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.date))
                    append(": ")
                    val startIndex = length
                    append(orderDate)
                    addStyle(
                        SpanStyle(color = ConstColors.lightBlue),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
            Space(8.dp)
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.time))
                    append(": ")
                    val startIndex = length
                    append(orderTime)
                    addStyle(
                        SpanStyle(color = ConstColors.lightBlue),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
        }*/
        //Divider(color = ConstColors.green)
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                tint = ConstColors.lightBlue,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
            )
            Space(14.dp)
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.order_email_confirmation))
                    append(" ")
                    val startIndex = length
                    append(email)
                    addStyle(
                        SpanStyle(color = ConstColors.lightBlue),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
        }*/
    }
    //}
}

@Composable
private fun OrderItem(seller: SellerOrder) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(.7f)) {
                Text(
                    text = seller.tradeName,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                seller.seasonBoyRetailerName?.let {
                    Space(2.dp)
                    Text(
                        text = it,
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W500,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Space(2.dp)
                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.payment_method))
                            append(": ")
                            val startIndex = length
                            append(seller.paymentMethod.serverValue)
                            addStyle(
                                SpanStyle(color = ConstColors.lightBlue),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(6.dp)
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterVertically)
                    )
                    Space(6.dp)
                    Text(
                        text = seller.orderId,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(.3f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(id = R.string.total),
                    color = ConstColors.gray,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(2.dp)
                Text(
                    text = buildAnnotatedString {
                        append(seller.total.formattedPrice)
                        val startIndex = length
                        append("*")
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue),
                            startIndex,
                            length,
                        )
                    },
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun OrderTotal(price: String) {
    Column {
        //Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.order_total),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 22.sp,
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price,
                    color = ConstColors.lightGreen,
                    fontWeight = FontWeight.W700,
                    fontSize = 22.sp,
                )
                Space(2.dp)
                Text(
                    text = stringResource(id = R.string.tax_exclusive),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp,
                )
            }
        }
        //Divider()
    }
}