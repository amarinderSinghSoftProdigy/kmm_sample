package com.zealsoftsol.medico.screens.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.GeoLocation

@Composable
fun CartPreviewScreen(scope: CartPreviewScope) {
    val sellers = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Space(24.dp)
            Text(
                text = stringResource(id = R.string.place_order),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp,
            )
            Space(14.dp)
            sellers.value.forEach { seller ->
                SellerItem(seller)
                Space(12.dp)
            }
        }

        Column {
            OrderTotal(total.value?.formattedPrice.orEmpty())
            Space(16.dp)
            MedicoButton(
                text = stringResource(R.string.place_order),
                isEnabled = true,
                onClick = { scope.placeOrder() },
            )
            Space(16.dp)
        }
    }
}

@Composable
private fun SellerItem(seller: SellerCart) {
    FoldableItem(
        expanded = true,
        header = { isExpanded ->
            Column(
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = seller.sellerName,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(2.dp)
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.type))
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
                )
            }
            Row(
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxHeight()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.total),
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
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
                Space(12.dp)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        childItems = seller.items,
        item = { value, _ -> CartItem(value) },
    )
}

@Composable
private fun CartItem(cartItem: CartItem) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Space(12.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        BoxWithConstraints {
                            Text(
                                text = cartItem.productName,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W700,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = maxWidth * 2 / 3),
                            )
                        }
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.qty))
                                append(": ")
                                val startIndex = length
                                append(cartItem.quantity.formatted)
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
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    }
                    Space(6.dp)
                    Row {
                        Icon(
                            imageVector = Icons.Default.Info,
                            tint = ConstColors.lightBlue,
                            contentDescription = null,
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.CenterVertically),
                        )
                        Space(6.dp)
                        BoxWithConstraints {
                            Text(
                                text = cartItem.manufacturerName,
                                color = ConstColors.lightBlue,
                                fontWeight = FontWeight.W500,
                                fontSize = 14.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.widthIn(max = maxWidth / 3)
                            )
                        }
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
                            text = cartItem.standardUnit,
                            color = ConstColors.gray,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    }
                    Space(6.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.price))
                                append(": ")
                                val startIndex = length
                                append(cartItem.price.formatted)
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
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.subtotal))
                                append(": ")
                                val startIndex = length
                                append(cartItem.subtotalPrice.formatted)
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
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    }
                }
                Space(8.dp)
                cartItem.seasonBoyRetailer?.let {
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                    ) {
                        BoxWithConstraints {
                            Text(
                                text = it.tradeName,
                                color = ConstColors.gray,
                                fontWeight = FontWeight.W500,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.sizeIn(maxWidth = maxWidth * 2 / 3)
                            )
                        }
                        GeoLocation(it.geoData.city, textSize = 12.sp, tint = ConstColors.lightBlue)
                    }
                }
            }
            val labelColor = when (cartItem.stockInfo?.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
                null -> ConstColors.gray
            }
            val maxWidth =
                LocalContext.current.let { it.screenWidth / it.density }.dp - 37.dp - 5.dp
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = maxWidth)
                    .background(labelColor)
            )
        }
    }
}