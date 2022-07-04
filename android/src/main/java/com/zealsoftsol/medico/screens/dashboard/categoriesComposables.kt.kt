package com.zealsoftsol.medico.screens.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.CategoriesScope
import com.zealsoftsol.medico.screens.common.stringResourceByName

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesComposable(scope: CategoriesScope) {

    LazyVerticalGrid(cells = GridCells.Fixed(scope.CELL_COUNT), content = {
        itemsIndexed(scope.categoriesData, itemContent = { index, item ->
            CategoriesItems(scope.categoriesData[index]) {
                scope.startBrandSearch(item.title, "category")
            }
        })
    })
}

@Composable
fun CategoriesItems(
    categoriesData: CategoriesScope.Category,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val name = stringResourceByName(name = categoriesData.title)

    Card(
        modifier = modifier
            .height(160.dp)
            .selectable(
                selected = true,
                onClick = {
                    //send parameters for search based on product
                    onClick()
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

                val vec: Painter = when (categoriesData.imgPath) {
                    "1" -> painterResource(R.drawable.ic_ayurvedic)
                    "2" -> painterResource(R.drawable.ic_allopathic)
                    "3" -> painterResource(R.drawable.ic_homiopathic)
                    "4" -> painterResource(R.drawable.ic_otc)
                    "5" -> painterResource(R.drawable.ic_veterinary)
                    "6" -> painterResource(R.drawable.ic_cough_respiratory)
                    "7" -> painterResource(R.drawable.ic_diabetic)
                    "8" -> painterResource(R.drawable.ic_eye_care)
                    "9" -> painterResource(R.drawable.ic_pain_relief)
                    "10" -> painterResource(R.drawable.ic_skin_care)
                    "11" -> painterResource(R.drawable.ic_vitamins_n_supplemts)
                    "12" -> painterResource(R.drawable.ic_mental_care)
                    "13" -> painterResource(R.drawable.ic_dental_care)
                    "14" -> painterResource(R.drawable.ic_liver_care)
                    "15" -> painterResource(R.drawable.ic_pediatrics)
                    "16" -> painterResource(R.drawable.ic_cardiac_care)
                    "17" -> painterResource(R.drawable.ic_kidney_care)
                    "18" -> painterResource(R.drawable.ic_ortho_care)
                    "19" -> painterResource(R.drawable.ic_antibiotics)
                    "20" -> painterResource(R.drawable.ic_sexual_wellness)
                    "21" -> painterResource(R.drawable.ic_ent)
                    "22" -> painterResource(R.drawable.ic_cold_n_immunity)
                    "23" -> painterResource(R.drawable.ic_piles_care)
                    "24" -> painterResource(R.drawable.ic_personal_care)
                    "25" -> painterResource(R.drawable.ic_health_device)
                    else -> {
                        painterResource(id = R.drawable.ic_placeholder)
                    }
                }
                Image(
                    painter = vec,//categoriesData.imgPath,
                    contentDescription = "",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
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
