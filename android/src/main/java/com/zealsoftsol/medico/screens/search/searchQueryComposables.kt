package com.zealsoftsol.medico.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.screens.Separator
import com.zealsoftsol.medico.screens.TabBar

@Composable
fun SearchQueryScreen(scope: SearchScope.Query) {
    Column(modifier = Modifier.fillMaxSize()) {
        val input = scope.searchInput.flow.collectAsState()
        TabBar {
            BasicSearchBar(
                input = input.value,
                icon = Icons.Default.ArrowBack,
                searchBarEnd = SearchBarEnd.Filter { "filter" },
                onIconClick = { scope.goBack() },
                onSearch = { scope.searchInput(it) }
            )
        }
        Text(
            text = stringResource(id = R.string.clear_all)
        )


    }
}

@Composable
private fun FilterSection(
    name: String,

    ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Separator()
        Row {
            Text(
                text = name,
            )
            Text(
                text = ""
            )
        }
    }

}

@Composable
fun BasicSearchBar(
    input: String,
    searchBarEnd: SearchBarEnd,
    icon: VectorAsset = Icons.Default.Search,
    onIconClick: (() -> Unit)? = null,
    onSearch: (String) -> Unit
) {
    SearchBarBox {
        Icon(
            asset = icon,
            tint = ConstColors.gray,
            modifier = Modifier.size(24.dp)
                .run { if (onIconClick != null) clickable(onClick = onIconClick) else this },
        )
        Box(
            modifier = Modifier.padding(start = 24.dp).fillMaxWidth(),
            alignment = Alignment.CenterStart
        ) {
            if (input.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            BasicTextField(
                value = input,
                cursorColor = ConstColors.lightBlue,
                onValueChange = onSearch,
            )
            val modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
            when (searchBarEnd) {
                is SearchBarEnd.Eraser -> {
                    if (input.isNotEmpty()) {
                        Icon(
                            asset = Icons.Default.Close,
                            tint = ConstColors.gray,
                            modifier = modifier.clickable(onClick = { onSearch("") })
                        )
                    }
                }
                is SearchBarEnd.Filter -> {
                    Icon(
                        asset = vectorResource(id = R.drawable.ic_filter),
                        tint = ConstColors.gray,
                        modifier = modifier.clickable(onClick = { searchBarEnd.onClick() })
                    )
                }
            }
        }
    }
}

sealed class SearchBarEnd {
    object Eraser : SearchBarEnd()
    data class Filter(val onClick: () -> Unit) : SearchBarEnd()
}

@Composable
private fun SearchBarBox(body: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp)
            .background(Color.White, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        body()
    }
}