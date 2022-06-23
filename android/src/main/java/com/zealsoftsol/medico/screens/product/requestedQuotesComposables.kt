package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.RequestedQuotesScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.checkOffer
import com.zealsoftsol.medico.screens.search.YellowOutlineIndication


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun RequestedQuotesComposable(scope: RequestedQuotesScope) {
    val cartData = scope.cartData.flow.collectAsState()
    val entries = if (cartData.value != null) cartData.value?.sellerCarts?.get(0)?.items else null
    val cartItem = entries?.get(entries.size - 1)
    val showToast = scope.showToast.flow.collectAsState()
    val list = scope.requestedData.flow.collectAsState()
    val seller = scope.sellerInfoLocal.flow.collectAsState()
    val productList = list.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val enableButton = remember {
        mutableStateOf(false)
    }
    val isChecked = remember {
        mutableStateOf(false)
    }
    val isSelected = remember {
        mutableStateOf(false)
    }

    val quantity = remember {
        mutableStateOf(0)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Space(20.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    elevation = 5.dp
                ) {
                    CoilImage(
                        src = CdnUrlProvider.urlFor(
                            scope.productData.imageCode,
                            CdnUrlProvider.Size.Px320
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(80.dp)
                            .clickable {
                                scope.selectItem(scope.productData.imageCode)
                            },
                        onError = { ItemPlaceholder() },
                        onLoading = { ItemPlaceholder() },
                        isCrossFadeEnabled = false
                    )
                }
                Space(dp = 10.dp)
                Column {
                    Text(
                        text = scope.productData.name,
                        color = Color.Black,
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp,
                    )
                    Space(8.dp)
                    Text(
                        text = scope.productData.manufacturer,
                        color = Color.Black,
                        fontSize = 13.sp,
                    )
                }
            }
            Space(20.dp)
            Text(
                text = stringResource(id = R.string.sub_stockist_message),
                color = Color.Black,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()

            )
            Space(dp = 5.dp)

            Text(
                text = stringResource(id = R.string.only_cod_message),
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (productList.isEmpty()) {
                NoRecords(
                    icon = R.drawable.ic_missing_stores,
                    text = R.string.no_stockist,
                    onHome = { scope.goHome() },
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f)
                ) {
                    itemsIndexed(
                        items = productList,
                        itemContent = { index, item ->
                            SellerData(
                                result = item.sellerInfo,
                                seller.value?.unitCode == item.sellerInfo?.unitCode
                            ) {
                                scope.setSellerInfo(item.sellerInfo)
                                isSelected.value = it
                            }
                        },
                    )
                }

                if (!isSelected.value) {
                    Space(dp = 12.dp)
                    Text(
                        stringResource(id = R.string.select_stockist_message),
                        color = ConstColors.red,
                        textAlign = TextAlign.Start
                    )
                }


                if (!isChecked.value) {
                    Button(
                        onClick = { isChecked.value = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = ConstColors.yellow),
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            stringResource(id = R.string.add_to_cart),
                            modifier = Modifier.padding(
                                start = 10.dp,
                                end = 10.dp,
                                top = 3.dp,
                                bottom = 3.dp
                            )
                        )
                    }
                }

                if (isChecked.value) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp)
                    ) {
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    Box(modifier = Modifier.width(120.dp)) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.qty).uppercase(),
                                                    color = ConstColors.gray,
                                                    fontSize = 12.sp,
                                                )

                                                val wasQty = remember {
                                                    mutableStateOf(
                                                        if (quantity.value.toString().split(".")
                                                                .lastOrNull() == "0"
                                                        ) quantity.value.toString().split(".")
                                                            .first() else quantity.value.toString()
                                                    )
                                                }

                                                BasicTextField(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    value = TextFieldValue(
                                                        wasQty.value,
                                                        selection = TextRange(wasQty.value.length)
                                                    ),
                                                    onValueChange = {
                                                        val split =
                                                            it.text.replace(",", ".").split(".")
                                                        val beforeDot = split[0]
                                                        val afterDot = split.getOrNull(1)
                                                        var modBefore =
                                                            beforeDot.toIntOrNull() ?: 0
                                                        val modAfter = when (afterDot?.length) {
                                                            0 -> "."
                                                            in 1..Int.MAX_VALUE -> when (afterDot!!.take(
                                                                1
                                                            ).toIntOrNull()) {
                                                                0 -> ".0"
                                                                in 1..4 -> ".0"
                                                                5 -> ".5"
                                                                in 6..9 -> {
                                                                    modBefore++
                                                                    ".0"
                                                                }
                                                                null -> ""
                                                                else -> throw UnsupportedOperationException(
                                                                    "cant be that"
                                                                )
                                                            }
                                                            null -> ""
                                                            else -> throw UnsupportedOperationException(
                                                                "cant be that"
                                                            )
                                                        }
                                                        wasQty.value = "$modBefore$modAfter"
                                                        scope.productData.quantity =
                                                            wasQty.value.toDouble()
                                                        scope.productData.freeQuantity = checkOffer(
                                                            scope.productData.sellerInfo?.promotionData,
                                                            scope.productData.quantity
                                                        )
                                                        enableButton.value =
                                                            wasQty.value.isNotEmpty() && wasQty.value != "0.0"
                                                    },
                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Done
                                                    ),
                                                    maxLines = 1,
                                                    singleLine = true,
                                                    readOnly = false,
                                                    enabled = true,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        keyboardController?.hide()
                                                    }),
                                                    textStyle = TextStyle(
                                                        color = MaterialTheme.colors.background,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.W700,
                                                        textAlign = TextAlign.End,
                                                    )
                                                )
                                            }
                                            Divider(
                                                color = MaterialTheme.colors.background,
                                                thickness = 1.5.dp
                                            )
                                        }
                                    }
                                    Box(modifier = Modifier.width(120.dp)) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.free).uppercase(),
                                                    color = ConstColors.gray,
                                                    fontSize = 12.sp,
                                                )
                                                Text(
                                                    text = if (scope.productData.sellerInfo?.isPromotionActive == true)
                                                        scope.productData.freeQuantity.toString() else "0",
                                                    color = MaterialTheme.colors.background,
                                                    fontWeight = FontWeight.W700,
                                                    fontSize = 20.sp,
                                                )
                                            }

                                            Divider(
                                                color = MaterialTheme.colors.background,
                                                thickness = 1.5.dp
                                            )
                                        }
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
                                                isChecked.value = false
                                            },
                                            textSize = 12.sp,
                                            color = ConstColors.ltgray,
                                            txtColor = MaterialTheme.colors.background
                                        )
                                    }
                                    Box(modifier = Modifier.width(120.dp)) {
                                        MedicoButton(
                                            text = stringResource(id = R.string.add_to_cart),
                                            isEnabled = enableButton.value,
                                            height = 32.dp,
                                            elevation = null,
                                            onClick = { scope.buy(BuyingOption.BUY) },
                                            textSize = 12.sp,
                                            color = ConstColors.yellow,
                                            txtColor = MaterialTheme.colors.background
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showToast.value) {
        scope.setSellerInfo(null)
        enableButton.value = false
        isChecked.value = false
        isSelected.value = false
        quantity.value = 0
        scope.setSellerInfo(null)
        if (cartItem != null)
            ShowToastGlobal(
                msg = cartItem.productName + " " +
                        stringResource(id = R.string.added_to_cart) + " " +
                        stringResource(id = R.string.qty) +
                        " : " +
                        cartItem.quantity.formatted + " + " +
                        stringResource(id = R.string.free) + " " +
                        cartItem.freeQuantity.formatted
            )
        EventCollector.sendEvent(Event.Action.Search.showToast("", null))
    }
}

@Composable
fun SellerData(result: SellerInfo?, checked: Boolean = false, onChecked: ((Boolean) -> Unit)) {

    val isChecked = remember {
        mutableStateOf(checked)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        color = if (isChecked.value) {
            ConstColors.skyBlue
        } else {
            Color.White
        },
        border = BorderStroke(1.dp, color = ConstColors.lightGrey)
    ) {

        val labelColor = ConstColors.lightBlue
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
            )
            {

                Checkbox(
                    checked = isChecked.value,
                    colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                    onCheckedChange = {
                        isChecked.value = it
                        onChecked(isChecked.value)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Space(dp = 20.dp)

                Column {
                    Text(
                        text = result?.tradeName ?: "",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )

                    Space(dp = 8.dp)

                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_address),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Space(5.dp)

                        Text(
                            text = result?.geoData?.full() ?: "",
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                    }
                    Space(dp = 8.dp)

                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = ConstColors.orange
                        )

                        Space(5.dp)

                        Text(
                            text = result?.geoData?.formattedDistance ?: "",
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            Space(dp = 10.dp)

            if (isChecked.value) {
                val maxWidth =
                    LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp - 5.dp
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = maxWidth)
                        .background(labelColor)
                )
            }
        }


    }

    Space(dp = 10.dp)
}