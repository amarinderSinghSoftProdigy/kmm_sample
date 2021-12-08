package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreUsersScope
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd

@Composable
fun InStoreUsersScreen(scope: InStoreUsersScope) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ConstColors.newDesignGray),
    ) {
        val selectedUser = remember { mutableStateOf<InStoreUser?>(null) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            val search = scope.searchText.flow.collectAsState()
            BasicSearchBar(
                input = search.value,
                hint = R.string.search_tradename,
                searchBarEnd = SearchBarEnd.Eraser,
                icon = Icons.Default.Search,
                elevation = 0.dp,
                horizontalPadding = 16.dp,
                isSearchFocused = false,
                onSearch = { v, _ -> scope.search(v) },
            )
            val items = scope.items.flow.collectAsState()
            if (items.value.isEmpty() && scope.items.updateCount > 0) {
//            NoRecords(
//                icon = R.drawable.ic_missing_invoices,
//                text = R.string.missing_invoices,
//                onHome = { scope.goHome() },
//            )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(top = 16.dp),
                ) {
                    itemsIndexed(
                        items = items.value,
                        itemContent = { index, item ->
                            InStoreUserItem(
                                item,
                                item.mobileNumber == selectedUser.value?.mobileNumber
                            ) { selectedUser.value = item }
                            if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                                scope.loadItems()
                            }
                        },
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            MedicoRoundButton(
                modifier = Modifier.weight(.6f),
                text = stringResource(id = R.string.new_customer_plus),
                isEnabled = true,
                height = 48.dp,
                color = Color.Transparent,
                contentColor = ConstColors.lightBlue,
                border = BorderStroke(2.dp, ConstColors.lightBlue)
            ) { scope.goToInStoreCreateUser() }
            Space(dp = 12.dp)
            MedicoRoundButton(
                modifier = Modifier.weight(.4f),
                text = stringResource(id = R.string.continue_),
                isEnabled = selectedUser.value != null,
                height = 48.dp,
                color = ConstColors.lightBlue,
                contentColor = Color.White,
            ) { selectedUser.value?.let { scope.selectItem(it) } }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun InStoreUserItem(item: InStoreUser, isSelected: Boolean, onClick: () -> Unit) {
    val isExpandedOut = remember { mutableStateOf(false) }
    FoldableItem(
        expanded = isSelected,
        headerBackground = if (isExpandedOut.value) Color.White else ConstColors.newDesignGray,
        headerBorder = null,
        header = { isExpanded ->
            isExpandedOut.value = isExpanded
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isSelected,
                        enabled = true,
                        onClick = onClick,
                        colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
                    )
                    Space(12.dp)
                    Text(
                        text = item.tradeName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        childItems = listOf(Unit),
        itemHorizontalPadding = 54.dp,
        item = { _, _ ->
            Text(
                text = item.addressData.fullAddress(),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
            )
            Space(8.dp)
            Text(
                text = item.gstin,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
            )
            Space(8.dp)
            Text(
                text = "DL1: 20B: ${item.drugLicenseNo1}",
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
            Space(8.dp)
            Text(
                text = "DL2: 21B: ${item.drugLicenseNo2}",
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
            Space(8.dp)
            Text(
                text = buildAnnotatedString {
                    append("Status: ")
                    val startIndex = length
                    append(item.status)
                    addStyle(
                        SpanStyle(color = ConstColors.green),
                        startIndex,
                        length,
                    )
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .background(
                        ConstColors.green.copy(alpha = 0.05f),
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        ConstColors.green,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    )
}