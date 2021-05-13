package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.R

@Composable
fun GstinOrPanRequiredBadge() {
    Surface(
        color = Color(0xFFFFC122).copy(alpha = .12f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth().height(26.dp),
    ) {
        Box {
            Box(
                modifier = Modifier.fillMaxHeight().width(3.dp)
                    .background(Color(0xFFFFC122))
            )
            Text(
                text = stringResource(id = R.string.gstin_pan_required),
                color = Color.Black,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterStart)
                    .padding(start = 12.dp),
            )
            Icon(
                imageVector = Icons.Default.Warning,
                tint = Color(0xFFFFC122),
                contentDescription = null,
                modifier = Modifier.size(15.dp).align(Alignment.CenterEnd),
            )
        }
    }
}
