package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.Space
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun ProductScreen(scope: MainScope.ProductInfo) {
    val isDetailsOpened = scope.isDetailsOpened.flow.collectAsState()

    ScrollableColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Space(12.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            CoilImage(
                data = "",
                error = { ProductPlaceholder() },
                loading = { ProductPlaceholder() },
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
                    text = "Code: ${scope.product.productCode}",
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
            text = "PTR: ${scope.product.ptrPercentage}",
            color = ConstColors.gray,
            fontSize = 12.sp,
        )
        Space(4.dp)
        Text(
            text = "Description: ${scope.product.packageForm}",
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
                .clickable(indication = null) { scope.toggleDetails() }) {
            Text(
                text = stringResource(id = R.string.details),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            Icon(
                imageVector = if (isDetailsOpened.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                tint = ConstColors.gray,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
        if (isDetailsOpened.value) {
            Column(modifier = Modifier.fillMaxWidth()) {

            }
        }
    }
}

@Composable
private fun ProductDetail(title: String, description: String) {

}

@Composable
fun ProductPlaceholder() {
    Image(
        imageVector = vectorResource(R.drawable.ic_product),
    )
}