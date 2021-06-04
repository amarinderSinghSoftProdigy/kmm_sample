package com.zealsoftsol.medico.screens.orders

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@Composable
fun OrdersScreen(scope: OrdersScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        val search = scope.searchText.flow.collectAsState()
        val isFilterOpened = scope.isFilterOpened.flow.collectAsState()
        Space(16.dp)
        BasicSearchBar(
            input = search.value,
            searchBarEnd = if (scope.type != OrderType.RECEIVED) SearchBarEnd.Filter { scope.toggleFilter() } else null,
            icon = Icons.Default.Search,
            elevation = 0.dp,
            horizontalPadding = 16.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
        )
        Space(16.dp)

        if (!isFilterOpened.value) {
            val totalItems = scope.totalItems.flow.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(41.dp)
                    .padding(horizontal = 16.dp)
                    .background(ConstColors.lightBlue, MaterialTheme.shapes.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(
                        id = when (scope.type) {
                            OrderType.RECEIVED -> R.string.new_orders
                            OrderType.SENT -> R.string.orders
                            OrderType.HISTORY -> 0
                        }
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White,
                )
                if (totalItems.value != 0) {
                    Space(6.dp)
                    Text(
                        text = totalItems.value.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700,
                        color = ConstColors.yellow,
                    )
                }
            }
            val items = scope.items.flow.collectAsState()
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(top = 16.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { index, item ->
                        OrderItem(item) { scope.selectItem(item) }
                        if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadItems()
                        }
                    },
                )
            }
        } else {
            val dateRange = scope.dateRange.flow.collectAsState()
            DateRangeSelection(
                dateRange = dateRange.value,
                onClear = { scope.clearFilters() },
                onFrom = { scope.setFrom(it) },
                onTo = { scope.setTo(it) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun DateRangeSelection(
    dateRange: DateRange?,
    onClear: () -> Unit,
    onFrom: (Long) -> Unit,
    onTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.date),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
            )
            Text(
                text = stringResource(id = R.string.clear),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                color = ConstColors.gray,
                modifier = Modifier.clickable(onClick = onClear),
            )
        }
        Space(12.dp)

        DatePicker(
            pickedTimeMs = dateRange?.fromMs,
            hint = stringResource(id = R.string.from),
            onPicked = onFrom,
        )
        Space(12.dp)
        DatePicker(
            pickedTimeMs = dateRange?.toMs,
            hint = stringResource(id = R.string.to),
            onPicked = onTo,
        )
    }
}

@Composable
private fun OrderItem(order: Order, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)) {
                Text(
                    text = order.tradeName,
                    color = MaterialTheme.colors.background,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.6f),
                )

                Box(modifier = Modifier.weight(0.4f)) {
                    Text(
                        text = "${order.info.date} ${order.info.time}",
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
                modifier = Modifier.fillMaxWidth()
                    .background(ConstColors.gray.copy(0.05f))
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.type))
                        append(": ")
                        val startIndex = length
                        append(order.info.paymentMethod.serverValue)
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
                Text(
                    text = order.info.id,
                    color = ConstColors.gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.total))
                        val startIndex = length
                        append(order.info.total.formattedPrice)
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

@Composable
private fun DatePicker(pickedTimeMs: Long?, hint: String, onPicked: (Long) -> Unit) {
    val formatter: DateTimeFormatter = remember { DateTimeFormat.forPattern("d MMM yyyy") }
    Box(modifier = Modifier.fillMaxWidth()) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .clickable(onClick = {
                    val now = DateTime.now()
                    val dialog = DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            onPicked(
                                DateTime.now().withYear(year).withMonthOfYear(month)
                                    .withDayOfMonth(day).millis
                            )
                        },
                        now.year,
                        now.monthOfYear,
                        now.dayOfMonth,
                    )
                    dialog.show()
                })
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = pickedTimeMs?.let { DateTime(it).toString(formatter) } ?: hint,
                color = Color.Black,
                fontSize = 14.sp,
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ConstColors.gray,
            )
        }
    }
}