package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
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
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreProductsScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.PromotionData
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.cart.TextItem
import com.zealsoftsol.medico.screens.cart.TextItemString
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.checkOffer
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InStoreProductsScreen(scope: InStoreProductsScope) {
    remember { scope.firstLoad() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            TextItem(
                R.string.items,
                cart.value?.entries.orEmpty().size,
                txtColor = ConstColors.darkBlue
            )
            TextItem(
                R.string.qty,
                cart.value?.entries.orEmpty().sumOf { it.quantity.value },
                txtColor = ConstColors.darkBlue
            )
            TextItem(
                R.string.free,
                cart.value?.entries.orEmpty().sumOf { it.freeQty.value },
                txtColor = ConstColors.darkBlue
            )
            TextItemString(
                R.string.amount,
                cart.value?.total?.formattedPrice.orEmpty(),
                txtColor = ConstColors.darkBlue
            )
            MedicoRoundButton(
                text = stringResource(id = R.string.view_order),
                wrapTextSize = true,
                isEnabled = cart.value?.id != null,
            ) {
                scope.goToInStoreCart()
            }
        }
        Space(8.dp)
        Divider(thickness = (1.5).dp)
        Space(dp = 10.dp)
        val search = scope.searchText.flow.collectAsState()
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_products,
            searchBarEnd = SearchBarEnd.Eraser,
            icon = Icons.Default.Search,
            elevation = 3.dp,
            horizontalPadding = 16.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
            backgroundColor = ConstColors.lightBackground,
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
            val state = rememberLazyListState()
            LazyColumn(
                state = state,
                contentPadding = PaddingValues(top = 4.dp, start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { index, item ->
                        ProductItem(
                            item,
                            { scope.selectItem(item) },
                            { scope.selectImage(item.imageCode) },
                            scope, state = state, index
                        )
                        if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadItems()
                        }
                    },
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProductItem(
    item: InStoreProduct,
    onItemClick: () -> Unit,
    onImageClick: () -> Unit,
    addToCart: InStoreProductsScope,
    state: LazyListState? = null,
    index: Int = 0
) {
    BaseItem(
        addToCart = addToCart,
        index = index,
        state = state,
        item = item,
        qtyInitial = item.stockInfo.availableQty.toDouble(),
        freeQtyInitial = 0.0,
        promotionData = null,//item.sellerInfo?.promotionData?.takeIf { item.sellerInfo?.isPromotionActive == true },
        //forceMode = if (item.order?.isEmpty() != false) BottomSectionMode.AddToCart else BottomSectionMode.Update,
        onItemClick = onItemClick,
        //canAddToCart = item.stockInfo.status != StockStatus.OUT_OF_STOCK,
        headerContent = {
            Surface(
                color = ConstColors.separator,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ConstColors.separator),
                elevation = 3.dp
            ) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(item.imageCode, CdnUrlProvider.Size.Px320),
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .size(100.dp)
                        .clickable {
                            onImageClick()
                        },
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    isCrossFadeEnabled = false
                )
            }
            Space(dp = 20.dp)
            Column {
                Text(
                    text = item.name,
                    color = Color.Black,
                    fontWeight = FontWeight.W500,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Space(dp = 8.dp)
                Text(
                    text = buildAnnotatedString {
                        append("PTR: ")
                        val startIndex = length
                        append(item.priceInfo.price.formattedPrice)
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.W800
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W700,
                    fontSize = 15.sp,
                )
                Space(dp = 5.dp)
                Text(
                    text = buildAnnotatedString {
                        append("MRP: ")
                        val startIndex = length
                        append(item.priceInfo.mrp.formattedPrice)
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.W800
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W700,
                    fontSize = 15.sp,
                )
                val labelColor = when (item.stockInfo.status) {
                    StockStatus.IN_STOCK -> ConstColors.green
                    StockStatus.LIMITED_STOCK -> ConstColors.orange
                    StockStatus.OUT_OF_STOCK -> ConstColors.red
                    null -> ConstColors.gray
                }
                Space(dp = 5.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = labelColor, shape = CircleShape)
                            .size(6.dp)
                    )
                    Space(dp = 5.dp)
                    Text(
                        text = item.stockInfo.formattedStatus,
                        color = labelColor,
                        fontSize = 12.sp
                    )
                }
            }
        },
        mainBodyContent = {
            val sliderList = ArrayList<String>()
            item.manufacturer.let { sliderList.add(it) }
            /*if (item.drugFormName.isNotEmpty())
                sliderList.add(item.drugFormName)*/
            item.standardUnit.let { sliderList.add(it) }
            /*if (item.compositions.isNotEmpty())
                sliderList.addAll(item.compositions)*/
            item.priceInfo.marginPercent.let {
                sliderList.add(
                    "Margin: ".plus(
                        it
                    )
                )
            }
            LazyRow(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(top = 10.dp),
            ) {
                items(
                    items = sliderList,
                    itemContent = { value -> if (value.isNotEmpty()) RoundString(value) {} }
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RoundString(option: String, onClick: () -> Unit) {
    Surface(
        color = ConstColors.paleBlue,
        shape = RoundedCornerShape(5.dp),
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = option,
                color = Color.Black,
                fontWeight = FontWeight.W500,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp,
                    bottom = 4.dp,
                ),
            )
        }
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BaseItem(
    index: Int,
    item: InStoreProduct,
    headerContent: @Composable RowScope.() -> Unit,
    mainBodyContent: @Composable ColumnScope.() -> Unit,
    qtyInitial: Double,
    freeQtyInitial: Double,
    promotionData: PromotionData? = null,
    state: LazyListState? = null,
    onItemClick: () -> Unit,
    addToCart: InStoreProductsScope
) {
    val qty = remember { mutableStateOf(qtyInitial) }
    val freeQty = remember { mutableStateOf(freeQtyInitial) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val showButton = remember {
        mutableStateOf(false)
    }
    Space(12.dp)
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ConstColors.separator),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onItemClick,
        elevation = 3.dp
    ) {
        Box {
            promotionData?.let {
                Text(
                    text = it.displayLabel,
                    fontSize = 14.sp,
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
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) { headerContent() }
                mainBodyContent()
                Space(10.dp)
                Divider(color = ConstColors.ltgray)
                Space(9.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = item.order?.quantity?.formatted ?: "0",
                            color = Color.Black,
                            fontWeight = FontWeight.W700,
                            fontSize = 14.sp
                        )
                        Text(
                            text = stringResource(id = R.string.quantity),
                            color = ConstColors.darkBlue,
                            fontSize = 14.sp
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = item.order?.freeQty?.formatted ?: "0",
                            fontWeight = FontWeight.W700,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = stringResource(id = R.string.free),
                            color = ConstColors.darkBlue,
                            fontSize = 14.sp
                        )
                    }


                    val qtyValue = qty.value
                    if (!showButton.value) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clickable {
                                    if (qtyValue > 0.0)
                                        showButton.value = true
                                },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp),
                                painter = painterResource(R.drawable.ic_add_to_cart_instore),
                                contentDescription = null,
                                tint = if (qtyValue > 0.0) {
                                    ConstColors.lightBlue
                                } else {
                                    ConstColors.gray
                                }
                            )
                            Space(dp = 1.dp)
                            Text(
                                text = stringResource(id = R.string.add_to_cart),
                                color = if (qtyValue > 0.0) {
                                    ConstColors.lightBlue
                                } else {
                                    ConstColors.gray
                                },
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
                if (showButton.value) {
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
                                                if (qty.value.toString().split(".")
                                                        .lastOrNull() == "0"
                                                ) qty.value.toString().split(".")
                                                    .first() else qty.value.toString()
                                            )
                                        }

                                        BasicTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onFocusEvent {
                                                    if (it.isFocused) coroutineScope.launch {
                                                        state?.animateScrollToItem(index = index)
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
                                                qty.value = wasQty.value.toDouble()
                                                freeQty.value = checkOffer(
                                                    item.promotionData,
                                                    wasQty.value.toDouble()
                                                )
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
                                            text = freeQty.value.toString(),
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
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.cancel),
                                    isEnabled = true,
                                    height = 32.dp,
                                    elevation = null,
                                    onClick = {
                                        showButton.value = false
                                    },
                                    textSize = 12.sp,
                                    color = ConstColors.ltgray,
                                    contentColor = MaterialTheme.colors.background
                                )
                            }
                            Box(modifier = Modifier.width(120.dp)) {
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.add_to_cart),
                                    isEnabled = true,
                                    height = 32.dp,
                                    elevation = null,
                                    onClick = {
                                        showButton.value = false
                                        addToCart.addToCart(
                                            item.code,
                                            item.spid,
                                            qty.value,
                                            freeQty.value
                                        )
                                    },
                                    textSize = 12.sp,
                                    color = ConstColors.yellow,
                                    contentColor = MaterialTheme.colors.background
                                )
                            }
                        }
                    }
                }
            }
            Space(2.dp)
        }
    }
}