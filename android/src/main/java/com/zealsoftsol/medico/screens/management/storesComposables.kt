package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.SubscriptionStatus
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.FilterSection
import com.zealsoftsol.medico.screens.search.ProductItem
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.screens.search.SearchOption
import com.zealsoftsol.medico.screens.search.SortSection

// TODO reuse with management
@Composable
fun StoresScreen(scope: StoresScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is StoresScope.All -> AllStores(scope)
            is StoresScope.StorePreview -> StorePreview(scope)
        }
    }
}

@Composable
private fun StorePreview(scope: StoresScope.StorePreview) {
    Space(16.dp)
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Text(
            text = scope.store.tradeName,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
        )
        Space(10.dp)
        GeoLocation(scope.store.fullAddress())
        Space(4.dp)
        DataWithLabel(R.string.gstin_num, scope.store.gstin)
        Space(16.dp)
    }
    val search = scope.productSearch.flow.collectAsState()
    val filters = scope.filters.flow.collectAsState()
    val filterSearches = scope.filterSearches.flow.collectAsState()
    val products = scope.products.flow.collectAsState()
    val showFilter = scope.isFilterOpened.flow.collectAsState()
    val sortOptions = scope.sortOptions.flow.collectAsState()
    val selectedSortOption = scope.selectedSortOption.flow.collectAsState()
    val activeFilterIds = scope.activeFilterIds.flow.collectAsState()
    BasicSearchBar(
        input = search.value,
        hint = R.string.search_products,
        icon = null,
        horizontalPadding = 16.dp,
        searchBarEnd = SearchBarEnd.Filter(isHighlighted = activeFilterIds.value.isNotEmpty()) { scope.toggleFilter() },
        onIconClick = null,
        isSearchFocused = scope.storage.restore("focus") as? Boolean ?: true,
        onSearch = { value, _ -> scope.searchProduct(value, withAutoComplete = false) },
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
            Space(16.dp)
        }
    } else {
        if (products.value.isEmpty() && scope.products.updateCount > 0) {
            NoRecords(
                icon = R.drawable.ic_missing_stores,
                text = R.string.missing_inventory_stores,
                subtitle = scope.store.tradeName,
                onHome = { scope.goHome() },
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                itemsIndexed(
                    items = products.value,
                    key = { _, item -> item.id },
                    itemContent = { index, item ->
                        ProductItem(
                            item,
                            onClick = { scope.selectProduct(item) },
                            onBuy = { scope.buy(item) },
                        )
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
            isSearchFocused = true,
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
