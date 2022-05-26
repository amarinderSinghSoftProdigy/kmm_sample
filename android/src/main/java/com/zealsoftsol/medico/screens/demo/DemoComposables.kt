package com.zealsoftsol.medico.screens.demo

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.DemoScope
import com.zealsoftsol.medico.data.DemoResponse
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.deals.DealsItem
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DemoScreen(scope: DemoScope) {
    val demoData = scope.demoData.flow.collectAsState()

    Log.e("list", " " + demoData.value.size)
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp).fillMaxSize()) {
        LazyColumn {
            itemsIndexed(
                items = demoData.value,
                itemContent = { _, item ->
                    MyDemoScreen(item = item)
                },
            )
        }
    }
}

@Composable
fun MyDemoScreen(item: DemoResponse) {
    Surface(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
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
                        src = item.url,
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
