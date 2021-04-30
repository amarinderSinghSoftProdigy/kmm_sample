package com.zealsoftsol.medico.screens.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun ProductScreen(scope: ProductInfoScope) {
    val isDetailsOpened = scope.isDetailsOpened.flow.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Space(28.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            CoilImage(
                modifier = Modifier.size(123.dp),
                contentDescription = null,
                data = CdnUrlProvider.urlFor(scope.product.code, CdnUrlProvider.Size.Px123),
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
                    text = scope.product.formattedPrice.orEmpty(),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                )
            }
        }
        Space(10.dp)
        Text(
            text = "MRP: ${scope.product.formattedMrp}",
            color = ConstColors.gray,
            fontSize = 12.sp,
        )
        Space(4.dp)
        scope.product.marginPercent?.let {
            Text(
                text = "Margin: $it",
                color = ConstColors.gray,
                fontSize = 12.sp,
            )
            Space(4.dp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Description: ${scope.product.uomName}",
                color = ConstColors.lightBlue,
                fontSize = 14.sp,
            )
            scope.product.stockInfo?.let {
                Text(
                    text = it.formattedStatus,
                    color = when (it.status) {
                        StockStatus.IN_STOCK -> ConstColors.green
                        StockStatus.LIMITED_STOCK -> ConstColors.orange
                        StockStatus.OUT_OF_STOCK -> ConstColors.red
                    },
                    fontWeight = FontWeight.W700,
                    fontSize = 12.sp,
                )
            }
        }
        Space(12.dp)
        when (scope.product.buyingOption) {
            BuyingOption.BUY -> {
                val context = LocalContext.current
                MedicoButton(text = stringResource(id = R.string.add_to_cart), isEnabled = true) {
                    if (!scope.buy()) {
                        context.toast(R.string.something_went_wrong)
                    }
                }
            }
            BuyingOption.QUOTE -> {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        disabledBackgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.background,
                        disabledContentColor = MaterialTheme.colors.background,
                    ),
                    elevation = null,
                    enabled = true,
                    border = BorderStroke(2.dp, ConstColors.yellow),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.get_quote),
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
        }
        Space(32.dp)
        Box(
            modifier = Modifier.fillMaxWidth()
                .clickable(indication = null) { scope.toggleDetails() }
        ) {
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
                    description = scope.product.manufacturer,
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
        if (scope.alternativeBrands.isNotEmpty()) {
            Space(24.dp)
            Text(
                text = stringResource(id = R.string.alternative_brands),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
            )
            Space(8.dp)
            scope.alternativeBrands.forEach {
                ProductAlternative(it) { scope.selectAlternativeProduct(it) }
                Space(8.dp)
            }
        }
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
private fun ProductAlternative(product: AlternateProductData, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp)
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
                text = product.manufacturerName,
                color = MaterialTheme.colors.background,
                fontSize = 16.sp,
            )
            Space(18.dp)
            Text(
                text = product.priceRange,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
            )
        }
        Text(
            text = product.availableVariants,
            color = ConstColors.gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}