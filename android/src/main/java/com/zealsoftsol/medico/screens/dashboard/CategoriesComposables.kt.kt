package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.CategoriesScope
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.stringResourceByName

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesComposable(scope: CategoriesScope) {

    LazyVerticalGrid(cells = GridCells.Fixed(scope.CELL_COUNT), content = {
        itemsIndexed(scope.categoriesData, itemContent = { index, item ->
            CategoriesItems(scope.categoriesData[index], scope)
        })
    })
}

@Composable
fun CategoriesItems(categoriesData: CategoriesScope.Category, scope: CategoriesScope) {
    val name = stringResourceByName(name = categoriesData.title)

    Card(
        modifier = Modifier
            .height(160.dp)
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on product
                    scope.startBrandSearch(name, "category")
                })
            .padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = 3.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .height(90.dp),
            ) {
                CoilImage(
                    src = categoriesData.imgPath,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    onError = { Placeholder(R.drawable.ic_placeholder) },
                    onLoading = { Placeholder(R.drawable.ic_placeholder) },
                    isCrossFadeEnabled = false
                )
            }

            Text(
                text = name,
                textAlign = TextAlign.Center,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

    }
}
