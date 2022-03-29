package com.zealsoftsol.medico.screens.ioc

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.IocBuyerScope
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.InvContactDetails
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.PhoneFormatInputFieldForRegister
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.search.BasicSearchBar


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IocBuyerListingScreen(scope: IocBuyerScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is IocBuyerScope.InvUserListing -> InvBuyerUserListing(scope)
            is IocBuyerScope.InvListing -> InvBuyerListing(scope)
            is IocBuyerScope.InvDetails -> InvBuyerDetails(scope)
            is IocBuyerScope.IOCPayNow -> IocPayNow(scope)
            is IocBuyerScope.IOCPaymentMethod -> IocPaymentMethod(scope)
        }
    }
}

@SuppressLint("RememberReturnType")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvBuyerDetails(scope: IocBuyerScope.InvDetails) {
    val data = scope.data.flow.collectAsState()
    val items = scope.items.flow.collectAsState()

    remember {
        scope.loadData(scope.item.invoiceId)
    }
    Box(
        modifier = Modifier.background(Color.White)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = data.value?.invoiceNo ?: "",
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = data.value?.invoiceAmount?.formatted ?: "",
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End
                    )
                }

                Space(4.dp)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = data.value?.invoiceDate?.formatted ?: "",
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = data.value?.viewStatus ?: "",
                        modifier = Modifier.weight(0.5f),
                        color =
                        if (data.value?.viewStatus.isNullOrEmpty()) {
                            MaterialTheme.colors.background
                        } else {
                            when ((data.value?.viewStatus ?: "").uppercase()) {
                                "COMPLETED" -> ConstColors.lightGreen
                                "PENDING" -> ConstColors.orange
                                else -> MaterialTheme.colors.background
                            }
                        },
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )

                }
                Space(8.dp)
                Divider(thickness = 0.5.dp)
                Space(8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { scope.previewImage(data.value?.viewInvoiceUrl ?: "") },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = null,
                        tint = ConstColors.txtGrey
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = stringResource(id = R.string.view_invoice),
                        color = ConstColors.txtGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700
                    )
                }
            }
            Space(4.dp)

            if (items.value.isEmpty() && scope.items.updateCount > 0) {
                NoRecords(
                    icon = R.drawable.ic_missing_stores,
                    text = R.string.no_users_found,
                    subtitle = "",
                    buttonText = stringResource(id = R.string.clear),
                    onHome = { },
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp)
                ) {
                    itemsIndexed(
                        items = items.value,
                        itemContent = { _, item ->
                            BuyerPaymentOptionItem(item)
                        },
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            MedicoRoundButton(
                text = stringResource(id = R.string.pay_now),
                isEnabled = true,
                elevation = null,
                onClick = {
                    scope.openPaymentMethod(
                        data.value?.unitCode ?: "",
                        data.value?.invoiceId ?: ""
                    )
                },
                contentColor = MaterialTheme.colors.background,
                wrapTextSize = true,
            )
        }
    }
}

@SuppressLint("RememberReturnType")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvBuyerListing(scope: IocBuyerScope.InvListing) {
    val items = scope.items.flow.collectAsState()
    val data = scope.data.flow.collectAsState()
    val slider = scope.slider.flow.collectAsState()
    remember {
        scope.loadData()
    }
    Column {

        Surface(
            shape = RoundedCornerShape(5.dp),
            elevation = 3.dp,
            color = ConstColors.lightBlue,
            modifier = Modifier.padding(all = 16.dp),
        ) {
            Column {
                Slider(
                    modifier = Modifier.height(35.dp),
                    value = slider.value,
                    onValueChange = { },
                    valueRange = 0f..100f,
                    onValueChangeFinished = {},
                    colors = SliderDefaults.colors(
                        thumbColor = ConstColors.lightGreen,
                        activeTrackColor = ConstColors.lightGreen,
                        inactiveTrackColor = Color.White,
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.33f),
                    ) {
                        Text(
                            text = data.value?.totalAmount?.formatted ?: "0.0",
                            color = Color.White,
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(" ")
                                append(stringResource(id = R.string.total_))
                            },
                            color = Color.White,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.33f),
                    ) {


                        Text(
                            text = data.value?.amountReceived?.formatted ?: "0.0",
                            color = Color.White,
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(id = R.string.paid),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.33f),
                    ) {
                        Text(
                            text = data.value?.outstandingAmount?.formatted ?: "0.0",
                            color = Color.White,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = stringResource(id = R.string.out_amount),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Space(dp = 12.dp)

        if (items.value.isEmpty() && scope.items.updateCount > 0) {
            NoRecords(
                icon = R.drawable.ic_missing_invoices,
                text = R.string.no_invoices_found,
                subtitle = "",
                buttonText = stringResource(id = R.string.clear),
                onHome = { },
            )
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { _, item ->
                        BuyerInvoiceListItem(
                            item,
                            { scope.openIOCDetails(item) },
                            { scope.previewImage(item = item.viewInvoiceUrl) })
                    },
                )
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvBuyerUserListing(scope: IocBuyerScope.InvUserListing) {
    val search = scope.searchText.flow.collectAsState()
    val items = scope.items.flow.collectAsState()
    val total = scope.total.flow.collectAsState()
    val paid = scope.paid.flow.collectAsState()
    val outstand = scope.outstand.flow.collectAsState()
    val slider = scope.slider.flow.collectAsState()

    remember {
        scope.load("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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

            BasicSearchBar(
                input = search.value,
                hint = R.string.search_tradename,
                icon = Icons.Default.Search,
                horizontalPadding = 16.dp,
                onIconClick = null,
                isSearchFocused = false,
                onSearch = { value, _ ->
                    scope.load(value = value)
                },
                isSearchCross = true
            )
        }
        Divider(
            color = ConstColors.lightBlue,
            thickness = 0.5.dp,
            startIndent = 0.dp
        )
        Space(4.dp)
        Surface(
            shape = RoundedCornerShape(5.dp),
            elevation = 3.dp,
            color = ConstColors.lightBlue,
            modifier = Modifier.padding(all = 8.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(5.dp),
                elevation = 3.dp,
                color = ConstColors.darkBlue,
                modifier = Modifier.padding(all = 8.dp),
            ) {
                Column {
                    Slider(
                        modifier = Modifier.height(35.dp),
                        value = slider.value,
                        onValueChange = { },
                        valueRange = 0f..100f,
                        onValueChangeFinished = {},
                        colors = SliderDefaults.colors(
                            thumbColor = ConstColors.lightGreen,
                            activeTrackColor = ConstColors.lightGreen,
                            inactiveTrackColor = Color.White,
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),

                        ) {
                        Column(
                            modifier = Modifier
                                .weight(0.33f),
                        ) {
                            Text(
                                text = total.value?.formatted ?: "0.0",
                                color = Color.White,
                                fontWeight = FontWeight.W700,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append(" ")
                                    append(stringResource(id = R.string.total_))
                                },
                                color = Color.White,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(0.33f),
                        ) {


                            Text(
                                text = paid.value?.formatted ?: "0.0",
                                color = Color.White,
                                fontWeight = FontWeight.W700,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(id = R.string.paid),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(0.33f),
                        ) {
                            Text(
                                text = outstand.value?.formatted ?: "0.0",
                                color = Color.White,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = stringResource(id = R.string.out_amount),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
        Space(16.dp)
        if (items.value.isEmpty() && scope.items.updateCount > 0) {
            NoRecords(
                icon = R.drawable.ic_missing_stores,
                text = R.string.no_col_found,
                subtitle = "",
                buttonText = if (search.value.isNotEmpty()) stringResource(id = R.string.clear)
                else stringResource(id = R.string.go_back),
                onHome = { if (search.value.isNotEmpty()) scope.load("") else scope.goBack() },
            )
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { index, item ->
                        BuyerListItem(
                            item,
                        ) {
                            scope.openIOCListing(item)
                        }
                        if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadItems()
                        }
                    },
                )
            }
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun BuyerListItem(
    item: InvUserData,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
        onClick = onClick,
        border = BorderStroke(0.5.dp, ConstColors.txtGrey)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = item.tradeName,
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Space(4.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            val startIndex = length
                            append(item.totalInvoices.toString())
                            addStyle(
                                SpanStyle(
                                    color = ConstColors.txtGrey,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                            append(" ")
                            append(stringResource(id = R.string.invoices))
                        },
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = item.customerType,
                        modifier = Modifier.weight(0.5f),
                        fontSize = 12.sp,
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.End
                    )
                }
                Space(8.dp)
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .background(ConstColors.blueBack),
                ) {

                    Text(
                        text = buildAnnotatedString {
                            append(" ")
                            append(stringResource(id = R.string.total_))
                        },
                        color = ConstColors.lightBlue,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(item.totalAmount.formatted)
                            append(" ")
                        },
                        color = ConstColors.lightBlue,
                        modifier = Modifier
                            .padding(bottom = 12.dp, end = 8.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .background(ConstColors.greenBack),
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(" ")
                            append(stringResource(id = R.string.paid))
                        },
                        modifier = Modifier.padding(8.dp),
                        color = ConstColors.lightGreen,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(item.paidAmount.formatted)
                            append(" ")
                        },
                        color = ConstColors.lightGreen,
                        modifier = Modifier
                            .padding(bottom = 12.dp, end = 8.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .background(ConstColors.redBack),
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(" ")
                            append(stringResource(id = R.string.outstanding))
                        },
                        modifier = Modifier.padding(8.dp),
                        color = ConstColors.marron,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(item.outstandingAmount.formatted)
                            append(" ")
                        },
                        modifier = Modifier
                            .padding(bottom = 12.dp, end = 8.dp)
                            .fillMaxWidth(),
                        color = ConstColors.marron,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun BuyerInvoiceListItem(
    item: BuyerDetailsData,
    onItemClick: () -> Unit,
    onImageClick: () -> Unit,
) {

    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
        border = BorderStroke(0.5.dp, ConstColors.txtGrey)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onItemClick)
                .padding(all = 8.dp)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.invoiceNo,
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.invoiceAmount.formatted,
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp,
                    textAlign = TextAlign.End
                )
            }

            Space(4.dp)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item.invoiceDate.formatted,
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = item.viewStatus,
                        modifier = Modifier.weight(0.5f),
                        color = when (item.viewStatus.uppercase()) {
                            "COMPLETED" -> ConstColors.lightGreen
                            "PENDING" -> ConstColors.orange
                            else -> MaterialTheme.colors.background
                        },
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
            Space(12.dp)
            Divider(thickness = 0.5.dp)
            Space(8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .clickable(onClick = onImageClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = null,
                    tint = ConstColors.txtGrey
                )
                Space(dp = 4.dp)
                Text(
                    text = stringResource(id = R.string.view_invoice),
                    color = ConstColors.txtGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700
                )
            }
            Space(4.dp)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun BuyerPaymentOptionItem(
    item: InvContactDetails,
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
        border = BorderStroke(0.5.dp, ConstColors.txtGrey)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = when (item.paymentType) {
                            IocBuyerScope.PaymentTypes.CASH_IN_HAND.type -> painterResource(id = R.drawable.ic_cash_in_hand)
                            IocBuyerScope.PaymentTypes.GOOGLE_PAY.type -> painterResource(id = R.drawable.ic_gpay)
                            IocBuyerScope.PaymentTypes.PHONE_PE.type -> painterResource(id = R.drawable.ic_phonepe)
                            IocBuyerScope.PaymentTypes.AMAZON_PAY.type -> painterResource(id = R.drawable.ic_amazon_pay)
                            IocBuyerScope.PaymentTypes.BHIM_UPI.type -> painterResource(id = R.drawable.ic_upi)
                            IocBuyerScope.PaymentTypes.PAYTM.type -> painterResource(id = R.drawable.ic_paytm)
                            else -> painterResource(id = R.drawable.ic_net_banking)
                        },
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = when (item.paymentType) {
                            IocBuyerScope.PaymentTypes.CASH_IN_HAND.type -> stringResource(id = R.string.cash_in_hand)
                            IocBuyerScope.PaymentTypes.GOOGLE_PAY.type -> stringResource(id = R.string.g_pay)
                            IocBuyerScope.PaymentTypes.PHONE_PE.type -> stringResource(id = R.string.phone_pe)
                            IocBuyerScope.PaymentTypes.AMAZON_PAY.type -> stringResource(id = R.string.amazon_pay)
                            IocBuyerScope.PaymentTypes.BHIM_UPI.type -> stringResource(id = R.string.upi)
                            IocBuyerScope.PaymentTypes.PAYTM.type -> stringResource(id = R.string.paytm)
                            else -> stringResource(id = R.string.net_banking)
                        },
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.collectionDate.formatted,
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = item.collectionAmount.formatted,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

            if (item.paymentType == IocBuyerScope.PaymentTypes.CASH_IN_HAND.type) {
                Space(dp = 4.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = item.lineMan,
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = item.mobileNumber,
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
            Space(dp = 16.dp)
        }
    }
}

@SuppressLint("SimpleDateFormat", "HardwareIds")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IocPayNow(
    scope: IocBuyerScope.IOCPayNow
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val lineManName = scope.lineManName.flow.collectAsState()
    val totalAmount = scope.totalAmount.flow.collectAsState()
    val mobileNumber = scope.mobileNumber.flow.collectAsState()
    val validPhone = scope.validPhone(mobileNumber.value)
    val enable = scope.enableButton.flow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InputField(
                modifier = Modifier
                    .align(Alignment.Start)
                    .scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.line_man_name),
                text = lineManName.value,
                isValid = true,
                onValueChange = { scope.updateLineManName(it) },
                mandatory = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
            val total = remember {
                mutableStateOf(
                    if (totalAmount.value.split(".")
                            .lastOrNull() == ""
                    ) totalAmount.value.split(".")
                        .first() else totalAmount.value
                )
            }

            Space(12.dp)
            InputField(
                modifier = Modifier
                    .align(Alignment.Start)
                    .scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.enter_total_amount),
                text = totalAmount.value,
                isValid = true,
                onValueChange = {
                    val split =
                        it.replace(",", ".").split(".")
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
                    total.value = "$modBefore$modAfter"
                    scope.updateTotalAmount(total.value)
                },
                mandatory = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
            Space(12.dp)
            InputWithError(errorText = if (!validPhone) stringResource(id = R.string.phone_validation) else null) {
                PhoneFormatInputFieldForRegister(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.phone_number),
                    text = mobileNumber.value,
                    onValueChange = { phoneNumber ->
                        scope.updatePhoneNumber(phoneNumber.filter { it.isDigit() })
                    },
                    mandatory = true,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(26.dp)
            val context = LocalContext.current
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            MedicoRoundButton(
                text = stringResource(id = R.string.continue_),
                isEnabled = enable.value,
                elevation = null,
                onClick = {
                    scope.submitPayment(
                        mobileNumber.value.formatIndia(),
                        id
                    )
                },
                contentColor = MaterialTheme.colors.background,
            )
        }
    }
}


@SuppressLint("SimpleDateFormat")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IocPaymentMethod(
    scope: IocBuyerScope.IOCPaymentMethod
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val items = scope.items.flow.collectAsState()
        val selected = scope.selected.flow.collectAsState()
        val indexOld = remember { selected.value }
        Space(16.dp)
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            itemsIndexed(
                items = items.value,
                itemContent = { index, item ->
                    SelectPaymentOptionItem(item, indexOld == index) {
                        scope.openPayNow(scope.unitCode, scope.invoiceId, index, item)
                    }
                },
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SelectPaymentOptionItem(
    item: IocBuyerScope.PaymentTypes,
    index: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp,
        color = Color.White,
        onClick = onClick,
        modifier = Modifier.padding(all = 4.dp),
        border = BorderStroke(0.5.dp, if (index) ConstColors.yellow else ConstColors.txtGrey)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(all = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    elevation = 3.dp,
                    color = Color.White,
                    border = BorderStroke(0.5.dp, ConstColors.txtGrey),
                ) {
                    Image(
                        painter = when (item.type) {
                            IocBuyerScope.PaymentTypes.CASH_IN_HAND.type -> painterResource(id = R.drawable.ic_cash_in_hand)
                            IocBuyerScope.PaymentTypes.GOOGLE_PAY.type -> painterResource(id = R.drawable.ic_gpay)
                            IocBuyerScope.PaymentTypes.PHONE_PE.type -> painterResource(id = R.drawable.ic_phonepe)
                            IocBuyerScope.PaymentTypes.AMAZON_PAY.type -> painterResource(id = R.drawable.ic_amazon_pay)
                            IocBuyerScope.PaymentTypes.BHIM_UPI.type -> painterResource(id = R.drawable.ic_upi)
                            IocBuyerScope.PaymentTypes.PAYTM.type -> painterResource(id = R.drawable.ic_paytm)
                            else -> painterResource(id = R.drawable.ic_net_banking)
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(10.dp)
                    )
                }
                Space(dp = 12.dp)

                Text(
                    text = when (item.type) {
                        IocBuyerScope.PaymentTypes.CASH_IN_HAND.type -> stringResource(id = R.string.cash_in_hand)
                        IocBuyerScope.PaymentTypes.GOOGLE_PAY.type -> stringResource(id = R.string.g_pay)
                        IocBuyerScope.PaymentTypes.PHONE_PE.type -> stringResource(id = R.string.phone_pe)
                        IocBuyerScope.PaymentTypes.AMAZON_PAY.type -> stringResource(id = R.string.amazon_pay)
                        IocBuyerScope.PaymentTypes.BHIM_UPI.type -> stringResource(id = R.string.upi)
                        IocBuyerScope.PaymentTypes.PAYTM.type -> stringResource(id = R.string.paytm)
                        else -> stringResource(id = R.string.net_banking)
                    },
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
            }

            Divider()
        }
    }
}

