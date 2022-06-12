package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
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
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreCartScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.InStoreCartEntry
import com.zealsoftsol.medico.screens.cart.TextItem
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.product.BottomSectionMode

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InStoreCartScreen(scope: InStoreCartScope) {
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()
    val showErrorAlert = remember { mutableStateOf(false) }

    if (items.value.isEmpty()) {
        EmptyCart { scope.goHome() }
    } else {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Space(10.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 10.dp, end = 10.dp)
                        .background(ConstColors.lightBackground, RoundedCornerShape(25.dp)),
                ) {

                    Surface(
                        color = Color.Transparent,
                        onClick = { scope.clearCart() },
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterEnd),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = null,
                                tint = ConstColors.red,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(end = 58.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        TextItem(R.string.items, items.value.size)
                        TextItem(
                            R.string.qty,
                            items.value.sumOf { it.quantity.value })
                        TextItem(
                            R.string.free,
                            items.value.sumOf { it.freeQty.value })
                    }
                }
            }
            BoxWithConstraints {
                LazyColumn(
                    state = rememberLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(maxHeight - 80.dp),
                ) {
                    items(
                        items.value,
                        key = { it.id },
                        itemContent = { value ->
                            CartItem(
                                cartItem = value,
                                onSaveQty = { qty, freeQty ->
                                    scope.updateItemCount(
                                        value,
                                        qty!!,
                                        freeQty!!
                                    )
                                },
                                onRemove = { scope.removeItem(value) },
                            )
                        }
                    )
                }
            }
            Space(10.dp)
            total.value?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Space(8.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(0.3f)) {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.total))
                                    val startIndex = length
                                    append(it.formattedPrice)
                                    if (it.formattedPrice != "N/A") {
                                        append("*")
                                    }

                                    addStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colors.background,
                                            fontWeight = FontWeight.W700
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                fontWeight = FontWeight.W500,
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.background,
                            )

                            if (it.formattedPrice != "N/A") {
                                Space(2.dp)
                                Text(
                                    text = stringResource(id = R.string.tax_exclusive),
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                )
                            }
                        }

                        MedicoButton(
                            modifier = Modifier.weight(0.3f),
                            text = stringResource(id = R.string.complete_order),
                            isEnabled = true,
                            onClick = { scope.continueWithCart() },
                            height = 35.dp,
                            color = ConstColors.yellow,
                            contentColor = MaterialTheme.colors.background,
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
private fun CartItem(
    cartItem: InStoreCartEntry,
    onSaveQty: (Double?, Double?) -> Unit,
    onRemove: () -> Unit,
) {
    val labelColor = ConstColors.green
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.White,
        elevation = 5.dp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Space(dp = 16.dp)
            Surface(shape = RoundedCornerShape(5.dp), color = Color.White, elevation = 5.dp) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(
                        cartItem.imageCode ?: "",
                        CdnUrlProvider.Size.Px123
                    ),
                    size = 90.dp,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                )
            }
            BaseCartItem(
                qtyInitial = cartItem.quantity.value,
                freeQtyInitial = cartItem.freeQty.value,
                onSaveQty = onSaveQty,
                headerContent = {
                    Column {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Canvas(modifier = Modifier.size(10.dp)) {
                                    drawRoundRect(
                                        labelColor,
                                        cornerRadius = CornerRadius(8.dp.value)
                                    )
                                }
                                Space(8.dp)
                                Text(
                                    text = cartItem.productName,
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W700,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(end = 30.dp)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.dp, ConstColors.red),
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
                    }
                },
                mainBodyContent = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = buildAnnotatedString {
                                    append("PTR: ")
                                    val startIndex = length
                                    append(cartItem.price.formatted)
                                    addStyle(
                                        SpanStyle(
                                            color = ConstColors.lightGreen,
                                            fontWeight = FontWeight.W700
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                            )

                            Text(
                                text = buildAnnotatedString {
                                    append("MRP: ")
                                    val startIndex = length
                                    append(cartItem.mrp?.formatted ?: "")
                                    addStyle(
                                        SpanStyle(
                                            color = ConstColors.lightGreen,
                                            fontWeight = FontWeight.W700
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                            )
                        }

                        Space(10.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("QTY: ")
                                    val startIndex = length
                                    append(cartItem.quantity.formatted)
                                    addStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colors.background,
                                            fontWeight = FontWeight.W700
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                            )

                            Text(
                                text = buildAnnotatedString {
                                    append("FREE: ")
                                    val startIndex = length
                                    append(cartItem.freeQty.formatted)
                                    addStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colors.background,
                                            fontWeight = FontWeight.W700
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                            )
                        }

                    }
                },
            )
        }
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
                    MedicoButton(
                        text = stringResource(id = R.string.cancel),
                        contentColor = MaterialTheme.colors.background,
                        height = 35.dp,
                        isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value > 0.0 && qty.value >= freeQty.value,
                        color = ConstColors.lightGrey,
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
                    MedicoButton(
                        text = stringResource(id = R.string.confirm),
                        color = ConstColors.yellow,
                        contentColor = MaterialTheme.colors.background,
                        height = 35.dp,
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
                        /*Text(
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
                        )*/
                    }
                    MedicoButton(
                        modifier = Modifier.weight(1.5f),
                        text = stringResource(id = R.string.update),
                        color = ConstColors.yellow,
                        contentColor = MaterialTheme.colors.background,
                        onClick = { mode.value = BottomSectionMode.ConfirmQty },
                        isEnabled = true,
                        height = 35.dp
                    )
                }
            }
        }
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