package com.zealsoftsol.medico.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InventoryScope

@Composable
fun InventoryMainComposable(scope: InventoryScope) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        Card(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            elevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color.White,
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                val (status, online, offline, phoneLogo, locationLogo) = createRefs()
                Text(
                    text = stringResource(R.string.status),
                    color = Color.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.constrainAs(status) {
                        width = Dimension.preferredWrapContent
                        start.linkTo(parent.start, margin = 5.dp)
                        top.linkTo(parent.top, margin = 5.dp)
                    }
                )
                CommonRoundedView(
                    text = "${stringResource(R.string.online)}: 1000",
                    modifier = Modifier.constrainAs(online) {
                        width = Dimension.preferredWrapContent
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(offline.top, margin = 5.dp)
                    },
                    color = ConstColors.darkGreen
                )

                CommonRoundedView(
                    text = "${stringResource(R.string.offline)}: 1000",
                    modifier = Modifier.constrainAs(offline) {
                        width = Dimension.preferredWrapContent
                        start.linkTo(parent.start, margin = 5.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    },
                    color = ConstColors.darkRed
                )
            }
        }
    }
}

@Composable
fun CommonRoundedView(
    text: String,
    modifier: Modifier,
    color: Color
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(5.dp)).background(color).padding(3.dp),
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
        )

    }
}