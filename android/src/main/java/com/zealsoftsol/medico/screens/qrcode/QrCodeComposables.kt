package com.zealsoftsol.medico.screens.qrcode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.QrCodeScope
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun QrCodeScreen(scope: QrCodeScope) {

    val qrCode = scope.qrCode.flow.collectAsState()
    val qrCodeImage = scope.qrCodeImage.flow.collectAsState()

    if (qrCode.value.isNotEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.delivery_qr_code),
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = Color.Black
            )

            Space(dp = 30.dp)

            Surface(elevation = 5.dp, modifier = Modifier
                .width(300.dp)
                .height(300.dp)) {
                CoilImageBrands(
                    src = qrCodeImage.value,
                    contentScale = ContentScale.FillBounds,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                    height = 300.dp,
                    width = 300.dp,
                )
            }

            Space(dp = 10.dp)

            Text(
                text = qrCode.value,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = Color.Black
            )

            Space(dp = 30.dp)

            MedicoButton(
                text = stringResource(id = R.string.regenerate_code), isEnabled = true,
                modifier = Modifier.padding(horizontal = 50.dp)
            ) {
                scope.regenerateQrCode()
            }
        }
    }
}