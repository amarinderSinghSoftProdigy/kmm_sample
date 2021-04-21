package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.SubscriptionStatus
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.rememberPhoneNumberFormatter
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun ManagementScreen(scope: ManagementScope.User) {
    Column(modifier = Modifier.fillMaxSize()) {
        EntityManagementScreen(scope)
    }
    if (scope is ManagementScope.User.Retailer && scope.canAdd) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = { scope.requestCreateRetailer() },
                backgroundColor = ConstColors.yellow,
                contentColor = MaterialTheme.colors.background,
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
            )
        }
    }
}

// TODO reuse with stores
@Composable
private fun EntityManagementScreen(scope: ManagementScope.User) {
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
            val (icon, text) = when (scope) {
                is ManagementScope.User.Stockist -> R.drawable.ic_stockist to R.string.stockists
                is ManagementScope.User.Retailer -> R.drawable.ic_retailer to R.string.retailers
                is ManagementScope.User.SeasonBoy -> R.drawable.ic_season_boy to R.string.season_boys
                is ManagementScope.User.Hospital -> R.drawable.ic_hospital to R.string.hospitals
            }
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
    if (scope.tabs.isNotEmpty()) {
        Space(16.dp)
        Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            scope.tabs.forEach {
                var boxMod = Modifier.weight(1f).fillMaxHeight()
                boxMod = if (scope.tabs.size == 1) {
                    boxMod.padding(horizontal = 16.dp)
                } else {
                    boxMod.clickable { scope.selectTab(it) }
                }
                Box(modifier = boxMod) {
                    val isActive = activeTab.value == it
                    Text(
                        text = stringResourceByName(it.stringId),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = if (isActive) MaterialTheme.colors.background else ConstColors.gray,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    if (isActive) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(2.dp)
                                .background(color = MaterialTheme.colors.background)
                                .align(Alignment.BottomCenter),
                        )
                    }
                }
            }
        }
    }
    val items = scope.items.flow.collectAsState()
    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(top = 16.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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

@Composable
private fun NonSeasonBoyItem(
    entityInfo: EntityInfo,
    onClick: () -> Unit,
) {
    BaseManagementRowItem(onClick) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(0.65f),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = entityInfo.tradeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(end = if (entityInfo.isVerified == true) 18.dp + 8.dp else 0.dp)
                )
                if (entityInfo.isVerified == true) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).align(Alignment.TopEnd),
                    )
                }
            }
            GeoLocation(entityInfo.geoData.fullAddress())
        }
        Column(
            modifier = Modifier.fillMaxHeight().weight(0.35f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            entityInfo.subscriptionData?.let {
                Text(
                    text = it.status.serverValue,
                    color = if (it.status == SubscriptionStatus.SUBSCRIBED) ConstColors.lightBlue else ConstColors.yellow,
                    fontWeight = FontWeight.W500,
                )
            }
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

@Composable
private fun SeasonBoyItem(entityInfo: EntityInfo, onClick: () -> Unit) {
    BaseManagementRowItem(onClick) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(0.65f),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = entityInfo.tradeName,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.background,
            )
            GeoLocation(entityInfo.geoData.fullAddress())
        }
        entityInfo.subscriptionData?.let {
            Column(
                modifier = Modifier.fillMaxHeight().weight(0.35f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = it.status.serverValue,
                    color = if (it.status == SubscriptionStatus.SUBSCRIBED) ConstColors.lightBlue else ConstColors.yellow,
                    fontWeight = FontWeight.W500,
                )
                val formatter = rememberPhoneNumberFormatter()
                Text(
                    text = entityInfo.phoneNumber?.let { formatter.verifyNumber(it) ?: it }
                        .orEmpty(),
                    fontWeight = FontWeight.W600,
                    color = ConstColors.lightBlue,
                )
            }
        }
    }
}

@Composable
private fun BaseManagementRowItem(
    onClick: () -> Unit,
    body: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
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