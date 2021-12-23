package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

/**
 * for image of different proportions
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImageBrands(
    src: Any,
    height: Dp,
    width: Dp,
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
    contentScale: ContentScale
) {
    val painter = rememberImagePainter(src, builder = { crossfade(isCrossFadeEnabled) })
    Box(
        Modifier
            .height(height)
            .width(width), contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            modifier = Modifier
                .height(height)
                .width(width),
            contentScale = contentScale,
            contentDescription = null,
        )
    }

    when (val state = painter.state) {
        is ImagePainter.State.Loading -> onLoading?.invoke()
        is ImagePainter.State.Success -> Unit
        is ImagePainter.State.Error, is ImagePainter.State.Empty -> onError?.invoke()
    }
}