package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.BannerData
import com.zealsoftsol.medico.data.BrandsData
import com.zealsoftsol.medico.data.DashboardData
import com.zealsoftsol.medico.data.DealsData
import com.zealsoftsol.medico.data.OfferStatus
import com.zealsoftsol.medico.data.ProductSold
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.ShimmerItem
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.inventory.ManufacturersItem
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(scope: DashboardScope) {
    val dashboard = scope.dashboard.flow.collectAsState()
    if (scope.userType == UserType.STOCKIST) {
        ShowStockistDashBoard(dashboard, scope)
    } else if (scope.userType == UserType.RETAILER || scope.userType == UserType.HOSPITAL) {
        ShowRetailerAndHospitalDashboard(dashboard, scope)
    }
}

/**
 * show dashboard specific to retailer and hospitals
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowRetailerAndHospitalDashboard(
    dashboard: State<DashboardData?>,
    scope: DashboardScope
) {
    val lazyListState = rememberLazyListState()
    val activity = LocalContext.current as MainActivity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ConstColors.newDesignGray)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                dashboard.value?.banners?.let {
                    LazyRow(state = lazyListState) {
                        itemsIndexed(
                            items = it,
                            key = { pos, _ -> pos },
                            itemContent = { _, item ->
                                BannerItem(
                                    item, scope, modifier = Modifier
                                        .fillParentMaxWidth()
                                        .height(150.dp)
                                        .padding(horizontal = 16.dp)
                                )
                            },
                        )
                    }

                    val newPosition = remember { mutableStateOf(0) }
                    //auto rotate banner after every 3 seconds
                    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
                        delay(3000) // wait for 3 seconds.
                        // increasing the position and check the limit
                        newPosition.value = lazyListState.firstVisibleItemIndex + 1
                        if (newPosition.value > it.size - 1) newPosition.value = 0
                        // scrolling to the new position.
                        if (newPosition.value == 0) {
                            lazyListState.scrollToItem(newPosition.value)
                        } else {
                            lazyListState.animateScrollToItem(newPosition.value)
                        }
                    }
                }
            }

            Space(dp = 16.dp)
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                val shareText = stringResource(id = R.string.share_content)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    QuickActionItem(
                        title = stringResource(id = R.string.stockist),
                        icon = R.drawable.ic_menu_stockist
                    ) {
                        scope.selectSection(scope.sections[1])
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.orders),
                        icon = R.drawable.ic_menu_orders
                    ) {
                        scope.goToOrders()
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.stores),
                        icon = R.drawable.ic_menu_stores
                    ) {
                        scope.sendEvent(Event.Transition.Stores)
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.digital_invoice_payments),
                        icon = R.drawable.ic_menu_invoice
                    ) {
                        scope.sendEvent(Event.Transition.IOCBuyer)
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.my_account),
                        icon = R.drawable.ic_personal
                    ) {
                        scope.sendEvent(Event.Transition.Settings(true))
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.share_medico),
                        icon = R.drawable.ic_share
                    ) {
                        activity.shareTextContent(shareText)
                    }
                }
            }

            Space(dp = 16.dp)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.our_brands),
                        color = ConstColors.lightBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    //todo uncomment for view more on brands
                 /*   Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.clickable {
                            scope.sendEvent(Event.Transition.Manufacturers)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = null,
                            tint = ConstColors.lightBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.view_all),
                            color = ConstColors.lightBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .padding(end = 16.dp),
                        )
                    }*/
                }
                Space(dp = 16.dp)

                LazyRow(
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    dashboard.value?.brands?.let {
                        itemsIndexed(
                            items = it,
                            key = { index, _ -> index },
                            itemContent = { _, item ->
                                BrandsItem(item, scope) {
                                    scope.startBrandSearch(item.searchTerm, item.field)
                                }
                            },
                        )
                    }
                }
            }
            Space(dp = 16.dp)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.deals_of_the_day),
                        color = ConstColors.lightBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.clickable {
                            scope.sendEvent(Event.Transition.Deals)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = null,
                            tint = ConstColors.lightBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.view_all),
                            color = ConstColors.lightBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .padding(end = 16.dp),
                        )
                    }
                }

                Space(dp = 16.dp)
                val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 8.dp

                FlowRow(
                    mainAxisSize = SizeMode.Expand,
                    mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                ) {
                    dashboard.value?.dealsOfDay?.let {
                        it.forEachIndexed { index, _ ->
                            DealsItem(
                                it[index],
                                scope,
                                modifier = Modifier.width(itemSize),
                                itemSize
                            )
                        }
                    }
                }
            }
            Space(dp = 16.dp)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
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
}

/**
 * items for quick action
 */
@Composable
private fun QuickActionItem(title: String, icon: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = CenterHorizontally) {
        Box(modifier = Modifier.clickable { onClick() }) {
            Surface(
                modifier = Modifier
                    .size(60.dp),
                color = Color.White,
                shape = CircleShape,
                elevation = 5.dp
            ) {
            }
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .align(Center),
                painter = painterResource(id = icon),
                contentDescription = null,
            )
        }
        Space(dp = 5.dp)
        Text(
            modifier = Modifier.width(80.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = title,
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center
        )
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
                    scope.sendEvent(Event.Transition.Banners)
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
            height = 150.dp,
        )
    }
    Space(12.dp)
}

/**
 * ui item for brands listing
 */
@Composable
private fun BrandsItem(item: BrandsData, scope: DashboardScope, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .width(150.dp)
            .padding(start = 2.dp)
            .selectable(
                selected = true,
                onClick = onClick
            ),
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
                            .align(TopEnd)
                            .padding(all = 4.dp),
                        count = item.count,
                    )
                }
            }
        }
        Space(8.dp)
        if (!item.isSkeletonItem) {
            Text(
                modifier = Modifier.align(CenterHorizontally),
                text = item.productName,
                color = MaterialTheme.colors.background,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        } else {
            ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
        }
    }
}

/**
 * ui item for deals of the day listing
 */
@Composable
private fun DealsItem(item: DealsData, scope: DashboardScope, modifier: Modifier, width: Dp) {
    Card(
        modifier = modifier
            .selectable(
                selected = true,
                onClick = {

                })
            .padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        Box {
            Column(horizontalAlignment = CenterHorizontally) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(
                        item.productInfo.imageCode,
                        CdnUrlProvider.Size.Px123
                    ),
                    size = 150.dp,
                    onError = {
                        ItemPlaceholder()
                    },
                    onLoading = {
                        ItemPlaceholder()
                    },
                )
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                ) {
                    Space(5.dp)
                    Surface(
                        modifier = Modifier.padding(1.dp),
                        shape = RoundedCornerShape(1.dp),
                        color = ConstColors.red
                    ) {
                        Text(
                            text = stringResource(id = R.string.deals_of_the_day),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontWeight = FontWeight.W600,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }

                    Space(5.dp)
                    Text(
                        text = item.productInfo.name,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.W600,
                        fontSize = 13.sp,
                    )
                    Space(5.dp)

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.ptr))
                            append(": ")
                            val startIndex = length
                            append(item.productInfo.ptr.formatted)
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W600, color = Color.Black),
                                startIndex,
                                length,
                            )
                        },
                        textAlign = TextAlign.Center,
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W600,
                        fontSize = 13.sp,
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.mrp))
                            append(": ")
                            val startIndex = length
                            append(item.productInfo.mrp.formatted)
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W600, color = Color.Black),
                                startIndex,
                                length,
                            )
                        },
                        textAlign = TextAlign.Center,
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W600,
                        fontSize = 13.sp,
                    )

                }
            }
            Surface(
                modifier = Modifier.align(TopEnd),
                shape = RoundedCornerShape(2.dp),
                color = ConstColors.red
            ) {
                Text(
                    text = "${item.promotionInfo.offer} ${stringResource(id = R.string.offer)}",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(3.dp)
                )
            }

        }
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
        Column(horizontalAlignment = CenterHorizontally) {
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
    dashboard: State<DashboardData?>,
    scope: DashboardScope
) {
    val activity = LocalContext.current as MainActivity
    val shareText = stringResource(id = R.string.share_content)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ConstColors.newDesignGray)
            .verticalScroll(rememberScrollState()),
    ) {

        dashboard.value.let { dash ->
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 2.dp)
                ) {
                    QuickActionItem(
                        title = stringResource(id = R.string.orders),
                        icon = R.drawable.ic_menu_orders
                    ) {
                        scope.goToOrders()
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.retailers),
                        icon = R.drawable.ic_menu_retailers
                    ) {
                        scope.sendEvent(Event.Transition.Management(UserType.RETAILER))
                    }
                    Space(16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.online_collections),
                        icon = R.drawable.ic_menu_invoice
                    ) {
                        scope.sendEvent(Event.Transition.IOCSeller)
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.hospitals),
                        icon = R.drawable.ic_menu_hospitals
                    ) {
                        scope.sendEvent(Event.Transition.Management(UserType.HOSPITAL))
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.stockists),
                        icon = R.drawable.ic_menu_stockist
                    ) {
                        scope.sendEvent(Event.Transition.Management(UserType.STOCKIST))
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.stores),
                        icon = R.drawable.ic_menu_stores
                    ) {
                        scope.sendEvent(Event.Transition.Stores)
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.inventory),
                        icon = R.drawable.ic_menu_inventory
                    ) {
                        scope.sendEvent(Event.Transition.Inventory(InventoryScope.InventoryType.ALL))
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.deal_offer),
                        icon = R.drawable.ic_offer
                    ) {
                        scope.sendEvent(Event.Transition.Offers(OfferStatus.ALL))
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.my_account),
                        icon = R.drawable.ic_personal
                    ) {
                        scope.sendEvent(Event.Transition.Settings(true))
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.share_medico),
                        icon = R.drawable.ic_share
                    ) {
                        activity.shareTextContent(shareText)
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.employees),
                        icon = R.drawable.ic_customer_care_acc
                    ) {
                        scope.sendEvent(Event.Transition.AddEmployee)
                    }
                    Space(dp = 16.dp)
                    QuickActionItem(
                        title = stringResource(id = R.string.delivery_qr_code),
                        icon = R.drawable.ic_qr_code
                    ) {
                        scope.sendEvent(Event.Transition.QrCode)
                    }
                }
            }
            Space(dp = 16.dp)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.manufacturers),
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    //todo uncomment for view more on manufactrers

                    /*  Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.End,
                          modifier = Modifier.clickable {
                              scope.sendEvent(Event.Transition.Manufacturers)
                          }
                      ) {
                          Icon(
                              painter = painterResource(id = R.drawable.ic_eye),
                              contentDescription = null,
                              tint = ConstColors.lightBlue,
                              modifier = Modifier.size(20.dp)
                          )
                          Text(
                              text = stringResource(id = R.string.view_all),
                              color = ConstColors.lightBlue,
                              fontSize = 16.sp,
                              fontWeight = FontWeight.W600,
                              modifier = Modifier
                                  .padding(horizontal = 3.dp)
                                  .padding(end = 16.dp),
                          )
                      }*/
                }
                Space(dp = 16.dp)

                LazyRow(
                    contentPadding = PaddingValues(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    dashboard.value?.manufacturers?.let {
                        itemsIndexed(
                            items = it,
                            key = { index, _ -> index },
                            itemContent = { _, item ->
                                ManufacturersItem(item) {
                                    scope.moveToInventoryScreen(manufacturerCode = item.code)
                                }
                            },
                        )
                    }
                }
            }
            Space(16.dp)

            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                            .clickable {
                                scope.moveToInventoryScreen(InventoryScope.InventoryType.IN_STOCK)
                            }
                            .background(Color.White/*ConstColors.green.copy(alpha = .2f)*/, shape1)
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape1)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = CenterHorizontally,
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
                            .clickable {
                                scope.moveToInventoryScreen(InventoryScope.InventoryType.OUT_OF_STOCK)
                            }
                            .background(Color.White/*ConstColors.red.copy(alpha = .2f)*/, shape2)
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape2)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = CenterHorizontally,
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
            }
            Space(16.dp)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                                scope.moveToOffersScreen(OfferStatus.RUNNING)
                            }
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape1)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = CenterHorizontally,
                    ) {

                        Row {
                            Icon(
                                contentDescription = null,
                                tint = ConstColors.lightGreen,
                                painter = painterResource(id = R.drawable.ic_offer)
                            )
                            Space(dp = 8.dp)
                            dash?.offers?.let { it ->
                                val total: String =
                                    it.find { data -> data.status == OfferStatus.RUNNING }?.total.toString()
                                Text(
                                    text = if (total == "null") "0" else total,
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
                                scope.moveToOffersScreen(OfferStatus.ENDED)
                            }
                            .border(1.dp, ConstColors.gray.copy(alpha = .1f), shape2)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = CenterHorizontally,
                    ) {
                        Row {
                            Icon(
                                contentDescription = null,
                                tint = ConstColors.orange,
                                painter = painterResource(id = R.drawable.ic_offer)
                            )
                            Space(dp = 8.dp)
                            dash?.offers?.let {
                                val total: String =
                                    it.find { data -> data.status == OfferStatus.ENDED }?.total.toString()
                                Text(
                                    text = if (total == "null") "0" else total,
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
            }

            Space(16.dp)

            if (dash?.productInfo != null && dash.productInfo?.mostSold?.isNotEmpty()!!) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.today_sold),
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Space(8.dp)

                    dash.productInfo?.mostSold?.let {
                        LazyRow(
                            contentPadding = PaddingValues(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(
                                items = it,
                                itemContent = { _, item ->
                                    BrandsImageItem(item, scope)
                                },
                            )
                        }
                    } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 12.dp))
                }
            }

            Space(8.dp)

            if (dash?.productInfo != null && dash.productInfo?.mostSearched?.isNotEmpty()!!) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.most_searched),
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Space(8.dp)

                    dash.productInfo?.mostSearched?.let {
                        LazyRow(
                            contentPadding = PaddingValues(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(
                                items = it,
                                itemContent = { _, item ->
                                    BrandsImageItem(item, scope)
                                },
                            )
                        }
                    } ?: ShimmerItem(padding = PaddingValues(end = 12.dp, top = 12.dp))
                }
            }
        }
        Space(dp = 16.dp)
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