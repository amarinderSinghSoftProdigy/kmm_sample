package com.zealsoftsol.medico.screens.ioc

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.FormattedData
import com.zealsoftsol.medico.data.InvContactDetails
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.search.BasicSearchBar

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IocListingScreen(scope: IocScope, scaffoldState: ScaffoldState) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is IocScope.InvUserListing -> InvUserListing(scope)
            is IocScope.InvListing -> InvListing(scope)
            is IocScope.InvDetails -> InvDetails(scope)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvDetails(scope: IocScope.InvDetails) {
    val data = scope.data.flow.collectAsState()
    val items = scope.items.flow.collectAsState()

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
                    text = "Pending",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.orange,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
            Space(4.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.out_amount))
                        append(" ")
                        append(data.value?.invoiceOutstdAmount?.formatted ?: "")
                    },
                    color = ConstColors.marron,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
            }
            Space(8.dp)
            Divider(thickness = 0.5.dp)
            Space(8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.5f)
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
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable {
                            scope.openEditInvoice(
                                BuyerDetailsData(
                                    data.value?.unitCode ?: "",
                                    data.value?.tradeName ?: "",
                                    data.value?.invoiceNo ?: "",
                                    data.value?.invoiceAmount ?: FormattedData("0.0", 0.0),
                                    data.value?.viewInvoiceUrl ?: "",
                                    "Pending",
                                    data.value?.invoiceId ?: "",
                                    data.value?.invoiceDate ?: FormattedData("0", 0L),
                                ),
                                scope
                            )
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = null,
                        tint = ConstColors.lightBlue
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = stringResource(id = R.string.add_payment),
                        fontSize = 14.sp,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.End
                    )
                }
            }
            Space(4.dp)
        }

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
                        PaymentOptionItem(item)
                    },
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvListing(scope: IocScope.InvListing) {
    val item = scope.item
    val items = scope.items.flow.collectAsState()
    val data = scope.data.flow.collectAsState()

    Column {
        Surface(
            shape = RoundedCornerShape(5.dp),
            elevation = 1.dp,
            color = ConstColors.lightBlue,
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.amount_received),
                    color = Color.White,
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.value?.amountReceived?.formatted ?: "0.0",
                    color = Color.White,
                    fontWeight = FontWeight.W800,
                    fontSize = 26.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Space(dp = 16.dp)
                Row(
                    modifier = Modifier
                        .background(ConstColors.darkBlue)
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.outstanding))
                            append(": ")
                            append(data.value?.outstandingAmount?.formatted ?: "0.0")
                        },
                        color = Color.White,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End
                    )
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
                    itemContent = { index, item ->
                        InvoiceListItem(
                            item,
                            { scope.openIOCDetails(item) },
                            { scope.previewImage(item.viewInvoiceUrl) },
                            { scope.openEditInvoice(item, scope) }
                        )
                        /*if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                            scope.loadItems()
                        }*/
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
private fun InvUserListing(scope: IocScope.InvUserListing) {
    val search = scope.searchText.flow.collectAsState()
    val items = scope.items.flow.collectAsState()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.inv_online_collection),
                modifier = Modifier.weight(0.5f),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Text(
                text = stringResource(id = R.string.create_debt),
                modifier = Modifier
                    .weight(0.5f)
                    .clickable { scope.openCreateIOC() },
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.End
            )
        }

        if (items.value.isEmpty() && scope.items.updateCount > 0) {
            NoRecords(
                icon = R.drawable.ic_missing_stores,
                text = R.string.no_collection_found,
                subtitle = "",
                buttonText = stringResource(id = R.string.clear),
                onHome = { scope.load("") },
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
                        IocListItem(
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
fun IocListItem(
    item: InvUserData,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 1.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
        onClick = onClick
    ) {
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
                        append(stringResource(id = R.string.invoices))
                        append(": ")
                    },
                    color = ConstColors.txtGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_invoice_grey),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = ConstColors.txtGrey
                )
                Space(dp = 4.dp)
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
                        append(" (")
                        append(item.customerType)
                        append(")")
                    },
                    fontSize = 12.sp,
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            }
            Space(8.dp)

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.total),
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.paid),
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.lightGreen,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.outstanding),
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.marron,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
            Space(4.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.totalAmount.formatted,
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.paidAmount.formatted,
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.lightGreen,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.outstandingAmount.formatted,
                    modifier = Modifier.weight(0.33f),
                    color = ConstColors.marron,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            Space(8.dp)
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun InvoiceListItem(
    item: BuyerDetailsData,
    onItemClick: () -> Unit,
    onPreview: () -> Unit,
    onEditInvoice: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 1.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
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

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.invoiceDate.formatted,
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
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

            Space(16.dp)
            Divider(thickness = 0.5.dp)
            Space(8.dp)

            Column(modifier = Modifier.fillMaxWidth()) {
                val list = ArrayList<String>()
                list.add("")
                FoldableItem(
                    expanded = false,
                    headerBackground = Color.White,
                    headerBorder = BorderStroke(0.dp, Color.Transparent),
                    headerMinHeight = 25.dp,
                    header = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp),
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

                    },
                    childItems = list,
                    hasItemLeadingSpacing = false,
                    hasItemTrailingSpacing = false,
                    itemSpacing = 0.dp,
                    itemHorizontalPadding = 0.dp,
                    itemsBackground = Color.Transparent,
                    item = { _, _ ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            CoilImage(
                                onError = { Placeholder(R.drawable.ic_placeholder) },
                                src = item.viewInvoiceUrl,
                                size = LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp,
                                onLoading = { CircularProgressIndicator(color = ConstColors.yellow) }
                            )
                        }
                    }
                )
            }
            Space(4.dp)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun PaymentOptionItem(
    item: InvContactDetails,
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 1.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
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
                            IocScope.PaymentTypes.CASH_IN_HAND.type -> painterResource(id = R.drawable.ic_cash_in_hand)
                            IocScope.PaymentTypes.GOOGLE_PAY.type -> painterResource(id = R.drawable.ic_gpay)
                            IocScope.PaymentTypes.PHONE_PE.type -> painterResource(id = R.drawable.ic_phonepe)
                            IocScope.PaymentTypes.AMAZON_PAY.type -> painterResource(id = R.drawable.ic_amazon_pay)
                            IocScope.PaymentTypes.BHIM_UPI.type -> painterResource(id = R.drawable.ic_upi)
                            IocScope.PaymentTypes.PAYTM.type -> painterResource(id = R.drawable.ic_paytm)
                            else -> painterResource(id = R.drawable.ic_net_banking)
                        },
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = when (item.paymentType) {
                            IocScope.PaymentTypes.CASH_IN_HAND.type -> stringResource(id = R.string.cash_in_hand)
                            IocScope.PaymentTypes.GOOGLE_PAY.type -> stringResource(id = R.string.g_pay)
                            IocScope.PaymentTypes.PHONE_PE.type -> stringResource(id = R.string.phone_pe)
                            IocScope.PaymentTypes.AMAZON_PAY.type -> stringResource(id = R.string.amazon_pay)
                            IocScope.PaymentTypes.BHIM_UPI.type -> stringResource(id = R.string.upi)
                            IocScope.PaymentTypes.PAYTM.type -> stringResource(id = R.string.paytm)
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

            Space(dp = 4.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (item.paymentType == IocScope.PaymentTypes.CASH_IN_HAND.type) {
                    Text(
                        text = item.lineMan,
                        modifier = Modifier.weight(0.5f),
                        color = ConstColors.txtGrey,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                    )
                }
            }
            Space(dp = 16.dp)
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SpinnerItem(
    item: IocScope.PaymentTypes,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            Text(
                text = stringResourceByName(name = item.stringId),
                color = ConstColors.txtGrey,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }
        Divider(thickness = 0.5.dp)
    }
}


