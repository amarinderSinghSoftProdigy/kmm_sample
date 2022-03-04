package com.zealsoftsol.medico.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.StockInfo
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.FlowRow
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable

@Composable
fun SearchScreen(scope: SearchScope, listState: LazyListState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val search = scope.productSearch.flow.collectAsState()
        val autoComplete = scope.autoComplete.flow.collectAsState()
        val filters = scope.filters.flow.collectAsState()
        val filterSearches = scope.filterSearches.flow.collectAsState()
        val products = scope.products.flow.collectAsState()
        val showFilter = scope.isFilterOpened.flow.collectAsState()
        val sortOptions = scope.sortOptions.flow.collectAsState()
        val selectedSortOption = scope.selectedSortOption.flow.collectAsState()
        val activeFilterIds = scope.activeFilterIds.flow.collectAsState()

        if (showFilter.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp * 2 + 48.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Space(16.dp)
                    Text(
                        text = stringResource(id = R.string.filters),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colors.background,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    if (activeFilterIds.value.isNotEmpty()) {
                        Space(16.dp)
                        Separator(padding = 16.dp)
                        Space(12.dp)
                        FlowRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                            activeFilterIds.value.forEach { q ->
                                val filter = scope.getFilterNameById(q)
                                FilterChip(name = filter.name) {
                                    scope.clearFilter(filter)
                                }
                            }
                        }
                    }
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
                                scope.searchFilter(
                                    filter,
                                    it
                                )
                            },
                            onOptionClick = { scope.selectFilter(filter, it) },
                            onFilterClear = { scope.clearFilter(filter) },
                        )
                    }
                    Space(16.dp)
                }
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White)))
                    }
                    Separator(padding = 0.dp)
                    Row(modifier = Modifier.background(Color.White)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        ) {
                            MedicoButton(
                                modifier = Modifier.weight(.5f),
                                text = stringResource(R.string.clear),
                                isEnabled = true,
                                color = Color.Transparent,
                                contentColor = ConstColors.lightBlue,
                                border = BorderStroke(2.dp, ConstColors.lightBlue),
                                elevation = null,
                                onClick = { scope.clearFilter(null) },
                            )
                            Space(16.dp)
                            MedicoButton(
                                modifier = Modifier.weight(.5f),
                                text = stringResource(R.string.apply),
                                isEnabled = true,
                                color = ConstColors.yellow,
                                contentColor = MaterialTheme.colors.background,
                                elevation = null,
                                onClick = { scope.toggleFilter() },
                            )
                        }
                    }
                }
            }
        } else {
            if (autoComplete.value.isEmpty()) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = products.value,
                        key = { _, item -> item.id },
                        itemContent = { index, item ->
                            ProductItem(
                                item,
                                onClick = { scope.selectProduct(item) },
                                onBuy = { scope.buy(item) },
                                scope = scope
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
                        .background(color = Color.White)
                ) {
                    items(
                        items = autoComplete.value,
                        key = { item -> item.suggestion },
                        itemContent = { item ->
                            AutoCompleteItem(item, search.value) { scope.selectAutoComplete(item) }
                        },
                    )
                }
            }
        }
    }
}

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductItem(product: ProductSearch, onClick: () -> Unit, onBuy: () -> Unit, scope: SearchScope) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        indication = YellowOutlineIndication,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box {
            val labelColor = when (product.stockInfo?.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
                null -> ConstColors.gray
            }

            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Row {
                    CoilImage(
                        src = CdnUrlProvider.urlFor(product.imageCode!!, CdnUrlProvider.Size.Px123),
                        size = 80.dp,
                        onError = { ItemPlaceholder() },
                        onLoading = { ItemPlaceholder() },
                    )
                    Space(10.dp)
                    Column {
                        Text(
                            text = product.name,
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                        )
                        Space(4.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    text = product.formattedPrice.orEmpty(),
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W900,
                                    fontSize = 16.sp,
                                )
                                Space(4.dp)
                                Text(
                                    text = product.code,
                                    color = ConstColors.gray,
                                    fontSize = 12.sp,
                                )
                                product.stockInfo?.let {
                                    Space(4.dp)
                                    Text(
                                        text = it.formattedStatus,
                                        color = labelColor,
                                        fontWeight = FontWeight.W700,
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
                                product.marginPercent?.let {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Margin: ")
                                            val startIndex = length
                                            append(it)
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
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column {
                        BoxWithConstraints {
                            Divider(modifier = Modifier.width(maxWidth / 2))
                        }
                        Space(4.dp)
                        Text(
                            text = product.uomName,
                            color = ConstColors.lightBlue,
                            fontSize = 14.sp,
                        )
                        if(product.viewStockist!=null  && product.viewStockist!!.isNotEmpty()){
                            Space(4.dp)
                            Text(
                                text = "${stringResource(id = R.string.view_stockist)} (${product.viewStockist!!.size})",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.clickable{
                                    scope.showConnectedStockist(product.viewStockist!!)
                                }
                            )
                        }
                    }
                    Box(modifier = Modifier.width(120.dp)) {
                        when (product.buyingOption) {
                            BuyingOption.BUY -> MedicoButton(
                                text = stringResource(id = R.string.buy),
                                isEnabled = true,
                                height = 36.dp,
                                elevation = null,
                                onClick = onBuy,
                            )
                            BuyingOption.QUOTE -> MedicoButton(
                                text = stringResource(id = R.string.get_quote),
                                isEnabled = true,
                                height = 36.dp,
                                elevation = null,
                                color = ConstColors.yellow.copy(alpha = .1f),
                                border = BorderStroke(2.dp, ConstColors.yellow),
                                onClick = onBuy,
                            )
                            null -> MedicoButton(
                                text = stringResource(id = R.string.buy),
                                isEnabled = false,
                                height = 36.dp,
                                elevation = null,
                                onClick = {},
                            )
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
    }
}

@Composable
fun SortSection(
    options: List<SortOption>,
    selectedOption: SortOption?,
    onClick: (SortOption?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Separator(padding = 0.dp)
        Space(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.sort_by),
                color = MaterialTheme.colors.background,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
            )
            Text(
                text = stringResource(id = R.string.clear),
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                modifier = Modifier.clickable(
                    onClick = { onClick(null) },
                    indication = null,
                ),
            )
        }
        Space(12.dp)
        FlowRow {
            options.forEach {
                Chip(
                    option = Option.StringValue(it.name, isSelected = it == selectedOption),
                    onClick = { onClick(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BatchItem(
    options: StockInfo,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                bottom = 16.dp,
                end = 16.dp
            ),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(
            2.dp, if (options.formattedStatus.isNotEmpty()) {
                ConstColors.separator
            } else {
                ConstColors.lightBlue
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.batch_no))
                    val startIndex = length
                    append(options.formattedStatus)
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
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
            )
            Space(dp = 4.dp)
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.expiry))
                    val startIndex = length
                    append(options.expiry.formattedDate)
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
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
            )
            Space(dp = 4.dp)
            Text(
                text = buildAnnotatedString {
                    append("In-Stock : ")
                    val startIndex = length
                    append(options.availableQty.toString())
                    addStyle(
                        SpanStyle(
                            color = ConstColors.green,
                            fontWeight = FontWeight.W800
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.green,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
            )
        }
    }
}

@Composable
fun FilterSection(
    name: String,
    options: List<Option>,
    searchOption: SearchOption? = null,
    onOptionClick: (Option) -> Unit,
    onFilterClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Separator(padding = 0.dp)
        Space(12.dp)
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = stringResource(id = R.string.clear),
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        onClick = onFilterClear,
                        indication = null,
                    ),
            )
        }
        searchOption?.let {
            Space(12.dp)
            BasicSearchBar(
                input = it.input,
                onSearch = { v, _ -> it.onSearch(v) },
            )
        }
        if (options.isNotEmpty()) {
            Space(12.dp)
            FlowRow {
                options.forEach { Chip(it) { onOptionClick(it) } }
            }
        }
    }
}

@Composable
fun HorizontalFilterSection(
    name: String,
    options: List<Option>,
    searchOption: SearchOption? = null,
    onOptionClick: (Option) -> Unit,
    onFilterClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        if (options.isNotEmpty()) {
            LazyRow(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(top = 6.dp),
            ) {
                items(
                    items = options,
                    itemContent = { value ->
                        RoundChip(
                            value
                        ) { onOptionClick(value) }
                    }
                )
            }
        }
    }
}

data class SearchOption(val input: String, val onSearch: (String) -> Unit)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chip(option: Option, onClick: () -> Unit) {
    when (option) {
        is Option.StringValue -> {
            if (option.isVisible) Surface(
                color = if (option.isSelected) ConstColors.yellow else Color.White,
                shape = RoundedCornerShape(percent = 50),
                onClick = onClick,
                modifier = Modifier.padding(4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (option.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colors.background,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(20.dp),
                        )
                    }
                    Text(
                        text = option.value,
                        color = if (option.isSelected) MaterialTheme.colors.background else ConstColors.gray,
                        fontWeight = if (option.isSelected) FontWeight.W600 else FontWeight.Normal,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(
                            start = if (option.isSelected) 6.dp else 12.dp,
                            end = 12.dp,
                            top = 6.dp,
                            bottom = 6.dp,
                        ),
                    )
                }
            }
        }
        is Option.ViewMore -> Surface(
            color = Color.Transparent,
            border = BorderStroke(1.dp, ConstColors.lightBlue),
            contentColor = ConstColors.lightBlue,
            shape = RoundedCornerShape(percent = 50),
            onClick = onClick,
            modifier = Modifier.padding(4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.view_all),
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 7.dp, // compensate for 2.sp smaller text
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipString(option: String, onClick: () -> Unit) {
    Surface(
        color = ConstColors.ltgray,
        shape = RoundedCornerShape(percent = 50),
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = option,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RoundChip(
    option: Option, onClick: () -> Unit
) {
    when (option) {
        is Option.StringValue -> {
            if (option.isVisible) {
                Column {
                    Surface(
                        color = if (option.isSelected) ConstColors.yellow else Color.White,
                        shape = RoundedCornerShape(percent = 50),
                        onClick = onClick,
                        modifier = Modifier.padding(4.dp),
                        elevation = 8.dp,
                        border = if (option.isSelected) BorderStroke(
                            1.dp,
                            ConstColors.yellow
                        ) else BorderStroke(
                            1.dp, Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(30.dp))
                                .height(80.dp)
                                .width(80.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            /*if (option.isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.background,
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .size(20.dp),
                                )
                            }*/
                            CoilImage(
                                src = CdnUrlProvider.urlForM(
                                    option.id ?: ""
                                ),
                                size = 80.dp,
                                onError = { ItemPlaceholder() },
                                onLoading = { ItemPlaceholder() },
                            )

                            /*Text(
                            text = option.value,
                            color = if (option.isSelected) MaterialTheme.colors.background else ConstColors.gray,
                            fontWeight = if (option.isSelected) FontWeight.W600 else FontWeight.Normal,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(
                                start = if (option.isSelected) 6.dp else 12.dp,
                                end = 12.dp,
                                top = 6.dp,
                                bottom = 6.dp,
                            ),
                        )*/
                        }
                    }

                    Row(
                        modifier = Modifier.width(80.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        Text(
                            color = if (option.isSelected) MaterialTheme.colors.background else ConstColors.gray,
                            fontWeight = if (option.isSelected) FontWeight.W600 else FontWeight.Normal,
                            text = option.value,
                            fontSize = 12.sp,
                            modifier = Modifier.width(60.dp),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }


/*is Option.ViewMore -> Surface(
    color = Color.Transparent,
    border = BorderStroke(1.dp, ConstColors.lightBlue),
    contentColor = ConstColors.lightBlue,
    shape = RoundedCornerShape(percent = 50),
    onClick = onClick,
    modifier = Modifier.padding(4.dp),
) {
    Text(
        text = stringResource(id = R.string.view_all),
        fontSize = 12.sp,
        fontWeight = FontWeight.W700,
        modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 7.dp, // compensate for 2.sp smaller text
        ),
    )
}*/
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FilterChip(name: String, onRemove: () -> Unit) {
    Surface(
        color = ConstColors.lightBlue,
        shape = RoundedCornerShape(percent = 50),
        modifier = Modifier.padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 6.dp,
                    top = 6.dp,
                    bottom = 6.dp,
                ),
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(20.dp)
                    .clickable(indication = null, onClick = onRemove),
            )
        }
    }
}

@Composable
fun BasicSearchBar(
    input: String,
    hint: Int = R.string.search,
    searchBarEnd: SearchBarEnd? = SearchBarEnd.Eraser,
    icon: ImageVector? = Icons.Default.Search,
    onIconClick: (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    horizontalPadding: Dp = 8.dp,
    isSearchFocused: Boolean = false,
    onSearch: (String, Boolean) -> Unit,
    isSearchCross: Boolean = false
) {
    SearchBarBox(elevation = elevation, horizontalPadding = horizontalPadding) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ConstColors.gray,
                modifier = Modifier.size(24.dp)
                    .run { if (onIconClick != null) clickable(onClick = onIconClick) else this },
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (input.isEmpty()) {
                Text(
                    text = stringResource(id = hint),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            val focusRequester = FocusRequester()
            if (isSearchFocused) SideEffect { focusRequester.requestFocus() }
            BasicTextField(
                value = input.uppercase(),
                cursorBrush = SolidColor(ConstColors.lightBlue),
                onValueChange = { onSearch(it.replace("+","").replace("*",""), false) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { onSearch(input, true) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(end = 32.dp),
            )
            when (searchBarEnd) {
                is SearchBarEnd.Eraser -> {
                    if (input.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                                .clickable(
                                    indication = null,
                                    onClick = { onSearch("", false) }
                                )
                        )
                    }
                }
                is SearchBarEnd.Filter -> {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (input.isNotEmpty() && isSearchCross) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = ConstColors.gray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        indication = null,
                                        onClick = { onSearch("", false) }
                                    )
                            )
                        }

                        val boxMod = if (searchBarEnd.isHighlighted) {
                            Modifier.background(ConstColors.yellow, MaterialTheme.shapes.small)
                        } else {
                            Modifier
                        }
                        Box(
                            modifier = boxMod
                                .clickable(indication = null, onClick = searchBarEnd.onClick)
                                .padding(2.dp)
                        ) {
                            if (searchBarEnd.isHighlighted) {
                                Canvas(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(6.dp)
                                ) {
                                    drawCircle(Color.Red)
                                }
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = null,
                                tint = if (searchBarEnd.isHighlighted) MaterialTheme.colors.background else ConstColors.gray,
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class SearchBarEnd {
    object Eraser : SearchBarEnd()
    data class Filter(val isHighlighted: Boolean, val onClick: () -> Unit) : SearchBarEnd()
}

@Composable
fun SearchBarBox(
    modifier: Modifier = Modifier,
    elevation: Dp,
    horizontalPadding: Dp,
    body: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        elevation = elevation,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = horizontalPadding)
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            body()
        }
    }
}

object YellowOutlineIndication : Indication {

    private class YellowOutlineIndicationInstance(
        private val isPressed: State<Boolean>,
    ) : IndicationInstance {

        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value) {
                drawRoundRect(
                    color = ConstColors.yellow,
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    size = size
                )
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        return remember(interactionSource) { YellowOutlineIndicationInstance(isPressed) }
    }
}