package com.zealsoftsol.medico.screens.instore

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreProductsScope
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.PromotionData
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.cart.TextItem
import com.zealsoftsol.medico.screens.cart.TextItemString
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.product.BottomSectionMode
import com.zealsoftsol.medico.screens.product.NeededSurface
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun InStoreProductsScreen(scope: InStoreProductsScope) {
    remember { scope.firstLoad() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ConstColors.newDesignGray)
            .padding(top = 16.dp)
    ) {
        val cart = scope.cart.flow.collectAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextItem(R.string.items, cart.value?.entries.orEmpty().size)
            TextItem(
                R.string.qty,
                cart.value?.entries.orEmpty().sumOf { it.quantity.value })
            TextItem(
                R.string.free,
                cart.value?.entries.orEmpty().sumOf { it.freeQty.value })
            TextItemString(R.string.amount, cart.value?.total?.formattedPrice.orEmpty())
            MedicoRoundButton(
                text = stringResource(id = R.string.view_order),
                wrapTextSize = true,
                isEnabled = cart.value?.id != null,
            ) {
                scope.goToInStoreCart()
            }
        }
        Space(dp = 8.dp)
        val search = scope.searchText.flow.collectAsState()
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_products,
            searchBarEnd = SearchBarEnd.Eraser,
            icon = Icons.Default.Search,
            elevation = 0.dp,
            horizontalPadding = 16.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
        )
        Space(dp = 4.dp)
        val items = scope.items.flow.collectAsState()
        if (items.value.isEmpty() && scope.items.updateCount > 0) {
//            NoRecords(
//                icon = R.drawable.ic_missing_invoices,
//                text = R.string.missing_invoices,
//                onHome = { scope.goHome() },
//            )
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(top = 4.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { index, item ->
                        ProductItem(item) { scope.selectItem(item) }
                        if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadItems()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ProductItem(
    item: InStoreProduct,
    onItemClick: () -> Unit,
) {
    BaseItem(
        qtyInitial = item.stockInfo.availableQty.toDouble(),
        freeQtyInitial = 0.0,
        promotionData = null,//item.sellerInfo?.promotionData?.takeIf { item.sellerInfo?.isPromotionActive == true },
        forceMode = if (item.order?.isEmpty() != false) BottomSectionMode.AddToCart else BottomSectionMode.Update,
        onItemClick = onItemClick,
        canAddToCart = item.stockInfo.status != StockStatus.OUT_OF_STOCK,
        headerContent = {
            val labelColor = when (item.stockInfo.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
            }
            Canvas(modifier = Modifier.size(10.dp)) {
                drawRoundRect(labelColor, cornerRadius = CornerRadius(8.dp.value))
            }
            Space(6.dp)
            Text(
                text = item.name,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        mainBodyContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("PTR: ")
                        val startIndex = length
                        append(item.priceInfo.price.formattedPrice)
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
                Text(
                    text = buildAnnotatedString {
                        append("Stock: ")
                        val startIndex = length
                        append(item.stockInfo.availableQty.toString())
                        addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.W800
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                )
            }
            Space(8.dp)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                item.stockInfo.expiry.let { expiry ->
                    val color = Color(expiry.color.toColorInt())
                    Box(
                        modifier = Modifier.background(
                            color = color.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Expiry: ")
                                val startIndex = length
                                append(expiry.formattedDate)
                                addStyle(
                                    SpanStyle(
                                        color = color,
                                        fontWeight = FontWeight.W800
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
                Space(dp = 1.dp)
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .background(
//                            color = ConstColors.lightBlue.copy(alpha = 0.1f),
//                            shape = MaterialTheme.shapes.small
//                        )
//                        .padding(4.dp),
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.LocationOn,
//                        contentDescription = null,
//                        tint = ConstColors.lightBlue,
//                        modifier = Modifier.size(10.dp),
//                    )
//                    Space(4.dp)
//                    Text(
//                        text = "0 km",//"${item.sellerInfo?.geoData?.distance} km",
//                        color = ConstColors.lightBlue,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.W600,
//                    )
//                }
            }
        },
    )
}

@Composable
private fun BaseItem(
    headerContent: @Composable RowScope.() -> Unit,
    mainBodyContent: @Composable ColumnScope.() -> Unit,
    qtyInitial: Double,
    freeQtyInitial: Double,
    canAddToCart: Boolean,
    promotionData: PromotionData? = null,
    forceMode: BottomSectionMode? = null,
    onItemClick: () -> Unit,
) {
    val qty = remember { mutableStateOf(qtyInitial) }
    val freeQty = remember { mutableStateOf(freeQtyInitial) }
    val mode = remember {
        mutableStateOf(
            forceMode
                ?: if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
        )
    }

    Space(4.dp)
    NeededSurface(null) {
        Box {
            promotionData?.let {
                Text(
                    text = it.displayLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    modifier = Modifier
                        .height(16.dp)
                        .background(ConstColors.red, CutCornerShape(topStart = 16.dp))
                        .padding(horizontal = 16.dp)
                        .padding(start = 16.dp)
                        .align(Alignment.TopEnd)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) { headerContent() }
                Space(16.dp)
                when (mode.value) {
                    BottomSectionMode.Select, BottomSectionMode.AddToCart, BottomSectionMode.Update -> mainBodyContent()
                    BottomSectionMode.ConfirmQty -> Column(horizontalAlignment = Alignment.End) {
                        val isError =
                            (qty.value + freeQty.value) % 1 != 0.0 || freeQty.value > qty.value
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
                                    onFocus = {
                                        if (!wasErrorSaved && isError) focusedError.value = 0
                                    },
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
                                    onFocus = {
                                        if (!wasErrorSaved && isError) focusedError.value = 1
                                    },
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
                Space(12.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (mode.value) {
                        BottomSectionMode.Select -> {
                            MedicoRoundButton(
                                text = stringResource(id = R.string.select),
                                onClick = { },
                            )
                        }
                        BottomSectionMode.AddToCart -> {
                            MedicoRoundButton(
                                text = stringResource(id = R.string.add_to_cart),
                                onClick = onItemClick,
                                color = if (canAddToCart) ConstColors.yellow else Color.LightGray,
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
                                        .border(
                                            1.dp,
                                            ConstColors.lightBlue,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                )
                            }
                            MedicoRoundButton(
                                modifier = Modifier.weight(1.5f),
                                text = stringResource(id = R.string.update),
                                color = ConstColors.lightBlue,
                                contentColor = Color.White,
                                onClick = onItemClick,
                            )
                        }
                    }
                }
            }
        }
    }
    Space(4.dp)
}