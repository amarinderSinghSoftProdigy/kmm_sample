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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.checkOffer
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
    val saveClicked = scope.saveClicked.flow.collectAsState()
    val openDialog = scope.showAlert.flow.collectAsState()

    Column(modifier = Modifier.padding(all = 16.dp)) {

        Text(
            text = stringResource(id = R.string.please_select_type),
            color = MaterialTheme.colors.background,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
        )

        Space(dp = 12.dp)
        if (openDialog.value)
            ShowAlert(stringResource(id = R.string.update_successfull)) {
                scope.changeAlertScope(false)
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
                        .padding(all = 8.dp)
                ) {
                    Text(
                        text = product?.name ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.background,
                    )
                    Text(
                        text = product?.manufacturerName ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = ConstColors.gray,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Box(modifier = Modifier.width(120.dp)) {
                            EditField(
                                label = stringResource(id = R.string.qty),
                                qty = "",
                                onChange = { },
                                onFocus = {

                                },
                                isEnabled = saveClicked.value,
                            )
                        }
                        Box(modifier = Modifier.width(120.dp)) {
                            EditField(
                                label = stringResource(id = R.string.free),
                                qty = "",
                                onChange = { },
                                onFocus = {
                                },
                                isEnabled = saveClicked.value,
                            )
                        }
                    }
                    Space(dp = 8.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {

                        Box(modifier = Modifier.width(120.dp)) {
                            MedicoButton(
                                text = stringResource(id = R.string.cancel),
                                isEnabled = true,
                                height = 32.dp,
                                elevation = null,
                                onClick = {

                                },
                                textSize = 12.sp,
                                color = ConstColors.ltgray,
                                txtColor = MaterialTheme.colors.background
                            )
                        }
                        Box(modifier = Modifier.width(120.dp)) {
                            MedicoButton(
                                text = if (saveClicked.value) stringResource(id = R.string.save) else stringResource(
                                    id = R.string.confirm
                                ),
                                isEnabled = true,
                                height = 32.dp,
                                elevation = null,
                                onClick = {
                                    if (saveClicked.value) {
                                        scope.saveData()
                                    } else {
                                        scope.saveData()
                                    }
                                },
                                textSize = 12.sp,
                                color = ConstColors.yellow,
                                txtColor = MaterialTheme.colors.background
                            )
                        }
                    }

                    if (!saveClicked.value) {
                        Surface(
                            onClick = { scope.saveData() },
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = null,
                                    tint = ConstColors.lightBlue,
                                )
                                Text(
                                    text = stringResource(id = R.string.edit_Offer),
                                    color = ConstColors.lightBlue
                                )
                            }
                        }
                    }
                }
            }
        }
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

fun onChange(
    product: ProductSearch?,
    qty: String,
    selectedProduct: ProductSearch?
) {
    product?.quantity = qty.toDouble()
    if (product?.sellerInfo?.isPromotionActive == true) {
        product.freeQuantity = checkOffer(
            product.sellerInfo?.promotionData,
            product.quantity
        )
    }
    if (product?.quantity!! > 0) {
        if (product.quantity > (selectedProduct?.stockInfo?.availableQty
                ?: 0)
        ) {

        }
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
