package com.zealsoftsol.medico.screens.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Error
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
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlertDialog
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.product.BottomSectionMode

@SuppressLint("RememberReturnType")
@ExperimentalMaterialApi
@Composable
fun CartScreen(scope: CartScope) {
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()
    val isContinueEnabled = scope.isContinueEnabled.flow.collectAsState()
    val isPreviewEnabled = scope.isPreviewEnabled.flow.collectAsState()
    val showErrorAlert = remember { mutableStateOf(false) }
    var stockistName = remember {
        mutableStateOf("")
    }
    remember {
        scope.updatePreviewStatus(false)
    }

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
                Space(10.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 10.dp, end = 10.dp)
                        .background(ConstColors.lightBackground, RoundedCornerShape(25.dp)),
                ) {

                    if (!isPreviewEnabled.value) {
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
                    //Divider(modifier = Modifier.align(Alignment.BottomCenter))
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
                                    stockistName.value = value.sellerName
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
                                        openBottomSheet = { item, qty, freeQty ->
                                            scope.openBottomSheet(
                                                qty,
                                                freeQty,
                                                item = item,
                                                sellerCart = value,
                                                cartScope = scope
                                            )
                                        },
                                        isPreview = isPreviewEnabled.value
                                    )
                                }
                            }
                        )
                    }
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
                    //Divider()
                    Space(8.dp)
                    /*Text(
                        text = stringResource(id = R.string.earn_scratch),
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        color = ConstColors.gray,
                    )*/
                    //Space(4.dp)
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


                        /* Space(4.dp)
                         Text(
                             text = it.formattedPrice,
                             fontWeight = FontWeight.W700,
                             fontSize = 16.sp,
                             color = MaterialTheme.colors.background,
                         )*/

                        MedicoButton(
                            modifier = Modifier.weight(0.3f),
                            text = if (isPreviewEnabled.value) {
                                stringResource(id = R.string.place_order_)
                            } else {
                                stringResource(id = R.string.preview)
                            },
                            isEnabled = isContinueEnabled.value,
                            onClick = {
                                if (isPreviewEnabled.value) {
                                    scope.placeOrder(scope)

                                } else {
                                    showErrorAlert.value = true
                                }
                            },
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

    if (showErrorAlert.value)
        ShowAlertDialog(
            message = "Please check again before placing order with",
            stockistName.value,
            { showErrorAlert.value = false }
        ) {
            scope.updatePreviewStatus(true)
            showErrorAlert.value = false
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
    openBottomSheet: (CartItem, Double, Double) -> Unit,
    isPreview: Boolean = false
) {
    FoldableItem(
        expanded = expand,
        headerBackground = Color.White,
        headerBorder = BorderStroke(0.dp, Color.Transparent),
        headerMinHeight = 60.dp,
        header = { _ ->
            Surface(
                color = ConstColors.lightBackground,
                shape = RoundedCornerShape(25.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Space(12.dp)
                    if (!isPreview) {
                        Surface(
                            color = Color.Transparent,
                            onClick = onRemoveSeller,
                            modifier = Modifier.size(40.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {


                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = null,
                                    tint = ConstColors.red,
                                )
                            }
                        }
                    }
                    Space(12.dp)
                    Column {
                        Text(
                            text = sellerCart.sellerName,
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(2.dp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.payment_method))
                                    append(": ")
                                    val startIndex = length
                                    append(sellerCart.paymentMethod.serverValue)
                                    addStyle(
                                        SpanStyle(color = ConstColors.green),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray,
                                fontWeight = FontWeight.W500,
                                fontSize = 12.sp,
                            )
                            /*Space(4.dp)
                     Box(
                         modifier = Modifier
                             .height(10.dp)
                             .width(1.dp)
                             .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                     )
                     Space(4.dp)*/

                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.total_))
                                    val startIndex = length
                                    append(": ")
                                    append(sellerCart.total.formattedPrice)
                                    addStyle(
                                        SpanStyle(color = MaterialTheme.colors.background),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray,
                                fontWeight = FontWeight.W500,
                                fontSize = 12.sp,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
        childItems = sellerCart.items,
        hasItemLeadingSpacing = false,
        hasItemTrailingSpacing = false,
        itemSpacing = 8.dp,
        itemHorizontalPadding = 0.dp,
        itemsBackground = Color.White,
        item = { value, _ ->
            CartItem(
                cartItem = value,
                onSaveQty = { qty, freeQty -> onSaveQty(value, qty!!, freeQty!!) },
                onRemove = { onRemoveItem(value) },
                openBottomSheet = { item, qty, freeQty -> openBottomSheet(item, qty!!, freeQty!!) },
                isPreview = isPreview
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
    openBottomSheet: (CartItem, Double?, Double?) -> Unit,
    isPreview: Boolean = false
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
        shape = RoundedCornerShape(5.dp),
        color = Color.White,
        elevation = 5.dp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!isPreview) {

                Space(dp = 16.dp)

                Surface(shape = RoundedCornerShape(5.dp), color = Color.White, elevation = 5.dp) {
                    CoilImage(
                        src = CdnUrlProvider.urlFor(
                            cartItem.imageCode,
                            CdnUrlProvider.Size.Px123
                        ),
                        size = 90.dp,
                        onError = { ItemPlaceholder() },
                        onLoading = { ItemPlaceholder() },
                    )
                }
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

                            if (!isPreview) {
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
                            } else {
                                Space(dp = 24.dp)
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {


                            if (cartItem.quotedData?.isAvailable != false) {
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
                                    append("MRP: ")
                                    val startIndex = length
                                    append(cartItem.mrp.formatted)
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
                                    append(cartItem.freeQuantity.formatted)
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
                openBottomSheet = openBottomSheet,
                cartItem = cartItem,
                isPreview = isPreview
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
    openBottomSheet: (CartItem, Double?, Double?) -> Unit,
    cartItem: CartItem,
    isPreview: Boolean = false
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
            /* BottomSectionMode.ConfirmQty -> Column(horizontalAlignment = Alignment.End) {
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
             }*/
        }

        Space(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            when (mode.value) {
                BottomSectionMode.Select -> {
                    if (!isPreview) {
                        MedicoButton(
                            text = stringResource(id = R.string.select),
                            onClick = { onSaveQty(null, null) },
                            color = ConstColors.yellow,
                            contentColor = MaterialTheme.colors.background,
                            isEnabled = true,
                            height = 35.dp
                        )
                    }
                }
                BottomSectionMode.AddToCart -> {
                    if (!isPreview) {
                        MedicoButton(
                            text = stringResource(id = R.string.add_to_cart),
                            onClick = { mode.value = BottomSectionMode.ConfirmQty },
                            color = ConstColors.yellow,
                            contentColor = MaterialTheme.colors.background,
                            isEnabled = true,
                            height = 35.dp
                        )
                    }
                }
                BottomSectionMode.ConfirmQty -> {
                    if (!isPreview) {
                        MedicoButton(
                            text = stringResource(id = R.string.cancel),
                            color = ConstColors.ltgray,
                            contentColor = MaterialTheme.colors.background,
                            onClick = {
                                mode.value =
                                    if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                qty.value = qtyInitial
                                freeQty.value = freeQtyInitial
                            },
                            modifier = Modifier.weight(1f),
                            isEnabled = true,
                            height = 35.dp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(0.2f)
                                .fillMaxWidth()
                        )
                        MedicoButton(
                            text = stringResource(id = R.string.confirm),
                            isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value > 0.0 && qty.value >= freeQty.value,
                            onClick = {
                                mode.value =
                                    if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                onSaveQty(qty.value, freeQty.value)
                            },
                            modifier = Modifier.weight(1f),
                            color = ConstColors.yellow,
                            contentColor = MaterialTheme.colors.background,
                            height = 35.dp
                        )
                    }
                }
                BottomSectionMode.Update -> {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.weight(2f),
                    ) {
                        if (cartItem.isPromotionActive || !isPreview) {
                            cartItem.promotionData?.let {
                                Text(
                                    text = it.displayLabel,
                                    fontSize = 14.sp,
                                    color = ConstColors.red,
                                )
                            }
                        }

                        Text(
                            text = buildAnnotatedString {
                                append("ST: ")
                                val startIndex = length
                                append(cartItem.subtotalPrice.formatted)
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
                    if (!isPreview) {
                        MedicoButton(
                            modifier = Modifier.weight(1.5f),
                            text = stringResource(id = R.string.update),
                            color = ConstColors.yellow,
                            contentColor = MaterialTheme.colors.background,
                            onClick = { openBottomSheet(cartItem, qtyInitial, freeQtyInitial) },
                            isEnabled = true,
                            height = 35.dp
                        )
                    }
                }
            }
        }
        if (isPreview && cartItem.isPromotionActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
            ) {

                cartItem.promotionData?.let {
                    Text(
                        text = it.displayLabel,
                        fontSize = 14.sp,
                        color = ConstColors.red,
                    )

                }
            }
        }
    }
}

@Composable
fun TextItem(label: Int, count: Number, txtColor: Color = MaterialTheme.colors.background) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = label),
            color = txtColor,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = count.toString(),
            color = txtColor,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TextItemString(label: Int, count: String, txtColor: Color = MaterialTheme.colors.background) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = label),
            color = txtColor,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = count,
            color = txtColor,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp,
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