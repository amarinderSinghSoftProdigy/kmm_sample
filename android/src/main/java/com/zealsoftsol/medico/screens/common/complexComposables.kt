package com.zealsoftsol.medico.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> FoldableItem(
    expanded: Boolean,
    headerBackground: Color = Color.LightGray.copy(alpha = 0.2f),
    headerBorder: BorderStroke? = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
    headerMinHeight: Dp = 50.dp,
    header: @Composable RowScope.(Boolean) -> Unit,
    childItems: List<T>,
    itemHorizontalPadding: Dp = 8.dp,
    hasItemLeadingSpacing: Boolean = true,
    hasItemTrailingSpacing: Boolean = true,
    itemsBackground: Color = Color.White,
    itemSpacing: Dp = 12.dp,
    item: @Composable ColumnScope.(T, Int) -> Unit,
    elevation: Dp = 0.dp
) {
    val isExpanded = remember { mutableStateOf(expanded) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = headerBorder,
        elevation = elevation
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(headerBackground)
                    .fillMaxWidth()
                    .heightIn(min = headerMinHeight)
//                    .padding(vertical = 8.dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            ) {
                header(isExpanded.value)
            }
            AnimatedVisibility(isExpanded.value) {
                Column(
                    modifier = Modifier
                        .background(itemsBackground)
                        .padding(horizontal = itemHorizontalPadding)
                ) {
                    if (hasItemLeadingSpacing) Space(itemSpacing)
                    childItems.forEachIndexed { index, value ->
                        item(value, index)
                        if (index < childItems.lastIndex || hasItemTrailingSpacing) Space(
                            itemSpacing
                        )
                    }
                }
            }
        }
    }
}