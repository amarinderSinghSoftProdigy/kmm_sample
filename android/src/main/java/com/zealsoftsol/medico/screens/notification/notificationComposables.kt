package com.zealsoftsol.medico.screens.notification

import androidx.compose.foundation.InteractionState
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.NotificationStatus
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.management.GeoLocation
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.concurrent.TimeUnit

@Composable
fun NotificationScreen(scope: NotificationScope, listState: LazyListState) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Space(16.dp)
        when (scope) {
            is NotificationScope.All -> AllNotifications(scope, listState)
            is NotificationScope.Preview<*, *> -> PreviewNotifications(scope)
        }
    }
}

@Composable
private fun AllNotifications(scope: NotificationScope.All, listState: LazyListState) {
    val search = scope.searchText.flow.collectAsState()
    val showSearchOverlay = remember { mutableStateOf(true) }
    if (showSearchOverlay.value) {
        SearchBarBox(
            modifier = Modifier.clickable(
                indication = null,
                interactionState = remember { InteractionState() }) {
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
                text = stringResource(id = R.string.notifications),
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
            searchBarEnd = SearchBarEnd.Eraser,
            icon = Icons.Default.ArrowBack,
            elevation = 0.dp,
            horizontalPadding = 0.dp,
            isSearchFocused = true,
            onSearch = { scope.search(it) },
            onIconClick = { showSearchOverlay.value = true },
        )
    }
    Space(16.dp)
    val items = scope.items.flow.collectAsState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            items = items.value,
            itemContent = { index, item ->
                NotificationItem(item) { scope.selectItem(item) }
                if (index == items.value.lastIndex && scope.pagination.canLoadMore()) {
                    scope.loadItems()
                }
            },
        )
    }
}

@Composable
private fun NotificationItem(item: NotificationData, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
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
                    Text(
                        text = stringResourceByName(name = item.status.stringId),
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.background.copy(alpha = if (item.status == NotificationStatus.READ) 0.6f else 1f),
                    )
                }
            }
            Text(
                text = "${TimeUnit.HOURS.convert(Time.now - item.sentAt, TimeUnit.MILLISECONDS)}h",
                fontSize = 14.sp,
                color = ConstColors.gray,
                modifier = Modifier.align(Alignment.TopEnd),
            )
            if (item.actions.isNotEmpty()) {
                MedicoSmallButton(
                    text = stringResourceByName(
                        name = item.selectedAction?.completedActionStringId
                            ?: item.type.buttonStringId
                    ),
                    enabledColor = if (item.selectedAction != null) Color.Transparent else MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = if (item.selectedAction == null) {
                        onClick
                    } else {
                        {}
                    },
                )
            }
        }
    }
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
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            when (scope) {
                is NotificationScope.Preview.SubscriptionRequest -> {
                    val details = scope.details.flow.collectAsState()
                    details.value?.let { d ->
                        SubscriptionDeatails(d) { scope.changeOptions(it) }
                    }
                }
            }
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

@Composable
private fun SubscriptionDeatails(
    details: NotificationDetails.TypeSafe.Subscription,
    onOptionChange: (NotificationOption.Subscription) -> Unit,
) {
    val isSeasonBoy = details.customerData.customerType == UserType.SEASON_BOY.serverValue

    if (!isSeasonBoy) {
        Row(modifier = Modifier.fillMaxWidth().height(123.dp)) {
            CoilImage(
                modifier = Modifier.size(123.dp),
                data = "",
                contentDescription = null,
                error = { ItemPlaceholder() },
                loading = { ItemPlaceholder() },
            )
            Space(24.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                GeoLocation(details.customerData.customerAddressData.location, isBold = true)
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
                            details.customerData.customerAddressData.latitude,
                            details.customerData.customerAddressData.longitude,
                        )
                    },
                )
            }
        }
        Space(24.dp)
        DataWithLabel(R.string.phone_number, details.customerData.phoneNumber)
        DataWithLabel(R.string.gstin_num, details.customerData.gstin.orEmpty())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DataWithLabel(R.string.payment_method, "")
            Surface(
                modifier = Modifier.border(
                    1.dp,
                    ConstColors.gray.copy(alpha = ContentAlpha.medium),
                    RoundedCornerShape(4.dp)
                )
            ) {
                Dropdown(
                    modifier = Modifier.width(100.dp),
                    rememberChooseKey = this,
                    value = details.option.paymentMethod.serverValue,
                    dropDownItems = PaymentMethod.values().map { it.serverValue },
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
        if (details.option.paymentMethod == PaymentMethod.CREDIT) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DataWithLabel(R.string.credit_days, "")
                BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
                OutlinedTextField(
                    value = details.option.creditDays,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    activeColor = ConstColors.lightBlue,
                    inactiveColor = ConstColors.gray,
                    maxLines = 1,
                    onValueChange = { onOptionChange(details.option.copy(creditDays = it)) },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DataWithLabel(R.string.discount_rate, "")
            BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
            OutlinedTextField(
                value = details.option.discountRate,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                activeColor = ConstColors.lightBlue,
                inactiveColor = ConstColors.gray,
                maxLines = 1,
                onValueChange = { onOptionChange(details.option.copy(discountRate = it)) },
            )
        }
    } else {
        DataWithLabel(R.string.email, details.customerData.email)
        DataWithLabel(R.string.address, details.customerData.customerAddressData.address)
        DataWithLabel(R.string.phone_number, details.customerData.phoneNumber)
    }
}