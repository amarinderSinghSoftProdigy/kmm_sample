package com.zealsoftsol.medico.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
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
        contentColor = MaterialTheme.colors.onPrimary,
        shape = RoundedCornerShape(2.dp),
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable(onClick = onClick),
    ) {
        Box {
            BasicText(
                text = text,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
inline fun <reified T> launchScreen() {
    ContextAmbient.current.let {
        it.startActivity(Intent(it, T::class.java))
    }
}