package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SeasonBoyRetailer
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.data.WithTradeName
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.management.GeoLocation
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun BuyProductScreen(scope: BuyProductScope<WithTradeName>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoilImage(
                modifier = Modifier.size(71.dp),
                contentDescription = null,
                data = CdnUrlProvider.urlFor(scope.product.code, CdnUrlProvider.Size.Px123),
                error = { ItemPlaceholder() },
                loading = { ItemPlaceholder() },
            )
            Space(16.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = scope.product.name,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                )
                Space(4.dp)
                Row {
                    Text(
                        text = scope.product.code,
                        color = ConstColors.gray,
                        fontSize = 14.sp,
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
                        text = buildAnnotatedString {
                            append("Units: ")
                            val startIndex = length
                            append(scope.product.standardUnit.orEmpty())
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                    )
                }
                Space(4.dp)
                Text(
                    text = scope.product.uomName,
                    color = ConstColors.lightBlue,
                    fontSize = 14.sp,
                )
            }
        }
        (scope as? BuyProductScope.ChooseRetailer)?.sellerInfo?.let {
            Divider()
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 20.dp),
            ) {
                Text(
                    text = it.tradeName,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W500,
                    fontSize = 15.sp,
                )
                Space(10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("MRP: ")
                            val startIndex = length
                            append(it.priceInfo.mrp.formattedPrice)
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                    )
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Stock: ")
                            val startIndex = length
                            append(it.stockInfo.availableQty.toString())
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                    )
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Expiry: ")
                            val startIndex = length
                            append(it.stockInfo.expiry.formattedDate)
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.orange,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = it.priceInfo.price.formattedPrice,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                }
            }
        }
        Space(16.dp)
        val filter = scope.itemsFilter.flow.collectAsState()
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (filter.value.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.choose_seller),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            BasicTextField(
                value = filter.value,
                cursorBrush = SolidColor(ConstColors.lightBlue),
                onValueChange = { scope.filterItems(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colors.background,
                modifier = Modifier.size(24.dp).align(Alignment.CenterEnd),
            )
        }
        Space(12.dp)
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        Space(12.dp)
        val quantities = scope.quantities.flow.collectAsState()
        val sellers = scope.items.flow.collectAsState()
        sellers.value.forEach {
            when (it) {
                is SellerInfo -> SellerInfoItem(
                    product = scope.product,
                    sellerInfo = it,
                    isSelectable = (scope as? BuyProductScope.ChooseStockist)?.isSeasonBoy == true,
                    quantity = quantities.value[it] ?: 0,
                    onAddToCart = { scope.select(it) },
                    onInc = { scope.inc(it) },
                    onDec = { scope.dec(it) },
                )
                is SeasonBoyRetailer -> SeasonBoyReatilerInfoItem(
                    sellerInfo = (scope as BuyProductScope.ChooseRetailer).sellerInfo,
                    seasonBoyRetailer = it,
                    quantity = quantities.value[it] ?: 0,
                    onAddToCart = { scope.select(it) },
                    onInc = { scope.inc(it) },
                    onDec = { scope.dec(it) },
                )
            }
            Space(12.dp)
        }
    }
}

@Composable
private fun SellerInfoItem(
    product: ProductSearch,
    sellerInfo: SellerInfo,
    quantity: Int,
    isSelectable: Boolean,
    onAddToCart: () -> Unit,
    onInc: () -> Unit,
    onDec: () -> Unit,
) {
    BaseSellerItem(
        stockStatus = sellerInfo.stockInfo.status,
        sellerName = sellerInfo.tradeName,
        mainBodyContent = {
            Text(
                text = sellerInfo.tradeName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Space(4.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = sellerInfo.priceInfo.price.formattedPrice,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = product.code,
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                    Space(4.dp)
                    val expiry = sellerInfo.stockInfo.expiry
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
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = buildAnnotatedString {
                            append("MRP: ")
                            val startIndex = length
                            append(sellerInfo.priceInfo.mrp.formattedPrice)
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = buildAnnotatedString {
                            append("Margin: ")
                            val startIndex = length
                            append(sellerInfo.priceInfo.marginPercent)
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = buildAnnotatedString {
                            append("Stock: ")
                            val startIndex = length
                            append(sellerInfo.stockInfo.availableQty.toString())
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.lightBlue,
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
            }
        },
        onTopOfDivider = {
            GeoLocation(
                sellerInfo.geoData.fullAddress(),
                isBold = true,
                textSize = 12.sp,
                tint = MaterialTheme.colors.background,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${sellerInfo.geoData.distance} km",
                    color = ConstColors.lightBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                )
                Space(8.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = ConstColors.lightBlue,
                        modifier = Modifier.size(10.dp),
                    )
                    Space(4.dp)
                    val activity = LocalContext.current as MainActivity
                    Text(
                        text = stringResource(id = R.string.map_location),
                        color = ConstColors.lightBlue,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.clickable {
                            sellerInfo.geoData.origin.let {
                                activity.openMaps(it.latitude, it.longitude)
                            }
                        }
                    )
                }
            }
        },
        onBottomOfDivider = {
            if (isSelectable) {
                MedicoSmallButton(
                    text = stringResource(id = R.string.select),
                    widthModifier = { fillMaxWidth() },
                    isEnabled = true,
                    onClick = onAddToCart,
                )
            } else {
                PlusMinusQuantity(
                    quantity = quantity,
                    max = sellerInfo.stockInfo.availableQty,
                    onInc = onInc,
                    onDec = onDec,
                )
                MedicoSmallButton(
                    text = stringResource(id = R.string.add_to_cart),
                    isEnabled = quantity > 0,
                    onClick = onAddToCart,
                )
            }
        },
    )
}

@Composable
private fun SeasonBoyReatilerInfoItem(
    sellerInfo: SellerInfo,
    seasonBoyRetailer: SeasonBoyRetailer,
    quantity: Int,
    onAddToCart: () -> Unit,
    onInc: () -> Unit,
    onDec: () -> Unit,
) {
    BaseSellerItem(
        stockStatus = sellerInfo.stockInfo.status,
        sellerName = seasonBoyRetailer.tradeName,
        mainBodyContent = {
            Text(
                text = seasonBoyRetailer.tradeName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Space(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GeoLocation(
                    seasonBoyRetailer.fullAddress(),
                    isBold = true,
                    textSize = 12.sp,
                    tint = MaterialTheme.colors.background,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = ConstColors.lightBlue,
                        modifier = Modifier.size(10.dp),
                    )
                    Space(4.dp)
                    val activity = LocalContext.current as MainActivity
                    Text(
                        text = stringResource(id = R.string.map_location),
                        color = ConstColors.lightBlue,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.clickable {
                            sellerInfo.geoData.origin.let {
                                activity.openMaps(it.latitude, it.longitude)
                            }
                        }
                    )
                }
            }
        },
        onTopOfDivider = null,
        onBottomOfDivider = {
            PlusMinusQuantity(
                quantity = quantity,
                max = sellerInfo.stockInfo.availableQty,
                onInc = onInc,
                onDec = onDec,
            )
            MedicoSmallButton(
                text = stringResource(id = R.string.add_to_cart),
                isEnabled = quantity > 0,
                onClick = onAddToCart,
            )
        },
    )
}

@Composable
private fun BaseSellerItem(
    stockStatus: StockStatus,
    sellerName: String,
    mainBodyContent: @Composable ColumnScope.() -> Unit,
    onTopOfDivider: @Composable (RowScope.() -> Unit)?,
    onBottomOfDivider: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        Box {
            val labelColor = when (stockStatus) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CoilImage(
                        modifier = Modifier.size(65.dp),
                        contentDescription = null,
                        data = "",
                        error = { UserLogoPlaceholder(sellerName) },
                        loading = { UserLogoPlaceholder(sellerName) },
                    )
                    Space(16.dp)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        mainBodyContent()
                    }
                }
                if (onTopOfDivider != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        onTopOfDivider()
                    }
                }
                Space(10.dp)
                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    onBottomOfDivider()
                }
            }
            val maxWidth =
                LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp - 5.dp
            Box(
                modifier = Modifier.matchParentSize().padding(end = maxWidth).background(labelColor)
            )
        }
    }
}

@Composable
fun PlusMinusQuantity(
    modifier: Modifier = Modifier,
    quantity: Int,
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
            tint = if (quantity > 0) ConstColors.lightBlue else ConstColors.gray.copy(
                alpha = 0.5f
            ),
            contentDescription = null,
            modifier = if (quantity > 0) Modifier.clickable(onClick = onDec) else Modifier,
        )
        Space(12.dp)
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
        )
        Space(12.dp)
        Icon(
            imageVector = Icons.Default.Add,
            tint = if (quantity < max) ConstColors.lightBlue else ConstColors.gray.copy(alpha = 0.5f),
            contentDescription = null,
            modifier = if (quantity < max) Modifier.clickable(onClick = onInc) else Modifier,
        )
    }
}