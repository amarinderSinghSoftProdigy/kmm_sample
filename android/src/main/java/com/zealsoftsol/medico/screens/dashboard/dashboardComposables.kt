package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun DashboardScreen(scope: DashboardScope) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            val unreadNotifications = scope.unreadNotifications.flow.collectAsState()
            BigButton(
                icon = R.drawable.ic_bell,
                text = stringResource(id = R.string.notifications),
                counter = unreadNotifications.value,
                onClick = { scope.goToNotifications() },
            )
            Space(16.dp)
            BigButton(
                icon = R.drawable.ic_orders,
                text = stringResource(id = R.string.active_orders),
                counter = 0,
                onClick = {},
            )
        }
    }
}

@Composable
private fun RowScope.BigButton(
    icon: Int,
    text: String,
    counter: Int,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.weight(1f).clickable(onClick = onClick),
        color = Color.White,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp),
        ) {
            Box(modifier = Modifier.size(60.dp)) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).align(Alignment.Center),
                )
                if (counter > 0) {
                    RedCounter(
                        modifier = Modifier.align(Alignment.TopEnd),
                        count = counter,
                    )
                }
            }
            Text(
                text = text,
                color = ConstColors.gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
            )
        }
    }
}

@Composable
private fun RedCounter(
    modifier: Modifier,
    count: Int,
) {
    Box(
        modifier = modifier.background(Color.Red, CircleShape).size(30.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
        )
    }
}