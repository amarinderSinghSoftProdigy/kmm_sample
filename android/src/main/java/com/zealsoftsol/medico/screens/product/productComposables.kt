package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.ChipString

@Composable
fun ProductScreen(scope: ProductInfoScope) {
    val product = scope.product
    Box(modifier = Modifier.background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Space(25.dp)
            Surface(modifier = Modifier.align(CenterHorizontally), shape = CircleShape, elevation = 5.dp) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(product.imageCode, CdnUrlProvider.Size.Px320),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(180.dp)
                        .align(CenterHorizontally)
                        .clickable{
                            scope.zoomImage(product.imageCode)
                        },
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    isCrossFadeEnabled = false
                )
            }
            Space(16.dp)

            Text(
                text = product.name,
                color = Color.Black,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
            )
            Space(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.manufacturer,
                    color = Color.Black,
                    fontSize = 13.sp,
                )

                product.stockInfo?.let {
                    Text(
                        text = it.formattedStatus,
                        color = when (it.status) {
                            StockStatus.IN_STOCK -> ConstColors.green
                            StockStatus.LIMITED_STOCK -> ConstColors.orange
                            StockStatus.OUT_OF_STOCK -> ConstColors.red
                        },
                        fontWeight = FontWeight.W700,
                        fontSize = 13.sp,
                    )
                }
            }
            Space(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("PTR: ")
                        val startIndex = length
                        append(product.formattedPrice ?: "")
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontSize = 13.sp,
                )

                Text(
                    text = buildAnnotatedString {
                        append("MRP: ")
                        val startIndex = length
                        append(product.formattedMrp)
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontSize = 13.sp,
                )
            }

            Space(8.dp)

            val sliderList = ArrayList<String>()
            product.manufacturer.let { sliderList.add(it) }
            if (product.drugFormName.isNotEmpty())
                sliderList.add(product.drugFormName)
            product.standardUnit?.let { sliderList.add(it) }
            if (product.compositions.isNotEmpty())
                sliderList.addAll(product.compositions)
            product.sellerInfo?.priceInfo?.marginPercent?.let {
                sliderList.add(
                    "Margin: ".plus(
                        it
                    )
                )
            }
            LazyRow(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(top = 6.dp),
            ) {
                items(
                    items = sliderList,
                    itemContent = { value -> if (value.isNotEmpty()) ChipString(value) {} }
                )
            }

            Space(16.dp)

            Text(
                text = stringResource(id = R.string.variants),
                color = Color.Black,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
            )
            Space(8.dp)
            FoldableItem(
                expanded = false,
                elevation = 3.dp,
                headerBackground = Color.White,
                header = { isExpanded ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = product.name,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            modifier = Modifier
                                .rotate(if (isExpanded) 270f else 90f),
                            contentDescription = null
                        )
                    }
                },
                childItems = scope.variants,
                itemSpacing = 0.dp,
                itemHorizontalPadding = 0.dp,
                item = { item, _ ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable { scope.selectVariantProduct(item) }
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item.name,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            )
            Space(25.dp)
            val context = LocalContext.current
            when (product.buyingOption) {
                BuyingOption.BUY -> {
                    MedicoButton(
                        text = stringResource(id = R.string.add_to_cart),
                        isEnabled = true
                    ) {
                        if (!scope.buy()) {
                            context.toast(R.string.something_went_wrong)
                        }
                    }
                }
                null -> {
                    MedicoButton(
                        text = stringResource(id = R.string.add_to_cart),
                        isEnabled = false,
                        onClick = {})
                }
                BuyingOption.QUOTE -> {
                    Button(
                        onClick = {
                            if (!scope.buy()) {
                                context.toast(R.string.something_went_wrong)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            disabledBackgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.background,
                            disabledContentColor = MaterialTheme.colors.background,
                        ),
                        elevation = null,
                        enabled = true,
                        border = BorderStroke(2.dp, ConstColors.yellow),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.get_quote),
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                }
            }
            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(indication = null) { scope.toggleDetails() }
            ) {
                Text(
                    text = stringResource(id = R.string.details),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
                Icon(
                    imageVector = if (isDetailsOpened.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
            if (isDetailsOpened.value) {
                Space(24.dp)
                Column {
                    ProductDetail(
                        title = stringResource(id = R.string.manufacturer),
                        description = product.manufacturer,
                    )
                    Space(12.dp)
                    ProductDetail(
                        title = stringResource(id = R.string.compositions),
                        description = scope.compositionsString,
                    )
                    Space(12.dp)
                    ProductDetail(
                        title = stringResource(id = R.string.storage),
                        description = stringResource(id = R.string.storage_desc),
                    )
                }
            }*/
            if (scope.alternativeBrands.isNotEmpty()) {
                Space(24.dp)
                Text(
                    text = "${stringResource(id = R.string.alternative_brands)} ${product.sellerInfo?.tradeName} ",
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                )
                Space(8.dp)
                LazyRow {
                    itemsIndexed(
                        items = scope.alternativeBrands,
                        itemContent = { _, item ->
                            ProductAlternative(item) { scope.selectAlternativeProduct(item) }
                        },
                    )
                }

                Space(20.dp)
            }
        }
    }
}

@Composable
private fun ProductDetail(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
        )
        Space(4.dp)
        Text(
            text = description,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun ProductAlternative(product: AlternateProductData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(end = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CoilImageBrands(
                src = "",
                contentScale = ContentScale.Crop,
                onError = { ItemPlaceholder() },
                onLoading = { ItemPlaceholder() },
                height = 80.dp,
                width = 120.dp,
            )
            Text(
                text = product.name,
                color = ConstColors.gray,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
            )
            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append("PTR: ")
                    val startIndex = length
                    append(product.priceRange)
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.Bold
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.gray,
                fontSize = 16.sp,
            )
            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append("MRP: ")
                    val startIndex = length
                    append(product.priceRange)
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.Bold
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.gray,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
            )
        }
        Text(
            text = product.availableVariants,
            color = ConstColors.gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}