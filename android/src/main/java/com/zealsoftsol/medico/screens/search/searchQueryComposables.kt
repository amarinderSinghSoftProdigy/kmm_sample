package com.zealsoftsol.medico.screens.search

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TabBar
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun SearchQueryScreen(scope: SearchScope, listState: LazyListState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val product = scope.productSearch.flow.collectAsState()
        val manufacturer = scope.manufacturerSearch.flow.collectAsState()
        val filters = scope.filters.flow.collectAsState()
        val products = scope.products.flow.collectAsState()
        val showFilter = scope.isFilterOpened.flow.collectAsState()
        TabBar {
            BasicSearchBar(
                input = product.value,
                icon = Icons.Default.ArrowBack,
                searchBarEnd = SearchBarEnd.Filter { scope.toggleFilter() },
                onIconClick = { scope.goBack() },
                isSearchFocused = true,
                onSearch = { scope.searchProduct(it) },
            )
        }
        if (showFilter.value) {
            ScrollableColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    modifier = Modifier.align(Alignment.End)
                        .clickable(
                            indication = null,
                            interactionState = remember { InteractionState() }) {
                            scope.clearFilter(
                                null
                            )
                        },
                )
                filters.value.forEach { filter ->
                    FilterSection(
                        name = filter.name,
                        options = filter.options,
                        searchOption = if (filter.queryName == Filter.MANUFACTURER_ID)
                            SearchOption(manufacturer.value) { scope.searchManufacturer(it) }
                        else
                            null,
                        onOptionClick = { scope.selectFilter(filter, it) },
                        onFilterClear = { scope.clearFilter(filter) },
                    )
                }
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = products.value,
                    itemContent = { index, item ->
                        ProductItem(item) { scope.selectProduct(item) }
                        if (index == products.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadMoreProducts()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ProductItem(product: ProductSearch, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable(
                onClick = onClick,
                indication = YellowOutlineIndication,
                interactionState = remember { InteractionState() })
            .padding(10.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CoilImage(
                modifier = Modifier.size(123.dp),
                data = CdnUrlProvider.urlFor(product.medicineId, CdnUrlProvider.Size.Px123),
                contentDescription = null,
                error = { ItemPlaceholder() },
                loading = { ItemPlaceholder() },
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
                    text = product.formattedPrice,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W900,
                    fontSize = 16.sp,
                )
                Space(4.dp)
                Row {
                    Text(
                        text = "MRP: ${product.mrp}",
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = "PTR: ${product.ptrPercentage}",
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                }
                Space(4.dp)
                Text(
                    text = "Code: ${product.productCode}",
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                )
            }
        }
        Space(10.dp)
        Text(
            text = product.packageForm,
            color = ConstColors.lightBlue,
            fontSize = 14.sp,
        )
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun FilterSection(
    name: String,
    options: List<Option<String>>,
    searchOption: SearchOption? = null,
    onOptionClick: (Option<String>) -> Unit,
    onFilterClear: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
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
                    interactionState = remember { InteractionState() }
                ),
            )
        }
        searchOption?.let {
            Space(12.dp)
            BasicSearchBar(
                input = it.input,
                onSearch = it.onSearch,
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

private data class SearchOption(val input: String, val onSearch: (String) -> Unit)

@Composable
private fun Chip(option: Option<String>, onClick: () -> Unit) {
    Surface(
        color = if (option.isSelected) ConstColors.yellow else Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp).clickable(
            onClick = onClick,
            indication = null,
            interactionState = remember { InteractionState() }
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

@Composable
fun BasicSearchBar(
    input: String,
    searchBarEnd: SearchBarEnd = SearchBarEnd.Eraser,
    icon: ImageVector = Icons.Default.Search,
    onIconClick: (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    horizontalPadding: Dp = 8.dp,
    isSearchFocused: Boolean = false,
    onSearch: (String) -> Unit
) {
    SearchBarBox(elevation = elevation, horizontalPadding = horizontalPadding) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ConstColors.gray,
            modifier = Modifier.size(24.dp)
                .run { if (onIconClick != null) clickable(onClick = onIconClick) else this },
        )
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
                cursorColor = ConstColors.lightBlue,
                onValueChange = onSearch,
                singleLine = true,
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
                            modifier = modifier.clickable(onClick = { onSearch("") })
                        )
                    }
                }
                is SearchBarEnd.Filter -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = null,
                        tint = ConstColors.gray,
                        modifier = modifier.clickable(onClick = { searchBarEnd.onClick() })
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

    private object YellowOutlineIndicationInstance : IndicationInstance {

        override fun ContentDrawScope.drawIndication(interactionState: InteractionState) {
            drawContent()
            if (interactionState.contains(Interaction.Pressed)) {
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
    override fun createInstance() = YellowOutlineIndicationInstance
}