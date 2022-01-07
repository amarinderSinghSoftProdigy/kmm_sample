package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreOrderPlacedScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun InStoreOrderPlacedScreen(scope: InStoreOrderPlacedScope) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = ConstColors.green.copy(alpha = .06f),
            border = BorderStroke(2.dp, ConstColors.green)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                )
                Space(16.dp)
                Text(
                    text = stringResource(id = R.string.instore_order_success, scope.tradeName),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
                Space(22.dp)
//                Row {
//                    Text(
//                        text = buildAnnotatedString {
//                            append(stringResource(id = R.string.date))
//                            append(": ")
//                            val startIndex = length
//                            append(scope.order.info.date)
//                            addStyle(
//                                SpanStyle(color = ConstColors.lightBlue),
//                                startIndex,
//                                length,
//                            )
//                        },
//                        color = ConstColors.gray,
//                        fontWeight = FontWeight.W500,
//                        fontSize = 12.sp,
//                    )
//                    Space(8.dp)
//                    Text(
//                        text = buildAnnotatedString {
//                            append(stringResource(id = R.string.time))
//                            append(": ")
//                            val startIndex = length
//                            append(scope.order.info.time)
//                            addStyle(
//                                SpanStyle(color = ConstColors.lightBlue),
//                                startIndex,
//                                length,
//                            )
//                        },
//                        color = ConstColors.gray,
//                        fontWeight = FontWeight.W500,
//                        fontSize = 12.sp,
//                    )
//                }
//                Space(22.dp)
                MedicoButton(
                    text = stringResource(id = R.string.instore_orders),
                    isEnabled = true,
                    color = ConstColors.lightBlue,
                    contentColor = White,
                    onClick = { scope.goToOrders() },
                )
            }
        }
    }
}