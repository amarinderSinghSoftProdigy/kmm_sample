package com.zealsoftsol.medico.screens.ioc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.RetailerData
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.utils.PermissionCheckUIForInvoice
import com.zealsoftsol.medico.utils.PermissionViewModel

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IocScreen(scope: IocScope, scaffoldState: ScaffoldState) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is IocScope.IOCListing -> IOCListing(scope)
            is IocScope.IOCCreate -> IocCreate(scope, scaffoldState)
        }
    }
}

/**
 * items to be displayed in autocomplete dropdown list
 */
@Composable
private fun AutoCompleteItem(autoComplete: AutoComplete, input: String, onClick: () -> Unit) {
    val regex = "(?i)$input".toRegex()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 24.dp),
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
                        fontSize = 15.sp,
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
            Icon(
                imageVector = Icons.Default.ArrowForward,
                tint = ConstColors.lightBlue,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color(0xFFE6F0F7),
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IOCListing(scope: IocScope.IOCListing) {
    val search = scope.searchText.flow.collectAsState()
    val selectedIndex = scope.selectedIndex.flow.collectAsState()
    val items = scope.items.flow.collectAsState()

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
                onSearch = { v, _ -> scope.search(v) },
                onIconClick = {
                    scope.search("")
                },
                backgroundColor = ConstColors.lightBlue.copy(alpha = 0.1f)
            )


            if (items.value.isEmpty() && scope.items.updateCount > 0) {
                NoRecords(
                    icon = R.drawable.ic_missing_stores,
                    text = R.string.no_users_found,
                    subtitle = "",
                    buttonText = stringResource(id = R.string.clear),
                    onHome = { scope.search("") },
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
                            StoreItem(
                                item,
                                scope, index, selectedIndex.value
                            )
                            if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.loadItems()
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
                .background(color = ConstColors.lightBlue.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                MedicoRoundButton(
                    /*modifier = Modifier.width(150.dp),*/
                    text = stringResource(id = R.string.continue_),
                    isEnabled = selectedIndex.value != -1,
                    elevation = null,
                    onClick = { scope.selectItem(items.value[selectedIndex.value].tradeName) },
                    color = ConstColors.lightBlue,
                    contentColor = Color.White,
                    wrapTextSize = true,
                )
            }
        }
    }

}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun StoreItem(
    store: RetailerData,
    scope: IocScope.IOCListing,
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
            header = { _ ->
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    elevation = 5.dp,
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
                                onCheckedChange = { scope.updateIndex(index) },
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
                    item = value,
                    scope = scope,
                )
            }
        )
        Space(dp = 4.dp)
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IocItem(
    item: RetailerData,
    scope: IocScope.IOCListing,
) {

    Surface(
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        elevation = 5.dp,
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
            /*Text(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.status))
                append(": ")
                val startIndex = length
                append(item.status)
                addStyle(
                    SpanStyle(
                        color = ConstColors.lightGreen,
                        fontWeight = FontWeight.W700
                    ),
                    startIndex,
                    length,
                )
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colors.background,
        )*/
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun IocCreate(scope: IocScope.IOCCreate, scaffoldState: ScaffoldState) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val permissionViewModel = PermissionViewModel()
    val tradeCheck = false
    PermissionCheckUIForInvoice(scaffoldState, permissionViewModel)

    Column(modifier = Modifier.padding(all = 16.dp)) {
        Space(16.dp)
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_invoice_no),
            text = "",
            isValid = true,
            onValueChange = { },
            mandatory = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            })
        )
        Space(12.dp)
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_invoice_date),
            text = "",
            isValid = true,
            onValueChange = { },
            mandatory = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            })
        )
        Space(12.dp)
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_total_amount),
            text = "",
            isValid = true,
            onValueChange = { },
            mandatory = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            })
        )
        Space(12.dp)
        InputField(
            modifier = Modifier
                .align(Alignment.Start)
                .scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.enter_outstanding_amount),
            text = "",
            isValid = true,
            onValueChange = { },
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

        Surface(
            onClick = {
                permissionViewModel.setPerformLocationAction(true, "")
                //scope.showBottomSheet("TRADE_PROFILE", scope.registrationStep1)
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

        Space(16.dp)
        MedicoButton(
            text = stringResource(id = R.string.submit),
            isEnabled = true,
            elevation = null,
            onClick = { },
            color = ConstColors.lightBlue,
            contentColor = Color.White,
            txtColor = Color.White,
        )
    }
}




