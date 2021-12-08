package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreSellerScope
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun InStoreSellersScreen(scope: InStoreSellerScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ConstColors.newDesignGray),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.instore_orders),
                color = MaterialTheme.colors.background,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Space(20.dp)
            val search = scope.searchText.flow.collectAsState()
            BasicSearchBar(
                input = search.value,
                hint = R.string.search_tradename,
                searchBarEnd = SearchBarEnd.Eraser,
                icon = Icons.Default.Search,
                elevation = 0.dp,
                horizontalPadding = 16.dp,
                isSearchFocused = false,
                onSearch = { v, _ -> scope.search(v) },
            )
            val items = scope.items.flow.collectAsState()
            if (items.value.isEmpty() && scope.items.updateCount > 0) {
//            NoRecords(
//                icon = R.drawable.ic_missing_invoices,
//                text = R.string.missing_invoices,
//                onHome = { scope.goHome() },
//            )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(top = 16.dp),
                ) {
                    itemsIndexed(
                        items = items.value,
                        itemContent = { index, item ->
                            InStoreSellerItem(item) { scope.selectItem(item) }
                            if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.loadItems()
                            }
                        },
                    )
                }
            }
        }
        Box(modifier = Modifier.padding(16.dp)) {
            MedicoRoundButton(
                text = stringResource(id = R.string.instore_order_plus),
                isEnabled = true,
                height = 48.dp,
            ) { scope.goToInStoreUsers() }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun InStoreSellerItem(item: InStoreSeller, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        onClick = onClick,
        shape = RectangleShape,
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = item.tradeName,
                color = MaterialTheme.colors.background,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Space(4.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.city,
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                )
                Text(
                    text = item.phoneNumber,
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                )
            }
            Space(4.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text(
                        text = "${stringResource(id = R.string.items)}:",
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                    Space(4.dp)
                    Text(
                        text = item.items.toString(),
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                    )
                }
                Text(
                    text = item.total.formatted,
                    color = MaterialTheme.colors.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                )
            }
        }
    }
}