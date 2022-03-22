package com.zealsoftsol.medico.screens.ioc

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IocListingScreen(scope: IocScope, scaffoldState: ScaffoldState) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is IocScope.InvListing -> InvListing(scope)
            is IocScope.InvDetails -> InvDetails(scope)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun InvDetails(scope: IocScope.InvDetails) {
    val items = scope.items.flow.collectAsState()

    Column{
        Space(4.dp)
        if (items.value.isEmpty() && scope.items.updateCount > 0) {
            NoRecords(
                icon = R.drawable.ic_missing_stores,
                text = R.string.no_users_found,
                subtitle = "",
                buttonText = stringResource(id = R.string.clear),
                onHome = {  },
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
                        IivoiceListItem(
                            item,
                        ) {

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
private fun InvListing(scope: IocScope.InvListing) {
    val search = scope.searchText.flow.collectAsState()
    val items = scope.items.flow.collectAsState()
    val showSearchBar = remember { mutableStateOf(false) }
    var queryTextChangedJob: Job? = null
    val keyboardController = LocalSoftwareKeyboardController.current

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
                            if (showSearchBar.value) {
                                showSearchBar.value = false
                            } else {
                                scope.goBack()
                            }
                        }
                    )
            )
            if (showSearchBar.value) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .padding(end = 40.dp),
                    shape = RoundedCornerShape(3.dp),
                    elevation = 5.dp
                ) {
                    TextField(
                        modifier = Modifier.height(40.dp),
                        value = search.value,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            textColor = Color.Black,
                            placeholderColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        onValueChange = {
                            queryTextChangedJob?.cancel()
                            queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                //scope.startSearch(it)
                            }
                        },
                        placeholder = {
                            Text(
                                stringResource(id = R.string.search),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        maxLines = 1
                    )
                }
            }

            if (!showSearchBar.value) {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = Color.Transparent,
                        onClick = {
                            showSearchBar.value = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = ConstColors.gray,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(2.dp),
                        )

                    }
                }
            }
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
                    .padding(horizontal = 12.dp),
            ) {
                itemsIndexed(
                    items = items.value,
                    itemContent = { index, item ->
                        IocListItem(
                            item,
                        ) {
                            scope.openIOCDetails("Retailer 301")
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
    item: String,
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
                text = "Retailer 301",
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W800,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Space(4.dp)

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Paid `209999.00",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.lightGreen,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                )
                Text(
                    text = "Out.Amt `109999.00",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.marron,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
            }

            Space(4.dp)

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "10 Invoices",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.txtGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = "RETAILER",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 14.sp,
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            }
            Space(4.dp)
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun IivoiceListItem(
    item: String,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(5.dp),
        elevation = 1.dp,
        color = Color.White,
        modifier = Modifier.padding(all = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "245346626",
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W800,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Space(4.dp)

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "09-02-22",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.lightGreen,
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

            Divider(thickness = 0.5.dp)
            Space(4.dp)

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "10 Invoices",
                    modifier = Modifier.weight(0.5f),
                    color = ConstColors.txtGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = "RETAILER",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 14.sp,
                    color = ConstColors.txtGrey,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            }
            Space(4.dp)
        }
    }
}

