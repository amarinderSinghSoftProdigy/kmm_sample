package com.zealsoftsol.medico.screens.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.NotificationStatus
import com.zealsoftsol.medico.data.NotificationType
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.Chip
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.management.GeoLocation
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

@ExperimentalMaterialApi
@Composable
fun NotificationScreen(scope: NotificationScope, listState: LazyListState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Space(16.dp)
        when (scope) {
            is NotificationScope.All -> AllNotifications(scope, listState)
            is NotificationScope.Preview<*, *> -> PreviewNotifications(scope)
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AllNotifications(scope: NotificationScope.All, listState: LazyListState) {
    val search = scope.searchText.flow.collectAsState()
    val showSearchOverlay = remember { mutableStateOf(true) }
    val filter = scope.filter.flow.collectAsState()
    if (showSearchOverlay.value) {
        SearchBarBox(
            modifier = Modifier.clickable(indication = null) {
                showSearchOverlay.value = false
            },
            elevation = 0.dp,
            horizontalPadding = 0.dp,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = null,
                tint = ConstColors.lightBlue,
                modifier = Modifier.size(24.dp),
            )
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.search_notifications),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colors.background,
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    } else {
        BasicSearchBar(
            input = search.value,
            hint = R.string.search_notifications,
            searchBarEnd = SearchBarEnd.Eraser,
            icon = Icons.Default.ArrowBack,
            elevation = 0.dp,
            horizontalPadding = 0.dp,
            isSearchFocused = true,
            onSearch = { v, _ -> scope.search(v) },
            onIconClick = {
                scope.search("")
                showSearchOverlay.value = true
            },
        )
    }
    Space(12.dp)
    Row {
        scope.allFilters.forEach {
            Chip(
                text = stringResourceByName(it.stringId),
                isSelected = filter.value == it,
                onClick = { scope.selectFilter(it) }
            )
        }
    }
    Space(12.dp)
    val items = scope.items.flow.collectAsState()
    if (items.value.isEmpty() && scope.items.updateCount > 0) {
        NoRecords(
            icon = R.drawable.ic_missing_notification,
            text = R.string.missing_notifications,
            onHome = { scope.goHome() },
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = items.value,
                itemContent = { index, item ->
                    NotificationItem(item, { scope.selectItem(item) },
                        { scope.deleteNotification(item.id) })
                    if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                        scope.loadItems()
                    }
                },
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun NotificationItem(
    item: NotificationData,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            BoxWithConstraints {
                Column(modifier = Modifier.width(maxWidth * 3 / 4)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        color = ConstColors.lightBlue,
                    )
                    Space(4.dp)
                    Text(
                        text = item.body,
                        fontSize = 14.sp,
                        color = ConstColors.gray,
                    )
                    Space(16.dp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResourceByName(name = item.status.stringId),
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.background.copy(alpha = if (item.status == NotificationStatus.READ) 0.6f else 1f),
                        )
                        Space(8.dp)
                        Divider(
                            modifier = Modifier
                                .height(20.dp)
                                .width(1.dp)
                        )
                        Space(8.dp)
                        Text(
                            text = Duration(Time.now - item.sentAt).format(),
                            fontSize = 14.sp,
                            color = ConstColors.gray,
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                color = Color.White,
                onClick = onDeleteClick,
                indication = null
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cross),
                        modifier = Modifier
                            .size(20.dp),
                        contentDescription = ""
                    )
                }
            }
            MedicoSmallButton(
                text = stringResourceByName(
                    name = item.selectedAction?.completedActionStringId
                        ?: item.type.buttonStringId
                ),
                enabledColor = if (item.selectedAction != null)
                    Color.Transparent
                else when (item.type) {
                    NotificationType.SUBSCRIBE_REQUEST, NotificationType.SUBSCRIBE_DECISION -> ConstColors.yellow
                    NotificationType.ORDER_REQUEST, NotificationType.INVOICE_REQUEST -> ConstColors.lightBlue
                },
                contentColor = when (item.selectedAction) {
                    NotificationAction.ACCEPT -> ConstColors.green
                    NotificationAction.DECLINE -> ConstColors.red
                    else -> when (item.type) {
                        NotificationType.SUBSCRIBE_REQUEST, NotificationType.SUBSCRIBE_DECISION -> MaterialTheme.colors.onPrimary
                        NotificationType.ORDER_REQUEST, NotificationType.INVOICE_REQUEST -> Color.White
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClick,
            )
        }
    }
}

private fun Duration.format(): String {
    val formatter: PeriodFormatter = PeriodFormatterBuilder()
        .appendMonths()
        .appendSuffix("m ")
        .appendDays()
        .appendSuffix("d ")
        .appendHours()
        .appendSuffix("h")
        .toFormatter()

    val period: Period = toPeriod()
    val dayTimePeriod: Period = period.normalizedStandard(PeriodType.dayTime())
    return formatter.print(dayTimePeriod)
}

@Composable
private fun PreviewNotifications(scope: NotificationScope.Preview<*, *>) {
    Text(
        text = scope.notification.title,
        color = MaterialTheme.colors.background,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
    )
    Space(12.dp)
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (scope) {
                is NotificationScope.Preview.SubscriptionRequest -> {
                    val details = scope.details.flow.collectAsState()
                    details.value?.let { d ->
                        SubscriptionDeatails(d) { scope.changeOptions(it) }
                    }
                }
            }
            if (scope.notification.actions.isNotEmpty()) {
                Space(40.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    scope.notification.actions.forEach {
                        MedicoSmallButton(
                            modifier = Modifier.weight(1f),
                            widthModifier = { fillMaxWidth() },
                            text = stringResourceByName(name = it.actionStringId),
                            enabledColor = if (it.isHighlighted) ConstColors.yellow else MaterialTheme.colors.secondary,
                            onClick = { scope.selectAction(it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionDeatails(
    details: NotificationDetails.TypeSafe.Subscription,
    onOptionChange: (NotificationOption.Subscription) -> Unit,
) {
    val isSeasonBoy = details.customerData.customerType == UserType.SEASON_BOY.serverValue

    if (!isSeasonBoy) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            CoilImage(
                src = "",
                size = 100.dp,
                onError = { UserLogoPlaceholder(details.customerData.run { ""/*"$firstName $lastName"*/ }) },
                onLoading = { UserLogoPlaceholder(details.customerData.run { ""/*"$firstName $lastName"*/ }) },
            )
            Space(24.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                //GeoLocation(details.customerData.addressData.location, isBold = true)
//                Text(
//                    text = previewItem.geoData.distance,
//                    fontSize = 12.sp,
//                    color = ConstColors.gray,
//                )
                val activity = LocalContext.current as MainActivity
                Text(
                    text = stringResource(id = R.string.see_on_the_map),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ConstColors.lightBlue,
                    modifier = Modifier.clickable {
                        activity.openMaps(
                            details.customerData.latitude,
                            details.customerData.longitude,
                        )
                    },
                )
            }
        }
        Space(24.dp)
        //DataWithLabel(R.string.phone_number, details.customerData.phoneNumber)
        // DataWithLabel(R.string.gstin_num, details.customerData.gstin.orEmpty())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DataWithLabel(R.string.payment_method, "")
            if (details.isReadOnly) {
                Text(
                    text = details.option.paymentMethod.serverValue,
                    color = MaterialTheme.colors.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                )
            } else {
                Surface(
                    modifier = Modifier.border(
                        1.dp,
                        ConstColors.gray.copy(alpha = ContentAlpha.medium),
                        RoundedCornerShape(4.dp)
                    )
                ) {
                    Dropdown(
                        rememberChooseKey = this,
                        value = details.option.paymentMethod.serverValue,
                        hint = "",
                        dropDownItems = if (details.isReadOnly) emptyList() else PaymentMethod.values()
                            .map { it.serverValue },
                        readOnly = details.isReadOnly,
                        width = 100.dp,
                        onSelected = {
                            val method = when (it) {
                                PaymentMethod.CREDIT.serverValue -> PaymentMethod.CREDIT
                                PaymentMethod.CASH.serverValue -> PaymentMethod.CASH
                                else -> throw UnsupportedOperationException("unknown payment method")
                            }
                            onOptionChange(details.option.copy(paymentMethod = method))
                        }
                    )
                }
            }
        }
        if (details.option.paymentMethod == PaymentMethod.CREDIT) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DataWithLabel(R.string.credit_days, "")
                if (details.isReadOnly) {
                    Text(
                        text = details.option.creditDays,
                        color = MaterialTheme.colors.background,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                    )
                } else {
                    BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
                    OutlinedTextField(
                        value = details.option.creditDays,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = ConstColors.lightBlue,
                            unfocusedBorderColor = ConstColors.gray.copy(if (details.isReadOnly) 0.5f else 1f),
                        ),
                        maxLines = 1,
                        readOnly = details.isReadOnly,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onOptionChange(details.option.copy(creditDays = it))
                            }
                        },
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DataWithLabel(R.string.discount_rate, "")
            if (details.isReadOnly) {
                Text(
                    text = details.option.discountRate,
                    color = MaterialTheme.colors.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                )
            } else {
                BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
                OutlinedTextField(
                    value = details.option.discountRate,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ConstColors.lightBlue,
                        unfocusedBorderColor = ConstColors.gray.copy(if (details.isReadOnly) 0.5f else 1f),
                    ),
                    maxLines = 1,
                    readOnly = details.isReadOnly,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            onOptionChange(details.option.copy(discountRate = it))
                        }
                    },
                )
            }
        }
    } else {
        //DataWithLabel(R.string.email, details.customerData.email)
        //DataWithLabel(R.string.address, details.customerData.addressData.address)
        //DataWithLabel(R.string.phone_number, details.customerData.phoneNumber)
    }
}