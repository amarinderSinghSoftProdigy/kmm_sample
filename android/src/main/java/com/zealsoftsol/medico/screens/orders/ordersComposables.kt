package com.zealsoftsol.medico.screens.orders

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.geometry.CornerRadius
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
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderStatus
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@Composable
fun OrdersScreen(scope: OrdersScope, isInProgress: DataSource<Boolean>) {
    Column(modifier = Modifier.fillMaxSize()) {
        remember { scope.firstLoad() }
        val search = scope.searchText.flow.collectAsState()
        val isFilterOpened = scope.isFilterOpened.flow.collectAsState()
        Space(16.dp)
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_tradename,
            searchBarEnd = SearchBarEnd.Filter(isHighlighted = false) { scope.toggleFilter() },
            icon = Icons.Default.Search,
            elevation = 0.dp,
            horizontalPadding = 16.dp,
            isSearchFocused = false,
            onSearch = { v, _ -> scope.search(v) },
        )
        Space(16.dp)

        if (!isFilterOpened.value) {
            val activeTab = scope.activeTab.flow.collectAsState()
            val totalItems = scope.totalItems.flow.collectAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(41.dp)
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
            ) {
                scope.tabs.forEach {
                    var boxMod = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    boxMod = if (scope.tabs.size == 1) {
                        boxMod
                    } else {
                        boxMod
                            .padding(5.dp)
                            .clickable { scope.selectTab(it) }
                    }
                    val isActive = activeTab.value == it
                    boxMod = if (isActive) {
                        boxMod.background(ConstColors.lightBlue, MaterialTheme.shapes.medium)
                    } else {
                        boxMod
                    }
                    Row(
                        modifier = boxMod,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResourceByName(it.stringId),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = if (isActive) Color.White else MaterialTheme.colors.background,
                        )
                        if (isActive && totalItems.value != 0) {
                            Space(6.dp)
                            Text(
                                text = totalItems.value.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700,
                                color = ConstColors.yellow,
                            )
                        }
                    }
                }
            }

            val items = scope.items.flow.collectAsState()
            if (items.value.isEmpty() && !isInProgress.flow.value) {
                val (icon, text) = when (activeTab.value.orderType) {
                    OrderType.ORDER -> R.drawable.ic_missing_orders to R.string.missing_orders
                    OrderType.PURCHASE_ORDER -> R.drawable.ic_missing_orders to R.string.missing_po_orders
                    OrderType.HISTORY -> R.drawable.ic_missing_po_orders to R.string.missing_history_orders
                    OrderType.TAX_ORDER -> R.drawable.ic_missing_orders to R.string.missing_orders
                }
                NoRecords(
                    icon = icon,
                    text = text,
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
                            OrderItem(item) { scope.selectItem(item) }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrderItem(order: Order, onClick: () -> Unit) {
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
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = order.tradeName,
                    color = MaterialTheme.colors.background,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Space(4.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrdersStatus(order.info.status)
                Text(
                    text = "${order.info.date} ${order.info.time}",
                    color = ConstColors.gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ConstColors.gray.copy(0.05f))
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                        append(" ")
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
            Space(4.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ConstColors.gray.copy(0.05f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if(order.orderTypeInfo.type.formatted.isNotEmpty()) {
                    Text(
                        text = order.orderTypeInfo.type.formatted,
                        color = when (order.orderTypeInfo.type.formatted) {
                            InventoryScope.StockStatus.ONLINE.title -> ConstColors.green
                            InventoryScope.StockStatus.OFFLINE.title -> ConstColors.red
                            else -> ConstColors.orange
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Space(10.dp)
                if(order.orderTypeInfo.user.isNotEmpty()) {
                    Text(
                        text = order.orderTypeInfo.user,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun OrdersStatus(status: OrderStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawRoundRect(
                color = when (status) {
                    OrderStatus.COMPLETED -> ConstColors.green
                    OrderStatus.CANCELLED -> ConstColors.red
                    else -> ConstColors.orange
                },
                cornerRadius = CornerRadius(8.dp.value),
            )
        }
        Space(4.dp)
        Text(
            text = status.stringValue,
            color = ConstColors.gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
                                DateTime
                                    .now()
                                    .withYear(year)
                                    .withMonthOfYear(month + 1)
                                    .withDayOfMonth(day)
                                    .millis
                            )
                        },
                        now.year,
                        now.monthOfYear - 1,
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