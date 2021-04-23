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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.product.PlusMinusQuantity

@Composable
fun CartScreen(scope: CartScope) {
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()

    if (items.value.isEmpty()) {
        EmptyCart { scope.goBack() }
    } else {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
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
                        onIncItem = {
                            scope.updateItemCount(
                                value,
                                it,
                                it.quantity.value.toInt() + 1
                            )
                        },
                        onDecItem = {
                            scope.updateItemCount(
                                value,
                                it,
                                it.quantity.value.toInt() - 1
                            )
                        },
                        onRemoveItem = { scope.removeItem(value, it) },
                    )
                    Space(12.dp)
                }
            }

            total.value?.let {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
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
                            isEnabled = true,
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
    onIncItem: (CartItem) -> Unit,
    onDecItem: (CartItem) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(expand) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color.LightGray.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            ) {
                Box(
                    modifier = Modifier.background(ConstColors.red).size(50.dp)
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
                        )
                        Text(
                            text = "some address",
                            color = ConstColors.gray,
                            fontWeight = FontWeight.W500,
                            fontSize = 12.sp,
                        )
                    }
                }
                Space(12.dp)
                Icon(
                    imageVector = if (isExpanded.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Space(12.dp)
            }

            if (isExpanded.value && sellerCart.items.isNotEmpty()) Column(
                modifier = Modifier.background(Color.White).padding(horizontal = 8.dp)
            ) {
                Space(12.dp)
                sellerCart.items.forEach {
                    CartItem(
                        cartItem = it,
                        onInc = { onIncItem(it) },
                        onDec = { onDecItem(it) },
                        onRemove = { onRemoveItem(it) },
                    )
                    Space(12.dp)
                }
            }
        }
    }
}

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
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(start = 12.dp)
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
                        modifier = Modifier.size(14.dp).align(Alignment.CenterVertically),
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
            }
            Surface(
                shape = CircleShape,
                color = ConstColors.red.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clickable(indication = rememberRipple(radius = 12.dp), onClick = onRemove)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ConstColors.red,
                    modifier = Modifier.padding(2.dp),
                )
            }
            PlusMinusQuantity(
                quantity = cartItem.quantity.value.toInt(),
                max = cartItem.stock.availableQty,
                onInc = onInc,
                onDec = onDec,
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
            )
//            val labelColor = when (sellerInfo.stockInfo.status) {
//                StockStatus.IN_STOCK -> ConstColors.green
//                StockStatus.LIMITED_STOCK -> ConstColors.orange
//                StockStatus.OUT_OF_STOCK -> ConstColors.red
//            }
        }
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
            modifier = Modifier.align(Alignment.Center).padding(bottom = 64.dp),
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
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            text = stringResource(id = R.string.go_back),
            isEnabled = true,
            onClick = onBack
        )
    }
}