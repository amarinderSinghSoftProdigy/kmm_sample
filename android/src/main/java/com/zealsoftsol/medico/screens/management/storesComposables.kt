package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartInfo
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubscriptionStatus
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.ChipString
import com.zealsoftsol.medico.screens.search.FilterSection
import com.zealsoftsol.medico.screens.search.HorizontalFilterSection
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.screens.search.SearchOption
import com.zealsoftsol.medico.screens.search.SortSection
import com.zealsoftsol.medico.screens.search.YellowOutlineIndication
import kotlinx.coroutines.launch

// TODO reuse with management
@ExperimentalComposeUiApi
@Composable
fun StoresScreen(scope: StoresScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is StoresScope.All -> AllStores(scope)
            is StoresScope.StorePreview -> StorePreview(scope)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun StorePreview(scope: StoresScope.StorePreview) {
    val showToast = scope.showToast.flow.collectAsState()
    val cartData = scope.cartData.flow.collectAsState()
    val switchEnabled = remember { mutableStateOf(false) }
    val batchSelected = scope.isBatchSelected.flow.collectAsState()

    val entries = if (cartData.value != null) cartData.value?.sellerCarts?.get(0)?.items else null
    val cartItem = entries?.get(entries.size - 1)

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Column {
            Space(12.dp)
            val search = scope.productSearch.flow.collectAsState()
            val filters = scope.filters.flow.collectAsState()
            val filtersManufactures = scope.filtersManufactures.flow.collectAsState()
            val filterSearches = scope.filterSearches.flow.collectAsState()
            val products = scope.products.flow.collectAsState()
            val showFilter = scope.isFilterOpened.flow.collectAsState()
            val sortOptions = scope.sortOptions.flow.collectAsState()
            val selectedSortOption = scope.selectedSortOption.flow.collectAsState()
            val activeFilterIds = scope.activeFilterIds.flow.collectAsState()
            val autoComplete = scope.autoComplete.flow.collectAsState()
            val options = Option.StringValue(
                id = "offers",
                value = true.toString(),
                isSelected = true,
                isVisible = true,
            )
            val offersFilter = Filter(name = "Offers", queryId = "offers", options = emptyList())

            BasicSearchBar(
                input = search.value,
                hint = R.string.search_products,
                icon = null,
                horizontalPadding = 16.dp,
                searchBarEnd = SearchBarEnd.Filter(isHighlighted = activeFilterIds.value.isNotEmpty()) { scope.toggleFilter() },
                onIconClick = null,
                isSearchFocused = false,//scope.storage.restore("focus") as? Boolean ?: true,
                onSearch = { value, _ ->
                    scope.searchProduct(
                        value,
                        withAutoComplete = true,
                        scope.store.sellerUnitCode
                    )
                    if (value.isEmpty()) {
                        scope.startSearch()
                    }
                },
            )
            scope.storage.save("focus", false)
            if (showFilter.value) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Space(16.dp)
                    Text(
                        text = stringResource(id = R.string.clear_all),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 16.dp)
                            .clickable(indication = null) {
                                scope.clearFilter(null)
                            },
                    )
                    SortSection(
                        options = sortOptions.value,
                        selectedOption = selectedSortOption.value,
                        onClick = { scope.selectSortOption(it) },
                    )
                    filters.value.forEach { filter ->
                        FilterSection(
                            name = filter.name,
                            options = filter.options,
                            searchOption = SearchOption(filterSearches.value[filter.queryId].orEmpty()) {
                                scope.searchFilter(filter, it)
                            },
                            onOptionClick = { scope.selectFilter(filter, it) },
                            onFilterClear = { scope.clearFilter(filter) },
                        )
                    }
                    Space(8.dp)
                }
            } else {
                Space(16.dp)
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.width(maxWidth / 2 - 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye),
                                contentDescription = null,
                                tint = ConstColors.red,
                                modifier = Modifier.size(16.dp)
                            )
                            Space(8.dp)
                            Text(
                                text = stringResource(id = R.string.offers),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W700,
                                color = ConstColors.red,
                                textAlign = TextAlign.Center,
                            )
                            Space(8.dp)
                            Switch(
                                checked = switchEnabled.value, onCheckedChange = {
                                    switchEnabled.value = it
                                    if (it) {
                                        scope.selectFilter(offersFilter, options)
                                    } else {
                                        scope.clearFilter(offersFilter)
                                    }
                                }, colors = SwitchDefaults.colors(
                                    checkedThumbColor = ConstColors.green
                                )
                            )
                        }
                    }
                }

                if (autoComplete.value.isEmpty()) {
                    filtersManufactures.value.forEach { filter ->
                        if (filter.queryId == "manufacturers") {
                            Box(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                                HorizontalFilterSection(
                                    name = filter.name,
                                    options = filter.options,
                                    /*searchOption = SearchOption(filterSearches.value[filter.queryId].orEmpty()) {
                                         scope.searchFilter(filter, it)
                                     },*/
                                    onOptionClick = { scope.selectFilter(filter, it) },
                                    onFilterClear = { scope.clearFilter(null) }
                                )
                            }
                        }
                    }
                }


                //list of products
                if (products.value.isEmpty() && scope.products.updateCount > 0 && autoComplete.value.isEmpty()) {
                    NoRecords(
                        icon = R.drawable.ic_missing_stores,
                        text = R.string.missing_inventory_stores,
                        subtitle = scope.store.tradeName,
                        onHome = { scope.goHome() },
                    )
                } else {
                    if (autoComplete.value.isEmpty()) {
                        val listState = rememberLazyListState()

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp)
                        ) {
                            itemsIndexed(
                                items = products.value,
                                key = { _, item -> item.id },
                                itemContent = { index, item ->
                                    ProductItemStore(
                                        item,
                                        onClick = { scope.selectProduct(item) },
                                        onBuy = {
                                            //scope.resetButton(false)
                                            scope.selectBatch(false, product = item)
                                            scope.buy(item)
                                        },
                                        addToCart = {
                                            if (item.sellerInfo?.cartInfo != null) {
                                                scope.resetButton(true)
                                            }
                                            scope.addToCart(item)
                                        },
                                        scope = scope, index = index, state = listState,
                                        cartItem = cartItem,
                                        batchSelected.value
                                    )
                                    if (index == products.value.lastIndex && scope.pagination.canLoadMore()) {
                                        scope.loadMoreProducts()
                                    }
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            state = rememberLazyListState(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .background(color = Color.White)
                        ) {
                            items(
                                items = autoComplete.value,
                                key = { item -> item.suggestion },
                                itemContent = { item ->
                                    AutoCompleteItem(
                                        item,
                                        search.value
                                    ) { scope.selectAutoComplete(item) }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showToast.value) {
        if (cartItem != null)
            ShowToastGlobal(
                msg = cartItem.productName + " " +
                        stringResource(id = R.string.added_to_cart) + " " +
                        stringResource(id = R.string.qty) +
                        " : " +
                        cartItem.quantity.formatted + " + " +
                        stringResource(id = R.string.free) + " " +
                        cartItem.freeQuantity.formatted
            )
        EventCollector.sendEvent(Event.Action.Search.showToast("", null))
        //scope.startSearchWithNoLoader()
    }
}

/**
 * items to be displayed in autocomplete dropdown list
 */
@Composable
private fun AutoCompleteItem(autoComplete: AutoComplete, input: String, onClick: () -> Unit) {
    val regex = "(?i)$input".toRegex()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BoxWithConstraints {
                Column(modifier = Modifier.widthIn(max = maxWidth - 24.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            append(autoComplete.suggestion)
                            regex.find(autoComplete.suggestion)?.let {
                                addStyle(
                                    SpanStyle(fontWeight = FontWeight.W700),
                                    it.range.first,
                                    it.range.last + 1,
                                )
                            }
                        },
                        color = MaterialTheme.colors.background,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W400,
                    )
                    if (autoComplete.details.isNotEmpty()) {
                        Text(
                            text = autoComplete.details,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.W400,
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                tint = ConstColors.lightBlue,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color(0xFFE6F0F7),
        )
    }
}

@Composable
private fun AllStores(scope: StoresScope.All) {
    val search = scope.searchText.flow.collectAsState()
    val showSearchOverlay = remember { mutableStateOf(true) }
    Space(16.dp)
    if (showSearchOverlay.value) {
        SearchBarBox(
            modifier = Modifier.clickable(indication = null) {
                showSearchOverlay.value = false
            },
            elevation = 0.dp,
            horizontalPadding = 16.dp,
        ) {
            val (icon, text) = R.drawable.ic_stores to R.string.stores_search
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = ConstColors.lightBlue,
                modifier = Modifier.size(24.dp),
            )
            Space(16.dp)
            Text(
                text = stringResource(id = text),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colors.background,
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    } else {
        BasicSearchBar(
            input = search.value,
            hint = R.string.stores_search,
            searchBarEnd = SearchBarEnd.Eraser,
            icon = Icons.Default.ArrowBack,
            elevation = 0.dp,
            horizontalPadding = 16.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
            onIconClick = {
                scope.search("")
                showSearchOverlay.value = true
            },
        )
    }
    val items = scope.items.flow.collectAsState()
    if (items.value.isEmpty() && scope.items.updateCount > 0) {
        NoRecords(
            icon = R.drawable.ic_missing_stores,
            text = R.string.missing_stores,
            onHome = { scope.goHome() },
        )
    } else {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(top = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            itemsIndexed(
                items = items.value,
                itemContent = { index, item ->
                    StoreItem(item) { scope.selectItem(item) }
                    if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                        scope.loadItems()
                    }
                },
            )
        }
    }
}

@Composable
private fun StoreItem(
    store: Store,
    onClick: () -> Unit,
) {
    BaseManagementItem(onClick) {
        Column(
            modifier = Modifier
                .width(maxWidth * 0.65f)
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = store.tradeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colors.background,
                )
            }
            Space(8.dp)
            GeoLocation(store.fullAddress())
        }
        Column(
            modifier = Modifier
                .width(maxWidth * 0.35f)
                .align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = store.status.serverValue,
                color = when (store.status) {
                    SubscriptionStatus.SUBSCRIBED -> ConstColors.green
                    SubscriptionStatus.PENDING -> ConstColors.lightBlue
                    SubscriptionStatus.REJECTED -> ConstColors.red
                },
                fontWeight = FontWeight.W500,
                fontSize = 15.sp,
            )
            Space(8.dp)
            Text(
                text = store.formattedDistance,
                fontSize = 12.sp,
                color = ConstColors.gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductItemStore(
    product: ProductSearch,
    onClick: () -> Unit,
    onBuy: () -> Unit,
    addToCart: () -> Unit,
    scope: StoresScope.StorePreview,
    index: Int = 0,
    state: LazyListState? = null,
    cartItem: CartItem? = null,
    batchSelected: Boolean? = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val selectedProduct = scope.checkedProduct.flow.collectAsState()
    val enableButton = scope.enableButton.flow.collectAsState()
    if (cartItem != null) {
        if (product.sellerInfo?.spid != null && product.sellerInfo?.spid == cartItem.id.spid)
            product.sellerInfo?.cartInfo = CartInfo(
                quantity = cartItem.quantity,
                freeQuantity = cartItem.freeQuantity
            )
    }


    Column {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.large,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            border = BorderStroke(1.dp, ConstColors.ltgray),
            elevation = 8.dp
        ) {
            Box {
                val labelColor = when (product.stockInfo?.status) {
                    StockStatus.IN_STOCK -> ConstColors.green
                    StockStatus.LIMITED_STOCK -> ConstColors.orange
                    StockStatus.OUT_OF_STOCK -> ConstColors.red
                    null -> ConstColors.gray
                }

                Column(
                    modifier = Modifier.padding(all = 16.dp),
                ) {

                    Row {
                        CoilImage(
                            src = CdnUrlProvider.urlFor(
                                product.imageCode!!,
                                CdnUrlProvider.Size.Px123
                            ),
                            size = 70.dp,
                            onError = { ItemPlaceholder() },
                            onLoading = { ItemPlaceholder() },
                        )
                        Space(10.dp)
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = product.name,
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W800,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Space(4.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("PTR: ")
                                            val startIndex = length
                                            append(product.formattedPrice.orEmpty())
                                            addStyle(
                                                SpanStyle(
                                                    color = MaterialTheme.colors.background,
                                                    fontWeight = FontWeight.W600
                                                ),
                                                startIndex,
                                                length,
                                            )
                                        },
                                        color = ConstColors.gray,
                                        fontWeight = FontWeight.W500,
                                        fontSize = 12.sp,
                                    )
                                    product.stockInfo?.let {
                                        Space(4.dp)
                                        Text(
                                            text = buildAnnotatedString {
                                                append(it.formattedStatus)
                                                val startIndex = length
                                                append("(" + it.availableQty + ")")
                                                addStyle(
                                                    SpanStyle(
                                                        color = labelColor,
                                                        fontWeight = FontWeight.W600
                                                    ),
                                                    startIndex,
                                                    length,
                                                )
                                            },
                                            color = labelColor,
                                            fontWeight = FontWeight.W500,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("MRP: ")
                                            val startIndex = length
                                            append(product.formattedMrp)
                                            addStyle(
                                                SpanStyle(
                                                    color = MaterialTheme.colors.background,
                                                    fontWeight = FontWeight.W600
                                                ),
                                                startIndex,
                                                length,
                                            )
                                        },
                                        color = ConstColors.gray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                            }
                        }
                    }

                    val cartInfo = product.sellerInfo?.cartInfo
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
                            itemContent = { value -> ChipString(value) {} }
                        )
                    }
                    Space(8.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Box {
                            if (cartInfo != null) {
                                product.quantity = cartInfo.quantity.value
                                product.freeQuantity = cartInfo.freeQuantity.value
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.qty).uppercase(),
                                        fontSize = 12.sp,
                                        color = ConstColors.gray,
                                    )
                                    Space(6.dp)
                                    Text(
                                        text = cartInfo.quantity.formatted,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W700,
                                        color = MaterialTheme.colors.background,
                                    )
                                    Space(6.dp)
                                    Text(
                                        text = "+${cartInfo.freeQuantity.formatted}",
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
                            }
                        }

                        if (batchSelected == false) {
                            ShowButton(product, addToCart, onBuy)
                        } else if (selectedProduct.value?.id != product.id) {
                            ShowButton(product, addToCart, onBuy)
                        }

                    }



                    if (batchSelected == true && selectedProduct.value?.id == product.id) {
                        Surface(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium,
                            onClick = { },
                            indication = YellowOutlineIndication,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            border = BorderStroke(1.dp, ConstColors.ltgray),
                            elevation = 8.dp
                        ) {
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
                                        EditField(
                                            label = stringResource(id = R.string.qty),
                                            qty = cartInfo?.quantity?.formatted ?: "0",
                                            onChange = {
                                                product.quantity = it.toDouble()
                                                if (product.quantity > 0) {
                                                    if (product.quantity > (selectedProduct.value?.stockInfo?.availableQty
                                                            ?: 0)
                                                    )
                                                        scope.resetButton(false)
                                                    else
                                                        scope.resetButton(true)
                                                } else
                                                    scope.resetButton(false)
                                            },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(onDone = {
                                                scope.resetButton(false)
                                                scope.selectBatch(false, product = product)
                                                scope.buy(product = product)
                                                keyboardController?.hide()
                                            }),
                                            onFocus = {
                                                coroutineScope.launch {
                                                    state?.animateScrollToItem(index = index)
                                                }
                                                keyboardController?.show()
                                            },
                                            isEnabled = true,
                                        )
                                    }
                                    Box(modifier = Modifier.width(120.dp)) {
                                        EditField(
                                            label = stringResource(id = R.string.free),
                                            qty = cartInfo?.freeQuantity?.formatted ?: "0",
                                            onChange = {
                                                product.freeQuantity = it.toDouble()
                                            },
                                            isEnabled = product.sellerInfo?.isPromotionActive
                                                ?: false,
                                        )
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
                                            height = 32.dp,
                                            elevation = null,
                                            onClick = {
                                                product.quantity = 1.0
                                                scope.selectBatch(
                                                    false,
                                                    product = product
                                                )
                                            },
                                            textSize = 12.sp,
                                            color = ConstColors.ltgray,
                                            contentColor = MaterialTheme.colors.background
                                        )
                                    }
                                    Box(modifier = Modifier.width(120.dp)) {
                                        when (product.buyingOption) {
                                            BuyingOption.BUY -> MedicoButton(
                                                text = if (cartInfo != null) {
                                                    stringResource(id = R.string.update)
                                                } else stringResource(id = R.string.add_to_cart),
                                                isEnabled = enableButton.value,
                                                height = 32.dp,
                                                elevation = null,
                                                onClick = onBuy,
                                                textSize = 12.sp,
                                                color = if (cartInfo != null) {
                                                    ConstColors.lightBlue
                                                } else ConstColors.yellow,
                                                contentColor = if (cartInfo != null) {
                                                    Color.White
                                                } else MaterialTheme.colors.background
                                            )
                                            BuyingOption.QUOTE -> MedicoButton(
                                                text = stringResource(id = R.string.get_quote),
                                                isEnabled = true,
                                                height = 32.dp,
                                                elevation = null,
                                                color = ConstColors.yellow.copy(alpha = .1f),
                                                border = BorderStroke(2.dp, ConstColors.yellow),
                                                onClick = onBuy,
                                                textSize = 12.sp
                                            )
                                            null -> MedicoButton(
                                                text = stringResource(id = R.string.add_to_cart),
                                                isEnabled = false,
                                                height = 32.dp,
                                                elevation = null,
                                                onClick = {},
                                                textSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                if (labelColor != null) {
                    val maxWidth =
                        LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp - 5.dp
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(end = maxWidth)
                            .background(labelColor)
                    )
                }
            }

            if (product.sellerInfo?.isPromotionActive == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Box(
                        modifier = Modifier
                            .width(110.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_offer_back),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                        Text(
                            text = product.sellerInfo?.promotionData?.displayLabel ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun ShowButton(product: ProductSearch, addToCart: () -> Unit, onBuy: () -> Unit) {
    Box(modifier = Modifier.width(120.dp)) {
        val cartInfo = product.sellerInfo?.cartInfo
        when (product.buyingOption) {
            BuyingOption.BUY -> MedicoButton(
                text = if (cartInfo != null) {
                    stringResource(id = R.string.update)
                } else stringResource(id = R.string.add_to_cart),
                isEnabled = product.stockInfo?.availableQty.let {
                    (it ?: 0) > 0
                },
                height = 32.dp,
                elevation = null,
                onClick = addToCart,
                textSize = 12.sp,
                color = if (cartInfo != null) {
                    ConstColors.lightBlue
                } else ConstColors.yellow,
                contentColor = if (cartInfo != null) {
                    Color.White
                } else MaterialTheme.colors.background

            )
            BuyingOption.QUOTE -> MedicoButton(
                text = stringResource(id = R.string.get_quote),
                isEnabled = true,
                height = 32.dp,
                elevation = null,
                color = ConstColors.yellow.copy(alpha = .1f),
                border = BorderStroke(2.dp, ConstColors.yellow),
                onClick = onBuy,
                textSize = 12.sp
            )
            null -> MedicoButton(
                text = stringResource(id = R.string.add_to_cart),
                isEnabled = false,
                height = 32.dp,
                elevation = null,
                onClick = {},
                textSize = 12.sp
            )
        }
    }
}



