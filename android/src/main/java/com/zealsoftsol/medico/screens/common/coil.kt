package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

@Composable
fun CoilImage(
    src: Any,
    size: Dp,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val painter = rememberCoilPainter(src)
    when (painter.loadState) {
        is ImageLoadState.Loading -> Box(Modifier.size(size)) { onLoading?.invoke() }
        is ImageLoadState.Success -> Image(
            painter = painter,
            modifier = Modifier.size(size),
            contentDescription = null,
        )
        is ImageLoadState.Error, is ImageLoadState.Empty -> Box(Modifier.size(size)) { onError?.invoke() }
    }
}