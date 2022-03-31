package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import coil.request.CachePolicy

/**
 * for image of different proportions
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImageBrands(
    src: Any,
    height: Dp,
    width: Dp? = null,
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
    contentScale: ContentScale
) {
    val painter = rememberImagePainter(src){
        memoryCachePolicy(policy = CachePolicy.ENABLED)
    }
    Box(
        Modifier
            .height(height)
            .fillMaxWidth(), contentAlignment = Alignment.Center) {

        if(width == null){
            Image(
                painter = painter,
                modifier = Modifier
                    .height(height)
                    .fillMaxWidth(),
                contentScale = contentScale,
                contentDescription = null,
            )
        }else{
            Image(
                painter = painter,
                modifier = Modifier
                    .height(height)
                    .width(width),
                contentScale = contentScale,
                contentDescription = null,
            )
        }
    }

    when (val state = painter.state) {
        is ImagePainter.State.Loading -> onLoading?.invoke()
        is ImagePainter.State.Success -> Unit
        is ImagePainter.State.Error, is ImagePainter.State.Empty -> onError?.invoke()
    }
}