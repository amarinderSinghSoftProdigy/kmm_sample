package com.zealsoftsol.medico.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.screens.cart.OrderTotal
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun ConfirmOrderScreen(scope: ConfirmOrderScope) {
    val order = scope.order.flow.collectAsState()
    val activeTab = scope.activeTab.flow.collectAsState()
    val entries = scope.entries.flow.collectAsState()
    val checkedEntries = scope.checkedEntries.flow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(
                rememberScrollState()
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Space(20.dp)
            Text(
                text = stringResource(id = R.string.action_confirmation),
                color = MaterialTheme.colors.background,
                fontSize = 20.sp,
                fontWeight = FontWeight.W700,
            )
            Space(15.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(41.dp)
                    .background(MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
            ) {
                scope.tabs.forEach {
                    var boxMod = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    boxMod = if (scope.tabs.size == 1) {
                        boxMod
                    } else {
                        boxMod
                            .padding(5.dp)
                            .clickable { scope.selectTab(it) }
                    }
                    val isActive = activeTab.value == it
                    boxMod = if (isActive) {
                        boxMod.background(
                            Color(activeTab.value.bgColorHex.toColorInt()),
                            MaterialTheme.shapes.medium
                        )
                    } else {
                        boxMod
                    }
                    Row(
                        modifier = boxMod,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResourceByName(it.stringId),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = if (isActive) Color.White else MaterialTheme.colors.background,
                        )
                        if (isActive && entries.value.isNotEmpty()) {
                            Space(6.dp)
                            Text(
                                text = entries.value.size.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700,
                                color = ConstColors.yellow,
                            )
                        }
                    }
                }
            }
            Column {
                Space(8.dp)
                entries.value.forEach {
                    OrderEntryItem(
                        canEdit = true,
                        entry = it,
                        isChecked = it in checkedEntries.value,
                        onChecked = { _ -> scope.toggleCheck(it) },
                        onClick = { },
                    )
                }
                Space(8.dp)
            }
        }
        Column {
            OrderTotal(order.value.info.total.formattedPrice)
            Space(16.dp)
            val actions = scope.actions.flow.collectAsState()
            Row(modifier = Modifier.fillMaxWidth()) {
                actions.value.forEachIndexed { index, action ->
                    MedicoButton(
                        modifier = Modifier.weight(action.weight),
                        text = stringResourceByName(action.stringId),
                        isEnabled = true,
                        color = Color(action.bgColorHex.toColorInt()),
                        contentColor = Color(action.textColorHex.toColorInt()),
                        onClick = { scope.acceptAction(action) },
                    )
                    if (index != actions.value.lastIndex) {
                        Space(16.dp)
                    }
                }
            }
            Space(10.dp)
        }
    }
}