package com.zealsoftsol.medico.screens.ioc

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import com.zealsoftsol.medico.core.mvi.scope.nested.IocSellerScope
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.ImageLabel
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.offers.ShowAlert
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.utils.PermissionCheckUIForInvoice
import com.zealsoftsol.medico.utils.PermissionViewModel
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IocScreen(sellerScope: IocSellerScope, scaffoldState: ScaffoldState) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (sellerScope) {
            is IocSellerScope.IOCListing -> IOCListing(sellerScope)
            is IocSellerScope.IOCCreate -> IocCreate(sellerScope, scaffoldState)
        }
    }
}

@SuppressLint("RememberReturnType")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IOCListing(sellerScope: IocSellerScope.IOCListing) {
    val search = sellerScope.searchText.flow.collectAsState()
    val selectedIndex = sellerScope.selectedIndex.flow.collectAsState()
    val items = sellerScope.items.flow.collectAsState()

    remember {
        sellerScope.search("")
    }

    Box {
        Column {
            Space(16.dp)
            BasicSearchBar(
                input = search.value,
                hint = R.string.search,
                searchBarEnd = SearchBarEnd.Eraser,
                elevation = 0.dp,
                horizontalPadding = 16.dp,
                isSearchFocused = false,
                onSearch = { v, _ -> sellerScope.search(v) },
                onIconClick = {
                    sellerScope.search("")
                },
                backgroundColor = ConstColors.lightBlue.copy(alpha = 0.1f)
            )


            if (items.value.isEmpty() && sellerScope.items.updateCount > 0) {
                NoRecords(
                    icon = R.drawable.ic_missing_stores,
                    text = R.string.no_users_found,
                    subtitle = "",
                    buttonText = stringResource(id = R.string.clear),
                    onHome = { sellerScope.search("") },
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    itemsIndexed(
                        items = items.value,
                        itemContent = { index, item ->
                            ParentIocItem(
                                item,
                                sellerScope, index, selectedIndex.value
                            )
                            if (index == items.value.lastIndex && sellerScope.pagination.canLoadMore()) {
                                sellerScope.loadItems()
                            }
                        },
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                MedicoRoundButton(
                    text = stringResource(id = R.string.continue_),
                    isEnabled = selectedIndex.value != -1,
                    elevation = null,
                    onClick = { sellerScope.selectItem(items.value[selectedIndex.value]) },
                    contentColor = MaterialTheme.colors.background,
                    wrapTextSize = true,
                )
            }
        }
    }

}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun ParentIocItem(
    store: RetailerData,
    sellerScope: IocSellerScope.IOCListing,
    index: Int,
    selectedIndex: Int,
) {
    val items = ArrayList<RetailerData>()
    items.add(store)
    val expanded = remember { mutableStateOf(false) }
    Column {
        FoldableItem(
            expanded = expanded.value,
            headerBackground = Color.White,
            headerBorder = BorderStroke(0.dp, Color.Transparent),
            headerMinHeight = 50.dp,
            header = {
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    elevation = 1.dp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .height(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp)
                    ) {
                        Row(modifier = Modifier.weight(0.9f)) {
                            Space(4.dp)
                            Checkbox(
                                checked = selectedIndex == index,
                                colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                                onCheckedChange = { sellerScope.updateIndex(index) },
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                            Space(12.dp)
                            Text(
                                text = store.tradeName,
                                color = ConstColors.lightBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 2,
                            )
                            Space(12.dp)
                        }
                        Icon(
                            imageVector = if (expanded.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }

            },
            childItems = items,
            hasItemLeadingSpacing = false,
            hasItemTrailingSpacing = false,
            itemSpacing = 0.dp,
            itemHorizontalPadding = 0.dp,
            itemsBackground = Color.Transparent,
            item = { value, _ ->
                IocItem(
                    item = value
                )
            }
        )
        Space(dp = 4.dp)
    }
}

@ExperimentalComposeUiApi
@Composable
fun IocItem(
    item: RetailerData,
) {

    Surface(
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        elevation = 1.dp,
        color = Color.White,
        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = buildAnnotatedString {
                    append(item.cityOrTown)
                    append(", ")
                    append(item.pincode)
                },
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W800,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Space(4.dp)

            Text(
                text = item.gstin,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
            )

            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.dl_one_))
                    val startIndex = length
                    append(item.drugLicenseNo1)
                    addStyle(
                        SpanStyle(
                            color = ConstColors.txtGrey,
                            fontWeight = FontWeight.W500
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.txtGrey,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )

            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.dl_two_))
                    val startIndex = length
                    append(item.drugLicenseNo2)
                    addStyle(
                        SpanStyle(
                            color = ConstColors.txtGrey,
                            fontWeight = FontWeight.W500
                        ),
                        startIndex,
                        length,
                    )
                },
                color = ConstColors.txtGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500
            )

            Space(4.dp)

            Text(
                text = item.paymentMethod,
                fontSize = 12.sp,
                color = ConstColors.lightBlue,
            )
            Space(4.dp)
        }
    }
}


@SuppressLint("SimpleDateFormat")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IocCreate(sellerScope: IocSellerScope.IOCCreate, scaffoldState: ScaffoldState) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val permissionViewModel = PermissionViewModel()
    val context = LocalContext.current
    var tradeCheck = false
    val invoiceUpload = sellerScope.invoiceUpload.flow.collectAsState()
    val invoiceNum = sellerScope.invoiceNum.flow.collectAsState()
    val invoiceDate = sellerScope.invoiceDate.flow.collectAsState()
    val totalAmount = sellerScope.totalAmount.flow.collectAsState()
    val outstandingAmount = sellerScope.outstandingAmount.flow.collectAsState()
    val outstandingDiffAmount = sellerScope.outstandingDiffAmount.flow.collectAsState()
    val enable = sellerScope.enableButton.flow.collectAsState()
    val openDialog = sellerScope.showAlert.flow.collectAsState()
    val dialogMessage = sellerScope.dialogMessage.flow.collectAsState()
    if (invoiceUpload.value.cdnUrl.isNotEmpty()) {
        tradeCheck = true
    }

    PermissionCheckUIForInvoice(scaffoldState, permissionViewModel)
    if (openDialog.value)
        ShowAlert(
            if (dialogMessage.value.isNotEmpty())
                dialogMessage.value
            else stringResource(id = R.string.offer_successfull)
        ) {
            sellerScope.changeAlertScope(false)
            sellerScope.goBack()
        }
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Space(16.dp)
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_invoice_no),
            text = invoiceNum.value,
            isValid = true,
            onValueChange = { sellerScope.updateInvoiceNum(it) },
            mandatory = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            })
        )
        Space(16.dp)

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                val now = DateTime.now()
                val dialog = DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        formatter.isLenient = false
                        val oldTime = "$day/${month + 1}/${year}"
                        val oldDate: Date? = formatter.parse(oldTime)
                        val oldMillis: Long? = oldDate?.time
                        sellerScope.updateInvoiceDate(oldTime, oldMillis ?: 0)
                    },
                    now.year,
                    now.monthOfYear - 1,
                    now.dayOfMonth,
                )
                dialog.show()
            }) {

            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.enter_invoice_date))
                    val startIndex = length
                    append(" *")
                    addStyle(
                        SpanStyle(
                            color = ConstColors.red,
                            fontWeight = FontWeight.Normal
                        ),
                        startIndex,
                        length,
                    )
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ConstColors.inputFieldColor,
            )
            if (invoiceDate.value.isNotEmpty()) {
                Text(
                    text = invoiceDate.value,
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Space(dp = 12.dp)
            } else {
                Space(dp = 16.dp)
            }
        }
        Divider(
            color = ConstColors.inputFieldColor,
            thickness = 1.dp,
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
                sellerScope.updateTotalAmount(total.value)
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
        val outStand = remember {
            mutableStateOf(
                if (totalAmount.value.split(".")
                        .lastOrNull() == ""
                ) totalAmount.value.split(".")
                    .first() else totalAmount.value
            )
        }
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_outstanding_amount),
            text = outstandingAmount.value,
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
                outStand.value = "$modBefore$modAfter"
                sellerScope.updateOutstandingAmount(outStand.value)
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
        Space(8.dp)
        if (!enable.value && totalAmount.value.isNotEmpty()
            && outstandingAmount.value.isNotEmpty()
            && totalAmount.value.toDouble() < outstandingAmount.value.toDouble()
        ) {
            Text(
                text = stringResource(id = R.string.invoice_amount_validation),
                color = ConstColors.red,
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Space(12.dp)

        if (outstandingDiffAmount.value.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.outstanding_amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ConstColors.inputFieldColor,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = outstandingDiffAmount.value,
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Space(dp = 12.dp)

                Divider(
                    color = ConstColors.inputFieldColor,
                    thickness = 1.dp,
                )
            }
        }
        Space(16.dp)

        Surface(
            onClick = {
                permissionViewModel.setPerformLocationAction(true, "IOC_IMAGE")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = MaterialTheme.shapes.large,
            color = Color.White,
            border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_img_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                )
                Space(dp = 4.dp)
                Text(
                    text = if (tradeCheck) stringResource(id = R.string.file_uploaded_successfully)
                    else stringResource(id = R.string.upload_invoice),
                    color = if (tradeCheck) MaterialTheme.colors.background else ConstColors.gray,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = if (tradeCheck) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        if (invoiceUpload.value.cdnUrl.isNotEmpty()) {
            Space(16.dp)
            val url = invoiceUpload.value.cdnUrl
            ImageLabel(
                url, true
            ) { sellerScope.previewImage(url) }
        }

        Space(16.dp)
        MedicoButton(
            text = stringResource(id = R.string.submit),
            isEnabled = enable.value,
            elevation = null,
            onClick = { sellerScope.addInvoice() },
            contentColor = MaterialTheme.colors.background,
            txtColor = MaterialTheme.colors.background,
        )
    }
}




