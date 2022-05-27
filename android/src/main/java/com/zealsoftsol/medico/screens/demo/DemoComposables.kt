package com.zealsoftsol.medico.screens.demo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.DemoScope
import com.zealsoftsol.medico.data.DemoResponse
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.deals.DealsItem
import java.io.File

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DemoScreen(scope: DemoScope) {
    val context = LocalContext.current

    when (scope) {
        is DemoScope.DemoListing -> {
            val demoData = scope.demoData.flow.collectAsState()
            Log.e("list", " " + demoData.value.size)
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                    .fillMaxSize()
            ) {
                LazyColumn {
                    itemsIndexed(
                        items = demoData.value,
                        itemContent = { _, item ->
                            MyDemoScreen(item = item) {
                                scope.openVideo(item.url)
                            }
                        },
                    )
                }
            }
        }
        is DemoScope.DemoPlayer -> {
            val release = scope.releasePlayer.flow.collectAsState()
            val exoPlayer = remember {
                SimpleExoPlayer.Builder(context).build()
            }
            if (!release.value) {
                val url = scope.demoUrl.flow.collectAsState()
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    VideoPlayer(url = url.value, context, exoPlayer)
                }
            } else {
                exoPlayer.playWhenReady = false
                exoPlayer.release()
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String, context: Context, exoPlayer: SimpleExoPlayer) {
    val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(context, context.packageName)
    )
    val source = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(Uri.parse(url))
    exoPlayer.apply {
        exoPlayer.prepare(source)
        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_OFF
    }
    PlayVideo(exoPlayer = exoPlayer)
    exoPlayer.playWhenReady = true
}

@Composable
fun PlayVideo(exoPlayer: SimpleExoPlayer) {
    AndroidView(
        { context ->
            exoPlayer.playWhenReady = false
            PlayerView(context).apply {
                player = exoPlayer
                //resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                //exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyDemoScreen(item: DemoResponse, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        onClick = onClick,
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                elevation = 5.dp,
                shape = RoundedCornerShape(8.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp),
                ) {
                    CoilImage(
                        src = item.imageUrl,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(60.dp)
                            .height(60.dp),
                        onError = { Placeholder(R.drawable.ic_img_placeholder) },
                        onLoading = { Placeholder(R.drawable.ic_img_placeholder) },
                        isCrossFadeEnabled = false
                    )
                }
            }

            Space(dp = 8.dp)

            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = item.name,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_frwd_circle),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
