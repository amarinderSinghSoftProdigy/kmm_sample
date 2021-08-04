package com.zealsoftsol.medico.screens.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.GeoLocation
import kotlin.time.ExperimentalTime

@Composable
fun CartScreen(scope: CartScope) {
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()
    val isContinueEnabled = scope.isContinueEnabled.flow.collectAsState()

    if (items.value.isEmpty()) {
        EmptyCart { scope.goBack() }
    } else {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Space(16.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ColorItem(ConstColors.green, R.string.available)
                    ColorItem(ConstColors.orange, R.string.limited)
                    ColorItem(ConstColors.gray, R.string.quoted)
                    Button(
                        onClick = { scope.clearCart() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFFDE7E7),
                            contentColor = ConstColors.red,
                        ),
                        elevation = null,
                    ) {
                        Text(
                            text = stringResource(id = R.string.empty_cart),
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                        )
                    }
                }
                Space(12.dp)
                items.value.forEachIndexed { index, value ->
                    SellerCartItem(
                        sellerCart = value,
                        expand = index == 0,
                        onRemoveSeller = { scope.removeSellerItems(value) },
                        onIncItem = { indexI, item ->
                            scope.updateItemCountAndroid(
                                value,
                                item,
                                indexI,
                                +1,
                            )
                        },
                        onDecItem = { indexI, item ->
                            scope.updateItemCountAndroid(
                                value,
                                item,
                                indexI,
                                -1,
                            )
                        },
                        onRemoveItem = { scope.removeItem(value, it) },
                    )
                    Space(12.dp)
                }
            }

            total.value?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    if (!isContinueEnabled.value) {
                        Space(16.dp)
                        Row(
                            modifier = Modifier
                                .background(
                                    Color.Red.copy(alpha = .1f),
                                    RoundedCornerShape(percent = 50)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                tint = Color.Red,
                                contentDescription = null,
                            )
                            Space(8.dp)
                            Text(
                                text = stringResource(id = R.string.delete_products),
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500,
                            )
                        }
                        Space(16.dp)
                    }
                    Divider()
                    Space(16.dp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.total),
                            fontWeight = FontWeight.W500,
                            fontSize = 20.sp,
                            color = MaterialTheme.colors.background,
                        )
                        Space(4.dp)
                        Text(
                            text = it.formattedPrice,
                            fontWeight = FontWeight.W700,
                            fontSize = 20.sp,
                            color = MaterialTheme.colors.background,
                        )
                        Space(32.dp)
                        MedicoButton(
                            text = stringResource(id = R.string.continue_text),
                            isEnabled = isContinueEnabled.value,
                            onClick = { scope.continueWithCart() },
                        )
                    }
                    Space(16.dp)
                }
            }
        }
    }
}

@Composable
private fun SellerCartItem(
    sellerCart: SellerCart,
    expand: Boolean,
    onRemoveSeller: () -> Unit,
    onIncItem: (Int, CartItem) -> Unit,
    onDecItem: (Int, CartItem) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
) {
    FoldableItem(
        expanded = expand,
        header = { isExpanded ->
            Box(
                modifier = Modifier
                    .background(ConstColors.red)
                    .size(50.dp)
                    .clickable(onClick = onRemoveSeller),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Space(12.dp)
            BoxWithConstraints {
                Column(
                    modifier = Modifier.width(maxWidth - 50.dp),
                ) {
                    Text(
                        text = sellerCart.sellerName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(2.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.payment_method))
                            append(": ")
                            val startIndex = length
                            append(sellerCart.paymentMethod.serverValue)
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
            }
            Space(12.dp)
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                tint = ConstColors.gray,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Space(12.dp)
        },
        childItems = sellerCart.items,
        item = { value, index ->
            CartItem(
                cartItem = value,
                onInc = { onIncItem(index, value) },
                onDec = { onDecItem(index, value) },
                onRemove = { onRemoveItem(value) },
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CartItem(
    cartItem: CartItem,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        border = when (cartItem.buyingOption) {
            BuyingOption.BUY -> BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            BuyingOption.QUOTE -> BorderStroke(
                1.dp, when (cartItem.quotedData?.isAvailable) {
                    true -> ConstColors.green
                    false -> ConstColors.red
                    null -> Color.LightGray.copy(alpha = 0.5f)
                }
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
            ) {
                Space(12.dp)
                Text(
                    text = cartItem.productName,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 30.dp)
                )
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
                Text(
                    text = cartItem.price.formatted,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 15.sp
                )
                Space(12.dp)
                cartItem.quotedData?.let {
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .padding(end = 8.dp),
                    ) {
                        GeoLocation(it.message, textSize = 12.sp, tint = ConstColors.lightBlue)
                        Text(
                            text = stringResource(id = if (it.isAvailable) R.string.available else R.string.not_available),
                            color = if (it.isAvailable) ConstColors.green else ConstColors.red,
                            fontWeight = FontWeight.W500,
                            fontSize = 12.sp,
                        )
                    }
                } ?: cartItem.seasonBoyRetailer?.let {
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .padding(end = 8.dp),
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
            Surface(
                shape = CircleShape,
                color = ConstColors.red.copy(alpha = 0.12f),
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ConstColors.red,
                    modifier = Modifier.padding(2.dp),
                )
            }
            PlusMinusQuantityLocal(
                quantity = cartItem.quantity.value.toInt(),
                cartItem = cartItem,
                max = cartItem.stockInfo?.availableQty ?: Int.MAX_VALUE,
                isEnabled = true,
                onInc = onInc,
                onDec = onDec,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .padding(bottom = if (cartItem.seasonBoyRetailer != null || cartItem.quotedData != null) 16.dp else 0.dp)
            )
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

@OptIn(ExperimentalTime::class)
@Composable
private fun PlusMinusQuantityLocal(
    modifier: Modifier = Modifier,
    cartItem: CartItem,
    quantity: Int,
    isEnabled: Boolean,
    max: Int = Int.MAX_VALUE,
    onInc: () -> Unit,
    onDec: () -> Unit,
) {
    Row(
        modifier = modifier.defaultMinSize(minWidth = 100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Icon(
            imageVector = Icons.Default.Remove,
            tint = if (isEnabled && quantity > 0) ConstColors.lightBlue else ConstColors.gray.copy(
                alpha = 0.5f
            ),
            contentDescription = null,
            modifier = if (isEnabled && quantity > 0) Modifier.clickable(onClick = onDec) else Modifier,
        )
        Space(12.dp)
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colors.background.copy(if (isEnabled) 1f else 0.5f),
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
        )
        Space(12.dp)
        Icon(
            imageVector = Icons.Default.Add,
            tint = if (isEnabled && quantity < max) ConstColors.lightBlue else ConstColors.gray.copy(
                alpha = 0.5f
            ),
            contentDescription = null,
            modifier = if (isEnabled && quantity < max) Modifier.clickable(onClick = onInc) else Modifier,
        )
    }
}

@Composable
private fun ColorItem(color: Color, label: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(18.dp)) {
            drawCircle(color)
        }
        Space(4.dp)
        Text(
            text = stringResource(id = label),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun EmptyCart(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = null,
                tint = MaterialTheme.colors.background.copy(alpha = 0.2f),
                modifier = Modifier.size(78.dp),
            )
            Space(14.dp)
            Text(
                text = stringResource(id = R.string.empty_cart_hint_1),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Space(8.dp)
            Text(
                text = stringResource(id = R.string.empty_cart_hint_2),
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        MedicoButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            text = stringResource(id = R.string.go_back),
            isEnabled = true,
            onClick = onBack
        )
    }
}