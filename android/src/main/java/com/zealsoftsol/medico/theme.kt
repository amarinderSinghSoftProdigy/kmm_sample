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
    primary = Color(0xffF4F9FD), // very light blue
    onPrimary = Color(0xff003657),
    secondary = Color(0xffD9EDF9), // slightly darker blue
    onSecondary = Color(0xff003657),
    background = Color(0xff003657), // very dark blue
    onBackground = Color.White,
    surface = Color(0xffF4F9FD),
    onSurface = Color(0xff003657),
    error = Color(0xffFF1744),
)

private val DarkColors = darkColors()

object ConstColors {
    val yellow = Color(0xffFFD600)
    val darkBlue = Color(0xff00539B)
    val lightBlue = Color(0xff0084D4)
    val gray = Color(0xff666666)
    val ltgray = Color(0x14003657)
    val green = Color(0xff00c37d)
    val lightGreen = Color(0xff509F00)
    val orange = Color(0xffff912c)
    val red = Color(0xffed5152)
    val newDesignGray = Color(0xffF0F3F5)
    val darkGreen = Color(0xff509F00)
    val darkRed = Color(0xffF42525)
    val marron = Color(0xffCC1414)
    val separator = Color(0XffE0E0E0)
    val txtGrey = Color(0XffA6A6A6)
    val lightGrey = Color(0xffEEEEEE)
    val lightBackground = Color(0xffF9F9F9)
    val inputFieldColor = Color(0xff698896)
    val blueBack = Color(0xffF6FDFF)
    val greenBack = Color(0xffF9FDF4)
    val redBack = Color(0xffFFF5F5)
    val magenta = Color(0xffB22C45)
    val highlightGrey = Color(0xff979797)
    val cream = Color(0XffFFFAE1)
    val paleBlue = Color(0x1A009EFF)
    val skyBlue = Color(0x80DFEFF8)
}

@Composable
fun AppTheme(isDarkTheme: Boolean = false/*isSystemInDarkTheme()*/, content: @Composable () -> Unit) = MaterialTheme(
    colors = if (isDarkTheme) DarkColors else LightColors,
    shapes = Shapes(
        small = RoundedCornerShape(5.dp),
        medium = RoundedCornerShape(5.dp),
        large = RoundedCornerShape(5.dp),
    ),
    typography = Typography(
        h5 = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.W700),
    ),
    content = content,
)