package com.zealsoftsol.medico.screens.invoices

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.zealsoftsol.medico.core.mvi.scope.nested.InvoicesScope
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.orders.DateRangeSelection
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun InvoicesScreen(scope: InvoicesScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        val search = scope.searchText.flow.collectAsState()
        val isFilterOpened = scope.isFilterOpened.flow.collectAsState()
        Space(16.dp)
        BasicSearchBar(
            input = search.value,
            searchBarEnd = SearchBarEnd.Filter(isHighlighted = false) { scope.toggleFilter() },
            icon = Icons.Default.Search,
            elevation = 0.dp,
            horizontalPadding = 0.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
        )
        Space(16.dp)
        if (!isFilterOpened.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_invoice),
                    tint = ConstColors.lightBlue,
                    contentDescription = null,
                )
                Space(12.dp)
                Text(
                    text = stringResource(id = R.string.invoices),
                    color = MaterialTheme.colors.background,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            val items = scope.items.flow.collectAsState()
            if (items.value.isEmpty() && scope.items.updateCount > 0) {
                NoRecords(
                    icon = R.drawable.ic_missing_invoices,
                    text = R.string.missing_invoices,
                    onHome = { scope.goHome() },
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(top = 16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(
                        items = items.value,
                        itemContent = { index, item ->
                            InvoiceItem(item) { scope.selectItem(item) }
                            if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.loadItems()
                            }
                        },
                    )
                }
            }
        } else {
            val dateRange = scope.dateRange.flow.collectAsState()
            DateRangeSelection(
                dateRange = dateRange.value,
                onClear = { scope.clearFilters() },
                onFrom = { scope.setFrom(it) },
                onTo = { scope.setTo(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun InvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)) {
                Text(
                    text = invoice.tradeName,
                    color = MaterialTheme.colors.background,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.6f),
                )

                Box(modifier = Modifier.weight(0.4f)) {
                    Text(
                        text = "${invoice.info.date} ${invoice.info.time}",
                        color = ConstColors.gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ConstColors.gray.copy(0.05f))
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = invoice.info.id,
                    color = ConstColors.gray,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.invoice_total))
                        val startIndex = length
                        append(invoice.info.total.formattedPrice)
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                )
            }
        }
    }
}