package com.zealsoftsol.medico.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InventoryScope
import com.zealsoftsol.medico.utils.piechart.PieChart
import com.zealsoftsol.medico.utils.piechart.PieChartData
import com.zealsoftsol.medico.utils.piechart.renderer.SimpleSliceDrawer
import com.zealsoftsol.medico.utils.piechart.simpleChartAnimation

@Composable
fun InventoryMainComposable(scope: InventoryScope) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp)
        ) {
            item {
                StatusView(scope = scope, modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(8.dp), 100f)
            }
            item {
                AvailabilityAndExpiryView(scope = scope, modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(8.dp) ,70f)
            }
            item {
                AvailabilityAndExpiryView(scope = scope, modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(8.dp), 70f)
            }
        }
    }
}

/**
 * Status of online and offline
 */
@Composable
fun StatusView(scope: InventoryScope, modifier: Modifier, sliceThickness: Float) {
    Card(
        modifier = modifier,
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val (status, online, offline, chart) = createRefs()
            Text(
                text = stringResource(R.string.status),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.constrainAs(status) {
                    start.linkTo(parent.start, margin = 5.dp)
                    top.linkTo(parent.top, margin = 5.dp)
                }
            )
            CommonRoundedView(
                text = "${stringResource(R.string.online)}: 1000",
                modifier = Modifier.constrainAs(online) {
                    start.linkTo(parent.start, margin = 5.dp)
                    bottom.linkTo(offline.top, margin = 5.dp)
                },
                color = ConstColors.darkGreen
            )

            CommonRoundedView(
                text = "${stringResource(R.string.offline)}: 1000",
                modifier = Modifier.constrainAs(offline) {
                    start.linkTo(parent.start, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                },
                color = ConstColors.darkRed
            )

            MyChartParent(
                thickness = sliceThickness,
                listPieChartData = listOf(
                    PieChartData.Slice(10f, ConstColors.darkRed), PieChartData.Slice(
                        90f,
                        ConstColors.darkGreen
                    )
                ),
                modifier = Modifier.constrainAs(chart) {
                    width = Dimension.value(150.dp)
                    height = Dimension.value(150.dp)
                    end.linkTo(parent.end, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    top.linkTo(parent.top, margin = 10.dp)
                }
            )
        }
    }
}


/**
 * Status of online and offline
 */
@Composable
fun AvailabilityAndExpiryView(scope: InventoryScope, modifier: Modifier, sliceThickness: Float) {
    Card(
        modifier = modifier,
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val (status, online, offline, chart) = createRefs()
            Text(
                text = stringResource(R.string.status),
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.constrainAs(status) {
                    start.linkTo(parent.start, margin = 5.dp)
                    top.linkTo(parent.top, margin = 5.dp)
                }
            )
            CommonRoundedView(
                text = "${stringResource(R.string.online)}: 1000",
                modifier = Modifier.constrainAs(online) {
                    start.linkTo(parent.start, margin = 5.dp)
                    bottom.linkTo(offline.top, margin = 5.dp)
                },
                color = ConstColors.darkGreen
            )

            CommonRoundedView(
                text = "${stringResource(R.string.offline)}: 1000",
                modifier = Modifier.constrainAs(offline) {
                    start.linkTo(parent.start, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                },
                color = ConstColors.darkRed
            )

            MyChartParent(
                thickness = sliceThickness,
                listPieChartData = listOf(
                    PieChartData.Slice(10f, ConstColors.darkRed), PieChartData.Slice(
                        70f, ConstColors.darkGreen), PieChartData.Slice(
                        20f, ConstColors.orange)
                ),
                modifier = Modifier.constrainAs(chart) {
                    width = Dimension.value(150.dp)
                    height = Dimension.value(150.dp)
                    end.linkTo(parent.end, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    top.linkTo(parent.top, margin = 10.dp)
                }
            )
        }
    }
}

/**
 * draw chartview
 * @param modifier Modifiers to be applied to the chart view
 * @param thickness thickness of pie chart arcs (0-100f)
 * @param listPieChartData list of {@PieChartData.Slice} containing the values to be drawn
 */
@Composable
fun MyChartParent(modifier: Modifier, thickness: Float, listPieChartData: List<PieChartData.Slice>) {

    PieChart(
        pieChartData = PieChartData(
           listPieChartData
        ),
        modifier = modifier,
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer(sliceThickness = thickness)
    )
}

/**
 * common rounded textview
 */
@Composable
fun CommonRoundedView(
    text: String,
    modifier: Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(color)
            .padding(3.dp),
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
        )

    }
}

/**
 *  colored indicator textview
 */

@Composable
fun ColorIndicatorTextView(){

}
