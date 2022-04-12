package com.zealsoftsol.medico.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
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
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.PreferenceScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun PreferenceScreen(scope: PreferenceScope) {

    val showAlert = scope.showAlert.flow.collectAsState()
    val showAlertText = scope.showAlertText.flow.collectAsState()
    val isAutoApproved = scope.isAutoApproved.flow.collectAsState()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {

            Space(dp = 20.dp)
            Text(
                text = stringResource(id = R.string.connection_req),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
            Space(dp = 20.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.auto_approve),
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600
                )

                Switch(
                    checked = isAutoApproved.value,
                    onCheckedChange = {
                        scope.updateAutoApprovePreference(it)
                    }, colors = SwitchDefaults.colors(
                        checkedThumbColor = ConstColors.green
                    )
                )
            }
            Space(40.dp)
            MedicoButton(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = stringResource(id = R.string.save),
                isEnabled = true
            ) {
                scope.submitPreference()
            }
            Space(40.dp)
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = showAlertText.value,
                fontSize = 12.sp,
                color = Color.Gray
            )

        }

        if (showAlert.value)
            ShowAlert(message = showAlertText.value) {
                scope.showAlertBottomSheet(false)
            }
    }
}