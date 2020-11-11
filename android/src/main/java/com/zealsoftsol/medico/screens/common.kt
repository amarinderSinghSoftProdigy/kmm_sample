package com.zealsoftsol.medico.screens

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors

@Composable
fun TabBar(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        content()
    }
}

@Composable
fun MedicoButton(text: String, onClick: () -> Unit) {
    Surface(
        color = ConstColors.yellow,
        shape = RoundedCornerShape(2.dp),
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable(onClick = onClick)
    ) {
        Text(text = text)
    }
}