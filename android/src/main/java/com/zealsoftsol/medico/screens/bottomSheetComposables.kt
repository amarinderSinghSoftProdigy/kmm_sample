package com.zealsoftsol.medico.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.TaxInfo
import com.zealsoftsol.medico.data.TaxType
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia
import com.zealsoftsol.medico.screens.management.GeoLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun Scope.Host.showBottomSheet(
    activity: MainActivity,
    coroutineScope: CoroutineScope,
) {
    val bottomSheet = bottomSheet.flow.collectAsState()
    bottomSheet.value?.let { bs ->
        when (bs) {
            is BottomSheet.UploadDocuments -> {
                DocumentUploadBottomSheet(
                    supportedFileTypes = bs.supportedFileTypes,
                    useCamera = !bs.isSeasonBoy,
                    activity = activity,
                    coroutineScope = coroutineScope,
                    onFileReady = { bs.handleFileUpload(it) },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.PreviewManagementItem -> PreviewItemBottomSheet(
                entityInfo = bs.entityInfo,
                isForSeasonBoy = bs.isSeasonBoy,
                onSubscribe = if (bs.canSubscribe) {
                    { bs.subscribe() }
                } else null,
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.ModifyOrderEntry -> {
                ModifyOrderEntryBottomSheet(
                    bs,
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.PreviewStockist -> PreviewStockistBottomSheet(
                sellerInfo = bs.sellerInfo,
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.ViewTaxInfo -> ViewTaxInfoBottomSheet(
                taxInfo = bs.taxInfo,
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.ViewItemTax -> ViewItemTaxBottomSheet(
                invoiceEntry = bs.invoiceEntry,
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.ViewQrCode -> ViewQrBottomSheet(
                url = bs.qrUrl,
                onDismiss = { dismissBottomSheet() },
            )
        }
    }
}

@Composable
private fun ViewQrBottomSheet(
    url: String,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.qr_code).uppercase(),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp,
            )
            Space(16.dp)
            CoilImage(
                src = url,
                size = LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp,
                onLoading = { CircularProgressIndicator(color = ConstColors.yellow) }
            )
            Space(16.dp)
            Text(
                text = stringResource(id = R.string.qr_hint),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ViewItemTaxBottomSheet(
    invoiceEntry: InvoiceEntry,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoilImage(
                    src = CdnUrlProvider.urlFor(
                        invoiceEntry.productCode,
                        CdnUrlProvider.Size.Px123
                    ),
                    size = 71.dp,
                    onError = { ItemPlaceholder() },
                    onLoading = { ItemPlaceholder() },
                )
                Space(16.dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = invoiceEntry.productName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                    )
                    Space(4.dp)
                    Row {
                        Text(
                            text = invoiceEntry.productCode,
                            color = ConstColors.gray,
                            fontSize = 14.sp,
                        )
                        Space(6.dp)
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                                .align(Alignment.CenterVertically)
                        )
                        Space(6.dp)
                        Text(
                            text = buildAnnotatedString {
                                append("Units: ")
                                val startIndex = length
                                append(invoiceEntry.standardUnit)
                                addStyle(
                                    SpanStyle(
                                        color = ConstColors.lightBlue,
                                        fontWeight = FontWeight.W800
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontSize = 14.sp,
                        )
                    }
                    Space(4.dp)
                    Text(
                        text = invoiceEntry.manufacturerName,
                        color = ConstColors.lightBlue,
                        fontSize = 14.sp,
                    )
                }
            }
            Divider()
            Space(10.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.qty),
                    value = invoiceEntry.quantity.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.free),
                    value = invoiceEntry.freeQty.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
            }
            Space(8.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.price),
                    value = invoiceEntry.price.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.total),
                    value = invoiceEntry.totalAmount.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
            }
            Space(8.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                BoxWithConstraints {
                    ItemValue(
                        Modifier.width(maxWidth / 2 - 6.dp),
                        item = stringResource(id = R.string.discount),
                        value = invoiceEntry.discount.formatted,
                        itemTextColor = ConstColors.lightBlue,
                        valueTextColor = MaterialTheme.colors.background
                    )
                }
            }
            Space(10.dp)
            Divider()
            Column(Modifier.background(ConstColors.gray.copy(alpha = 0.05f))) {
                Space(10.dp)
                if (invoiceEntry.cgstTax.amount.value > 0.0 || invoiceEntry.sgstTax.amount.value > 0.0 || invoiceEntry.igstTax.amount.value > 0.0) {
                    val taxTypeString = when (invoiceEntry.taxType) {
                        TaxType.IGST -> "IGST"
                        else -> "GST"
                    }
                    ItemValue(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        item = "$taxTypeString(${invoiceEntry.gstTaxRate.string})",
                        value = invoiceEntry.totalTaxAmount.formatted,
                        valueTextColor = MaterialTheme.colors.background
                    )
                    Space(8.dp)
                    Column(Modifier.padding(start = 30.dp, end = 16.dp)) {
//                        if (invoiceEntry.igstTax.amount.value > 0.0) {
//                            ItemValue(
//                                Modifier.fillMaxWidth(),
//                                item = "GST(${invoiceEntry.igstTax.rate.string})",
//                                value = invoiceEntry.igstTax.amount.formatted,
//                                valueTextColor = MaterialTheme.colors.background,
//                                itemTextColor = ConstColors.gray,
//                            )
//                            Space(8.dp)
//                        }
                        if (invoiceEntry.cgstTax.amount.value > 0.0) {
                            ItemValue(
                                Modifier.fillMaxWidth(),
                                item = "CGST(${invoiceEntry.cgstTax.percent.formatted})",
                                value = invoiceEntry.cgstTax.amount.formatted,
                                valueTextColor = MaterialTheme.colors.background,
                                itemTextColor = ConstColors.gray,
                            )
                            Space(8.dp)
                        }
                        if (invoiceEntry.sgstTax.amount.value > 0.0) {
                            ItemValue(
                                Modifier.fillMaxWidth(),
                                item = "SGST(${invoiceEntry.sgstTax.percent.formatted})",
                                value = invoiceEntry.sgstTax.amount.formatted,
                                valueTextColor = MaterialTheme.colors.background,
                                itemTextColor = ConstColors.gray,
                            )
                            Space(8.dp)
                        }
                    }
                } else {
                    ItemValue(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        item = "GST(0.0%)",
                        value = invoiceEntry.totalTaxAmount.formatted,
                        valueTextColor = MaterialTheme.colors.background
                    )
                    Space(8.dp)
                }
                Space(10.dp)
            }
        }
    }
}

@Composable
private fun ViewTaxInfoBottomSheet(
    taxInfo: TaxInfo,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.items),
                    value = taxInfo.total.itemCount.toString()
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.units),
                    value = taxInfo.noOfUnits.toString()
                )
            }
            Space(8.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.adjust),
                    value = taxInfo.adjWithoutRounded.formatted
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.rounding),
                    value = taxInfo.adjRounded.formatted
                )
            }
            Space(10.dp)
            Divider()
            Space(10.dp)
            ItemValue(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                item = stringResource(id = R.string.gross),
                value = taxInfo.grossAmount.formatted,
                valueTextColor = MaterialTheme.colors.background
            )
            Space(8.dp)
            ItemValue(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                item = stringResource(id = R.string.discount),
                value = "${taxInfo.invoiceDiscount.value} | ${taxInfo.totalDiscountAmt.formatted}",
                valueTextColor = MaterialTheme.colors.background
            )
            Space(8.dp)
            ItemValue(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                item = stringResource(id = R.string.freight),
                value = taxInfo.freight.formatted,
                valueTextColor = MaterialTheme.colors.background
            )
            Space(8.dp)
            ItemValue(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                item = stringResource(id = R.string.total_tax),
                value = taxInfo.totalTaxAmount.formatted,
                valueTextColor = MaterialTheme.colors.background
            )
            Space(10.dp)
            Divider()
            Column(
                Modifier
                    .background(ConstColors.gray.copy(alpha = 0.05f))
            ) {
                Space(10.dp)
                val availableTaxes =
                    taxInfo.totalTaxRates.filter { it.totalTaxableAmount.value > 0.0 }
                if (availableTaxes.isNotEmpty()) {
                    val taxTypeString = when (taxInfo.type) {
                        TaxType.IGST -> "IGST"
                        else -> "GST"
                    }
                    availableTaxes.forEach {
                        ItemValue(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            item = "$taxTypeString(${it.gstDisplayName})",
                            value = it.totalTaxableAmount.formatted,
                            valueTextColor = MaterialTheme.colors.background
                        )
                        Space(8.dp)
                        if (taxInfo.type != TaxType.IGST) Column(
                            Modifier.padding(
                                start = 30.dp,
                                end = 16.dp
                            )
                        ) {
                            if (it.cgstTotalAmt.value > 0.0) {
                                ItemValue(
                                    Modifier.fillMaxWidth(),
                                    item = "CGST(${it.cgstTaxPercent.formatted})",
                                    value = it.cgstTotalAmt.formatted,
                                    valueTextColor = MaterialTheme.colors.background,
                                    itemTextColor = ConstColors.gray,
                                )
                                Space(8.dp)
                            }
                            if (it.sgstTotalAmt.value > 0.0) {
                                ItemValue(
                                    Modifier.fillMaxWidth(),
                                    item = "SGST(${it.sgstTaxPercent.formatted})",
                                    value = it.sgstTotalAmt.formatted,
                                    valueTextColor = MaterialTheme.colors.background,
                                    itemTextColor = ConstColors.gray,
                                )
                                Space(8.dp)
                            }
                        }
                    }
                } else {
                    ItemValue(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        item = "GST(0.0%)",
                        value = taxInfo.totalIGST.formatted,
                        valueTextColor = MaterialTheme.colors.background
                    )
                    Space(8.dp)
                }
                Space(2.dp)
                Divider()
                Space(10.dp)
                ItemValue(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    item = stringResource(id = R.string.net_payable),
                    value = taxInfo.netAmount.formatted,
                    itemTextSize = 20.sp,
                    valueTextSize = 20.sp,
                    valueTextColor = MaterialTheme.colors.background,
                )
                Space(16.dp)
            }
        }
    }
}

@Composable
private fun ItemValue(
    modifier: Modifier = Modifier,
    item: String,
    value: String,
    itemTextColor: Color = MaterialTheme.colors.background,
    valueTextColor: Color = ConstColors.lightBlue,
    itemTextSize: TextUnit = 16.sp,
    valueTextSize: TextUnit = 15.sp,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = item,
            color = itemTextColor,
            fontWeight = FontWeight.W600,
            fontSize = itemTextSize,
        )
        Text(
            text = value,
            color = valueTextColor,
            fontWeight = FontWeight.W700,
            fontSize = valueTextSize,
        )
    }
}

@Composable
private fun PreviewStockistBottomSheet(
    sellerInfo: SellerInfo,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CoilImage(
                    src = "",
                    size = 77.dp,
                    onError = { UserLogoPlaceholder(sellerInfo.tradeName) },
                    onLoading = { UserLogoPlaceholder(sellerInfo.tradeName) },
                )
                Space(16.dp)
                Column {
                    Text(
                        text = sellerInfo.tradeName,
                        color = MaterialTheme.colors.background,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                    )
                    Space(4.dp)
                    Text(
                        text = sellerInfo.geoData.full(),
                        color = MaterialTheme.colors.background,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                    )
                }
            }
            Space(16.dp)
            sellerInfo.promotionData?.takeIf { sellerInfo.isPromotionActive }?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ConstColors.red, RoundedCornerShape(4.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        text = it.displayLabel,
                        color = ConstColors.red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                    )
                    Space(8.dp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = it.offerPrice.formatted,
                            color = MaterialTheme.colors.background,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                        )
                        Space(8.dp)
                        Text(
                            text = sellerInfo.priceInfo?.price?.formattedPrice.orEmpty(),
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    it.validity?.let { v ->
                        Space(8.dp)
                        Text(
                            text = v,
                            color = ConstColors.gray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.W300,
                        )
                    }
                }
                Space(16.dp)
            }
            Column(
                modifier = Modifier
                    .border(1.dp, ConstColors.gray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = sellerInfo.priceInfo?.price?.formattedPrice.orEmpty(),
                        color = MaterialTheme.colors.background,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Stock:",
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                        )
                        Space(4.dp)
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colors.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(4.dp)
                        ) {
                            Text(
                                text = sellerInfo.stockInfo?.availableQty?.toString() ?: "",
                                color = MaterialTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W700,
                            )
                        }
                    }
                }
                Space(12.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val color = sellerInfo.stockInfo?.expiry?.color?.toColorInt()?.let { Color(it) }
                        ?: MaterialTheme.colors.background
                    Box(
                        modifier = Modifier.background(
                            color = color.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Expiry: ")
                                val startIndex = length
                                append(sellerInfo.stockInfo?.expiry?.formattedDate.orEmpty())
                                addStyle(
                                    SpanStyle(
                                        color = color,
                                        fontWeight = FontWeight.W800
                                    ),
                                    startIndex,
                                    length,
                                )
                            },
                            color = ConstColors.gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = ConstColors.lightBlue,
                            modifier = Modifier.size(10.dp),
                        )
                        Space(4.dp)
                        Text(
                            text = "${sellerInfo.geoData.distance} km",
                            color = ConstColors.lightBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
                Space(12.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("MRP: ")
                            val startIndex = length
                            append(sellerInfo.priceInfo?.mrp?.formattedPrice.orEmpty())
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Margin: ")
                            val startIndex = length
                            append(sellerInfo.priceInfo?.marginPercent.orEmpty())
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                }
                Space(12.dp)
                Text(
                    text = buildAnnotatedString {
                        append("Batch No: ")
                        val startIndex = length
                        append("N/A")
                        addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.W800
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ModifyOrderEntryBottomSheet(
    entry: BottomSheet.ModifyOrderEntry,
    onDismiss: () -> Unit,
) {
    val qty = entry.quantity.flow.collectAsState()
    val freeQty = entry.freeQuantity.flow.collectAsState()
    val ptr = entry.ptr.flow.collectAsState()
    val batch = entry.batch.flow.collectAsState()
    val expiry = entry.expiry.flow.collectAsState()
    val isChecked = entry.isChecked.flow.collectAsState()

    val canEdit = false//entry.canEdit

    BaseBottomSheet(onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.12f),
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column {
                Row(modifier = Modifier.padding(end = 30.dp)) {
//                    if (canEdit) {
//                        Checkbox(
//                            checked = isChecked.value,
//                            colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
//                            onCheckedChange = { entry.toggleCheck() },
//                        )
//                        Space(18.dp)
//                    }
                    Column {
                        Text(
                            text = entry.orderEntry.productName,
                            color = MaterialTheme.colors.background,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = "${stringResource(id = R.string.batch_no)} ${batch.value}",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = "${stringResource(id = R.string.expiry)} ${expiry.value}",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Row {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.price))
                                    append(": ")
                                    val startIndex = length
                                    append(entry.orderEntry.price.formatted)
                                    addStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colors.background,
                                            fontWeight = FontWeight.W600
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Space(8.dp)
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.requested_qty))
                                    append(" ")
                                    val startIndex = length
                                    append(entry.orderEntry.requestedQty.formatted)
                                    addStyle(
                                        SpanStyle(
                                            color = ConstColors.lightBlue,
                                            fontWeight = FontWeight.W600
                                        ),
                                        startIndex,
                                        length,
                                    )
                                },
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                Space(20.dp)
                Divider()
                Space(20.dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.qty),
                                    qty = qty.value.toString(),
                                    onChange = { entry.updateQuantity(it.toDouble()) },
                                    isEnabled = canEdit,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2 - 8.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                EditField(
                                    label = stringResource(id = R.string.free),
                                    qty = freeQty.value.toString(),
                                    onChange = { entry.updateFreeQuantity(it.toDouble()) },
                                    isEnabled = canEdit,
                                )
                            }
                        }
                    }
                    Space(8.dp)
                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.ptr),
                                    qty = ptr.value,
                                    onChange = { entry.updatePtr(it) },
                                    isEnabled = canEdit,
                                    formattingRule = false,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2 - 8.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                EditField(
                                    label = stringResource(id = R.string.batch),
                                    qty = batch.value,
                                    onChange = { entry.updateBatch(it) },
                                    isEnabled = canEdit,
                                    formattingRule = false,
                                    keyboardOptions = KeyboardOptions.Default,
                                )
                            }
                        }
                    }
                    Space(8.dp)
                    Box {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2 - 8.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.expiry_),
                                    qty = expiry.value,
                                    onChange = { entry.updateExpiry(it) },
                                    isEnabled = canEdit,
                                    formattingRule = false,
                                    keyboardOptions = KeyboardOptions.Default,
                                )
                            }
                        }
                    }
                }
                Space(20.dp)
                Divider()
                Space(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${stringResource(id = R.string.subtotal)}: ${entry.orderEntry.totalAmount.formatted}",
                        color = MaterialTheme.colors.background,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
//                    if (canEdit) {
//                        MedicoSmallButton(
//                            text = stringResource(id = R.string.save),
//                            enabledColor = ConstColors.lightBlue,
//                            contentColor = Color.White,
//                            onClick = { entry.save() },
//                        )
//                    }
                }
//                Space(8.dp)
            }
        }
    }
}

@Composable
private fun DocumentUploadBottomSheet(
    supportedFileTypes: Array<FileType>,
    useCamera: Boolean,
    activity: MainActivity,
    coroutineScope: CoroutineScope,
    onFileReady: (File) -> Unit,
    onDismiss: () -> Unit,
) {
    SectionsBottomSheet(
        title = stringResource(id = R.string.actions),
        cells = listOfNotNull(
            BottomSheetCell(R.string.upload, Icons.Filled.CloudUpload),
            if (useCamera)
                BottomSheetCell(R.string.use_camera, Icons.Filled.CameraAlt)
            else
                null
        ),
        onCellClick = { (stringId, _) ->
            coroutineScope.launch {
                val file = when (stringId) {
                    R.string.upload -> activity.openFilePicker(supportedFileTypes)
                    R.string.use_camera -> activity.takePicture()
                    else -> null
                }
                if (file != null) {
                    onDismiss()
                    onFileReady(file)
                }
            }
        },
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PreviewItemBottomSheet(
    entityInfo: EntityInfo,
    isForSeasonBoy: Boolean,
    onSubscribe: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.12f),
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isForSeasonBoy) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_season_boy),
                            contentDescription = null,
                            tint = MaterialTheme.colors.background,
                            modifier = Modifier.size(24.dp),
                        )
                        Space(16.dp)
                    }
                    Text(
                        text = entityInfo.tradeName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colors.background,
                        modifier = Modifier.padding(end = 30.dp),
                    )
                }
                Space(4.dp)
                if (isForSeasonBoy) {
                    Space(8.dp)
                    SeasonBoyPreviewItem(entityInfo)
                } else {
                    NonSeasonBoyPreviewItem(entityInfo, onSubscribe)
                }
            }
        }
    }
}

@Composable
private fun SeasonBoyPreviewItem(entityInfo: EntityInfo) {
    val phoneNumber = entityInfo.phoneNumber?.formatIndia().orEmpty()
    val activity = (LocalContext.current as MainActivity)
    Text(
        text = phoneNumber,
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.End,
        color = ConstColors.lightBlue,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { activity.openDialer(phoneNumber) },
    )
    Space(12.dp)
    Separator(padding = 0.dp)
    Space(12.dp)
    entityInfo.subscriptionData?.let {
        DataWithLabel(label = R.string.status, data = it.status.serverValue)
    }
    entityInfo.seasonBoyData?.let {
        DataWithLabel(label = R.string.email, data = it.email)
    }
    DataWithLabel(label = R.string.address, data = entityInfo.geoData.fullAddress())
    entityInfo.subscriptionData?.let {
        DataWithLabel(label = R.string.orders, data = it.orders.toString())
    }
    entityInfo.seasonBoyData?.let {
        DataWithLabel(label = R.string.retailers, data = it.retailers.toString())
    }
}

@Composable
private fun NonSeasonBoyPreviewItem(entityInfo: EntityInfo, onSubscribe: (() -> Unit)?) {
    Text(
        text = entityInfo.geoData.fullLandmark(),
        fontSize = 14.sp,
        color = ConstColors.gray,
    )
    Space(16.dp)
    Row(modifier = Modifier.fillMaxWidth()) {
        CoilImage(
            src = "",
            size = 123.dp,
            onError = { UserLogoPlaceholder(entityInfo.tradeName) },
            onLoading = { UserLogoPlaceholder(entityInfo.tradeName) },
        )
        Space(24.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 123.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            GeoLocation(entityInfo.geoData.fullAddress(), isBold = true)
            Text(
                text = entityInfo.geoData.formattedDistance,
                fontSize = 12.sp,
                color = ConstColors.gray,
            )
            val activity = LocalContext.current as MainActivity
            Text(
                text = stringResource(id = R.string.see_on_the_map),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ConstColors.lightBlue,
                modifier = Modifier.clickable {
                    entityInfo.geoData.destination?.let {
                        activity.openMaps(it.latitude, it.longitude)
                    }
                },
            )
            if (entityInfo.isVerified == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Space(6.dp)
                    Text(
                        text = stringResource(id = R.string.verified),
                        color = Color(0xFF00C37D),
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                    )
                }
            }
            if (onSubscribe != null) {
                MedicoSmallButton(
                    text = stringResource(id = R.string.subscribe),
                    onClick = onSubscribe,
                )
            }
        }
    }
    Space(24.dp)
    when {
        entityInfo.subscriptionData != null -> entityInfo.subscriptionData?.let { data ->
            DataWithLabel(
                label = R.string.status,
                data = data.status.serverValue
            )
            entityInfo.gstin?.let {
                DataWithLabel(label = R.string.gstin_num, data = it)
            }
            entityInfo.panNumber?.let {
                DataWithLabel(label = R.string.pan_number, data = it)
            }
            DataWithLabel(
                label = R.string.payment_method,
                data = data.paymentMethod.serverValue
            )
            DataWithLabel(
                label = R.string.orders,
                data = data.orders.toString()
            )
        }
        entityInfo.seasonBoyRetailerData != null -> entityInfo.seasonBoyRetailerData?.let { data ->
            entityInfo.gstin?.let {
                DataWithLabel(label = R.string.gstin_num, data = it)
            }
            entityInfo.panNumber?.let {
                DataWithLabel(label = R.string.pan_number, data = it)
            }
            DataWithLabel(
                label = R.string.orders,
                data = data.orders.toString()
            )
        }
        else -> {
            entityInfo.gstin?.let {
                DataWithLabel(label = R.string.gstin_num, data = it)
            }
            entityInfo.panNumber?.let {
                DataWithLabel(label = R.string.pan_number, data = it)
            }
        }
    }
}

data class BottomSheetCell(val stringId: Int, val iconAsset: ImageVector)

@Composable
private fun SectionsBottomSheet(
    title: String,
    cells: List<BottomSheetCell>,
    onCellClick: (BottomSheetCell) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column {
            Row(
                modifier = Modifier
                    .height(52.dp)
                    .padding(start = 18.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    color = ConstColors.gray,
                )
            }
            cells.forEach {
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable { onCellClick(it) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = it.iconAsset,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    Text(
                        text = stringResource(id = it.stringId),
                        color = ConstColors.gray,
                    )
                }
            }
        }
    }
}

@Composable
private fun BaseBottomSheet(
    onDismiss: () -> Unit,
    body: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(indication = NoOpIndication) { onDismiss() })
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(indication = null) { /* intercept touches */ }
                .align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            body()
        }
    }
}