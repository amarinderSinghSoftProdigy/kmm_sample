package com.zealsoftsol.medico.screens.offers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun OffersScreen(scope: OffersScope) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Space(12.dp)
        Text(
            text = stringResource(id = R.string.preferred_language),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = Color.Black,
        )
        Space(20.dp)
        MedicoButton(
            text = stringResource(id = R.string.save),
            onClick = { },
            isEnabled = true
        )
    }
}
