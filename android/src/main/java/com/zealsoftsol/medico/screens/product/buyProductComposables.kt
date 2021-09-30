package com.zealsoftsol.medico.screens.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.SeasonBoyRetailer
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.StockStatus
import com.zealsoftsol.medico.data.TapMode
import com.zealsoftsol.medico.data.WithTradeName
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.management.GeoLocation
import kotlin.time.ExperimentalTime

@Composable
fun BuyProductScreen(scope: BuyProductScope<WithTradeName>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoilImage(
                src = CdnUrlProvider.urlFor(scope.product.code, CdnUrlProvider.Size.Px123),
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
                    text = scope.product.name,
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                )
                Space(4.dp)
                Row {
                    Text(
                        text = scope.product.code,
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
                            append(scope.product.standardUnit.orEmpty())
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
                    text = scope.product.uomName,
                    color = ConstColors.lightBlue,
                    fontSize = 14.sp,
                )
            }
        }
        (scope as? BuyProductScope.ChooseRetailer)?.sellerInfo?.let {
            Divider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val labelColor = when (it.stockInfo?.status) {
                        StockStatus.IN_STOCK -> ConstColors.green
                        StockStatus.LIMITED_STOCK -> ConstColors.orange
                        StockStatus.OUT_OF_STOCK -> ConstColors.red
                        null -> ConstColors.gray
                    }
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawRoundRect(labelColor, cornerRadius = CornerRadius(8.dp.value))
                    }
                    Space(6.dp)
                    Text(
                        text = it.tradeName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W500,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Space(10.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("PTR: ")
                            val startIndex = length
                            append(it.priceInfo?.price?.formattedPrice.orEmpty())
                            addStyle(
                                SpanStyle(
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W800
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("MRP: ")
                            val startIndex = length
                            append(it.priceInfo?.mrp?.formattedPrice.orEmpty())
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W800,
                                    color = ConstColors.lightBlue,
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 12.sp,
                    )
                }
                Space(8.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    it.stockInfo?.expiry?.let { expiry ->
                        val color = Color(expiry.color.toColorInt())
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
                                    append(expiry.formattedDate)
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
                    }
                    Text(
                        text = buildAnnotatedString {
                            append("Stock: ")
                            val startIndex = length
                            append(it.stockInfo?.availableQty?.toString().orEmpty())
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W800,
                                    color = ConstColors.lightBlue,
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
        (scope as? BuyProductScope.ChooseQuote)?.let { chooseQuote ->
            val selectedOption = chooseQuote.selectedOption.flow.collectAsState()
            val sellers = chooseQuote.items.flow.collectAsState()
            val chosenSeller = chooseQuote.chosenSeller.flow.collectAsState()
            val quantities = scope.quantities.flow.collectAsState()

            Space(16.dp)
            QuotedItem(
                title = stringResource(id = R.string.quote_existing_stockist),
                isSelected = selectedOption.value == BuyProductScope.ChooseQuote.Option.EXISTING_STOCKIST,
                isSeasonBoy = chooseQuote.isSeasonBoy,
                qtyInitial = chosenSeller.value?.let { quantities.value[it]?.first ?: 0.0 } ?: 0.0,
                freeQtyInitial = chosenSeller.value?.let { quantities.value[it]?.second ?: 0.0 }
                    ?: 0.0,
                onSaveQty = { qty, freeQty ->
                    chosenSeller.value?.let {
                        if (qty != null && freeQty != null) {
                            scope.saveQuantitiesAndSelect(it, qty, freeQty)
                        } else {
                            scope.select(it)
                        }
                    }
                },
                onToggle = { chooseQuote.toggleOption(BuyProductScope.ChooseQuote.Option.EXISTING_STOCKIST) },
                body = {
                    Column {
                        Dropdown(
                            rememberChooseKey = chosenSeller.value,
                            value = chosenSeller.value?.tradeName,
                            hint = stringResource(id = R.string.select_stockist),
                            dropDownItems = sellers.value.map { it.tradeName },
                            backgroundColor = MaterialTheme.colors.primary,
                            arrowTintColor = ConstColors.lightBlue,
                            onSelected = { selected -> chooseQuote.chooseSeller(sellers.value.first { it.tradeName == selected }) },
                        )
                    }
                },
            )
            Space(16.dp)
            QuotedItem(
                title = stringResource(id = R.string.quote_anyone),
                isSelected = selectedOption.value == BuyProductScope.ChooseQuote.Option.ANYONE,
                isSeasonBoy = chooseQuote.isSeasonBoy,
                onToggle = { chooseQuote.toggleOption(BuyProductScope.ChooseQuote.Option.ANYONE) },
                qtyInitial = SellerInfo.anyone.let { quantities.value[it]?.first ?: 0.0 },
                freeQtyInitial = SellerInfo.anyone.let { quantities.value[it]?.second ?: 0.0 },
                onSaveQty = { qty, freeQty ->
                    if (qty != null && freeQty != null) {
                        scope.saveQuantities(SellerInfo.anyone, qty, freeQty)
                        scope.selectAnyone()
                    } else {
                        scope.selectAnyone()
                    }
                },
                body = {}
            )
        } ?: run {
            Column(modifier = Modifier.background(Color.White)) {
                val filter = scope.itemsFilter.flow.collectAsState()
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .background(
                            ConstColors.ltgray,
                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        )
                        .padding(horizontal = 16.dp)
                        .padding(top = 6.dp, bottom = 12.dp)
                ) {
                    if (filter.value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.choose_seller),
                            color = ConstColors.gray.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 2.dp),
                        )
                    }
                    BasicTextField(
                        value = filter.value,
                        cursorBrush = SolidColor(ConstColors.lightBlue),
                        onValueChange = { scope.filterItems(it) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 32.dp),
                    )
                    Space(42.dp)
                    Divider(
                        color = MaterialTheme.colors.background,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colors.background,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterEnd),
                    )
                }
            }
            val quantities = scope.quantities.flow.collectAsState()
            val sellers = scope.items.flow.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                sellers.value.forEach {
                    when (it) {
                        is SellerInfo -> SellerInfoItem(
                            sellerInfo = it,
                            isSelectable = (scope as? BuyProductScope.ChooseStockist)?.isSeasonBoy == true,
                            quantity = quantities.value[it]?.first ?: 0.0,
                            quantityFree = quantities.value[it]?.second ?: 0.0,
                            onSaveQty = { qty, freeQty ->
                                if (qty != null && freeQty != null) {
                                    scope.saveQuantitiesAndSelect(it, qty, freeQty)
                                } else {
                                    scope.select(it)
                                }
                            },
                            onItemClick = (scope as? BuyProductScope.ChooseStockist)?.let { s ->
                                {
                                    s.previewStockist(
                                        it
                                    )
                                }
                            },
                        )
                        is SeasonBoyRetailer -> SeasonBoyReatilerInfoItem(
                            seasonBoyRetailer = it,
                            quantity = quantities.value[it]?.first ?: 0.0,
                            quantityFree = quantities.value[it]?.second ?: 0.0,
                            onSaveQty = { qty, freeQty ->
                                if (qty != null && freeQty != null) {
                                    scope.saveQuantitiesAndSelect(it, qty, freeQty)
                                } else {
                                    scope.select(it)
                                }
                            },
                        )
                    }
                    Space(8.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun QuotedItem(
    title: String,
    isSelected: Boolean,
    isSeasonBoy: Boolean,
    qtyInitial: Double,
    freeQtyInitial: Double,
    onSaveQty: (Double?, Double?) -> Unit,
    onToggle: () -> Unit,
    body: @Composable () -> Unit,
) {
    Surface(
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    enabled = true,
                    onClick = onToggle,
                    colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
                )
                Space(16.dp)
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.W700 else FontWeight.W400,
                )
            }
            val qty = remember { mutableStateOf(qtyInitial) }
            val freeQty = remember { mutableStateOf(freeQtyInitial) }
            val mode = remember {
                mutableStateOf(
                    when {
                        isSeasonBoy -> BottomSectionMode.Select
                        qtyInitial > 0 || freeQtyInitial > 0 -> BottomSectionMode.Update
                        else -> BottomSectionMode.AddToCart
                    }
                )
            }
            AnimatedVisibility(visible = isSelected) {
                Column {
                    Space(16.dp)
                    Divider()
                    Space(16.dp)
                    body()
                    when (mode.value) {
                        BottomSectionMode.Select, BottomSectionMode.AddToCart, BottomSectionMode.Update -> Unit
                        BottomSectionMode.ConfirmQty -> {
                            Space(12.dp)
                            Column(horizontalAlignment = Alignment.End) {
                                val isError =
                                    (qty.value + freeQty.value) % 1 != 0.0 || freeQty.value > qty.value
                                val wasError = remember { mutableStateOf(isError) }
                                val wasErrorSaved = wasError.value
                                val focusedError = remember(mode.value) { mutableStateOf(-1) }
                                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.width(maxWidth / 3)) {
                                        EditField(
                                            label = stringResource(id = R.string.qty),
                                            qty = qty.value.toString(),
                                            isError = isError && focusedError.value == 0,
                                            onChange = { qty.value = it.toDouble() },
                                            onFocus = {
                                                if (!wasErrorSaved && isError) focusedError.value =
                                                    0
                                            },
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(maxWidth / 3)
                                            .align(Alignment.BottomEnd)
                                    ) {
                                        EditField(
                                            label = stringResource(id = R.string.free),
                                            isEnabled = qty.value > 0.0,
                                            isError = isError && focusedError.value == 1,
                                            qty = freeQty.value.toString(),
                                            onChange = { freeQty.value = it.toDouble() },
                                            onFocus = {
                                                if (!wasErrorSaved && isError) focusedError.value =
                                                    1
                                            },
                                        )
                                    }
                                }
                                if (isError) {
                                    Space(8.dp)
                                    Text(
                                        text = stringResource(id = if (freeQty.value > qty.value) R.string.free_more_qty else R.string.invalid_qty),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        color = ConstColors.red,
                                    )
                                }
                                wasError.value = isError
                            }
                        }
                    }
                    Space(12.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        when (mode.value) {
                            BottomSectionMode.Select -> {
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.select),
                                    onClick = { onSaveQty(null, null) },
                                )
                            }
                            BottomSectionMode.AddToCart -> {
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.add_to_cart),
                                    onClick = { mode.value = BottomSectionMode.ConfirmQty },
                                )
                            }
                            BottomSectionMode.ConfirmQty -> {
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.cancel),
                                    color = ConstColors.ltgray,
                                    onClick = {
                                        mode.value =
                                            if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(
                                    modifier = Modifier
                                        .weight(0.4f)
                                        .fillMaxWidth()
                                )
                                MedicoRoundButton(
                                    text = stringResource(id = R.string.confirm),
                                    isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value >= freeQty.value,
                                    onClick = {
                                        mode.value =
                                            if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                        onSaveQty(qty.value, freeQty.value)
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            BottomSectionMode.Update -> {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.weight(2f),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.qty).uppercase(),
                                        fontSize = 12.sp,
                                        color = ConstColors.gray,
                                    )
                                    Space(6.dp)
                                    Text(
                                        text = qty.value.toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W700,
                                        color = MaterialTheme.colors.background,
                                    )
                                    Space(6.dp)
                                    Text(
                                        text = "+${freeQty.value}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W700,
                                        color = ConstColors.lightBlue,
                                        modifier = Modifier
                                            .background(
                                                ConstColors.lightBlue.copy(alpha = 0.05f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                ConstColors.lightBlue,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                    )
                                }
                                MedicoRoundButton(
                                    modifier = Modifier.weight(1.5f),
                                    text = stringResource(id = R.string.update),
                                    color = ConstColors.lightBlue,
                                    contentColor = Color.White,
                                    onClick = { mode.value = BottomSectionMode.ConfirmQty },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerInfoItem(
    sellerInfo: SellerInfo,
    quantity: Double,
    quantityFree: Double,
    isSelectable: Boolean,
    onSaveQty: (Double?, Double?) -> Unit,
    onItemClick: (() -> Unit)?,
) {
    BaseSellerItem(
        qtyInitial = quantity,
        freeQtyInitial = quantityFree,
        onSaveQty = onSaveQty,
        forceMode = if (isSelectable) BottomSectionMode.Select else null,
        onItemClick = onItemClick,
        canAddToCart = sellerInfo.stockInfo?.status != StockStatus.OUT_OF_STOCK,
        headerContent = {
            val labelColor = when (sellerInfo.stockInfo?.status) {
                StockStatus.IN_STOCK -> ConstColors.green
                StockStatus.LIMITED_STOCK -> ConstColors.orange
                StockStatus.OUT_OF_STOCK -> ConstColors.red
                null -> ConstColors.gray
            }
            Canvas(modifier = Modifier.size(10.dp)) {
                drawRoundRect(labelColor, cornerRadius = CornerRadius(8.dp.value))
            }
            Space(6.dp)
            Text(
                text = sellerInfo.tradeName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        mainBodyContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("PTR: ")
                        val startIndex = length
                        append(sellerInfo.priceInfo?.price?.formattedPrice.orEmpty())
                        addStyle(
                            SpanStyle(
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.W800
                            ),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.gray,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                )
                Text(
                    text = buildAnnotatedString {
                        append("Stock: ")
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
            Space(8.dp)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                sellerInfo.stockInfo?.expiry?.let { expiry ->
                    val color = Color(expiry.color.toColorInt())
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
                                append(expiry.formattedDate)
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
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = ConstColors.lightBlue.copy(alpha = 0.1f),
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
        },
    )
}

@Composable
private fun SeasonBoyReatilerInfoItem(
    seasonBoyRetailer: SeasonBoyRetailer,
    quantity: Double,
    quantityFree: Double,
    onSaveQty: (Double?, Double?) -> Unit,
) {
    BaseSellerItem(
        qtyInitial = quantity,
        freeQtyInitial = quantityFree,
        onSaveQty = onSaveQty,
        onItemClick = null,
        canAddToCart = true,
        headerContent = {
            Text(
                text = seasonBoyRetailer.tradeName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        mainBodyContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GeoLocation(
                    seasonBoyRetailer.geoData.fullAddress(),
                    isBold = true,
                    textSize = 12.sp,
                    tint = MaterialTheme.colors.background,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = ConstColors.lightBlue.copy(alpha = 0.1f),
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
                        text = "${seasonBoyRetailer.geoData.distance} km",
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NeededSurface(
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            content = content,
        )
    } else {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            content = content,
        )
    }
}

@Composable
private fun BaseSellerItem(
    headerContent: @Composable RowScope.() -> Unit,
    mainBodyContent: @Composable ColumnScope.() -> Unit,
    qtyInitial: Double,
    freeQtyInitial: Double,
    canAddToCart: Boolean,
    onSaveQty: (Double?, Double?) -> Unit,
    forceMode: BottomSectionMode? = null,
    onItemClick: (() -> Unit)? = null,
) {
    val qty = remember { mutableStateOf(qtyInitial) }
    val freeQty = remember { mutableStateOf(freeQtyInitial) }
    val mode = remember {
        mutableStateOf(
            forceMode
                ?: if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
        )
    }

    NeededSurface(if (mode.value == BottomSectionMode.Update || mode.value == BottomSectionMode.AddToCart) onItemClick else null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) { headerContent() }
            Space(16.dp)
            when (mode.value) {
                BottomSectionMode.Select, BottomSectionMode.AddToCart, BottomSectionMode.Update -> mainBodyContent()
                BottomSectionMode.ConfirmQty -> Column(horizontalAlignment = Alignment.End) {
                    val isError =
                        (qty.value + freeQty.value) % 1 != 0.0 || freeQty.value > qty.value
                    val wasError = remember { mutableStateOf(isError) }
                    val wasErrorSaved = wasError.value
                    val focusedError = remember(mode.value) { mutableStateOf(-1) }
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 3)) {
                            EditField(
                                label = stringResource(id = R.string.qty),
                                qty = qty.value.toString(),
                                isError = isError && focusedError.value == 0,
                                onChange = { qty.value = it.toDouble() },
                                onFocus = { if (!wasErrorSaved && isError) focusedError.value = 0 },
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 3)
                                .align(Alignment.BottomEnd)
                        ) {
                            EditField(
                                label = stringResource(id = R.string.free),
                                isEnabled = qty.value > 0.0,
                                isError = isError && focusedError.value == 1,
                                qty = freeQty.value.toString(),
                                onChange = { freeQty.value = it.toDouble() },
                                onFocus = { if (!wasErrorSaved && isError) focusedError.value = 1 },
                            )
                        }
                    }
                    if (isError) {
                        Space(8.dp)
                        Text(
                            text = stringResource(id = if (freeQty.value > qty.value) R.string.free_more_qty else R.string.invalid_qty),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            color = ConstColors.red,
                        )
                    }
                    wasError.value = isError
                }
            }
            Space(10.dp)
            Divider(color = ConstColors.ltgray)
            Space(12.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (mode.value) {
                    BottomSectionMode.Select -> {
                        MedicoRoundButton(
                            text = stringResource(id = R.string.select),
                            onClick = { onSaveQty(null, null) },
                        )
                    }
                    BottomSectionMode.AddToCart -> {
                        MedicoRoundButton(
                            text = stringResource(id = R.string.add_to_cart),
                            onClick = {
                                if (canAddToCart) {
                                    mode.value = BottomSectionMode.ConfirmQty
                                }
                            },
                            color = if (canAddToCart) ConstColors.yellow else Color.LightGray,
                        )
                    }
                    BottomSectionMode.ConfirmQty -> {
                        MedicoRoundButton(
                            text = stringResource(id = R.string.cancel),
                            color = ConstColors.ltgray,
                            onClick = {
                                mode.value =
                                    if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                qty.value = qtyInitial
                                freeQty.value = freeQtyInitial
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxWidth()
                        )
                        MedicoRoundButton(
                            text = stringResource(id = R.string.confirm),
                            isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value > 0.0 && qty.value >= freeQty.value,
                            onClick = {
                                mode.value =
                                    if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                onSaveQty(qty.value, freeQty.value)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    BottomSectionMode.Update -> {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.weight(2f),
                        ) {
                            Text(
                                text = stringResource(id = R.string.qty).uppercase(),
                                fontSize = 12.sp,
                                color = ConstColors.gray,
                            )
                            Space(6.dp)
                            Text(
                                text = qty.value.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W700,
                                color = MaterialTheme.colors.background,
                            )
                            Space(6.dp)
                            Text(
                                text = "+${freeQty.value}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W700,
                                color = ConstColors.lightBlue,
                                modifier = Modifier
                                    .background(
                                        ConstColors.lightBlue.copy(alpha = 0.05f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(1.dp, ConstColors.lightBlue, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                            )
                        }
                        MedicoRoundButton(
                            modifier = Modifier.weight(1.5f),
                            text = stringResource(id = R.string.update),
                            color = ConstColors.lightBlue,
                            contentColor = Color.White,
                            onClick = { mode.value = BottomSectionMode.ConfirmQty },
                        )
                    }
                }
            }
        }
    }
}

sealed class BottomSectionMode {
    object Select : BottomSectionMode()
    object AddToCart : BottomSectionMode()
    object ConfirmQty : BottomSectionMode()
    object Update : BottomSectionMode()
}

@OptIn(ExperimentalTime::class)
@Composable
fun PlusMinusQuantity(
    modifier: Modifier = Modifier,
    quantity: Int,
    isEnabled: Boolean,
    max: Int = Int.MAX_VALUE,
    onInc: (TapMode) -> Unit,
    onDec: (TapMode) -> Unit,
) {
    Row(
        modifier = modifier.defaultMinSize(minWidth = 100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Icon(
            imageVector = Icons.Default.Remove,
            tint = if (isEnabled && quantity > 0) ConstColors.lightBlue else ConstColors.gray.copy(
                alpha = 0.5f
            ),
            contentDescription = null,
            modifier = if (isEnabled && quantity > 0) Modifier.pointerInput(isEnabled) {
                var isLongTap = false
                detectTapGestures(
                    onLongPress = {
                        isLongTap = true
                        onDec(TapMode.LONG_PRESS)
                    },
                    onTap = {
                        onDec(TapMode.CLICK)
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (isLongTap) {
                            onDec(TapMode.RELEASE)
                            isLongTap = false
                        }
                    },
                )
            } else Modifier,
        )
        Space(12.dp)
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colors.background.copy(if (isEnabled) 1f else 0.5f),
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
        )
        Space(12.dp)
        Icon(
            imageVector = Icons.Default.Add,
            tint = if (isEnabled && quantity < max) ConstColors.lightBlue else ConstColors.gray.copy(
                alpha = 0.5f
            ),
            contentDescription = null,
            modifier = if (isEnabled && quantity < max) Modifier.pointerInput(isEnabled) {
                var isLongTap = false
                detectTapGestures(
                    onLongPress = {
                        isLongTap = true
                        onInc(TapMode.LONG_PRESS)
                    },
                    onTap = {
                        onInc(TapMode.CLICK)
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (isLongTap) {
                            onInc(TapMode.RELEASE)
                            isLongTap = false
                        }
                    },
                )
            } else Modifier,
        )
    }
}