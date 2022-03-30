package com.zealsoftsol.medico.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.utils.ZoomableImage

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImage(
    src: Any,
    size: Dp,
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val painter =
        rememberImagePainter(src, builder = { memoryCachePolicy(policy = CachePolicy.ENABLED) })
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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImage(
    src: Any,
    modifier: Modifier,
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val painter =
        rememberImagePainter(src, builder = { memoryCachePolicy(policy = CachePolicy.ENABLED) })
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            contentScale = ContentScale.FillBounds,
            painter = painter,
            modifier = modifier,
            contentDescription = null,
        )
        when (val state = painter.state) {
            is ImagePainter.State.Loading -> onLoading?.invoke()
            is ImagePainter.State.Success -> Unit
            is ImagePainter.State.Error, is ImagePainter.State.Empty -> onError?.invoke()
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CoilImageZoom(
    src: Any,
    modifier: Modifier = Modifier.size(LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp),
    isCrossFadeEnabled: Boolean = true,
    onError: @Composable (() -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null,
) {
    val painter =
        rememberImagePainter(src, builder = { memoryCachePolicy(policy = CachePolicy.ENABLED) })
    Box(modifier, contentAlignment = Alignment.Center) {
        ZoomableImage(
            painter = painter,
            modifier = modifier,
        )
        when (val state = painter.state) {
            is ImagePainter.State.Loading -> onLoading?.invoke()
            is ImagePainter.State.Success -> Unit
            is ImagePainter.State.Error, is ImagePainter.State.Empty -> onError?.invoke()
        }
    }
}
