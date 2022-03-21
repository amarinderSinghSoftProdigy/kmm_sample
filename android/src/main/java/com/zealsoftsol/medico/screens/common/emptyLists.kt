package com.zealsoftsol.medico.screens.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R

@Composable
fun NoRecords(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    subtitle: String? = null,
    buttonText: String = stringResource(id = R.string.home),
    onHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Space(16.dp)
        Text(
            text = stringResource(id = text),
            fontSize = 15.sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colors.background,
            textAlign = TextAlign.Center,
        )
        subtitle?.let {
            Space(8.dp)
            Text(
                text = it,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = ConstColors.gray,
                textAlign = TextAlign.Center,
            )
        }
        Space(16.dp)
        MedicoSmallButton(
//            modifier = Modifier.padding(horizontal = ),
            text = buttonText,
            onClick = onHome,
        )
    }
}