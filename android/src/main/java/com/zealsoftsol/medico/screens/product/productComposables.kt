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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.checkOffer
import com.zealsoftsol.medico.screens.search.ChipString
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProductScreen(scope: ProductInfoScope) {
    val product = scope.product
    val showButtons = scope.showButton.flow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val enableButton = scope.enableButton.flow.collectAsState()
    val showToast = scope.showToast.flow.collectAsState()
    val cartData = scope.cartData.flow.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(modifier = Modifier.background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Space(25.dp)
            Surface(
                modifier = Modifier.align(CenterHorizontally),
                shape = CircleShape,
                elevation = 5.dp
            ) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(product.imageCode, CdnUrlProvider.Size.Px320),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(180.dp)
                        .align(CenterHorizontally)
                        .clickable {
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

            if (!showButtons.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    //todo un comment and consume view stockist API to display list of stockists
                   /* if (scope.userType == UserType.RETAILER) {
                        MedicoButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            text = stringResource(id = R.string.view_stockist),
                            isEnabled = true
                        ) {
                            scope.showConnectedStockist(product.viewStockist)
                        }
                        Space(50.dp)
                    }*/
                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        text = stringResource(id = R.string.add_to_cart),
                        isEnabled = true
                    ) {
                        scope.openButtons()
                    }
                }
            }

            if (showButtons.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        /*.focusRequester(focusRequester)*/
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Box(modifier = Modifier.width(120.dp)) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.qty).uppercase(),
                                        color = ConstColors.gray,
                                        fontSize = 12.sp,
                                    )

                                    val wasQty = remember {
                                        mutableStateOf(
                                            if (scope.product.quantity.toString().split(".")
                                                    .lastOrNull() == "0"
                                            ) scope.product.quantity.toString().split(".")
                                                .first() else scope.product.quantity.toString()
                                        )
                                    }

                                    BasicTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusEvent {
                                                if (it.isFocused) coroutineScope.launch {
                                                    scrollState.scrollTo(Int.MAX_VALUE)
                                                }
                                            },
                                        value = TextFieldValue(
                                            wasQty.value,
                                            selection = TextRange(wasQty.value.length)
                                        ),
                                        onValueChange = {
                                            val split =
                                                it.text.replace(",", ".").split(".")
                                            val beforeDot = split[0]
                                            val afterDot = split.getOrNull(1)
                                            var modBefore =
                                                beforeDot.toIntOrNull() ?: 0
                                            val modAfter = when (afterDot?.length) {
                                                0 -> "."
                                                in 1..Int.MAX_VALUE -> when (afterDot!!.take(
                                                    1
                                                ).toIntOrNull()) {
                                                    0 -> ".0"
                                                    in 1..4 -> ".0"
                                                    5 -> ".5"
                                                    in 6..9 -> {
                                                        modBefore++
                                                        ".0"
                                                    }
                                                    null -> ""
                                                    else -> throw UnsupportedOperationException(
                                                        "cant be that"
                                                    )
                                                }
                                                null -> ""
                                                else -> throw UnsupportedOperationException(
                                                    "cant be that"
                                                )
                                            }
                                            wasQty.value = "$modBefore$modAfter"
                                            scope.enableAddToCart(wasQty.value)
                                            onChange(scope.product, wasQty.value)
                                        },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        maxLines = 1,
                                        singleLine = true,
                                        readOnly = false,
                                        enabled = true,
                                        keyboardActions = KeyboardActions(onDone = {
                                            keyboardController?.hide()
                                        }),
                                        textStyle = TextStyle(
                                            color = MaterialTheme.colors.background,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.W700,
                                            textAlign = TextAlign.End,
                                        )
                                    )
                                }
                                Divider(
                                    color = MaterialTheme.colors.background,
                                    thickness = 1.5.dp
                                )
                            }
                        }
                        Box(modifier = Modifier.width(120.dp)) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.free).uppercase(),
                                        color = ConstColors.gray,
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        text = if (scope.product.sellerInfo?.isPromotionActive == true)
                                            scope.product.freeQuantity.toString() else "0",
                                        color = MaterialTheme.colors.background,
                                        fontWeight = FontWeight.W700,
                                        fontSize = 20.sp,
                                    )
                                }

                                Divider(
                                    color = MaterialTheme.colors.background,
                                    thickness = 1.5.dp
                                )
                            }
                        }
                    }
                    Space(dp = 8.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {

                        Box(modifier = Modifier.width(120.dp)) {
                            MedicoButton(
                                text = stringResource(id = R.string.cancel),
                                isEnabled = true,
                                height = 40.dp,
                                elevation = null,
                                onClick = {
                                    scope.openButtons()
                                },
                                textSize = 12.sp,
                                color = ConstColors.ltgray,
                                txtColor = MaterialTheme.colors.background
                            )
                        }
                        Box(modifier = Modifier.width(120.dp)) {
                            when (scope.product.buyingOption) {
                                BuyingOption.BUY -> MedicoButton(
                                    text = /*if (cartInfo != null) {
                                stringResource(id = R.string.update)
                            } else*/ stringResource(id = R.string.add_to_cart),
                                    isEnabled = enableButton.value,
                                    height = 40.dp,
                                    elevation = null,
                                    onClick = {
                                        if (!scope.buy(scope.product)) {
                                            context.toast(R.string.something_went_wrong)
                                        }
                                        scope.openButtons()
                                    },
                                    textSize = 12.sp,
                                    color = /*if (cartInfo != null) {
                                ConstColors.lightBlue
                            } else*/ ConstColors.yellow,
                                    txtColor =/* if (cartInfo != null) {
                                Color.White
                            } else*/ MaterialTheme.colors.background
                                )
                                BuyingOption.QUOTE -> MedicoButton(
                                    text = stringResource(id = R.string.get_quote),
                                    isEnabled = true,
                                    height = 40.dp,
                                    elevation = null,
                                    color = ConstColors.yellow.copy(alpha = .1f),
                                    border = BorderStroke(2.dp, ConstColors.yellow),
                                    onClick = {
                                        if (!scope.buy(scope.product)) {
                                            context.toast(R.string.something_went_wrong)
                                        }
                                        scope.openButtons()
                                    },
                                    textSize = 12.sp
                                )
                                null -> MedicoButton(
                                    text = stringResource(id = R.string.add_to_cart),
                                    isEnabled = false,
                                    height = 40.dp,
                                    elevation = null,
                                    onClick = {
                                    },
                                    textSize = 12.sp
                                )
                            }
                        }
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
                Space(20.dp)
                Text(
                    text = "${stringResource(id = R.string.alternative_brands)} ${product.sellerInfo?.tradeName} ",
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )
                Space(16.dp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(3.dp)
                ) {
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

    if (showToast.value) {
        if (cartData.value != null) {
            val entries =
                if (cartData.value != null) cartData.value?.sellerCarts?.get(0)?.items else null
            val cartItem = entries?.get(entries.size - 1)
            ShowToastGlobal(
                msg = cartItem?.productName + " " +
                        stringResource(id = R.string.added_to_cart) + " " +
                        stringResource(id = R.string.qty) +
                        " : " +
                        cartItem?.quantity?.formatted + " + " +
                        stringResource(id = R.string.free) + " " +
                        cartItem?.freeQuantity?.formatted
            )
            EventCollector.sendEvent(Event.Action.Search.showToast("", null))
        }
        //scope.startSearchWithNoLoader()
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
    Box {
        Column {
            Card(
                modifier = Modifier
                    .height(90.dp)
                    .width(150.dp)
                    .selectable(
                        selected = true,
                        onClick = onClick
                    ),
                elevation = 3.dp,
                shape = RoundedCornerShape(5.dp),
                backgroundColor = Color.White,
            ) {
                CoilImageBrands(
                    src = CdnUrlProvider.urlFor(product.imageCode, CdnUrlProvider.Size.Px123),
                    contentScale = ContentScale.Crop,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 90.dp,
                    width = 150.dp,
                )
            }
            Space(5.dp)
            Text(
                modifier = Modifier
                    .width(150.dp),
                text = product.name,
                color = ConstColors.txtGrey,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append("PTR: ")
                    val startIndex = length
                    append(product.ptr.formatted)
                    addStyle(
                        SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        ),
                        startIndex,
                        length,
                    )
                },
                fontWeight = FontWeight.W600,
                color = ConstColors.txtGrey,
                fontSize = 12.sp,
            )
            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append("MRP: ")
                    val startIndex = length
                    append(product.mrp.formatted)
                    addStyle(
                        SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.txtGrey,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
            )

        }
        Text(
            text = product.availableVariants,
            color = ConstColors.txtGrey,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}


fun onChange(
    product: ProductSearch,
    qty: String,
) {
    product.quantity = qty.toDouble()
    if (product.sellerInfo?.isPromotionActive == true) {
        product.freeQuantity = checkOffer(
            product.sellerInfo?.promotionData,
            product.quantity
        )
    }
}