package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.ProductData
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun ProductScreen(scope: ProductInfoScope) {
    val isDetailsOpened = scope.isDetailsOpened.flow.collectAsState()

    ScrollableColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Space(12.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            CoilImage(
                modifier = Modifier.size(123.dp),
                contentDescription = null,
                data = CdnUrlProvider.urlFor(scope.product.medicineId, CdnUrlProvider.Size.Px123),
                error = { ItemPlaceholder() },
                loading = { ItemPlaceholder() },
            )
            Space(10.dp)
            Column {
                Text(
                    text = scope.product.name,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                )
                Space(4.dp)
                Text(
                    text = "Code: ${scope.product.code}",
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                )
                Space(10.dp)
                Text(
                    text = scope.product.formattedPrice,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                )
            }
        }
        Space(10.dp)
        Text(
            text = "MRP: ${scope.product.mrp}",
            color = ConstColors.gray,
            fontSize = 12.sp,
        )
        Space(4.dp)
        Text(
            text = "PTR: ${scope.product.ptr}",
            color = ConstColors.gray,
            fontSize = 12.sp,
        )
        Space(4.dp)
        Text(
            text = "Description: ${scope.product.unitOfMeasureData.name}",
            color = ConstColors.lightBlue,
            fontSize = 14.sp,
        )
        Space(12.dp)
        MedicoButton(text = stringResource(id = R.string.add_to_cart), isEnabled = true) {
            scope.addToCart()
        }
        Space(32.dp)
        Box(
            modifier = Modifier.fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionState = remember { InteractionState() }) { scope.toggleDetails() }) {
            Text(
                text = stringResource(id = R.string.details),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            Icon(
                imageVector = if (isDetailsOpened.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ConstColors.gray,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
        if (isDetailsOpened.value) {
            Space(24.dp)
            Column {
                ProductDetail(
                    title = stringResource(id = R.string.manufacturer),
                    description = scope.product.manufacturer.name,
                )
                Space(12.dp)
                ProductDetail(
                    title = stringResource(id = R.string.compositions),
                    description = scope.compositionsString,
                )
                Space(12.dp)
                ProductDetail(
                    title = stringResource(id = R.string.storage),
                    description = stringResource(id = R.string.storage_desc),
                )
            }
        }
        Space(24.dp)
        Text(
            text = stringResource(id = R.string.alternative_brands),
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
        )
//        if (scope.alternativeBrands.isNotEmpty()) {
//            Space(8.dp)
//            scope.alternativeBrands.forEach {
//                ProductAlternative(it) { scope.selectAlternativeProduct(it) }
//            }
//        }
    }
}

@Composable
private fun ProductDetail(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
        )
        Space(4.dp)
        Text(
            text = description,
            color = MaterialTheme.colors.background,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun ProductAlternative(product: ProductData, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        Column {
            Text(
                text = product.name,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
            )
            Space(4.dp)
            Text(
                text = product.manufacturer.name,
                color = MaterialTheme.colors.background,
                fontSize = 16.sp,
            )
            Space(18.dp)
            Text(
                text = "from to",
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
            )
        }
        Text(
            text = "10 variants",
            color = ConstColors.gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}