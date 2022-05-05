package com.zealsoftsol.medico.screens.manufacturers

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.ManufacturerScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.ManufacturerItem
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.inventory.CommonRoundedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ManufacturerScreen(scope: ManufacturerScope) {
    val totalResults = scope.totalItems
    val manufacturerList = scope.manufacturers.flow.collectAsState().value
    val searchTerm = remember { mutableStateOf("") }
    var queryTextChangedJob: Job? = null

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ConstColors.newDesignGray)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable(
                            indication = null,
                            onClick = {
                                scope.goBack()
                            }
                        )
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(start = 10.dp)
                        .padding(end = 45.dp)
                        .align(CenterVertically),
                    shape = RoundedCornerShape(3.dp),
                    elevation = 3.dp,
                    color = White
                ) {
                    Row(
                        modifier = Modifier
                            .height(45.dp)
                            .fillMaxWidth(), verticalAlignment = CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            value = searchTerm.value,
                            maxLines = 1,
                            singleLine = true,
                            onValueChange = {
                                searchTerm.value = it

                                queryTextChangedJob?.cancel()

                                queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                                    delay(500)
                                    scope.startSearch(it)
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 14.sp,
                                background = White,
                            ),
                            decorationBox = { innerTextField ->
                                Row(modifier = Modifier) {
                                    if (searchTerm.value.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.search),
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                        )
                                    }
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }
            Divider(
                color = ConstColors.lightBlue,
                thickness = 0.5.dp,
                startIndent = 0.dp
            )
            Space(16.dp)

            val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 3) - 15.dp

            FlowRow(
                mainAxisSize = SizeMode.Expand,
                mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
            ) {
                manufacturerList.let {
                    it.forEachIndexed { _, item ->
                        ManufacturerListItem(item, itemSize) {
                            scope.startBrandSearch(item.name)
                        }
                    }
                }

                if (manufacturerList.size < totalResults) {
                    MedicoButton(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 5.dp, bottom = 5.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.more),
                        isEnabled = true,
                    ) {
                        scope.getManufacturers(search = searchTerm.value)
                    }
                }
            }
        }
    }
}

/**
 * ui item for manufacturer listing
 */
@Composable
fun ManufacturerListItem(
    item: ManufacturerItem,
    itemSize: Dp,
    onClick: () -> Unit
) {
    Column {
        Card(
            modifier = Modifier
                .height(90.dp)
                .width(itemSize)
                .selectable(
                    selected = true,
                    onClick = onClick
                ),
            elevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            backgroundColor = White,
        ) {
            Box {
                CoilImageBrands(
                    src = CdnUrlProvider.urlForM(item.id),
                    contentScale = ContentScale.Crop,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 90.dp,
                    width = itemSize
                )
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .align(Alignment.TopEnd)
                ) {
                    CommonRoundedView(
                        text = item.totalVariantProducts.toString(), modifier = Modifier
                            .align(Alignment.TopEnd), color = ConstColors.darkGreen, radius = 2
                    )
                }
            }
        }
        Space(5.dp)
        Text(
            modifier = Modifier
                .width(itemSize),
            text = item.name,
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Space(16.dp)
    }
}

