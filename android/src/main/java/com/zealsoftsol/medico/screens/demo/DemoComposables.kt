package com.zealsoftsol.medico.screens.demo

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.DemoScope
import com.zealsoftsol.medico.data.DemoResponse
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.deals.DealsItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DemoScreen(scope: DemoScope) {
    val demoData = scope.demoData.flow.collectAsState()

    Log.e("list"," "+demoData.value.size)
    LazyColumn {
        itemsIndexed(
            items = demoData.value,
            itemContent = { _, item ->
                MyDemoScreen(item = item)
            },
        )
    }
}

@Composable
fun MyDemoScreen(item: DemoResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(80.dp)
                    .background(shape = RoundedCornerShape(5.dp), color = Color.White),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoilImage(src = item.url, size = 40.dp)
            }
            Space(dp = 8.dp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
        }

        Image(
            painter = painterResource(id = R.drawable.ic_frwd_circle),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
