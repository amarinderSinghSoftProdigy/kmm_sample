package com.zealsoftsol.medico.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.RewardsScope
import com.zealsoftsol.medico.data.RewardItem
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable

@Composable
fun RewardsAndCashbackScreen(scope: RewardsScope) {

    val rewardsList = scope.rewardsList.flow.collectAsState()
    val totalResults = scope.totalItems
    val showNoCashback = scope.showNoCashback.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = 3.dp
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_rewards_header),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            )
            Row {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                        .clickable(
                            indication = null,
                            onClick = {
                                scope.goBack()
                            }
                        )
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

        Space(20.dp)

        LazyColumn(
            contentPadding = PaddingValues(3.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rewardsList.value.let {
                itemsIndexed(
                    items = it,
                    key = { index, _ -> index },
                    itemContent = { _, item ->
                        OfferItem(item, scope) {
                        }
                    },
                )
                item {
                    if (rewardsList.value.size < totalResults && !showNoCashback.value) {
                        MedicoButton(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(top = 5.dp, bottom = 5.dp)
                                .height(40.dp),
                            text = stringResource(id = R.string.more),
                            isEnabled = true,
                        ) {
                            scope.getRewards(false)
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun OfferItem(item: RewardItem, scope: RewardsScope, onClick: () -> Unit) {

    Surface(
        modifier = Modifier
            .height(140.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(3.dp),
        color = ConstColors.cream,
        elevation = 5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp),
                shape = RoundedCornerShape(3.dp),
                color = ConstColors.lightGreen,
                elevation = 5.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    Surface(
                        modifier = Modifier
                            .size(70.dp)
                            .align(CenterHorizontally),
                        color = Color.White,
                        shape = RoundedCornerShape(70.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = item.rewardAmount.formatted,
                                color = ConstColors.lightGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.cashback_earned),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }
            Space(16.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                /* Text(
                     text = item.orderId,
                     color = Color.Black,
                     fontSize = 14.sp,
                     fontWeight = FontWeight.W700
                 )
                 Text(
                     text = buildAnnotatedString {
                         append(stringResource(id = R.string.location))
                         append(" ")
                         val startIndex = length
                         append("location")
                         addStyle(
                             SpanStyle(fontWeight = FontWeight.W600),
                             startIndex,
                             length,
                         )
                     },
                     color = Color.Black,
                     fontWeight = FontWeight.W600,
                     fontSize = 14.sp,
                 )*/
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.order_no))
                        append(": ")
                        val startIndex = length
                        append(item.orderId)
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.W500),
                            startIndex,
                            length,
                        )
                    },
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                )
                Space(dp = 20.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.amount))
                            append(" ")
                            val startIndex = length
                            append(item.amount.formatted)
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W500),
                                startIndex,
                                length,
                            )
                        },
                        color = Color.Black,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.date))
                            append(": ")
                            val startIndex = length
                            append(item.date)
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W500),
                                startIndex,
                                length,
                            )
                        },
                        color = Color.Black,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .align(BottomEnd),
                shape = RoundedCornerShape(3.dp),
                color = Color.White,
                elevation = 5.dp
            ) {

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.expires_on))
                        append(": ")
                        val startIndex = length
                        append(item.expiresIn)
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.W600),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.red,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                )
            }
        }
    }

}