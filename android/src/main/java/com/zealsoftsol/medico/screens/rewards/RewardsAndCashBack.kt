package com.zealsoftsol.medico.screens.rewards

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.RewardsScope
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun RewardsAndCashbackScreen(scope: RewardsScope) {
    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            contentPadding = PaddingValues(3.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* ssss.value?.let {
                 item {
                     Surface(
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(200.dp)
                     ) {
                         Image(
                             painter = painterResource(id = R.drawable.ic_rewards_header),
                             contentDescription = null,
                             modifier = Modifier
                                 .height(200.dp)
                                 .fillMaxWidth()
                         )

                         Row(
                             modifier = Modifier.fillMaxSize(),
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.SpaceEvenly
                         ) {
                             Image(
                                 modifier = Modifier.size(150.dp),
                                 painter = painterResource(id = R.drawable.img_offer_bg),
                                 contentDescription = null
                             )

                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                 Text(
                                     text = stringResource(id = R.string.rewards_cashback),
                                     color = Color.Black,
                                     fontSize = 14.sp,
                                     fontWeight = FontWeight.W800
                                 )
                                 Space(8.dp)
                                 Text(
                                     text = stringResource(id = R.string.cashback_details),
                                     color = Color.Black,
                                     fontSize = 14.sp,
                                     fontWeight = FontWeight.W500
                                 )
                             }
                         }
                     }
                 }
                 *//*  itemsIndexed(
                      items = it,
                      key = { index, _ -> index },
                      itemContent = { _, item ->
                          OfferItem(scope) {
                          }
                      },
                  )*//*
            }*/
        }

    }
}

@Composable
private fun OfferItem(scope: RewardsScope, onClick: () -> Unit) {

    Surface(
        modifier = Modifier
            .height(180.dp)
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
                    .height(130.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                color = ConstColors.lightGreen,
                elevation = 5.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    Surface(
                        modifier = Modifier.size(50.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(CenterHorizontally),
                            text = stringResource(id = R.string.cashback_details),
                            color = ConstColors.lightGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.cashback_details),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }
            Space(16.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "stockist 101",
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
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.order_no))
                        append(" ")
                        val startIndex = length
                        append("daksdjaksd")
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.W600),
                            startIndex,
                            length,
                        )
                    },
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                )
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
                            append("amount")
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W600),
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
                            append(" ")
                            val startIndex = length
                            append("date")
                            addStyle(
                                SpanStyle(fontWeight = FontWeight.W600),
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
                        append(" ")
                        val startIndex = length
                        append("date")
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