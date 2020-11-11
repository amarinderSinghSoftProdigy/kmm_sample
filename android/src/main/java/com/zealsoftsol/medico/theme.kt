package com.zealsoftsol.medico

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColors(
    primary = Color.White,
    onPrimary = Color(0xff262626),
    background = Color.White
)

private val DarkColors = darkColors(
    primary = Color(0xff262626),
    onPrimary = Color(0xffc9c9c9),
    background = Color(0xff262626),
)

@Composable
fun AppTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = MaterialTheme(
    colors = if (isDarkTheme) DarkColors else LightColors,
    shapes = Shapes(
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(10.dp),
        large = RoundedCornerShape(10.dp),
    ),
    typography = Typography(
        body1 = MaterialTheme.typography.body1,
        body2 = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.onPrimary.copy(
                alpha = 0.8f
            )
        )
    ),
    content = content
)