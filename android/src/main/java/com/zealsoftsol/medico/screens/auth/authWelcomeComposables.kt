package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.Space

@Composable
fun Welcome(
    fullName: String,
    onUploadClick: (() -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${stringResource(R.string.welcome)}, $fullName!",
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
        )
        Space(50.dp)
        Icon(
            asset = vectorResource(id = if (onUploadClick == null) R.drawable.ic_welcome else R.drawable.ic_upload),
            tint = Color.Unspecified,
        )
        Space(30.dp)
        if (onUploadClick == null) {
            Text(
                text = stringResource(id = R.string.thanks_for_registration),
                textAlign = TextAlign.Center,
            )
            Space(18.dp)
            Text(
                text = stringResource(id = R.string.documents_under_review),
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = stringResource(id = R.string.provide_drug_license_hint),
                textAlign = TextAlign.Center,
                color = ConstColors.gray,
            )
            Space(50.dp)
            MedicoButton(
                text = stringResource(id = R.string.upload_new_document),
                isEnabled = true,
                onClick = onUploadClick
            )
        }
    }
}