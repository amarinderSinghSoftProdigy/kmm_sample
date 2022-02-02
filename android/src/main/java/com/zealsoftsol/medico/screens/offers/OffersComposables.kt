package com.zealsoftsol.medico.screens.offers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.Manufacturer
import com.zealsoftsol.medico.data.PromotionStatusData
import com.zealsoftsol.medico.data.Promotions
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.NoRecordsWithoutHome
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.BasicSearchBar

@Composable
fun OffersScreen(scope: OffersScope) {
    val search = scope.productSearch.flow.collectAsState()
    val offers = scope.items.flow.collectAsState()
    val manufacturer = scope.manufacturer.flow.collectAsState()
    val statuses = scope.statuses.flow.collectAsState()
    Column {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(0.65f),
                shape = MaterialTheme.shapes.large,
                color = Color.Transparent,
                border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyColumn {
                        itemsIndexed(
                            items = statuses.value,
                            itemContent = { _, item ->
                                StatusItem(item)

                            },
                        )
                    }
                }
            }

            Space(dp = 16.dp)
            SectionButton(
                modifier = Modifier.weight(0.35f),
                icon = painterResource(id = R.drawable.ic_offer),
                text = stringResource(id = R.string.create_offer),
                isClickable = false,
                counter = 0,
                counterSupported = false
            ) {

            }
        }
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_by_product,
            icon = null,
            horizontalPadding = 16.dp,
            onIconClick = null,
            isSearchFocused = false,
            onSearch = { value, _ ->
                scope.startSearch(search = value)
            },
            isSearchCross = false
        )
        Space(dp = 16.dp)
        Divider(thickness = 0.5.dp)
        Space(dp = 8.dp)
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(id = R.string.manufacturers),
            color = ConstColors.lightBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
        )
        Space(dp = 8.dp)
        LazyRow(modifier = Modifier.padding(start = 16.dp)) {
            itemsIndexed(
                items = manufacturer.value,
                itemContent = { _, item ->
                    ManufacturerItem(item, scope)
                },
            )
        }
        Space(dp = 12.dp)
        Divider(thickness = 0.5.dp)
        Space(dp = 8.dp)
        if (offers.value.isEmpty()) {
            NoRecordsWithoutHome(
                icon = R.drawable.ic_missing_stores,
                text = R.string.missing_offers,
                subtitle = "",
                onHome = { scope.startSearch() },
            )
        } else {
            LazyColumn {
                itemsIndexed(
                    items = offers.value,
                    itemContent = { index, item ->
                        OfferItem(item, scope)
                        if (index == offers.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadMoreProducts()
                        }
                    },
                )
            }
        }
    }
}

/**
 * ui item for manufaturer listing
 */
@Composable
fun ManufacturerItem(item: Manufacturer, scope: OffersScope) {
    val manufacturer = scope.manufacturerSearch.flow.collectAsState()
    Card(
        modifier = Modifier
            .height(90.dp)
            .width(150.dp)
            .selectable(
                selected = true,
                onClick = {
                    val list = manufacturer.value
                    if (list.contains(item.code)) {
                        list.remove(item.code)
                    } else {
                        list.add(item.code)
                    }
                    scope.startSearch(query = list)
                }),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
        border = if (manufacturer.value.contains(item.code)) {
            BorderStroke(1.dp, ConstColors.yellow)
        } else BorderStroke(1.dp, Color.White)
    ) {
        CoilImageBrands(
            src = CdnUrlProvider.urlForM(item.code),
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
 * ui item for offer listing
 */
@Composable
fun OfferItem(item: Promotions, scope: OffersScope) {
    Column {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Row {
                Column {
                    Text(
                        text = item.productName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.manufacturerName,
                        fontSize = 12.sp,
                        color = ConstColors.gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (item.active) stringResource(id = R.string.running) else "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.Bold
                    )
                    Space(dp = 4.dp)
                    Switch(
                        checked = item.active, onCheckedChange = {
                            scope.showBottomSheet(item.promoCode, item.productName, it)
                        }, colors = SwitchDefaults.colors(
                            checkedThumbColor = ConstColors.green
                        )
                    )
                }
            }
            Space(dp = 4.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .selectable(
                            selected = true,
                            onClick = {
                                //scope.startBrandSearch(item.searchTerm, item.field)
                            }),
                    elevation = 3.dp,
                    shape = RoundedCornerShape(5.dp),
                    backgroundColor = ConstColors.red,
                ) {
                    Text(
                        text = item.offer,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                    )
                }
                Text(
                    text = "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.background
                )
            }
            Space(12.dp)

        }
        Divider()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SectionButton(
    modifier: Modifier,
    icon: Painter,
    text: String,
    isClickable: Boolean,
    counter: Int?,
    counterSupported: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = ConstColors.yellow,
        enabled = isClickable,
        onClick = onClick,
    ) {

        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(70.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colors.background
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

@Composable
fun StatusItem(item: PromotionStatusData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp)
            .background(
                when (item.status) {
                    "Ended" -> ConstColors.red
                    "Running" -> ConstColors.lightGreen
                    else -> ConstColors.ltgray
                },
                RoundedCornerShape(5.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.status,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier.padding(all = 4.dp),
        )
        Text(
            text = item.total.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier.padding(all = 4.dp),
        )
    }
}