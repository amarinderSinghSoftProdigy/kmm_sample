package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreOrderPlacedScope
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun InStoreOrderPlacedScreen(scope: InStoreOrderPlacedScope) {

    val order = scope.order

    val isOfferSwiped = scope.isOfferSwiped.flow.collectAsState()
    val showOfferAlert = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            OrderPlacedTile(scope.tradeName)
            if (order.isRewardsRequired) { //show rewards when it is true from API
                Space(10.dp)
                OffersView(scope, isOfferSwiped)
            }
            Space(20.dp)
        }

        Column {
            OrderTotal(order.total.formattedPrice)
            Space(dp = 16.dp)
            MedicoButton(
                text = stringResource(id = R.string.instore_orders),
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
private fun OrderPlacedTile(
    seller: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
        Text(
            text = seller,
            color = ConstColors.lightGreen,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun OffersView(scope: InStoreOrderPlacedScope, isOfferSwiped: State<Boolean>) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 10.dp),
        shape = MaterialTheme.shapes.medium,
        color = White,
        elevation = 5.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                color = White,
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
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
                            color = White,
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