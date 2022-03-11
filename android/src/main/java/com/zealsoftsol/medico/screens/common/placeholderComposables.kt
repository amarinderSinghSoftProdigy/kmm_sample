package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R

@Composable
fun ItemPlaceholder() {
    Image(
        painter = painterResource(R.drawable.ic_placeholder),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun Placeholder(id: Int) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun PlaceholderText() {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, ConstColors.gray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_preview_available),
                color = MaterialTheme.colors.background,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
fun UserLogoPlaceholder(fullName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary, CircleShape)
    ) {
        Text(
            text = fullName.split(" ").take(2).map { it.first().toString() }
                .reduce { acc, s -> "$acc$s" },
            color = MaterialTheme.colors.background,
            fontSize = 36.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}