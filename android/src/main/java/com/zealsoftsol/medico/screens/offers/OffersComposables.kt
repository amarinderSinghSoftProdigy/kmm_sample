package com.zealsoftsol.medico.screens.offers

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
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

@SuppressLint("RememberReturnType")
@Composable
fun OffersScreen(scope: OffersScope.ViewOffers) {
    val offers = scope.items.flow.collectAsState()
    val manufacturer = scope.manufacturer.flow.collectAsState()
    val statuses = scope.statuses.flow.collectAsState()
    val manufacturerList = scope.manufacturerSearch.flow.collectAsState()
    val switchEnabled = remember { mutableStateOf(false) }
    val showManufacturersList = scope.showManufacturers.flow.collectAsState()

    remember {
        scope.startSearch()
    }
    Column {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionButton(
                modifier = Modifier.weight(0.5f),
                icon = painterResource(id = R.drawable.ic_offer),
                text = stringResource(id = R.string.offer),
                color = ConstColors.lightBlue,
                tint = Color.White
            ) {
                switchEnabled.value = !switchEnabled.value
            }

            Space(dp = 16.dp)
            SectionButton(
                modifier = Modifier.weight(0.5f),
                icon = painterResource(id = R.drawable.ic_offer),
                text = stringResource(id = R.string.create_offer),
            ) {
                scope.openCreateOffer()
            }
        }

        if (switchEnabled.value) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
                shape = MaterialTheme.shapes.large,
                color = ConstColors.lightBlue.copy(alpha = 0.1f)
            ) {

                val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 20.dp

                FlowRow(
                    mainAxisSize = SizeMode.Expand,
                    mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                ) {
                    statuses.value.let {
                        it.forEachIndexed { _, item ->
                            StatusItem(item, modifier = Modifier.width(itemSize))
                        }
                    }
                }
            }
            Space(dp = 16.dp)
        }
        //dasdsad
      /*  BasicSearchBar(
            input = search.value,
            hint = R.string.search_by_product,
            icon = null,
            horizontalPadding = 12.dp,
            onIconClick = null,
            isSearchFocused = false,
            onSearch = { value, _ ->
                scope.startSearch(search = value)
            },
            isSearchCross = false
        )
        Space(dp = 16.dp)
        Divider(thickness = 0.5.dp)*/
        if(showManufacturersList.value) {
            Space(dp = 8.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(id = R.string.manufacturers),
                    color = ConstColors.lightBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                )
                if (manufacturerList.value.size > 0) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "( " + manufacturerList.value.size + " )",
                        color = ConstColors.lightBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
            }
            Space(dp = 8.dp)
            LazyRow(modifier = Modifier.padding(start = 16.dp)) {
                itemsIndexed(
                    items = manufacturer.value,
                    itemContent = { _, item ->
                        ManufacturerItem(item, scope, manufacturerList.value)
                    },
                )
            }
            Space(dp = 12.dp)
            Divider(thickness = 0.5.dp)
        }
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
fun ManufacturerItem(item: Manufacturer, scope: OffersScope.ViewOffers, list: ArrayList<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(130.dp)
            .padding(end = 12.dp)
    ) {
        Card(
            modifier = Modifier
                .height(80.dp)
                .width(130.dp)
                .selectable(
                    selected = true,
                    onClick = {
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
            border = if (list.contains(item.code)) {
                BorderStroke(1.dp, ConstColors.yellow)
            } else BorderStroke(1.dp, Color.White)
        ) {
            CoilImageBrands(
                src = CdnUrlProvider.urlForM(item.code),
                contentScale = ContentScale.FillBounds,
                onError = { ItemPlaceholder() },
                onLoading = { ItemPlaceholder() },
                height = 70.dp,
                width = 120.dp,
            )
        }
        Space(8.dp)
        Text(
            text = item.name,
            fontSize = 12.sp,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(start = 12.dp)
        )
        Space(12.dp)
    }
}

/**
 * ui item for offer listing
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OfferItem(item: Promotions, scope: OffersScope.ViewOffers) {
    Column {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Row {
                Column {
                    Text(
                        text = item.productName,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.manufacturerName,
                        fontSize = 14.sp,
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
                        fontSize = 14.sp,
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

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.width(maxWidth / 2),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Card(
                        modifier = Modifier
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
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(maxWidth / 2)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        onClick = { scope.showEditBottomSheet(item) },
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = null,
                                tint = ConstColors.lightBlue,
                            )
                            Space(dp = 4.dp)
                            Text(
                                text = stringResource(id = R.string.edit_Offer),
                                color = ConstColors.lightBlue,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
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
    color: Color = ConstColors.yellow,
    tint: Color = MaterialTheme.colors.background,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color,
        enabled = true,
        onClick = onClick,
    ) {

        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(70.dp),
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
                        tint = tint
                    )
                }
                Text(
                    text = text,
                    color = tint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                )
            }
        }
    }
}

@Composable
fun StatusItem(item: PromotionStatusData, modifier: Modifier) {
    Row {
        Surface(
            color = Color.Transparent,
            modifier = modifier
                .padding(8.dp),
            border = BorderStroke(1.dp, Color.Black),
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.status,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(all = 8.dp),
                )
                Text(
                    text = item.total.toString(),
                    color = when (item.status.uppercase()) {
                        "ENDED" -> ConstColors.red
                        "RUNNING" -> ConstColors.lightGreen
                        else -> MaterialTheme.colors.background
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(all = 4.dp),
                )
            }
        }
    }
}