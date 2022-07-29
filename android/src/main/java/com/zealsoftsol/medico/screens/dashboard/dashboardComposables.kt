package com.zealsoftsol.medico.screens.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.screens.common.ShimmerItem
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun DashboardScreen(scope: DashboardScope) {
        ShowStockistDashBoard(scope)
}



/**
 * rewards and cashback view
 */
@Composable
fun RewardsAndCashback(scope: DashboardScope) {
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
                text = stringResource(id = R.string.rewards_cashback),
                color = ConstColors.lightBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.clickable {
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .padding(end = 16.dp),
                )
            }
        }
        Space(dp = 16.dp)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = 5.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rewards_header),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    modifier = Modifier
                        .size(150.dp),
                    painter = painterResource(id = R.drawable.img_offer_bg),
                    contentDescription = null
                )

                Column(horizontalAlignment = CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.rewards_cashback),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W800
                    )
                    Space(8.dp)
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.cashback_details),
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
    }
}

/**
 * items for quick action
 */
@Composable
private fun QuickActionItem(title: String, icon: Int, count: Int = 0, onClick: () -> Unit) {
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
            if (count != 0)
                RedCounter(
                    modifier = Modifier
                        .align(TopEnd),
                    count = count,
                )
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
 * show user type specific to stockist only
 */
@Composable
private fun ShowStockistDashBoard(
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

                }
                Space(16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.retailers),
                    icon = R.drawable.ic_menu_retailers
                ) {

                }
                Space(16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.bank_details),
                    icon = R.drawable.ic_menu_invoice
                ) {

                }
                Space(16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.upi_account),
                    icon = R.drawable.ic_menu_invoice
                ) {

                }
                Space(16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.online_collections),
                    icon = R.drawable.ic_menu_invoice
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.hospitals),
                    icon = R.drawable.ic_menu_hospitals
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.stockists),
                    icon = R.drawable.ic_menu_stockist
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.stores),
                    icon = R.drawable.ic_menu_stores
                ) {

                }
                Space(16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.demo),
                    icon = R.drawable.ic_demo
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.inventory),
                    icon = R.drawable.ic_menu_inventory
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.deal_offer),
                    icon = R.drawable.ic_offer
                ) {

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.my_account),
                    icon = R.drawable.ic_personal
                ) {

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

                }
                Space(dp = 16.dp)
                QuickActionItem(
                    title = stringResource(id = R.string.delivery_qr_code),
                    icon = R.drawable.ic_qr_code
                ) {

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
                }
            Space(dp = 16.dp)

            LazyRow(
                contentPadding = PaddingValues(3.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

            }
        }

        Space(16.dp)

        RewardsAndCashback(scope)

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
                        ShimmerItem(padding = PaddingValues(end = 12.dp, top = 8.dp))
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
                        ShimmerItem(padding = PaddingValues(start = 12.dp, top = 8.dp))
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


    }
    Space(dp = 16.dp)
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
