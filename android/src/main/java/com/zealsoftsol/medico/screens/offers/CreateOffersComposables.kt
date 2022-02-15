package com.zealsoftsol.medico.screens.offers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.Promotions
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.YellowOutlineIndication


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateOffersScreen(scope: OffersScope.CreateOffer) {
    val search = scope.productSearch.flow.collectAsState()
    val types = scope.promoTypes.flow.collectAsState()
    val activeTab = scope.activeTab.flow.collectAsState()
    val autoComplete = scope.autoComplete.flow.collectAsState()
    val selectedProduct = scope.selectedProduct.flow.collectAsState()
    val activeIndex = scope.activeType.flow.collectAsState()
    val openDialog = scope.showAlert.flow.collectAsState()
    val dialogMessage = scope.dialogMessage.flow.collectAsState()
    val requestData = scope.requestData.flow.collectAsState()
    val request = OfferProductRequest()

    Column(modifier = Modifier.padding(all = 16.dp)) {

        Text(
            text = stringResource(id = R.string.please_select_type),
            color = MaterialTheme.colors.background,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
        )

        Space(dp = 12.dp)
        if (openDialog.value)
            ShowAlert(
                if (dialogMessage.value.isNotEmpty())
                    dialogMessage.value
                else stringResource(id = R.string.offer_successfull)
            ) {
                scope.changeAlertScope(false)
                scope.goBack()
            }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.dp)
                .background(ConstColors.ltgray, MaterialTheme.shapes.large)
        ) {
            types.value.forEach {
                var boxMod = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                boxMod = if (types.value.size == 1) {
                    boxMod
                } else {
                    boxMod
                        .padding(1.dp)
                        .clickable { scope.selectTab(it.name) }
                }
                val isActive = activeTab.value == it.name
                boxMod = if (isActive) {
                    request.promotionType = it.code
                    boxMod.background(ConstColors.lightGreen, MaterialTheme.shapes.large)
                } else {
                    boxMod
                }
                Row(
                    modifier = boxMod,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = it.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = if (isActive) Color.White else MaterialTheme.colors.background,
                        modifier = Modifier.padding(all = 2.dp)
                    )
                }
            }
        }

        Space(dp = 16.dp)
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_by_product_name,
            icon = null,
            horizontalPadding = 0.dp,
            onIconClick = null,
            isSearchFocused = false,
            onSearch = { value, _ ->
                scope.searchProduct(search = value)
            },
            isSearchCross = false
        )
        if (autoComplete.value.isNotEmpty()) {
            Divider()
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White, shape = MaterialTheme.shapes.large)
            ) {
                items(
                    items = autoComplete.value,
                    key = { item -> item.suggestion },
                    itemContent = { item ->
                        AutoCompleteItem(
                            item,
                            search.value
                        ) { scope.selectAutoComplete(item) }
                    },
                )
            }
        }

        if (selectedProduct.value != null) {
            val product = selectedProduct.value
            Surface(
                color = Color.White,
                shape = MaterialTheme.shapes.medium,
                onClick = { },
                indication = YellowOutlineIndication,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                border = BorderStroke(1.dp, ConstColors.ltgray),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp)
                ) {
                    Text(
                        text = product?.name ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.background,
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = product?.manufacturerName ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = ConstColors.gray,
                    )
                    Space(dp = 8.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Box(modifier = Modifier.width(120.dp)) {
                            EditField(
                                label = if (activeIndex.value == 0) stringResource(id = R.string.qty) else stringResource(
                                    id = R.string.discount
                                ),
                                qty = if (activeIndex.value == 0) {
                                    (requestData.value?.buy ?: 0.0).toString()
                                } else {
                                    (requestData.value?.discount ?: 0.0).toString()
                                },
                                onChange = {
                                    if (activeIndex.value == 0) {
                                        request.discount = 0.0
                                        request.buy = it.toDouble()
                                    } else {
                                        request.buy = 0.0
                                        request.discount = it.toDouble()
                                    }
                                },
                                isEnabled = true,//saveClicked.value,
                            )
                        }

                        if (activeIndex.value == 0) {
                            Box(modifier = Modifier.width(120.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.free),
                                    qty = (requestData.value?.free ?: 0.0).toString(),
                                    onChange = { request.free = it.toDouble() },
                                    isEnabled = true,//saveClicked.value,
                                )
                            }
                        } else {
                            request.buy = 0.0
                            request.free = 0.0
                        }
                    }
                    Space(dp = 12.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val switchEnabled = remember { mutableStateOf(false) }
                        Box(modifier = Modifier.width(120.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(id = R.string.stop),
                                        color = ConstColors.red,
                                        fontSize = 14.sp,
                                        fontWeight = if (!switchEnabled.value) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "/",
                                        color = MaterialTheme.colors.background,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = stringResource(id = R.string.start),
                                        color = ConstColors.lightGreen,
                                        fontSize = 14.sp,
                                        fontWeight = if (switchEnabled.value) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                Space(dp = 4.dp)
                                Switch(
                                    checked = switchEnabled.value,
                                    onCheckedChange = {
                                        switchEnabled.value = it
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ConstColors.green,
                                        uncheckedThumbColor = ConstColors.red,
                                    )
                                )
                                Space(dp = 4.dp)
                            }
                        }
                        Box(modifier = Modifier.width(120.dp)) {
                            MedicoButton(
                                text = stringResource(
                                    id = R.string.confirm
                                ),
                                isEnabled = true,
                                height = 35.dp,
                                elevation = null,
                                onClick = {
                                    request.active = switchEnabled.value
                                    scope.saveOffer(request = request, product = product)
                                },
                                textSize = 14.sp,
                                color = ConstColors.yellow,
                                txtColor = MaterialTheme.colors.background
                            )
                        }
                    }
                }
            }
            Space(dp = 16.dp)
            if(selectedProduct.value!!.promotionData.isNotEmpty()){
                LazyColumn {
                    itemsIndexed(
                        items = selectedProduct.value!!.promotionData,
                        itemContent = { index, item ->
                            OfferItem(item, scope)
                        },
                    )
                }
            }
        }
    }
}

/**
 * ui item for offer listing
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OfferItem(item: Promotions, scope: OffersScope.CreateOffer) {
    Column {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Row {
                Column {
                    Text(
                        text = item.productName,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.manufacturerName,
                        fontSize = 14.sp,
                        color = ConstColors.gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (item.active) stringResource(id = R.string.running) else "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.Bold
                    )
                    Space(dp = 4.dp)
                    Switch(
                        checked = item.active, onCheckedChange = {
                            scope.showBottomSheet(item.promoCode, item.productName, it)
                        }, colors = SwitchDefaults.colors(
                            checkedThumbColor = ConstColors.green
                        )
                    )
                }
            }
            Space(dp = 4.dp)

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.width(maxWidth / 2),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Card(
                        modifier = Modifier
                            .selectable(
                                selected = true,
                                onClick = {
                                    //scope.startBrandSearch(item.searchTerm, item.field)
                                }),
                        elevation = 3.dp,
                        shape = RoundedCornerShape(5.dp),
                        backgroundColor = ConstColors.red,
                    ) {
                        Text(
                            text = item.offer,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                        )
                    }
                }

                Box(
                    modifier = Modifier.width(maxWidth / 2).align(Alignment.BottomEnd),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        onClick = { scope.showEditBottomSheet(item) },
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = null,
                                tint = ConstColors.lightBlue,
                            )
                            Space(dp = 4.dp)
                            Text(
                                text = stringResource(id = R.string.edit_Offer),
                                color = ConstColors.lightBlue,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Space(12.dp)
        }
        Divider()
    }
}


/**
 * items to be displayed in autocomplete dropdown list
 */
@Composable
private fun AutoCompleteItem(
    autoComplete: AutoComplete, input: String,
    onClick: () -> Unit
) {
    val regex = "(?i)$input".toRegex()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BoxWithConstraints {
                Column(modifier = Modifier.widthIn(max = maxWidth - 24.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            append(autoComplete.suggestion)
                            regex.find(autoComplete.suggestion)?.let {
                                addStyle(
                                    SpanStyle(fontWeight = FontWeight.W700),
                                    it.range.first,
                                    it.range.last + 1,
                                )
                            }
                        },
                        color = MaterialTheme.colors.background,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400,
                    )
                    if (autoComplete.details.isNotEmpty()) {
                        Text(
                            text = autoComplete.details,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.background,
                            fontWeight = FontWeight.W400,
                        )
                    }
                }
            }
        }
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color(0xFFE6F0F7),
        )
    }
}


/**
 * @param scope current scope to get the current and updated state of views
 */

@Composable
fun ShowAlert(message: String, onClick: () -> Unit) {
    MaterialTheme {
        AlertDialog(
            onDismissRequest = onClick,
            text = {
                Text(message)
            },
            confirmButton = {
                Button(
                    onClick = onClick
                ) {
                    Text(stringResource(id = R.string.okay))
                }
            }
        )
    }
}