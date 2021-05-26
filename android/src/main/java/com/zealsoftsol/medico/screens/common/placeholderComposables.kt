package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
fun UserLogoPlaceholder(fullName: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary, CircleShape)
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