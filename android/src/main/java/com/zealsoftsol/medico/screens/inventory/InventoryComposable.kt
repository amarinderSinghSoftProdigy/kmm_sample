package com.zealsoftsol.medico.screens.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.ManufacturerData
import com.zealsoftsol.medico.data.ProductsData
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.utils.piechart.PieChart
import com.zealsoftsol.medico.utils.piechart.PieChartData
import com.zealsoftsol.medico.utils.piechart.renderer.SimpleSliceDrawer
import com.zealsoftsol.medico.utils.piechart.simpleChartAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun InventoryMainComposable(scope: InventoryScope) {
    val manufacturersList = scope.manufacturersList.flow.collectAsState().value
    val productsList = scope.productsList.flow.collectAsState().value
    val totalResults = scope.totalProducts
    val searchTerm = remember { mutableStateOf("") }
    var queryTextChangedJob: Job? = null
    val showNoBatchesDialog = scope.showNoBatchesDialog.flow.collectAsState().value
    val showSearchBar = remember { mutableStateOf(false) }
    val showManufacturers = remember { mutableStateOf(false) }
    val showGraphs = remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(
                        indication = null,
                        onClick = {
                            if (showSearchBar.value) {
                                showSearchBar.value = false
                            } else {
                                scope.goBack()
                            }
                        }
                    )
            )
                AnimatedVisibility(visible = showSearchBar.value) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .padding(start = 10.dp)
                            .padding(end = 45.dp)
                            .align(CenterVertically),
                        shape = RoundedCornerShape(3.dp),
                        elevation = 3.dp,
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .height(45.dp)
                                .fillMaxWidth(), verticalAlignment = CenterVertically
                        ) {
                            BasicTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 5.dp),
                                value = searchTerm.value,
                                maxLines = 1,
                                singleLine = true,
                                onValueChange = {
                                    searchTerm.value = it

                                    queryTextChangedJob?.cancel()

                                    queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                                        delay(500)
                                        scope.startSearch(it)
                                    }
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    background = Color.White,
                                ),
                                decorationBox = { innerTextField ->
                                    Row(modifier = Modifier) {
                                        if (searchTerm.value.isEmpty()) {
                                            Text(
                                                text = stringResource(id = R.string.search_inventory),
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }


            if (!showSearchBar.value) {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = Color.Transparent,
                        onClick = {
                            showGraphs.value = !showGraphs.value
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pie_graph),
                            tint = ConstColors.gray,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(2.dp),
                        )

                    }
                    Space(10.dp)
                    Surface(
                        color = Color.Transparent,
                        onClick = {
                            showManufacturers.value = !showManufacturers.value
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_manufacturer_toolbar),
                            tint = ConstColors.gray,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(2.dp),
                        )

                    }
                    Space(10.dp)
                    Surface(
                        color = Color.Transparent,
                        onClick = {
                            showSearchBar.value = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = ConstColors.gray,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(2.dp),
                        )

                    }
                }
            }
        }
        Divider(
            color = ConstColors.lightBlue,
            thickness = 0.5.dp,
            startIndent = 0.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StockStatus(scope)
            InventoryStatus(scope)
        }

    AnimatedVisibility(visible = showGraphs.value) {
        Space(10.dp)
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp)
        ) {
            item {
                StatusView(
                    scope = scope, modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(8.dp)
                )
            }
            item {
                AvailabilityView(
                    scope = scope,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(8.dp),
                )
            }
            item {
                ExpiryView(
                    scope = scope,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(8.dp),
                )
            }
        }

        //auto rotate banner after every 3 seconds
        LaunchedEffect(lazyListState.firstVisibleItemIndex) {
            delay(5000) // wait for 5 seconds.
            // increasing the position and check the limit
            var newPosition = lazyListState.firstVisibleItemIndex + 1
            if (newPosition > 3 - 1) newPosition = 0
            // scrolling to the new position.
            if (newPosition == 0) {
                lazyListState.scrollToItem(newPosition)
            } else {
                lazyListState.animateScrollToItem(newPosition)
            }
        }
    }

    AnimatedVisibility(visible = showManufacturers.value) {
        Space(12.dp)
        LazyRow(
            contentPadding = PaddingValues(start = 3.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            manufacturersList.let {
                itemsIndexed(
                    items = it,
                    key = { index, _ -> index },
                    itemContent = { index, item ->
                        ManufacturersItem(item) {
                            it.forEachIndexed { _, it ->
                                it.isChecked = false
                            }
                            it[index].isChecked = true
                            scope.updateManufacturer(item.code)
                        }
                    },
                )
            }
        }
    }

        Space(dp = 16.dp)
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 5.dp),
            elevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color.White,
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                if (productsList.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 3.dp),
                        modifier = Modifier
                            .padding(horizontal = 7.dp)
                            .weight(0.9f),
                    ) {
                        itemsIndexed(
                            items = productsList,
                            key = { index, _ -> index },
                            itemContent = { _, item ->
                                ProductsItem(item, scope)
                            },
                        )

                        item {
                            if (productsList.size < totalResults) {
                                MedicoButton(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(top = 5.dp, bottom = 5.dp)
                                        .height(40.dp),
                                    text = stringResource(id = R.string.more),
                                    isEnabled = true,
                                ) {
                                    scope.getInventory(search = searchTerm.value)
                                }
                            }
                        }
                    }

                } else {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.no_products),
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (showNoBatchesDialog) {
        ShowAlert(message = stringResource(id = R.string.no_batches_available)) {
            scope.hideBatchesDialog()
        }
    }
}

@Composable
fun StockStatus(scope: InventoryScope) {

    val options = listOf(
        InventoryScope.StockStatus.ALL,
        InventoryScope.StockStatus.ONLINE,
        InventoryScope.StockStatus.OFFLINE,
    )
    val stockStatus = scope.stockStatus.flow.collectAsState().value
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .horizontalScroll(
                rememberScrollState()
            ),
    ) {
        Text(
            modifier = Modifier.padding(end = 5.dp),
            text = "${stringResource(id = R.string.status)}:",
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.W600
        )
        options.forEach {
            Row(Modifier.padding(all = 5.dp)) {
                Text(
                    text = it.title,
                    color = if (it == stockStatus) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 10.dp,
                            ),
                        )
                        .clickable {
                            scope.updateInventoryStatus(it)
                        }
                        .background(
                            if (it == stockStatus) {
                                ConstColors.green
                            } else {
                                Color.LightGray
                            }
                        )
                        .padding(
                            vertical = 5.dp,
                            horizontal = 15.dp,
                        ),
                )
            }
        }
    }
}

@Composable
fun InventoryStatus(scope: InventoryScope) {

    val options = listOf(
        InventoryScope.InventoryType.ALL,
        InventoryScope.InventoryType.IN_STOCK,
        InventoryScope.InventoryType.LIMITED_STOCK,
        InventoryScope.InventoryType.OUT_OF_STOCK,
    )
    val inventoryStatus = scope.inventoryType.flow.collectAsState().value
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Text(
            modifier = Modifier.padding(end = 5.dp),
            text = "${stringResource(id = R.string.stock_status)}:",
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.W600
        )
        options.forEach {
            Row(modifier = Modifier.padding(all = 5.dp)) {
                Text(
                    text = it.title,
                    color = if (it == inventoryStatus) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 10.dp,
                            ),
                        )
                        .clickable {
                            scope.updateInventoryType(it)
                        }
                        .background(
                            if (it == inventoryStatus) {
                                ConstColors.green
                            } else {
                                Color.LightGray
                            }
                        )
                        .padding(
                            vertical = 5.dp,
                            horizontal = 5.dp,
                        ),
                )
            }
        }
    }
}

/**
 * Display product data
 */
@Composable
private fun ProductsItem(item: ProductsData, scope: InventoryScope) {
    Column(
        verticalArrangement = Arrangement.Center, modifier = Modifier
            .height(90.dp)
            .clickable {
                scope.getBatchesData(item.spid ?: "", item)
            }) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.vendorProductName ?: "",
                color = Color.Black,
                fontSize = 12.sp,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = item.ptr?.formattedValue ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(end = 5.dp)
                )

                Divider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier
                        .height(15.dp)
                        .width(1.dp)
                )

                Text(
                    text = item.expiryDate?.formattedValue ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.stock),
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CommonRoundedView(
                    text = item.availableQty?.formattedValue ?: "", modifier = Modifier.padding(
                        end = 5.dp
                    ), color = ConstColors.darkGreen, radius = 2
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = item.stockStatus ?: "",
                    color = when (item.stockStatusCode ?: "") {
                        StockStatus.IN_STOCK.name -> ConstColors.green
                        StockStatus.LIMITED_STOCK.name -> ConstColors.orange
                        StockStatus.OUT_OF_STOCK.name -> ConstColors.red
                        else -> ConstColors.gray
                    },
                    fontSize = 12.sp,
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = item.status ?: "",
                    color = when (item.status) {
                        "ONLINE" -> {
                            ConstColors.lightGreen
                        }
                        "OFFLINE" -> {
                            ConstColors.red
                        }
                        else -> {
                            Color.Black
                        }
                    },
                    fontSize = 12.sp,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(.7f),
                text = item.vendorMnfrName ?: "",
                fontSize = 12.sp,
                color = ConstColors.green,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.weight(.3f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = CenterVertically
            ) {
                //to be uncommented when batch count is added in API
                /*Text(
                    text = stringResource(id = R.string.batchs),
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CommonRoundedView(
                    text = item.availableQty?.formattedValue ?: "", modifier = Modifier.padding(
                        end = 5.dp
                    ), color = ConstColors.darkGreen, radius = 2
                )*/
            }
        }

        Divider(
            thickness = 1.dp,
            color = ConstColors.separator.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

/**
 * ui item for manufacturer listing
 */
@Composable
fun ManufacturersItem(
    item: ManufacturerData,
    onClick: () -> Unit
) {
    Column {
        Card(
            modifier = Modifier
                .height(90.dp)
                .width(150.dp)
                .selectable(
                    selected = true,
                    onClick = onClick
                ),
            elevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color.White,
            border = if (item.isChecked) BorderStroke(
                2.dp,
                ConstColors.yellow.copy(alpha = 0.5f),
            ) else
                BorderStroke(
                    1.dp,
                    Color.White,
                )
        ) {
            Box {
                CoilImageBrands(
                    src = CdnUrlProvider.urlForM(item.code),
                    contentScale = ContentScale.Crop,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 90.dp,
                    width = 150.dp,
                )
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .align(Alignment.TopEnd)
                ) {
                    CommonRoundedView(
                        text = item.count.toString(), modifier = Modifier
                            .align(Alignment.TopEnd), color = ConstColors.darkGreen, radius = 2
                    )
                }
            }
        }
        Space(5.dp)
        Text(
            modifier = Modifier
                .width(150.dp),
            text = item.name,
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * View to display Status of online and offline
 */
@Composable
private fun StatusView(scope: InventoryScope, modifier: Modifier) {
    val statusData = scope.onlineStatusData.flow.collectAsState().value

    Card(
        modifier = modifier,
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            statusData?.let {
                val (status, online, offline, chart) = createRefs()
                Text(
                    text = stringResource(R.string.status),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.constrainAs(status) {
                        start.linkTo(parent.start, margin = 5.dp)
                        top.linkTo(parent.top, margin = 5.dp)
                    }
                )
                CommonRoundedView(
                    text = "${stringResource(R.string.online)}: ${it.onlineProductsCount}",
                    modifier = Modifier.constrainAs(online) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(offline.top, margin = 5.dp)
                    },
                    color = ConstColors.darkGreen
                )

                CommonRoundedView(
                    text = "${stringResource(R.string.offline)}: ${it.offlineProductCount}",
                    modifier = Modifier.constrainAs(offline) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    },
                    color = ConstColors.darkRed
                )

                MyChartParent(
                    thickness = 70f,
                    listPieChartData = listOf(
                        //online product
                        PieChartData.Slice(
                            it.onlineProductsCount.divideToPercent(it.onlineProductsCount + it.offlineProductCount),
                            ConstColors.darkGreen
                        ),
                        //offline product
                        PieChartData.Slice(
                            it.offlineProductCount.divideToPercent(it.onlineProductsCount + it.offlineProductCount),
                            ConstColors.darkRed
                        )
                    ),
                    modifier = Modifier.constrainAs(chart) {
                        width = Dimension.value(140.dp)
                        height = Dimension.value(140.dp)
                        end.linkTo(parent.end, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                        top.linkTo(parent.top, margin = 10.dp)
                    }
                )
            }

        }
    }
}


/**
 * View to display Availability of products
 */
@Composable
private fun AvailabilityView(
    scope: InventoryScope,
    modifier: Modifier,
) {
    val availabilityData = scope.stockStatusData.flow.collectAsState().value

    Card(
        modifier = modifier,
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val (status, first, second, third, chart) = createRefs()
            Text(
                text = stringResource(id = R.string.availability),
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.constrainAs(status) {
                    start.linkTo(parent.start, margin = 5.dp)
                    top.linkTo(parent.top, margin = 5.dp)
                }
            )
            availabilityData?.let {
                ColorIndicatorTextView(
                    text = "${stringResource(R.string.in_stock)}: ${it.inStock}",
                    modifier = Modifier.constrainAs(first) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(second.top, margin = 5.dp)
                    },
                    color = ConstColors.darkGreen
                )

                ColorIndicatorTextView(
                    text = "${stringResource(R.string.limited_stock)}: ${it.limitedStock}",
                    modifier = Modifier.constrainAs(second) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(third.top, margin = 5.dp)
                    },
                    color = ConstColors.orange
                )

                ColorIndicatorTextView(
                    text = "${stringResource(R.string.out_stock)}: ${it.outOfStock}",
                    modifier = Modifier.constrainAs(third) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    },
                    color = ConstColors.darkRed
                )

                MyChartParent(
                    thickness = 70F,
                    listPieChartData = listOf(
                        //out of stock data
                        PieChartData.Slice(
                            it.outOfStock.divideToPercent(it.outOfStock + it.inStock + it.limitedStock),
                            ConstColors.darkRed
                        ),
                        // in stock data
                        PieChartData.Slice(
                            it.inStock.divideToPercent(it.outOfStock + it.inStock + it.limitedStock),
                            ConstColors.darkGreen
                        ),
                        //limited stock data
                        PieChartData.Slice(
                            it.limitedStock.divideToPercent(it.limitedStock + it.inStock + it.limitedStock),
                            ConstColors.orange
                        )
                    ),
                    modifier = Modifier.constrainAs(chart) {
                        width = Dimension.value(140.dp)
                        height = Dimension.value(140.dp)
                        end.linkTo(parent.end, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                        top.linkTo(parent.top, margin = 10.dp)
                    }
                )
            }
        }
    }
}

/**
 * View to display data of expiry of products
 */

@Composable
private fun ExpiryView(
    scope: InventoryScope,
    modifier: Modifier,
) {
    val expiryData = scope.stockExpiredData.flow.collectAsState().value

    Card(
        modifier = modifier,
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val (status, first, second, third, chart) = createRefs()
            Text(
                text = stringResource(id = R.string.expiry_),
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.constrainAs(status) {
                    start.linkTo(parent.start, margin = 5.dp)
                    top.linkTo(parent.top, margin = 5.dp)
                }
            )
            expiryData?.let {
                ColorIndicatorTextView(
                    text = "${stringResource(R.string.long_expiry)}: ${it.moreThan6Months}",
                    modifier = Modifier.constrainAs(first) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(second.top, margin = 5.dp)
                    },
                    color = ConstColors.lightBlue
                )

                ColorIndicatorTextView(
                    text = "${stringResource(R.string.near_expiry)}: ${it.lessThan6Months}",
                    modifier = Modifier.constrainAs(second) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(third.top, margin = 5.dp)
                    },
                    color = ConstColors.orange
                )

                ColorIndicatorTextView(
                    text = "${stringResource(R.string.expired)}: ${it.expired}",
                    modifier = Modifier.constrainAs(third) {
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    },
                    color = ConstColors.darkRed
                )

                MyChartParent(
                    thickness = 70f,
                    listPieChartData = listOf(
                        //Long expiry data
                        PieChartData.Slice(
                            it.moreThan6Months.divideToPercent(
                                it.moreThan6Months + it.lessThan6Months + it.expired
                            ), ConstColors.lightBlue
                        ),
                        //Expired data
                        PieChartData.Slice(
                            it.expired.divideToPercent(
                                it.moreThan6Months + it.lessThan6Months + it.expired
                            ),
                            ConstColors.darkRed
                        ),
                        //Near expiry data
                        PieChartData.Slice(
                            it.lessThan6Months.divideToPercent(
                                it.moreThan6Months + it.lessThan6Months + it.expired
                            ), ConstColors.orange
                        )
                    ),
                    modifier = Modifier.constrainAs(chart) {
                        width = Dimension.value(140.dp)
                        height = Dimension.value(140.dp)
                        end.linkTo(parent.end, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                        top.linkTo(parent.top, margin = 10.dp)
                    }
                )
            }
        }
    }
}


/**
 * draw chartview
 * @param modifier Modifiers to be applied to the chart view
 * @param thickness thickness of pie chart arcs (0-100f)
 * @param listPieChartData list of {@PieChartData.Slice} containing the values to be drawn
 */
@Composable
private fun MyChartParent(
    modifier: Modifier,
    thickness: Float,
    listPieChartData: List<PieChartData.Slice>
) {

    PieChart(
        pieChartData = PieChartData(
            listPieChartData
        ),
        modifier = modifier,
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer(sliceThickness = thickness)
    )
}

/**
 * common rounded textview
 */
@Composable
private fun CommonRoundedView(
    text: String,
    modifier: Modifier,
    color: Color,
    radius: Int = 5
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius.dp))
            .background(color)
            .height(20.dp)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
        )

    }
}

/**
 *  colored indicator textview
 */

@Composable
private fun ColorIndicatorTextView(color: Color, text: String, modifier: Modifier) {

    Row(modifier = modifier, verticalAlignment = CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .height(12.dp)
                .width(12.dp)

        )

        Text(
            text = text,
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 5.dp)
        )

    }

}

/**
 * calculate percentage of inventory based on total products
 */
fun Int.divideToPercent(divideTo: Int): Float {
    return if (divideTo == 0) 0F
    else (this / divideTo.toFloat())
}
