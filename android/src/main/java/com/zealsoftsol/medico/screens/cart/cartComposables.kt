package com.zealsoftsol.medico.screens.cart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.product.BottomSectionMode

@Composable
fun CartScreen(scope: CartScope) {
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()
    val isContinueEnabled = scope.isContinueEnabled.flow.collectAsState()

    if (items.value.isEmpty()) {
        EmptyCart { /*scope.goBack()*/ }
    } else {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.White),
                ) {
                    Button(
                        onClick = { scope.clearCart() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ConstColors.red,
                            contentColor = Color(0xFFFDE7E7),
                        ),
                        shape = RectangleShape,
                        elevation = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(58.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart_remove),
                            contentDescription = null,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(end = 58.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        TextItem(R.string.items, items.value.sumOf { it.items.size })
                        TextItem(
                            R.string.qty,
                            items.value.sumOf { it.items.sumOf { it.quantity.value } })
                        TextItem(
                            R.string.free,
                            items.value.sumOf { it.items.sumOf { it.freeQuantity.value } })
                        TextItem(R.string.stockist, items.value.size)
                    }
                    Divider(modifier = Modifier.align(Alignment.BottomCenter))
                }
                BoxWithConstraints {
                    LazyColumn(
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(top = 6.dp),
                        modifier = Modifier.height(maxHeight - 80.dp),
                    ) {
                        items(
                            items.value,
                            key = { it.sellerCode },
                            itemContent = { value ->
                                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                    SellerCartItem(
                                        sellerCart = value,
                                        expand = true,
                                        onRemoveSeller = { scope.removeSellerItems(value) },
                                        onSaveQty = { item, qty, freeQty ->
                                            scope.updateItemCount(
                                                value,
                                                item,
                                                qty,
                                                freeQty,
                                            )
                                        },
                                        onRemoveItem = { scope.removeItem(value, it) },
                                    )
                                }
                            }
                        )
                    }
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
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
                        }
                        MedicoRoundButton(
                            text = stringResource(id = R.string.continue_text),
                            isEnabled = isContinueEnabled.value,
                            onClick = { scope.continueWithCart() },
                            height = 48.dp,
                            wrapTextSize = true,
                        )
                    }
                    Space(16.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SellerCartItem(
    sellerCart: SellerCart,
    expand: Boolean,
    onRemoveSeller: () -> Unit,
    onSaveQty: (CartItem, Double, Double) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
) {
    FoldableItem(
        expanded = expand,
        headerBackground = ConstColors.ltgray,
        headerMinHeight = 60.dp,
        header = { _ ->
            Space(12.dp)
            Surface(
                shape = CircleShape,
                color = Color.Red.copy(alpha = 0.12f),
                onClick = onRemoveSeller,
                modifier = Modifier.size(24.dp),
            ) {
                Box(modifier = Modifier.padding(2.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = ConstColors.red,
                    )
                }
            }
            Space(12.dp)
            Column {
                Text(
                    text = sellerCart.sellerName,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(2.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Space(4.dp)
                    Box(
                        modifier = Modifier
                            .height(10.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                    )
                    Space(4.dp)
                    Text(
                        text = sellerCart.total.formattedPrice,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        childItems = sellerCart.items,
        hasItemLeadingSpacing = false,
        hasItemTrailingSpacing = false,
        itemSpacing = 8.dp,
        itemHorizontalPadding = 0.dp,
        itemsBackground = ConstColors.ltgray,
        item = { value, _ ->
            CartItem(
                cartItem = value,
                onSaveQty = { qty, freeQty -> onSaveQty(value, qty!!, freeQty!!) },
                onRemove = { onRemoveItem(value) },
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CartItem(
    cartItem: CartItem,
    onSaveQty: (Double?, Double?) -> Unit,
    onRemove: () -> Unit,
) {
    val labelColor = when (cartItem.buyingOption) {
        BuyingOption.BUY -> ConstColors.green
        BuyingOption.QUOTE -> when (cartItem.quotedData?.isAvailable) {
            true -> ConstColors.green
            false -> ConstColors.red
            null -> Color.LightGray
        }
    }
    Surface(
        shape = RectangleShape,
        color = Color.White,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            drawRect(labelColor)
        }
        BaseCartItem(
            qtyInitial = cartItem.quantity.value,
            freeQtyInitial = cartItem.freeQuantity.value,
            onSaveQty = onSaveQty,
            headerContent = {
                Column {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(modifier = Modifier.size(10.dp)) {
                                drawRoundRect(labelColor, cornerRadius = CornerRadius(8.dp.value))
                            }
                            Space(8.dp)
                            Text(
                                text = cartItem.productName,
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W700,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 30.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Red.copy(alpha = 0.12f),
                            onClick = onRemove,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterEnd),
                        ) {
                            Box(modifier = Modifier.padding(2.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = ConstColors.red,
                                )
                            }
                        }
                    }
                    cartItem.seasonBoyRetailer?.let {
                        Space(4.dp)
                        Text(
                            text = it.tradeName,
                            color = ConstColors.gray,
                            fontWeight = FontWeight.W500,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
            mainBodyContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (cartItem.quotedData?.isAvailable != false) {
                        Text(
                            text = buildAnnotatedString {
                                append("PTR: ")
                                val startIndex = length
                                append(cartItem.price.formatted)
                                addStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colors.background,
                                        fontWeight = FontWeight.W800
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.not_available),
                            color = ConstColors.red,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            append("TOT: ")
                            val startIndex = length
                            append(cartItem.subtotalPrice.formatted)
                            addStyle(
                                SpanStyle(
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                }
            },
        )
    }
}

@Composable
private fun BaseCartItem(
    headerContent: @Composable RowScope.() -> Unit,
    mainBodyContent: @Composable ColumnScope.() -> Unit,
    qtyInitial: Double,
    freeQtyInitial: Double,
    onSaveQty: (Double?, Double?) -> Unit,
) {
    val qty = remember { mutableStateOf(qtyInitial) }
    val freeQty = remember { mutableStateOf(freeQtyInitial) }
    val mode = remember {
        mutableStateOf(
            if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) { headerContent() }
        Space(8.dp)
        when (mode.value) {
            BottomSectionMode.AddToCart, BottomSectionMode.Update -> mainBodyContent()
            BottomSectionMode.ConfirmQty -> Column(horizontalAlignment = Alignment.End) {
                val isError = (qty.value + freeQty.value) % 1 != 0.0 || freeQty.value > qty.value
                val wasError = remember { mutableStateOf(isError) }
                val wasErrorSaved = wasError.value
                val focusedError = remember(mode.value) { mutableStateOf(-1) }
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(maxWidth / 3)) {
                        EditField(
                            label = stringResource(id = R.string.qty),
                            qty = qty.value.toString(),
                            isError = isError && focusedError.value == 0,
                            onChange = { qty.value = it.toDouble() },
                            onFocus = { if (!wasErrorSaved && isError) focusedError.value = 0 },
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(maxWidth / 3)
                            .align(Alignment.BottomEnd)
                    ) {
                        EditField(
                            label = stringResource(id = R.string.free),
                            isEnabled = qty.value > 0.0,
                            isError = isError && focusedError.value == 1,
                            qty = freeQty.value.toString(),
                            onChange = { freeQty.value = it.toDouble() },
                            onFocus = { if (!wasErrorSaved && isError) focusedError.value = 1 },
                        )
                    }
                }
                if (isError) {
                    Space(8.dp)
                    Text(
                        text = stringResource(id = if (freeQty.value > qty.value) R.string.free_more_qty else R.string.invalid_qty),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = ConstColors.red,
                    )
                }
                wasError.value = isError
            }
        }
        Space(10.dp)
        Divider(color = ConstColors.ltgray)
        Space(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (mode.value) {
                BottomSectionMode.Select -> {
                    MedicoRoundButton(
                        text = stringResource(id = R.string.select),
                        onClick = { onSaveQty(null, null) },
                    )
                }
                BottomSectionMode.AddToCart -> {
                    MedicoRoundButton(
                        text = stringResource(id = R.string.add_to_cart),
                        onClick = { mode.value = BottomSectionMode.ConfirmQty },
                    )
                }
                BottomSectionMode.ConfirmQty -> {
                    MedicoRoundButton(
                        text = stringResource(id = R.string.cancel),
                        color = ConstColors.ltgray,
                        onClick = {
                            mode.value =
                                if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                            qty.value = qtyInitial
                            freeQty.value = freeQtyInitial
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxWidth()
                    )
                    MedicoRoundButton(
                        text = stringResource(id = R.string.confirm),
                        isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value > 0.0 && qty.value >= freeQty.value,
                        onClick = {
                            mode.value =
                                if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                            onSaveQty(qty.value, freeQty.value)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                BottomSectionMode.Update -> {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.weight(2f),
                    ) {
                        Text(
                            text = stringResource(id = R.string.qty).uppercase(),
                            fontSize = 12.sp,
                            color = ConstColors.gray,
                        )
                        Space(6.dp)
                        Text(
                            text = qty.value.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                            color = MaterialTheme.colors.background,
                        )
                        Space(6.dp)
                        Text(
                            text = "+${freeQty.value}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W700,
                            color = ConstColors.lightBlue,
                            modifier = Modifier
                                .background(
                                    ConstColors.lightBlue.copy(alpha = 0.05f),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, ConstColors.lightBlue, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                        )
                    }
                    MedicoRoundButton(
                        modifier = Modifier.weight(1.5f),
                        text = stringResource(id = R.string.update),
                        color = ConstColors.lightBlue,
                        contentColor = Color.White,
                        onClick = { mode.value = BottomSectionMode.ConfirmQty },
                    )
                }
            }
        }
    }
}

@Composable
fun TextItem(label: Int, count: Number) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = label),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = count.toString(),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
fun TextItemString(label: Int, count: String) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = label),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = count,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 6.dp)
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
        //unused go back button
     /*   MedicoButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            text = stringResource(id = R.string.go_back),
            isEnabled = true,
            onClick = onBack
        )*/
    }
}