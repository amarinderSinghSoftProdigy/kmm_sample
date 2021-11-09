package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.data.DashboardData
import com.zealsoftsol.medico.data.ProductSold
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.ShimmerItem
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun DashboardScreen(scope: DashboardScope) {
    val unreadNotifications = scope.unreadNotifications.flow.collectAsState()
    val dashboard = scope.dashboard.flow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Space(dp = 16.dp)
        Row {
            BigButton(
                icon = R.drawable.ic_bell,
                text = stringResource(id = R.string.notifications),
                counter = unreadNotifications.value,
                onClick = { scope.goToNotifications() },
            )
            Space(16.dp)
            BigButton(
                icon = R.drawable.ic_orders,
                text = stringResource(id = R.string.orders),
                counter = dashboard.value?.ordersCount ?: 0,
                onClick = { scope.goToOrders() },
            )
        }

        dashboard.value.let { dash ->
            scope.sections.windowed(2, 2).forEach { (first, second) ->
                Space(16.dp)
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
                }
            }

            if (scope.userType == UserType.STOCKIST) {
                Space(16.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    val shape1 = MaterialTheme.shapes.large.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(ConstColors.green.copy(alpha = .2f), shape1)
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape1)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = stringResource(id = R.string.in_stock),
                            color = ConstColors.gray.copy(alpha = .5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                        dash?.stockStatusData?.inStock?.let {
                            Text(
                                text = it.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
                    }
                    val shape2 = MaterialTheme.shapes.large.copy(
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(ConstColors.red.copy(alpha = .2f), shape2)
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape2)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = stringResource(id = R.string.out_stock),
                            color = ConstColors.gray.copy(alpha = .5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                        dash?.stockStatusData?.outOfStock?.let {
                            Text(
                                text = it.toString(),
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } ?: ShimmerItem(padding = PaddingValues(start = 12.dp, top = 8.dp))
                    }
                }
                Space(16.dp)
                val soldExpanded = remember { mutableStateOf(false) }
                FoldableItem(
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
                )

                Space(16.dp)
                val searchExpanded = remember { mutableStateOf(false) }
                FoldableItem(
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
                )
            }
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
        color = Color.White,
        enabled = isClickable,
        onClick = onClick,
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            if (isClickable) Icon(
                imageVector = Icons.Default.ChevronRight,
                tint = MaterialTheme.colors.background,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd),
            )
            if (counterSupported) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                        .padding(end = 12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(start = 6.dp),
                    ) {
                        Text(
                            text = text,
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                        if (counter != null) {
                            Text(
                                text = counter.toString(),
                                color = ConstColors.lightBlue,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.W700,
                            )
                        } else {
                            ShimmerItem(padding = PaddingValues(end = 48.dp, top = 8.dp))
                        }
                    }
                    Icon(
                        painter = icon,
                        tint = ConstColors.gray.copy(alpha = .5f),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd),
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(modifier = Modifier.size(60.dp)) {
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
                        color = ConstColors.gray,
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
            .size(30.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 16.sp,
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
private inline fun DashboardScope.Section.getCount(dashboard: DashboardData): Int? = when (this) {
    DashboardScope.Section.STOCKIST_COUNT -> dashboard.userData.stockist.totalSubscribed
    DashboardScope.Section.STOCKIST_ADD -> null
    DashboardScope.Section.STOCKIST_CONNECT -> null
    DashboardScope.Section.RETAILER_COUNT -> dashboard.userData.retailer?.totalSubscribed
    DashboardScope.Section.RETAILER_ADD -> null
    DashboardScope.Section.HOSPITAL_COUNT -> dashboard.userData.hospital?.totalSubscribed
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