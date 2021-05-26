package com.zealsoftsol.medico.screens.search

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.FlowRow
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
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
        if (showFilter.value) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            ) {
                Space(16.dp)
                Text(
                    text = stringResource(id = R.string.clear_all),
                    modifier = Modifier.align(Alignment.End).padding(horizontal = 16.dp)
                        .clickable(indication = null) {
                            scope.clearFilter(null)
                        },
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
        } else {
            if (autoComplete.value.isEmpty()) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = products.value,
                        key = { _, item -> item.id },
                        itemContent = { index, item ->
                            ProductItem(item) { scope.selectProduct(item) }
                            if (index == products.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.loadMoreProducts()
                            }
                        },
                    )
                }
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxSize().background(color = Color.White)
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp, horizontal = 24.dp),
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
fun ProductItem(product: ProductSearch, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
            .height(182.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                indication = YellowOutlineIndication,
                onClick = onClick,
            )
    ) {
        Box {
            val labelColor = when (product.stockInfo?.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
                null -> ConstColors.gray
            }
            Box(modifier = Modifier.width(5.dp).height(182.dp).background(labelColor))
            Column(
                modifier = Modifier.padding(horizontal = 12.dp).align(Alignment.Center),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CoilImage(
                        src = CdnUrlProvider.urlFor(product.code, CdnUrlProvider.Size.Px123),
                        size = 123.dp,
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
                        Text(
                            text = product.formattedPrice.orEmpty(),
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.W900,
                            fontSize = 16.sp,
                        )
                        Space(4.dp)
                        Row {
                            Text(
                                text = "MRP: ${product.formattedMrp}",
                                color = ConstColors.gray,
                                fontSize = 12.sp,
                            )
                            Space(4.dp)
                            product.marginPercent?.let {
                                Text(
                                    text = "Margin: $it",
                                    color = ConstColors.gray,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                        Space(4.dp)
                        Text(
                            text = "Code: ${product.code}",
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                        )
                    }
                }
                Space(10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = product.uomName,
                        color = ConstColors.lightBlue,
                        fontSize = 14.sp,
                    )
                    product.stockInfo?.let {
                        Text(
                            text = it.formattedStatus,
                            color = labelColor,
                            fontWeight = FontWeight.W700,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
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
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Separator(padding = 2.dp)
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
                modifier = Modifier.align(Alignment.CenterEnd).clickable(
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

data class SearchOption(val input: String, val onSearch: (String) -> Unit)

@Composable
private fun Chip(option: Option, onClick: () -> Unit) {
    when (option) {
        is Option.StringValue -> {
            if (option.isVisible) Surface(
                color = if (option.isSelected) ConstColors.yellow else Color.White,
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.padding(4.dp).clickable(
                    onClick = onClick,
                    indication = null,
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (option.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colors.background,
                            modifier = Modifier.padding(start = 10.dp).size(20.dp),
                        )
                    }
                    Text(
                        text = option.value,
                        color = if (option.isSelected) MaterialTheme.colors.background else ConstColors.gray,
                        fontWeight = if (option.isSelected) FontWeight.W600 else FontWeight.Normal,
                        fontSize = 14.sp,
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
            modifier = Modifier.padding(4.dp).clickable(
                onClick = onClick,
                indication = null,
            )
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

@Composable
fun BasicSearchBar(
    input: String,
    searchBarEnd: SearchBarEnd = SearchBarEnd.Eraser,
    icon: ImageVector? = Icons.Default.Search,
    onIconClick: (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    horizontalPadding: Dp = 8.dp,
    isSearchFocused: Boolean = false,
    onSearch: (String, Boolean) -> Unit
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
            modifier = Modifier.padding(start = 24.dp).fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (input.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            val focusRequester = FocusRequester()
            if (isSearchFocused) SideEffect { focusRequester.requestFocus() }
            BasicTextField(
                value = input,
                cursorBrush = SolidColor(ConstColors.lightBlue),
                onValueChange = { onSearch(it, false) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { onSearch(input, true) },
                modifier = Modifier.focusRequester(focusRequester).fillMaxWidth()
                    .padding(end = 32.dp),
            )
            val modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
            when (searchBarEnd) {
                is SearchBarEnd.Eraser -> {
                    if (input.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = modifier.clickable(
                                indication = null,
                                onClick = { onSearch("", false) })
                        )
                    }
                }
                is SearchBarEnd.Filter -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = null,
                        tint = ConstColors.gray,
                        modifier = modifier.clickable(indication = null, onClick = { searchBarEnd.onClick() })
                    )
                }
            }
        }
    }
}

sealed class SearchBarEnd {
    object Eraser : SearchBarEnd()
    data class Filter(val onClick: () -> Unit) : SearchBarEnd()
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
        modifier = Modifier.fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = horizontalPadding)
    ) {
        Row(
            modifier = modifier.fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            body()
        }
    }
}

private object YellowOutlineIndication : Indication {

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