package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.data.BannerData
import com.zealsoftsol.medico.data.BrandsData
import com.zealsoftsol.medico.data.DashboardData
import com.zealsoftsol.medico.data.ProductSold
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.ShimmerItem
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(scope: DashboardScope) {
    val unreadNotifications = scope.unreadNotifications.flow.collectAsState()
    val dashboard = scope.dashboard.flow.collectAsState()
    if (scope.userType == UserType.STOCKIST) {
        ShowStockistDashBoard(unreadNotifications, dashboard, scope)
    } else if (scope.userType == UserType.RETAILER || scope.userType == UserType.HOSPITAL) {
        ShowRetailerAndHospitalDashboard(unreadNotifications, dashboard, scope)
    }
}

/**
 * show dashboard specific to retailer and hospitals
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowRetailerAndHospitalDashboard(
    unreadNotifications: State<Int>,
    dashboard: State<DashboardData?>,
    scope: DashboardScope
) {
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Space(dp = 16.dp)
            LazyRow(state = lazyListState) {
                dashboard.value?.banners?.let {
                    itemsIndexed(
                        items = it,
                        key = { pos, _ -> pos },
                        itemContent = { _, item ->
                            BannerItem(
                                item, scope, modifier = Modifier
                                    .fillParentMaxWidth()
                                    .height(180.dp)
                                    .padding(horizontal = 16.dp)
                            )
                        },
                    )
                }
            }
            //auto rotate banner after every 3 seconds
            dashboard.value?.banners?.let {
                LaunchedEffect(lazyListState.firstVisibleItemIndex) {
                    delay(3000) // wait for 3 seconds.
                    // increasing the position and check the limit
                    var newPosition = lazyListState.firstVisibleItemIndex + 1
                    if (newPosition > it.size - 1) newPosition = 0
                    // scrolling to the new position.
                    if (newPosition == 0) {
                        lazyListState.scrollToItem(newPosition)
                    } else {
                        lazyListState.animateScrollToItem(newPosition)
                    }
                }
            }

            Space(dp = 16.dp)
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                BigButtonRetailer(
                    icon = R.drawable.ic_orders_dashboard,
                    text = stringResource(id = R.string.orders),
                    counter = dashboard.value?.ordersCount ?: 0,
                    onClick = { scope.goToOrders() },
                )
                Space(16.dp)
                BigButtonRetailer(
                    icon = R.drawable.ic_bell_dashboard,
                    text = stringResource(id = R.string.notifications),
                    counter = unreadNotifications.value,
                    onClick = { scope.goToNotifications() },
                )
                Space(16.dp)
                BigButtonRetailer(
                    icon = R.drawable.ic_stockist_dashboard,
                    text = stringResource(id = R.string.stockist),
                    counter = scope.sections[1].getCount(dashboard = dashboard.value),
                    onClick = { scope.selectSection(scope.sections[1]) },
                )
            }
            Space(dp = 16.dp)
            Text(
                text = stringResource(id = R.string.our_brands),
                color = ConstColors.lightBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Space(dp = 16.dp)
            LazyRow(
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                dashboard.value?.brands?.let {
                    itemsIndexed(
                        items = it,
                        key = { index, _ -> index },
                        itemContent = { _, item ->
                            BrandsItem(item, scope)
                        },
                    )
                }
            }
            Space(dp = 16.dp)
            Text(
                text = stringResource(id = R.string.our_categories),
                color = ConstColors.lightBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Space(dp = 16.dp)
            val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 8.dp

            FlowRow(
                mainAxisSize = SizeMode.Expand,
                mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
            ) {
                dashboard.value?.categories?.let {
                    it.forEachIndexed { index, _ ->
                        CategoriesItem(it[index], scope, modifier = Modifier.width(itemSize))
                    }
                }
            }
        }
    }
}

/**
 * UI for items in Banner on top
 */
@Composable
private fun BannerItem(item: BannerData, scope: DashboardScope, modifier: Modifier) {
    Card(
        modifier = modifier
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on category
                }),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        CoilImageBrands(
            src = item.cdnUrl,
            contentScale = ContentScale.FillBounds,
            onError = { ItemPlaceholder() },
            onLoading = { ItemPlaceholder() },
            height = 180.dp,
        )
    }
    Space(12.dp)
}

/**
 * ui item for brands listing
 */
@Composable
private fun BrandsItem(item: BrandsData, scope: DashboardScope) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .width(150.dp)
            .padding(start = 2.dp)
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on category
                    scope.startBrandSearch(item.searchTerm, item.field)
                }),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        CoilImageBrands(
            src = item.imageUrl,
            contentScale = ContentScale.FillBounds,
            onError = { ItemPlaceholder() },
            onLoading = { ItemPlaceholder() },
            height = 90.dp,
            width = 150.dp,
        )
    }
    Space(12.dp)
}

/**
 * ui item for brands listing
 */

@Composable
private fun BrandsImageItem(item: ProductSold, scope: DashboardScope) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .padding(end = 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .height(80.dp)
                .width(120.dp),
            elevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            color = Color.White,
        ) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(120.dp),
            ) {
                CoilImageBrands(
                    src = "",
                    contentScale = ContentScale.Crop,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 80.dp,
                    width = 120.dp,
                )
                if (item.count > 0) {
                    RedCounter(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(all = 4.dp),
                        count = item.count,
                    )
                }
            }
        }
        Space(8.dp)
        if (!item.isSkeletonItem) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = item.productName,
                color = MaterialTheme.colors.background,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        } else {
            ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
        }
        Space(8.dp)
    }
}

/**
 * ui item for categories listing
 */
@Composable
private fun CategoriesItem(item: BrandsData, scope: DashboardScope, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(215.dp)
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on product
                    scope.startBrandSearch(item.searchTerm, item.field)
                })
            .padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CoilImageBrands(
                src = item.imageUrl,
                contentScale = ContentScale.Crop,
                onError = { ItemPlaceholder() },
                onLoading = { ItemPlaceholder() },
                height = 180.dp,
            )

            Text(
                text = item.name!!,
                textAlign = TextAlign.Center,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
            )
        }
    }
}

/**
 * show user type specific to stockist only
 */
@Composable
private fun ShowStockistDashBoard(
    unreadNotifications: State<Int>,
    dashboard: State<DashboardData?>,
    scope: DashboardScope
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Space(dp = 16.dp)
        dashboard.value.let { dash ->
            scope.sections.windowed(3, 3).forEach { (first, second, third) ->
                Space(4.dp)
                Row {
                    SectionButton(
                        icon = first.getIcon(),
                        text = stringResourceByName(first.stringId),
                        isClickable = first.isClickable,
                        counter = dash?.let { first.getCount(dashboard = dash) },
                        counterSupported = first.countSupported(),
                        onClick = { scope.selectSection(first) },
                    )
                    Space(16.dp)
                    SectionButton(
                        icon = second.getIcon(),
                        text = stringResourceByName(second.stringId),
                        isClickable = second.isClickable,
                        counter = dash?.let { second.getCount(dashboard = dash) },
                        counterSupported = second.countSupported(),
                        onClick = { scope.selectSection(second) },
                    )
                    Space(16.dp)
                    SectionButton(
                        icon = third.getIcon(),
                        text = stringResourceByName(third.stringId),
                        isClickable = third.isClickable,
                        counter = dash?.let { third.getCount(dashboard = dash) },
                        counterSupported = third.countSupported(),
                        onClick = { scope.selectSection(third) },
                    )
                }
            }
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.inventory),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Space(dp = 8.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                val shape1 = MaterialTheme.shapes.large.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White/*ConstColors.green.copy(alpha = .2f)*/, shape1)
                        .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape1)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Row {
                        Icon(
                            contentDescription = null,
                            tint = ConstColors.lightGreen,
                            painter = painterResource(id = R.drawable.ic_menu_inventory)
                        )
                        Space(dp = 8.dp)
                        dash?.stockStatusData?.inStock?.let {
                            Text(
                                text = it.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.in_stock),
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
                val shape2 = MaterialTheme.shapes.large.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White/*ConstColors.red.copy(alpha = .2f)*/, shape2)
                        .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape2)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        Icon(
                            contentDescription = null,
                            tint = ConstColors.orange,
                            painter = painterResource(id = R.drawable.ic_menu_inventory)
                        )
                        Space(dp = 8.dp)
                        dash?.stockStatusData?.outOfStock?.let {
                            Text(
                                text = it.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(start = 12.dp, top = 8.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.out_stock),
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
            }
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.offers),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Space(dp = 8.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                val shape1 = MaterialTheme.shapes.large.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape1)
                        .clickable {
                            scope.moveToOffersScreen()
                        }
                        .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape1)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Row {
                        Icon(
                            contentDescription = null,
                            tint = ConstColors.lightGreen,
                            painter = painterResource(id = R.drawable.ic_offer)
                        )
                        Space(dp = 8.dp)
                        dash?.offers?.let {
                            Text(
                                text = if (it.isNotEmpty() && it.size > 1) it[1].total.toString() else "0",
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.running),
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
                val shape2 = MaterialTheme.shapes.large.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape2)
                        .clickable {
                            scope.moveToOffersScreen()
                        }
                        .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape2)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        Icon(
                            contentDescription = null,
                            tint = ConstColors.orange,
                            painter = painterResource(id = R.drawable.ic_offer)
                        )
                        Space(dp = 8.dp)
                        dash?.offers?.let {
                            Text(
                                text = if (it.isNotEmpty()) it[0].total.toString() else "0",
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(start = 12.dp, top = 8.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.ended),
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
            }
            Space(16.dp)
            val soldExpanded = remember { mutableStateOf(false) }

            Text(
                text = stringResource(id = R.string.today_sold),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Space(8.dp)
            /*FoldableItem(
                expanded = soldExpanded.value,
                headerBackground = Color.White,
                headerMinHeight = 62.dp,
                header = { isExpanded ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.today_sold),
                            color = MaterialTheme.colors.background,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            tint = ConstColors.lightBlue,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                childItems = dash?.productInfo?.mostSold ?: listOf(ProductSold.skeleton),
                itemHorizontalPadding = 0.dp,
                itemSpacing = 0.dp,
                item = { value, index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (!value.isSkeletonItem) 40.dp else 60.dp)
                            .background(
                                if (index % 2 != 0) Color.White else ConstColors.gray.copy(
                                    alpha = .05f
                                )
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!value.isSkeletonItem) {
                            Text(
                                text = value.productName,
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = value.count.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .background(
                                        ConstColors.gray.copy(.15f),
                                        MaterialTheme.shapes.medium
                                    )
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                            )
                        } else {
                            ShimmerItem(padding = PaddingValues(horizontal = 12.dp))
                        }
                    }
                },
            )*/

            dash?.productInfo?.mostSold?.let {
                LazyRow {
                    itemsIndexed(
                        items = it,
                        itemContent = { _, item ->
                            BrandsImageItem(item, scope)
                        },
                    )
                }
            } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 12.dp))


            Space(8.dp)
            Text(
                text = stringResource(id = R.string.most_searched),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Space(8.dp)
            val searchExpanded = remember { mutableStateOf(false) }
            /*FoldableItem(
                expanded = searchExpanded.value,
                headerBackground = Color.White,
                headerMinHeight = 62.dp,
                header = { isExpanded ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.most_searched),
                            color = MaterialTheme.colors.background,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            tint = ConstColors.lightBlue,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                childItems = dash?.productInfo?.mostSearched ?: listOf(ProductSold.skeleton),
                itemHorizontalPadding = 0.dp,
                itemSpacing = 0.dp,
                item = { value, index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (!value.isSkeletonItem) 40.dp else 60.dp)
                            .background(
                                if (index % 2 != 0) Color.White else ConstColors.gray.copy(
                                    alpha = .05f
                                )
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!value.isSkeletonItem) {
                            Text(
                                text = value.productName,
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = value.count.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .background(
                                        ConstColors.gray.copy(.15f),
                                        MaterialTheme.shapes.medium
                                    )
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                            )
                        } else {
                            ShimmerItem(padding = PaddingValues(horizontal = 12.dp))
                        }
                    }
                },
            )*/

            dash?.productInfo?.mostSearched?.let {
                LazyRow {
                    itemsIndexed(
                        items = it,
                        itemContent = { _, item ->
                            BrandsImageItem(item, scope)
                        },
                    )
                }
            } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 12.dp))
            /*Text(
                text = stringResource(id = R.string.no_products),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )*/

        }
        Space(dp = 16.dp)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RowScope.BigButton(
    icon: Int,
    text: String,
    counter: Int,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        onClick = onClick,
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                tint = MaterialTheme.colors.background,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd),
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.size(60.dp)) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                    if (counter > 0) {
                        RedCounter(
                            modifier = Modifier.align(Alignment.TopEnd),
                            count = counter,
                        )
                    }
                }
                Text(
                    text = text,
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RowScope.BigButtonRetailer(
    icon: Int,
    text: String,
    counter: Int?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.medium,
        color = ConstColors.yellow,
        onClick = onClick,
        elevation = 3.dp
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.size(50.dp)) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center),
                    )
                    if (counter != null && counter > 0) {
                        RedCounter(
                            modifier = Modifier.align(Alignment.TopEnd),
                            count = counter,
                        )
                    }
                }
                Text(
                    text = text,
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RowScope.SectionButton(
    icon: Painter,
    text: String,
    isClickable: Boolean,
    counter: Int?,
    counterSupported: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.medium,
        color = ConstColors.yellow,
        enabled = isClickable,
        onClick = onClick,
    ) {

        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            /* if (isClickable) Icon(
                 imageVector = Icons.Default.ChevronRight,
                 tint = MaterialTheme.colors.background,
                 contentDescription = null,
                 modifier = Modifier.align(Alignment.TopEnd),
             )*/
            if (counterSupported) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp),
                        ) {

                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {

                                Icon(
                                    painter = icon,
                                    tint = MaterialTheme.colors.background,//ConstColors.gray.copy(alpha = .5f),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp),
                                )
                            }
                            if (counter != null)
                                RedCounter(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(all = 4.dp),
                                    count = counter,
                                )

                        }


                        /*Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Icon(
                                painter = icon,
                                tint = MaterialTheme.colors.background,//ConstColors.gray.copy(alpha = .5f),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ConstColors.red,
                                    modifier = Modifier.size(25.dp)
                                ) {
                                    if (counter != null) {
                                        Text(
                                            modifier = Modifier.padding(start = 7.dp, top = 3.dp),
                                            text = counter.toString(),
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.W700,
                                        )
                                    } *//*else {
                                ShimmerItem(padding = PaddingValues(end = 48.dp, top = 8.dp))
                            }*//*

                                }
                            }

                        }
*/
                        Space(dp = 4.dp)
                        Text(
                            text = text,
                            color = MaterialTheme.colors.background,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center),
                        )
                    }
                    Text(
                        text = text,
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
            }
        }
    }
}

@Composable
private fun RedCounter(
    modifier: Modifier,
    count: Int,
) {
    Box(
        modifier = modifier
            .background(Color.Red, CircleShape)
            .size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Composable
private inline fun DashboardScope.Section.getIcon(): Painter = when (this) {
    DashboardScope.Section.STOCKIST_COUNT -> painterResource(id = R.drawable.ic_stockist)
    DashboardScope.Section.STOCKIST_ADD -> painterResource(id = R.drawable.ic_stockist)
    DashboardScope.Section.STOCKIST_CONNECT -> painterResource(id = R.drawable.ic_stockist_connect)
    DashboardScope.Section.RETAILER_COUNT -> painterResource(id = R.drawable.ic_retailer)
    DashboardScope.Section.RETAILER_ADD -> painterResource(id = R.drawable.ic_retailer)
    DashboardScope.Section.HOSPITAL_COUNT -> painterResource(id = R.drawable.ic_hospital)
//    DashboardScope.Section.SEASON_BOY_COUNT -> painterResource(id = R.drawable.ic_season_boy)
}

@Composable
private inline fun DashboardScope.Section.getCount(dashboard: DashboardData?): Int? = when (this) {
    DashboardScope.Section.STOCKIST_COUNT -> dashboard?.userData?.stockist?.totalSubscribed
    DashboardScope.Section.STOCKIST_ADD -> null
    DashboardScope.Section.STOCKIST_CONNECT -> null
    DashboardScope.Section.RETAILER_COUNT -> dashboard?.userData?.retailer?.totalSubscribed
    DashboardScope.Section.RETAILER_ADD -> null
    DashboardScope.Section.HOSPITAL_COUNT -> dashboard?.userData?.hospital?.totalSubscribed
//    DashboardScope.Section.SEASON_BOY_COUNT -> dashboard.userData.seasonBoy?.totalSubscribed
}

private inline fun DashboardScope.Section.countSupported(): Boolean = when (this) {
    DashboardScope.Section.STOCKIST_COUNT -> true
    DashboardScope.Section.STOCKIST_ADD -> false
    DashboardScope.Section.STOCKIST_CONNECT -> false
    DashboardScope.Section.RETAILER_COUNT -> true
    DashboardScope.Section.RETAILER_ADD -> false
    DashboardScope.Section.HOSPITAL_COUNT -> true
//    DashboardScope.Section.SEASON_BOY_COUNT -> true
}