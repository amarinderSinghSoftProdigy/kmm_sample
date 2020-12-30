package com.zealsoftsol.medico.screens.search

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumnFor
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.Product
import com.zealsoftsol.medico.screens.Separator
import com.zealsoftsol.medico.screens.Space
import com.zealsoftsol.medico.screens.TabBar

@Composable
fun SearchQueryScreen(scope: SearchScope) {
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
                onSearch = { scope.searchProduct(it) }
            )
        }
        if (showFilter.value) {
            ScrollableColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    modifier = Modifier.align(Alignment.End)
                        .clickable(indication = null) { scope.clearFilter(null) },
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
            LazyColumnFor(
                items = products.value,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                ProductItem(it)
            }
        }
    }
}

@Composable
private fun ProductItem(product: Product) {
    Text(product.baseProduct)
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
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (option.isSelected) {
                Icon(
                    asset = Icons.Default.Check,
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
    icon: VectorAsset = Icons.Default.Search,
    onIconClick: (() -> Unit)? = null,
    onSearch: (String) -> Unit
) {
    SearchBarBox {
        Icon(
            asset = icon,
            tint = ConstColors.gray,
            modifier = Modifier.size(24.dp)
                .run { if (onIconClick != null) clickable(onClick = onIconClick) else this },
        )
        Box(
            modifier = Modifier.padding(start = 24.dp).fillMaxWidth(),
            alignment = Alignment.CenterStart
        ) {
            if (input.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            BasicTextField(
                value = input,
                cursorColor = ConstColors.lightBlue,
                onValueChange = onSearch,
                modifier = Modifier.fillMaxWidth(),
            )
            val modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
            when (searchBarEnd) {
                is SearchBarEnd.Eraser -> {
                    if (input.isNotEmpty()) {
                        Icon(
                            asset = Icons.Default.Close,
                            tint = ConstColors.gray,
                            modifier = modifier.clickable(onClick = { onSearch("") })
                        )
                    }
                }
                is SearchBarEnd.Filter -> {
                    Icon(
                        asset = vectorResource(id = R.drawable.ic_filter),
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
private fun SearchBarBox(body: @Composable RowScope.() -> Unit) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            body()
        }
    }
}