package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.PreviewUserScope
import com.zealsoftsol.medico.screens.NonSeasonBoyPreviewItem
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.rememberPhoneNumberFormatter

@Composable
fun PreviewUserScreen(scope: PreviewUserScope) {
    val isConfirmed = scope.isConfirmed.flow.collectAsState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Text(
                text = scope.tradeName,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.background,
                modifier = Modifier.padding(end = 30.dp),
            )
            Space(4.dp)
            NonSeasonBoyPreviewItem(scope, {})
            Space(12.dp)
            DataWithLabel(label = R.string.gstin_num, data = scope.gstin)
            val formatter = rememberPhoneNumberFormatter()
            DataWithLabel(
                label = R.string.phone_number,
                data = formatter.verifyNumber(scope.phoneNumber) ?: scope.phoneNumber
            )
            Space(12.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isConfirmed.value,
                    colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                    onCheckedChange = { scope.changeConfirm(it) },
                )
                Space(8.dp)
                Text(
                    text = stringResource(id = R.string.confirm_info),
                    fontSize = 12.sp,
                    color = ConstColors.gray,
                )
            }
        }
        MedicoButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = stringResource(id = R.string.add_retailer),
            isEnabled = isConfirmed.value,
            onClick = { scope.addRetailer() },
        )
    }
//    scope.showNotificationAlert()
}