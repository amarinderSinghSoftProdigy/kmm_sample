package com.zealsoftsol.medico.screens.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InventoryMainComposable(scope: InventoryScope) {
    val manufacturersList = scope.manufacturersList.flow.collectAsState().value
    val productsList = scope.productsList.flow.collectAsState().value
    val totalResults = scope.totalProducts
    val mCurrentManufacturer = scope.mCurrentManufacturerName.flow.collectAsState().value
    val searchTerm = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var queryTextChangedJob: Job? = null
    val showNoBatchesDialog = scope.showNoBatchesDialog.flow.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(
                        indication = null,
                        onClick = {
                            scope.goBack()
                        }
                    )
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
                    .padding(end = 40.dp),
                shape = RoundedCornerShape(3.dp),
                elevation = 5.dp
            ) {
                TextField(
                    modifier = Modifier.height(48.dp),
                    value = searchTerm.value,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        placeholderColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    onValueChange = {

                        searchTerm.value = it

                        queryTextChangedJob?.cancel()

                        queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            scope.startSearch(it)
                        }
                    },
                    placeholder = {
                        Text(
                            stringResource(id = R.string.search_inventory),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    maxLines = 1
                )
            }
        }
        Divider(
            color = ConstColors.lightBlue,
            thickness = 0.5.dp,
            startIndent = 0.dp
        )
        Space(10.dp)
        LazyRow(
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
        LazyRow(
            contentPadding = PaddingValues(start = 3.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            manufacturersList.let {
                itemsIndexed(
                    items = it,
                    key = { index, _ -> index },
                    itemContent = { index, item ->
                        ManufacturersItem(item, scope) {
                            it.forEachIndexed { _, it ->
                                it.isChecked = false
                            }
                            it[index].isChecked = true
                            scope.updateManufacturer(item.name, item.code)
                        }
                    },
                )
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
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = mCurrentManufacturer,
                        color = ConstColors.green,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        modifier = Modifier.padding(top = 5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_prod),
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = totalResults.toString(),
                            color = Color.Black,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    color = ConstColors.green,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .padding(3.dp)
                        )

                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(start = 3.dp),
                    modifier = Modifier
                        .padding(horizontal = 7.dp)
                        .weight(0.9f),
                ) {
                    itemsIndexed(
                        items = productsList,
                        key = { index, _ -> index },
                        itemContent = { index, item ->
                            ProductsItem(item, scope)
                        },
                    )
                }

                if (productsList.size < totalResults) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(top = 5.dp)
                            .height(30.dp)
                            .clickable {
                                scope.getInventory(search = searchTerm.value)
                            },
                        text = stringResource(id = R.string.more),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp
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

/**
 * Display product data
 */
@Composable
private fun ProductsItem(item: ProductsData, scope: InventoryScope) {
    Column(
        verticalArrangement = Arrangement.Center, modifier = Modifier
            .height(75.dp)
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
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
                verticalAlignment = Alignment.CenterVertically
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
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
                verticalAlignment = Alignment.CenterVertically
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
private fun ManufacturersItem(
    item: ManufacturerData,
    scope: InventoryScope,
    onClick: () -> Unit
) {
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
        CoilImageBrands(
            src = CdnUrlProvider.urlForManufacturers(item.code),
            contentScale = ContentScale.Crop,
            onError = { ItemPlaceholder() },
            onLoading = { ItemPlaceholder() },
            height = 90.dp,
            width = 150.dp,
        )
    }
    Space(12.dp)
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

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
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
