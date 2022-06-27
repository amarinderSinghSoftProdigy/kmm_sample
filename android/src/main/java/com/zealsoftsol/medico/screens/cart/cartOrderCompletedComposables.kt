package com.zealsoftsol.medico.screens.cart

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.CartOrderCompletedScope
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.SellerOrder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space


@SuppressLint("RememberReturnType")
@Composable
fun CartOrderCompletedScreen(scope: CartOrderCompletedScope) {

    val isOfferSwiped = scope.isOfferSwiped.flow.collectAsState()
    val showOfferAlert = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    remember {
        vibrator.vibrate(200)
        mediaPlayer.start()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            OrderPlacedTile(scope.order)
            if (scope.order.isRewardsRequired) { //show rewards when it is true from API
                Space(10.dp)
                OffersView(scope, isOfferSwiped)
            }
            Space(20.dp)
            scope.order.sellersOrder.forEach {
                OrderItem(it)
                Space(12.dp)
            }
        }

        Column {
            OrderTotal(scope.total.formattedPrice)
            Space(dp = 16.dp)
            MedicoButton(
                text = stringResource(id = R.string.orders),
                isEnabled = true,
                onClick = {
                    if (isOfferSwiped.value)
                        scope.goToOrders()
                    else
                        showOfferAlert.value = true
                },
            )
            Space(16.dp)
        }
    }

    if (showOfferAlert.value)
        ShowAlert(
            onClick = { showOfferAlert.value = false },
            message = stringResource(id = R.string.offer_claim_warning)
        )
}

@Composable
private fun OffersView(scope: CartOrderCompletedScope, isOfferSwiped: State<Boolean>) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 10.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        elevation = 5.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.hurray),
                    color = ConstColors.lightGreen,
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp,
                )
                Space(5.dp)
                Text(
                    text = stringResource(id = R.string.won_scratch),
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                color = Color.White,
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .align(CenterHorizontally),
                    painter = if (isOfferSwiped.value) painterResource(id = R.drawable.ic_offer_opened) else painterResource(
                        id = R.drawable.ic_unopeded_scratch_card
                    ),
                    contentDescription = null
                )
                if (isOfferSwiped.value) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = scope.order.rewards.amount.formatted,
                            color = Color.White,
                            fontWeight = FontWeight.W800,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            if (isOfferSwiped.value) { //show rewards won when offer is swiped
                Space(dp = 5.dp)
                Text(
                    text = scope.order.rewards.message,
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                )
            }
            Space(10.dp)
            if (isOfferSwiped.value) {
                Image(
                    painter = painterResource(id = R.drawable.ic_swiped),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_unswiped),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                val (x, _) = dragAmount
                                when {
                                    x > 0 -> {
                                        if (x > 30) { //swipe direction is right, enable offer
                                            scope.submitReward()
                                        }
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun OrderPlacedTile(
    order: CartSubmitResponse,
) {
    /*Surface(
        shape = MaterialTheme.shapes.medium,
        color = ConstColors.green.copy(alpha = .06f),
        border = BorderStroke(2.dp, ConstColors.green)
    ) {*/
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_order_placed),
            contentDescription = null,
        )
        Text(
            text = stringResource(id = R.string.order_success_old),
            color = ConstColors.lightGreen,
            fontWeight = FontWeight.W800,
            fontSize = 16.sp,
        )
        Space(4.dp)
        Text(
            text = stringResource(id = R.string.order_email_confirmation),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
        Space(4.dp)
        val tradeNames = order.sellersOrder.map { it.tradeName }.toList().joinToString(",")
        Text(
            text = tradeNames,
            color = ConstColors.lightGreen,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun OrderItem(seller: SellerOrder) {
    Surface(
        shape = RoundedCornerShape(25.dp),
        color = ConstColors.lightBackground,
        border = BorderStroke(1.dp, ConstColors.lightBackground),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 12.dp, horizontal = 16.dp),
        ) {

            Text(
                text = seller.tradeName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            /* seller.seasonBoyRetailerName?.let {
                 Space(2.dp)
                 Text(
                     text = it,
                     color = ConstColors.gray,
                     fontWeight = FontWeight.W500,
                     fontSize = 11.sp,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                 )
             }*/
            Space(2.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.payment_method))
                        append(": ")
                        val startIndex = length
                        append(seller.paymentMethod.serverValue)
                        addStyle(
                            SpanStyle(color = ConstColors.lightGreen),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                /*Space(6.dp)
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                        .align(Alignment.CenterVertically)
                )
                Space(6.dp)
                Text(
                    text = seller.orderId,
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )*/
                Row {
                    Text(
                        text = stringResource(id = R.string.total),
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(2.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(seller.total.formattedPrice)
                            val startIndex = length
                            append("*")
                            addStyle(
                                SpanStyle(color = ConstColors.lightBlue),
                                startIndex,
                                length,
                            )
                        },
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }


            }
            /* Column(
                 modifier = Modifier.weight(.3f),
                 horizontalAlignment = Alignment.End,
             ) {
                 Text(
                     text = stringResource(id = R.string.total),
                     color = ConstColors.gray,
                     fontWeight = FontWeight.W500,
                     fontSize = 12.sp,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                 )
                 Space(2.dp)
                 Text(
                     text = buildAnnotatedString {
                         append(seller.total.formattedPrice)
                         val startIndex = length
                         append("*")
                         addStyle(
                             SpanStyle(color = ConstColors.lightBlue),
                             startIndex,
                             length,
                         )
                     },
                     color = MaterialTheme.colors.background,
                     fontWeight = FontWeight.W700,
                     fontSize = 16.sp,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                 )
             }*/
        }
    }
}

@Composable
fun OrderTotal(price: String) {
    Column {
        //Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.order_total),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 22.sp,
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = buildAnnotatedString {
                        val startIndex = length
                        append(price)
                        val nextIndex = length
                        addStyle(
                            SpanStyle(
                                color = ConstColors.lightGreen,
                                fontWeight = FontWeight.W700,
                                fontSize = 22.sp
                            ),
                            startIndex,
                            length,
                        )
                        append("*")
                        addStyle(
                            SpanStyle(
                                color = ConstColors.lightBlue,
                                fontWeight = FontWeight.W500
                            ),
                            nextIndex,
                            length,
                        )
                    },
                )
                Space(2.dp)
                Text(
                    text = stringResource(id = R.string.tax_exclusive),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp,
                )
            }
        }
        //Divider()
    }
}