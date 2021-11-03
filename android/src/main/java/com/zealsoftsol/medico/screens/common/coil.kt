package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImage(
    src: Any,
    size: Dp,
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val painter = rememberImagePainter(src, builder = { crossfade(isCrossFadeEnabled) })
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            modifier = Modifier.size(size),
            contentDescription = null,
        )
        when (val state = painter.state) {
            is ImagePainter.State.Loading -> onLoading?.invoke()
            is ImagePainter.State.Success -> Unit
            is ImagePainter.State.Error, is ImagePainter.State.Empty -> onError?.invoke()
        }
    }
}