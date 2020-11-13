package com.zealsoftsol.medico.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zealsoftsol.medico.ConstColors
import kotlinx.coroutines.Deferred

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
fun IndefiniteProgressBar() {
    Dialog(onDismissRequest = {}) { CircularProgressIndicator() }
}

@Composable
fun <T> Deferred<T>.awaitAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    LaunchedTask(this) {
        state.value = await()
    }
    return state
}

@Composable
inline fun <reified T> launchScreen() {
    ContextAmbient.current.let {
        it.startActivity(Intent(it, T::class.java))
    }
}

inline fun <reified T> Context.launchScreen() {
    startActivity(Intent(this, T::class.java))
}