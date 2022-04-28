package com.zealsoftsol.medico.screens.dashboard

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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.BannersScope
import com.zealsoftsol.medico.data.BannerItemData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.clickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BannersScreen(scope: BannersScope) {
    val totalResults = scope.totalItems
    val bannerList = scope.bannersList.flow.collectAsState().value
    val searchTerm = remember { mutableStateOf("") }
    var queryTextChangedJob: Job? = null
    val showToast = scope.showToast.flow.collectAsState()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                        .align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(3.dp),
                    elevation = 3.dp,
                    color = White
                ) {
                    Row(
                        modifier = Modifier
                            .height(45.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
            LazyColumn(
                contentPadding = PaddingValues(start = 3.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                itemsIndexed(
                    items = bannerList,
                    key = { index, _ -> index },
                    itemContent = { _, item ->
                        BannerItem(item, scope)
                    },
                )

                item {
                    if (bannerList.size < totalResults) {
                        MedicoButton(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(top = 5.dp, bottom = 5.dp)
                                .height(40.dp),
                            text = stringResource(id = R.string.more),
                            isEnabled = true,
                        ) {
                            scope.getBanners(search = searchTerm.value)
                        }
                    }
                }
            }
        }
        if (showToast.value) {
            ShowToastGlobal(
                msg = scope.productName + " " +
                        stringResource(id = R.string.added_to_cart) + " " +
                        stringResource(id = R.string.qty) +
                        " : " +
                        scope.qty + " + " +
                        stringResource(id = R.string.free) + " " +
                        scope.freeQty
            )
            scope.updateAlertVisibility(false)
        }
    }
}

/**
 * UI for items in Banner on top
 */
@Composable
fun BannerItem(item: BannerItemData, scope: BannersScope) {
    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = White,
    ) {
        Column {
            Box(modifier = Modifier.clickable {
                scope.zoomImage(item.url)
            }) {
                CoilImageBrands(
                    src = item.url,
                    contentScale = ContentScale.FillBounds,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 150.dp,
                )
            }

            MedicoButton(
                modifier = Modifier
                    .padding(10.dp),
                text = stringResource(id = R.string.add_to_cart),
                isEnabled = true
            ) {
                scope.addToCart(
                    item.sellerUnitCode,
                    item.productCode,
                    BuyingOption.BUY,
                    CartIdentifier(item.spid, null),
                    item.quantity,
                    item.free,
                    item.productName
                )
            }
        }

    }
}