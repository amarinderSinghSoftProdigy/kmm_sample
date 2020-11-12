package com.zealsoftsol.medico

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val LightColors = lightColors(
    primary = Color(0xffF4F9FD),
    onPrimary = Color(0xff003657),
    secondary = Color(0xff003657),
    onSecondary = Color.White,
    background = Color(0xffF4F9FD),
    surface = Color(0xffF4F9FD),
    onSurface = Color(0xff003657)
)

private val DarkColors = darkColors()

object ConstColors {
    val yellow = Color(0xffFFD600)
    val lightBlue = Color(0xff0084D4)
    val gray = Color(0xff666666)
}

@Composable
fun AppTheme(isDarkTheme: Boolean = false/*isSystemInDarkTheme()*/, content: @Composable () -> Unit) = MaterialTheme(
    colors = if (isDarkTheme) DarkColors else LightColors,
    shapes = Shapes(
        small = RoundedCornerShape(0.dp),
        medium = RoundedCornerShape(0.dp),
        large = RoundedCornerShape(0.dp),
    ),
    typography = Typography(
        h5 = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.W700)
    ),
    content = content
)