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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> FoldableItem(
    expanded: Boolean,
    header: @Composable RowScope.(Boolean) -> Unit,
    childItems: List<T>,
    item: @Composable ColumnScope.(T) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(expanded) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color.LightGray.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            ) {
                header(isExpanded.value)
            }
            AnimatedVisibility(isExpanded.value) {
                Column(
                    modifier = Modifier.background(Color.White).padding(horizontal = 8.dp)
                ) {
                    Space(12.dp)
                    childItems.forEach {
                        item(it)
                        Space(12.dp)
                    }
                }
            }
        }
    }
}