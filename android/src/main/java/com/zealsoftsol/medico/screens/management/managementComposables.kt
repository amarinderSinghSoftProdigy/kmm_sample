package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.SubscriptionStatus
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun ManagementScreen(scope: ManagementScope.User, isInProgress: DataSource<Boolean>) {
    Column(modifier = Modifier.fillMaxSize()) {
        EntityManagementScreen(scope, isInProgress)
    }
    if (scope is ManagementScope.User.Retailer && scope.canAdd) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = { scope.requestCreateRetailer() },
                backgroundColor = ConstColors.yellow,
                contentColor = MaterialTheme.colors.background,
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
            )
        }
    }
}

@Composable
private fun EntityManagementScreen(scope: ManagementScope.User, isInProgress: DataSource<Boolean>) {
    val search = scope.searchText.flow.collectAsState()
    val showAlert = scope.showAlert.flow.collectAsState()
    val showSearchOverlay = remember { mutableStateOf(true) }
    Space(16.dp)
    if (showSearchOverlay.value) {
        SearchBarBox(
            modifier = Modifier.clickable(indication = null) {
                showSearchOverlay.value = false
            },
            elevation = 0.dp,
            horizontalPadding = 18.dp,
        ) {
            val (icon, text) = when (scope) {
                is ManagementScope.User.Stockist -> R.drawable.ic_stockist to R.string.stockists_search
                is ManagementScope.User.Retailer -> R.drawable.ic_retailer_search to R.string.retailers_search
                is ManagementScope.User.SeasonBoy -> R.drawable.ic_season_boy to R.string.season_boys_search
                is ManagementScope.User.Hospital -> R.drawable.ic_hospital to R.string.hospitals_search
            }
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = ConstColors.lightGreen,
                modifier = Modifier.size(24.dp),
            )
            Space(16.dp)
            Text(
                text = stringResource(id = text),
                fontWeight = FontWeight.W700,
                color = ConstColors.txtGrey,
                fontSize = 12.sp
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    } else {
        BasicSearchBar(
            input = search.value,
            hint = when (scope) {
                is ManagementScope.User.Stockist -> R.string.stockists_search
                is ManagementScope.User.Retailer -> R.string.retailers_search
                is ManagementScope.User.SeasonBoy -> R.string.season_boys_search
                is ManagementScope.User.Hospital -> R.string.hospitals_search
            },
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
    val activeTab = scope.activeTab.flow.collectAsState()
    val totalItems = scope.totalItems.flow.collectAsState()
    if (scope.tabs.isNotEmpty()) {
        Space(16.dp)
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
                    boxMod.background(ConstColors.lightGreen, MaterialTheme.shapes.medium)
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
                            color = if (isActive) Color.White else MaterialTheme.colors.background,
                        )
                    }
                }
            }
        }
    }
    val items = scope.items.flow.collectAsState()
    if (items.value.isEmpty() && !isInProgress.flow.value) {
        val (icon, text) = when (scope) {
            is ManagementScope.User.Stockist -> R.drawable.ic_missing_stockists to R.string.missing_stockists
            is ManagementScope.User.Retailer -> R.drawable.ic_missing_retailers to R.string.missing_retailers
            is ManagementScope.User.Hospital -> R.drawable.ic_missing_hospitals to R.string.missing_hospitals
            is ManagementScope.User.SeasonBoy -> R.drawable.ic_missing_sb to R.string.missing_sb
        }
        NoRecords(
            icon = icon,
            text = text,
            subtitle = if (scope is ManagementScope.User.Stockist) stringResource(R.string.please_connect_stockists) else null,
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
            val isSeasonBoy = scope is ManagementScope.User.SeasonBoy
            itemsIndexed(
                items = items.value,
                itemContent = { index, item ->
                    if (isSeasonBoy) {
                        SeasonBoyItem(item) { scope.selectItem(item) }
                    } else {
                        NonSeasonBoyItem(item) { scope.selectItem(item) }
                    }
                    if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                        scope.loadItems()
                    }
                },
            )
        }
    }
    if(showAlert.value)
        ShowAlert(message = stringResource(id = R.string.update_successfull)) {
            scope.changeAlertVisibility(false)
        }
}

@Composable
private fun NonSeasonBoyItem(
    entityInfo: EntityInfo,
    onClick: () -> Unit,
) {
    BaseManagementItem(onClick) {
        Column(
            modifier = Modifier.widthIn(max = maxWidth),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                BoxWithConstraints {
                    Text(
                        text = entityInfo.tradeName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colors.background,
                        modifier = Modifier.widthIn(max = maxWidth - 22.dp)
                    )
                }
                if (entityInfo.isVerified == true) {
                    Space(4.dp)
                    Image(
                        painter = painterResource(id = R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Text(
                text = entityInfo.geoData.fullLocationCityAddress(),
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = ConstColors.gray,
            )
            Space(4.dp)
            Divider(thickness = 0.5.dp)
            Space(4.dp)
            /*Text(
                text = entityInfo.geoData.landmark,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = ConstColors.gray,
            )
            Space(4.dp)*/
            //GeoLocation(entityInfo.geoData.fullAddress())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(maxWidth / 2)) {
                        entityInfo.subscriptionData?.let {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_connected),
                                    contentDescription = null,
                                    tint = when (it.status) {
                                        SubscriptionStatus.SUBSCRIBED -> ConstColors.lightGreen
                                        SubscriptionStatus.PENDING -> ConstColors.lightBlue
                                        SubscriptionStatus.REJECTED -> ConstColors.red
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                                Space(dp = 4.dp)
                                Text(
                                    text = it.status.serverValue,
                                    fontSize = 12.sp,
                                    color = when (it.status) {
                                        SubscriptionStatus.SUBSCRIBED -> ConstColors.lightGreen
                                        SubscriptionStatus.PENDING -> ConstColors.lightBlue
                                        SubscriptionStatus.REJECTED -> ConstColors.red
                                    },
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        } ?: Space(4.dp)
                    }


                    Box(
                        modifier = Modifier
                            .width(maxWidth / 2)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = ConstColors.orange
                            )
                            Space(4.dp)
                            Text(
                                text = entityInfo.geoData.formattedDistance,
                                fontSize = 12.sp,
                                color = ConstColors.gray,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonBoyItem(entityInfo: EntityInfo, onClick: () -> Unit) {
    BaseManagementItem(onClick) {
        Column(
            modifier = Modifier
                .width(maxWidth * 0.6f)
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = entityInfo.tradeName,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.background,
            )
            Space(8.dp)
            GeoLocation(entityInfo.geoData.fullAddress())
        }
        entityInfo.subscriptionData?.let {
            Column(
                modifier = Modifier
                    .width(maxWidth * 0.4f)
                    .align(Alignment.CenterEnd),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = it.status.serverValue,
                    color = when (it.status) {
                        SubscriptionStatus.SUBSCRIBED -> ConstColors.green
                        SubscriptionStatus.PENDING -> ConstColors.lightBlue
                        SubscriptionStatus.REJECTED -> ConstColors.red
                    },
                    fontWeight = FontWeight.W500,
                    fontSize = 15.sp,
                )
                Space(8.dp)
                Text(
                    text = entityInfo.phoneNumber?.formatIndia().orEmpty(),
                    fontWeight = FontWeight.W600,
                    color = ConstColors.lightBlue,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseManagementItem(
    onClick: () -> Unit,
    body: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(2.dp, ConstColors.separator)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            body()
        }
    }
}

@Composable
fun GeoLocation(
    location: String,
    isBold: Boolean = false,
    textSize: TextUnit = 14.sp,
    tint: Color = ConstColors.gray,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(10.dp),
        )
        Space(4.dp)
        Text(
            text = location,
            fontSize = textSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = tint,
        )
    }
}

@Composable
fun GeoLocationSheet(
    location: String,
    isBold: Boolean = false,
    textSize: TextUnit = 14.sp,
    painter: Painter = painterResource(id = R.drawable.ic_bulding),
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = ConstColors.lightGreen
        )
        Space(4.dp)
        Text(
            text = location,
            fontSize = textSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colors.background,
        )
    }
}