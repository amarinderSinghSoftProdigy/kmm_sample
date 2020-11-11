package com.zealsoftsol.medico.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Deferred

@Composable
fun <T> Deferred<T>.awaitAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    LaunchedTask(this) {
        state.value = await()
    }
    return state
}