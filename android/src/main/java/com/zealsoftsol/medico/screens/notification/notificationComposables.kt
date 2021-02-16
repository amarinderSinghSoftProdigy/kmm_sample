package com.zealsoftsol.medico.screens.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationStatus
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarBox
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import java.util.concurrent.TimeUnit

@Composable
fun NotificationScreen(scope: NotificationScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        Space(16.dp)
        when (scope) {
            is NotificationScope.All -> AllNotifications(scope)
        }
    }
}

@Composable
private fun AllNotifications(scope: NotificationScope.All) {
    val search = scope.searchText.flow.collectAsState()
    val showSearchOverlay = remember { mutableStateOf(true) }
    if (showSearchOverlay.value) {
        SearchBarBox(
            rowModifier = Modifier.clickable(indication = null) { showSearchOverlay.value = false },
            elevation = 0.dp,
            horizontalPadding = 16.dp,
        ) {
            Icon(
                imageVector = vectorResource(id = R.drawable.ic_bell),
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
            horizontalPadding = 16.dp,
            isSearchFocused = true,
            onSearch = { scope.search(it) },
            onIconClick = { showSearchOverlay.value = true },
        )
    }
    Space(16.dp)
    val items = scope.items.flow.collectAsState()
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
            WithConstraints {
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
            MedicoSmallButton(
                text = stringResourceByName(
                    name = item.selectedAction?.completedActionStringId ?: item.type.buttonStringId
                ),
                enabledColor = if (item.selectedAction != null) Color.Transparent else MaterialTheme.colors.secondary,
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClick,
            )
        }
    }
}