package com.zealsoftsol.medico.screens.management

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.data.CompanyData
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun CompaniesScreen(scope: ManagementScope.CompaniesScope) {

    val companies = scope.companiesList.flow.collectAsState().value
    val totalResults = scope.totalResults

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp)
    ) {
        if (companies.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(start = 3.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = companies,
                    key = { index, _ -> index },
                    itemContent = { _, item ->
                        CompanyItem(item = item)
                    },
                )

                item {
                    if (companies.size < totalResults) {
                        MedicoButton(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(top = 5.dp, bottom = 5.dp)
                                .height(40.dp),
                            text = stringResource(id = R.string.more),
                            isEnabled = true,
                        ) {
                            scope.getCompanies(false)
                        }
                    }
                }
            }

        } else {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(id = R.string.no_companies),
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompanyItem(item: CompanyData) {
    val visibility = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(3.dp),
        color = White,
        elevation = 3.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
                    .clickable {
                        visibility.value = !visibility.value
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name, color = MaterialTheme.colors.background,
                    fontSize = 15.sp, fontWeight = FontWeight.W600
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.padding(end = 20.dp),
                        text = item.products.size.toString(),
                        color = MaterialTheme.colors.background,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        modifier = Modifier
                            .rotate(if (visibility.value) 270f else 90f),
                        contentDescription = null
                    )
                }
            }
            if (item.products.isNotEmpty()) {
                AnimatedVisibility(visible = visibility.value) {
                    Space(dp = 10.dp)
                    FlowRow(
                        mainAxisSize = SizeMode.Expand,
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                    ) {
                        item.products.let {
                            it.forEachIndexed { index, value ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = value.name,
                                        color = Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 14.sp
                                    )
                                    Space(dp = 5.dp)
                                    if (index != it.size - 1)
                                        Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}