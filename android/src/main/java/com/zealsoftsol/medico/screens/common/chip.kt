package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chip(
    text: String,
    isSelected: Boolean,
    color: Color = ConstColors.lightBlue,
    onClick: () -> Unit,
) {
    Surface(
        color = if (isSelected) color else Color.Transparent,
        border = if (!isSelected) BorderStroke(1.dp, color) else null,
        shape = RoundedCornerShape(percent = 50),
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else color,
            fontSize = 12.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp, // compensate for 2.sp smaller text
            ),
        )
    }
}