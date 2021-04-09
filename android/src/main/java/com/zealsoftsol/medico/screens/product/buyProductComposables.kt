package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.management.GeoLocation
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun BuyProductScreen(scope: BuyProductScope) {
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
                verticalArrangement = Arrangement.SpaceEvenly,
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
        Space(16.dp)
        val filter = scope.sellersFilter.flow.collectAsState()
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
                onValueChange = { scope.filterSellers(it) },
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
        val sellers = scope.sellersInfo.flow.collectAsState()
        sellers.value.forEach {
            SellerInfoItem(
                product = scope.product,
                sellerInfo = it,
                quantity = quantities.value[it] ?: 0,
                onAddToCart = { scope.addToCart(it) },
                onInc = { scope.inc(it) },
                onDec = { scope.dec(it) },
            )
            Space(12.dp)
        }
    }
}

@Composable
private fun SellerInfoItem(
    product: ProductSearch,
    sellerInfo: SellerInfo,
    quantity: Int,
    onAddToCart: () -> Unit,
    onInc: () -> Unit,
    onDec: () -> Unit,
) {
    val h = 180.dp
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
            .height(h)
            .padding(horizontal = 16.dp),
    ) {
        Box {
            val labelColor = when (sellerInfo.stockInfo.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LOW_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
                null -> ConstColors.gray
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
                        error = { UserLogoPlaceholder(sellerInfo.tradeName) },
                        loading = { UserLogoPlaceholder(sellerInfo.tradeName) },
                    )
                    Space(16.dp)
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                                Text(
                                    text = buildAnnotatedString {
                                        append("Expiry: ")
                                        val startIndex = length
                                        append(sellerInfo.stockInfo.expireDate)
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
                                    fontSize = 12.sp,
                                )
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
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GeoLocation(
                        sellerInfo.geoData.fullAddress(),
                        isBold = true,
                        textSize = 12.sp,
                        tint = MaterialTheme.colors.background,
                    )
                    Row {
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
                }
                Space(10.dp)
                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.defaultMinSize(minWidth = 100.dp),
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
                            tint = if (quantity < sellerInfo.stockInfo.availableQty) ConstColors.lightBlue else ConstColors.gray.copy(
                                alpha = 0.5f
                            ),
                            contentDescription = null,
                            modifier = if (quantity < sellerInfo.stockInfo.availableQty) Modifier.clickable(
                                onClick = onInc
                            ) else Modifier,
                        )
                    }
                    MedicoSmallButton(
                        text = stringResource(id = R.string.add_to_cart),
                        isEnabled = quantity > 0,
                        onClick = onAddToCart,
                    )
                }
            }
            Box(modifier = Modifier.width(5.dp).height(h).background(labelColor))
        }
    }
}