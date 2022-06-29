package com.zealsoftsol.medico.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import com.zealsoftsol.medico.core.extensions.screenHeight
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.BaseSearchScope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.HeaderData
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderTaxInfo
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.StockInfo
import com.zealsoftsol.medico.data.SubscriptionStatus
import com.zealsoftsol.medico.data.TaxInfo
import com.zealsoftsol.medico.data.TaxType
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.Value
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.CoilImageBrands
import com.zealsoftsol.medico.screens.common.CoilImageZoom
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.EditField
import com.zealsoftsol.medico.screens.common.EditText
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.ItemPlaceholder
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.PlaceholderText
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.SingleTextLabel
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia
import com.zealsoftsol.medico.screens.common.roundToNearestDecimalOf5
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.ioc.SpinnerItem
import com.zealsoftsol.medico.screens.management.GeoLocationSheet
import com.zealsoftsol.medico.screens.product.BottomSectionMode
import com.zealsoftsol.medico.screens.product.ProductAlternative
import com.zealsoftsol.medico.screens.search.BatchItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
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
                    name = bs.type,
                    supportedFileTypes = bs.supportedFileTypes,
                    useCamera = !bs.isSeasonBoy,
                    activity = activity,
                    coroutineScope = coroutineScope,
                    onFileReady = {
                        if (bs.type.isEmpty()) {
                            bs.handleFileUpload(it)
                        } else {
                            bs.handleUpload(it, bs.type, bs.registrationStep1)
                        }
                    },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.UploadProfileData -> {
                DocumentUploadBottomSheet(
                    supportedFileTypes = bs.supportedFileTypes,
                    useCamera = !bs.isSeasonBoy,
                    activity = activity,
                    coroutineScope = coroutineScope,
                    onFileReady = { bs.handleProfileUpload(it, bs.type) },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.GetOcrImageData -> {
                DocumentUploadBottomSheet(
                    supportedFileTypes = bs.supportedFileTypes,
                    useCamera = true,
                    activity = activity,
                    coroutineScope = coroutineScope,
                    onFileReady = { bs.handleOcrImage(it, bs.type) },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.UploadInvoiceData -> {
                DocumentUploadBottomSheet(
                    supportedFileTypes = bs.supportedFileTypes,
                    useCamera = false,
                    activity = activity,
                    coroutineScope = coroutineScope,
                    onFileReady = { bs.handleInvoiceUpload(it, bs.type) },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.PreviewManagementItem -> PreviewItemBottomSheet(
                headerData = bs.headerData,
                isForSeasonBoy = bs.isSeasonBoy,
                onSubscribe = if (bs.canSubscribe) {
                    { bs.subscribe() }
                } else null,
                onDismiss = { dismissBottomSheet() },
                bs,
            )
            is BottomSheet.UpdateOfferStatus -> UpdateOfferItemBottomSheet(
                info = bs.info,
                name = bs.name,
                active = bs.active,
                onSubscribe = {
                    dismissBottomSheet()
                    bs.update()
                },
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.UpdateOffer -> EditOfferItemBottomSheet(
                info = bs,
                onSubscribe = {
                    dismissBottomSheet()
                    bs.update()
                },
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
            is BottomSheet.InStoreViewProduct -> InStoreViewProductBottomSheet(
                product = bs.product,
                onSaveQty = { qty, freeQty -> bs.addToCart(qty, freeQty) },
                onDismiss = { dismissBottomSheet() },
            )

            is BottomSheet.BatchViewProduct -> BatchViewProductBottomSheet(
                product = bs.product,
                onSaveQty = { qty, freeQty -> bs.addToCart(qty, freeQty) },
                onDismiss = { dismissBottomSheet() },
                scope = bs.scope
            )
            is BottomSheet.EditBatchSheet -> EditBatchItemBottomSheet(
                info = bs,
                onSubscribe = {
                    dismissBottomSheet()
                    bs.editBatch()
                },
                onDismiss = { dismissBottomSheet() }
            )
            is BottomSheet.ViewLargeImage -> ViewLargeImageBottomSheet(
                url = bs.url,
                type = bs.type,
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.InvoiceViewProduct -> ViewInvoiceBottomSheet(
                info = bs.orderTaxDetails,
                onDismiss = {
                    dismissBottomSheet()
                    bs.confirm()
                },
            )
            is BottomSheet.InvoiceViewItemProduct -> ViewInvoiceItemTaxBottomSheet(
                orderEntry = bs.orderDetails,
                onDismiss = {
                    dismissBottomSheet()
                    bs.confirm()
                },
                bs.scope
            )

            is BottomSheet.EditCartItem -> ViewEditCartBottomSheet(
                bs.qtyInitial,
                bs.freeQtyInitial,
                bs.item,
                onDismiss = {
                    dismissBottomSheet()
                },
                onUpdate = { qtyInitial,
                             freeQtyInitial ->
                    dismissBottomSheet()
                    bs.cartScope.updateItemCount(
                        bs.sellerCart,
                        bs.item,
                        qtyInitial,
                        freeQtyInitial
                    )
                }
            )
            is BottomSheet.ShowConnectedStockist -> ShowConnectedStockist(stockist = bs.stockist) { dismissBottomSheet() }
            is BottomSheet.EditIOC -> EditIOCBottomSheet(
                bs,
                onConfirm = {
                    dismissBottomSheet()
                    bs.confirm()
                },
                onDismiss = { dismissBottomSheet() },
            )
            is BottomSheet.AlternateProducts -> ShowAlternateProducts(
                bs.productList,
                bs,
                onDismiss = { dismissBottomSheet() })
            is BottomSheet.FilerManufacturers -> ShowManufacturersFilter(
                data = bs.listManufacturers,
                selectedFilters = bs.selectedFilters,
                bs,
                tradeName = bs.tradeName,
                onDismiss = { dismissBottomSheet() }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun ShowManufacturersFilter(
    data: List<Value>,
    selectedFilters: List<Value> = emptyList(),
    bs: BottomSheet.FilerManufacturers,
    tradeName: String,
    onDismiss: () -> Unit
) {

    val searchTerm = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listOfManufacturers = remember { mutableStateOf(data) }
    val listSelectedItems = remember { mutableStateOf(selectedFilters) }

    BaseBottomSheet(onDismiss) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp, top = 10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = tradeName,
                        color = ConstColors.lightBlue,
                        fontSize = 14.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.W700,
                        overflow = TextOverflow.Ellipsis
                    )
                    Space(5.dp)
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = ConstColors.gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onDismiss() },
                    )
                }

                Space(15.dp)

                if (listSelectedItems.value.isNotEmpty()) {

                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "${stringResource(id = R.string.selected)}(${listSelectedItems.value.size})",
                        color = ConstColors.lightBlue,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Space(10.dp)

                    listSelectedItems.value.let {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            itemsIndexed(
                                items = it.asReversed(),
                                key = { index: Int, _: Value -> index },
                                itemContent = { index, item ->
                                    Column(horizontalAlignment = CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .height(55.dp)
                                                .width(55.dp),
                                            contentAlignment = TopEnd
                                        ) {
                                            Surface(
                                                elevation = 5.dp,
                                                shape = CircleShape,
                                                color = Color.White,
                                            ) {

                                                CoilImageBrands(
                                                    src = CdnUrlProvider.urlForM(item.id),
                                                    contentScale = ContentScale.Crop,
                                                    onError = { ItemPlaceholder() },
                                                    onLoading = { ItemPlaceholder() },
                                                    height = 55.dp,
                                                    width = 55.dp,
                                                )
                                            }
                                            Icon(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        listSelectedItems.value =
                                                            listSelectedItems.value - item
                                                    },
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = ConstColors.gray,
                                            )
                                        }
                                        Space(5.dp)
                                        Text(
                                            modifier = Modifier
                                                .width(70.dp),
                                            text = item.value,
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            )
                        }

                    }
                    Space(10.dp)
                    Divider()
                    Space(15.dp)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = 5.dp,
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                border = BorderStroke(1.dp, color = Color.LightGray),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        value = searchTerm.value,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            textColor = Color.Black,
                            placeholderColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        onValueChange = {
                            searchTerm.value = it
                            if (it.isNotEmpty()) {
                                val searchList: List<Value> = data.filter { s ->
                                    s.value.lowercase().contains(it.lowercase())
                                }
                                listOfManufacturers.value = searchList
                            } else {
                                listOfManufacturers.value = data
                            }
                        },
                        placeholder = {
                            Text(
                                modifier = Modifier
                                    .height(50.dp),
                                text = stringResource(id = R.string.search),
                                color = ConstColors.txtGrey,
                                fontSize = 14.sp
                            )
                        },
                        maxLines = 1

                    )
                }

                Space(15.dp)

                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "${stringResource(id = R.string.manufacturers)}(${listOfManufacturers.value.size})",
                    color = ConstColors.lightBlue,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Space(10.dp)

                LazyVerticalGrid(
                    cells = GridCells.Fixed(3),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(listOfManufacturers.value.size) {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Space(10.dp)
                            Surface(
                                modifier = Modifier
                                    .height(55.dp)
                                    .width(55.dp)
                                    .clickable {
                                        val contains = listSelectedItems.value.any { data ->
                                            data.id ==
                                                    listOfManufacturers.value[it].id
                                        }
                                        if (!contains)
                                            listSelectedItems.value =
                                                listSelectedItems.value + listOfManufacturers.value[it]
                                        else
                                            listSelectedItems.value =
                                                listSelectedItems.value - listOfManufacturers.value[it]
                                    },
                                elevation = 5.dp,
                                shape = CircleShape,
                                color = Color.White,
                            ) {
                                CoilImageBrands(
                                    src = CdnUrlProvider.urlForM(listOfManufacturers.value[it].id),
                                    contentScale = ContentScale.Crop,
                                    onError = { ItemPlaceholder() },
                                    onLoading = { ItemPlaceholder() },
                                    height = 55.dp,
                                    width = 55.dp,
                                )
                            }
                            Space(5.dp)
                            Text(
                                text = listOfManufacturers.value[it].value,
                                color = Color.Black,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            Space(10.dp)
            MedicoButton(
                modifier = Modifier.padding(20.dp),
                text = stringResource(id = R.string.apply), isEnabled = true,
                txtColor = Color.White, color = ConstColors.lightBlue
            ) {
                onDismiss()
                bs.updateSelectedFilter(listSelectedItems.value)
            }
        }

    }
}

@Composable
private fun ShowAlternateProducts(
    productList: List<AlternateProductData>,
    bs: BottomSheet.AlternateProducts,
    onDismiss: () -> Unit
) {
    BaseBottomSheet(onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            bs.sellerName?.let {
                Space(10.dp)
                Text(
                    text = "${stringResource(id = R.string.alternative_brands)} $it ",
                    color = Color.Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )
                Space(16.dp)
            }
            Text(
                text = stringResource(id = R.string.alt_brands),
                color = Color.Black,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
            )
            Space(16.dp)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(3.dp)
            ) {
                itemsIndexed(
                    items = productList,
                    itemContent = { _, item ->
                        ProductAlternative(item) {
                            onDismiss()
                            bs.selectAlternativeProduct(item)
                        }
                    },
                )
            }
        }
    }

}

@Composable
private fun ShowConnectedStockist(stockist: List<ConnectedStockist>, onDismiss: () -> Unit) {
    BaseBottomSheet(onDismiss) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ConstColors.gray,
                    modifier = Modifier
                        .size(25.dp)
                        .padding(end = 5.dp, top = 5.dp)
                        .clickable { onDismiss() },
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_connected),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.connected_stockist)}:",
                    color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.W700
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 3.dp),
                modifier = Modifier
                    .heightIn(0.dp, 380.dp) //mention max height here
                    .fillMaxWidth(),
            ) {
                itemsIndexed(
                    items = stockist,
                    key = { index, _ -> index },
                    itemContent = { index, item ->
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 5.dp)
                            ) {
                                Image(
                                    painter = if (item.connected) painterResource(id = R.drawable.ic_connected) else painterResource(
                                        id = R.drawable.ic_not_connected
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(end = 5.dp),
                                )
                                Text(
                                    text = item.tradeName,
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                                    .padding(bottom = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.location,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                )
                                Row(modifier = Modifier.padding(end = 16.dp)) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_location),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(25.dp)
                                            .padding(end = 5.dp)
                                    )
                                    Text(
                                        text = item.distance.formatted,
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    },
                )
            }
            Space(10.dp)
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InStoreViewProductBottomSheet(
    product: InStoreProduct,
    onSaveQty: (Double, Double) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
                        product.code,
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
                        text = product.name,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                    )
                    Space(4.dp)
                    Row {
                        Text(
                            text = product.code,
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
                                append(product.standardUnit)
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
//                    Space(4.dp)
//                    Text(
//                        text = product.uom,
//                        color = ConstColors.lightBlue,
//                        fontSize = 14.sp,
//                    )
                }
            }
            val qtyInitial = product.order?.quantity?.value ?: 0.0
            val freeQtyInitial = product.order?.freeQty?.value ?: 0.0
            val qty = remember { mutableStateOf(qtyInitial) }
            val freeQty = remember { mutableStateOf(freeQtyInitial) }
            val mode = remember {
                mutableStateOf(
                    if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) { headerContent() }
//                Space(8.dp)
                when (mode.value) {
                    BottomSectionMode.AddToCart -> Unit
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
                                    onFocus = {
                                        if (!wasErrorSaved && isError) focusedError.value = 0
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            if (qty.value > 0 && !isError) {
                                                mode.value =
                                                    if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                                onSaveQty(qty.value, freeQty.value)
                                                onDismiss()
                                            }

                                        }
                                    )
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
                                        if (!wasErrorSaved && isError) focusedError.value = 1
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            if (qty.value > 0 && !isError) {
                                                mode.value =
                                                    if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                                                onSaveQty(qty.value, freeQty.value)
                                                onDismiss()
                                            }
                                        }
                                    )
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
                    else -> {

                    }
                }
                Space(10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (mode.value) {
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
                                    onDismiss()
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
                        else -> {

                        }
                    }
                }
            }
            Space(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .run {
                        if (product.isPromotionActive && product.promotionData != null)
                            border(2.dp, ConstColors.red, RoundedCornerShape(4.dp)).padding(16.dp)
                        else
                            this
                    },
            ) {
                product.promotionData?.takeIf { product.isPromotionActive }?.let {
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
                            text = product.priceInfo.price.formattedPrice,
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
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(ConstColors.red, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.no_avail_offer),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .border(1.dp, ConstColors.gray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.priceInfo.price.formattedPrice,
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
                                text = product.stockInfo.availableQty.toString(),
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
                    val color = product.stockInfo.expiry.color.toColorInt().let { Color(it) }
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
                                append(product.stockInfo.expiry.formattedDate)
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
                    Space(dp = 1.dp)
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .background(
//                                color = MaterialTheme.colors.primary,
//                                shape = MaterialTheme.shapes.small
//                            )
//                            .padding(4.dp),
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.LocationOn,
//                            contentDescription = null,
//                            tint = ConstColors.lightBlue,
//                            modifier = Modifier.size(10.dp),
//                        )
//                        Space(4.dp)
//                        Text(
//                            text = "${product.geoData.distance} km",
//                            color = ConstColors.lightBlue,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.W600,
//                        )
//                    }
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
                            append(product.priceInfo.mrp.formattedPrice)
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
                            append(product.priceInfo.marginPercent)
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
private fun BatchViewProductBottomSheet(
    product: ProductSearch,
    onSaveQty: (Double, Double) -> Unit,
    onDismiss: () -> Unit,
    scope: BaseSearchScope
) {
    BaseBottomSheet(onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            ) {

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(maxWidth / 2)
                            .align(Alignment.CenterStart),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = stringResource(id = R.string.batchs),
                            color = MaterialTheme.colors.background,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(maxWidth / 2)
                            .align(Alignment.BottomEnd)
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
                    }
                }

            }

            val sliderList = ArrayList<StockInfo>()
            sliderList.add(product.stockInfo!!)

            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier.height(maxHeight - 120.dp),
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    itemsIndexed(
                        items = sliderList,
                        itemContent = { _, value ->
                            BatchItem(
                                value
                            ) {
                                scope.selectBatch(false, product = product)
                                onDismiss()
                            }
                        },
                    )
                }
            }
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
    name: String? = "",
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
            //Uncomment the code to open camera option for upload
            /*if (useCamera)
                BottomSheetCell(R.string.use_camera, Icons.Filled.CameraAlt)
            else
                null*/
        ),
        onCellClick = { (stringId, _) ->
            coroutineScope.launch {
                val file = when (stringId) {
                    R.string.upload -> activity.openFilePicker(supportedFileTypes)
                    R.string.use_camera -> activity.takePicture(name)
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
    headerData: HeaderData,
    isForSeasonBoy: Boolean,
    onSubscribe: (() -> Unit)?,
    onDismiss: () -> Unit,
    bs: BottomSheet.PreviewManagementItem,
) {
    val activity = LocalContext.current as MainActivity
    val isExpired =
        headerData.dlExpiryDate.value != 0.0 && Time.now >= headerData.dlExpiryDate.value

    BaseBottomSheet(onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.12f),
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp),
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,

                            tint = ConstColors.gray,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Space(dp = 16.dp)
                Column {
                    CoilImage(
                        src = headerData.tradeProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        onError = { PlaceholderText() },
                        onLoading = { PlaceholderText() },
                        isCrossFadeEnabled = false
                    )
                    Space(dp = 8.dp)
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
                            text = headerData.tradeName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colors.background,
                            modifier = Modifier.padding(end = 10.dp),
                        )
                        if (headerData.isVerified) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_verified),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                    Space(4.dp)
                    Text(
                        text = headerData.name,
                        color = MaterialTheme.colors.background,
                        fontSize = 14.sp
                    )
                    Space(4.dp)
                    headerData.mobileNumber.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ClickableText(
                                text = AnnotatedString(it),
                                style = TextStyle(
                                    color = ConstColors.gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.W600
                                ),
                                onClick = { activity.openDialer(headerData.mobileNumber) },
                            )
                            Text(
                                text = when {
                                    isExpired -> stringResource(id = R.string.expired).uppercase()
                                    headerData.dlExpiryDate.value == 0.0 -> stringResource(id = R.string.not_available).uppercase()
                                    else -> ""
                                },
                                fontSize = 15.sp,
                                color = ConstColors.red,
                                fontWeight = FontWeight.W600
                            )

                        }
                    }
                    if (isForSeasonBoy) {
                        Space(8.dp)
                        SeasonBoyPreviewItem(headerData)
                    } else {
                        NonSeasonBoyPreviewItem(headerData, onSubscribe, bs, onDismiss, isExpired)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UpdateOfferItemBottomSheet(
    info: String?,
    name: String,
    active: Boolean,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.12f),
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp),
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (!active) stringResource(id = R.string.stop_offer_message) else stringResource(
                            id = R.string.start_offer_message
                        ), fontSize = 14.sp
                    )
                    Text(
                        text = "$name?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Space(dp = 16.dp)

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(
                        modifier = Modifier.weight(0.4f),
                        color = ConstColors.yellow,
                        shape = MaterialTheme.shapes.large,
                        onClick = onDismiss,
                        elevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(all = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                fontSize = 14.sp,
                                color = MaterialTheme.colors.background
                            )
                        }

                    }
                    Space(dp = 16.dp)
                    Surface(
                        modifier = Modifier.weight(0.4f),
                        shape = MaterialTheme.shapes.medium,
                        color = ConstColors.txtGrey,
                        onClick = onSubscribe,
                        elevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(all = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.okay),
                                fontSize = 14.sp,
                                color = MaterialTheme.colors.background,
                            )
                        }
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EditOfferItemBottomSheet(
    info: BottomSheet.UpdateOffer,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        val promo = info.promo.flow.collectAsState()
        val free = info.freeQuantity.flow.collectAsState()
        val types = info.types
        val type = info.promotionType.flow.collectAsState()
        val active = info.active.flow.collectAsState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.12f),
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp),
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Space(dp = 16.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(41.dp)
                            .background(ConstColors.ltgray, MaterialTheme.shapes.large)
                    ) {
                        types.forEach {
                            var boxMod = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                            boxMod = if (types.size == 1) {
                                boxMod
                            } else {
                                boxMod
                                    .padding(1.dp)
                                    .clickable { info.updatePromotionType(it.code) }
                            }
                            val isActive = type.value == it.code
                            boxMod = if (isActive) {
                                boxMod.background(
                                    ConstColors.lightGreen,
                                    MaterialTheme.shapes.large
                                )
                            } else {
                                boxMod
                            }
                            Row(
                                modifier = boxMod,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = it.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.W600,
                                    color = if (isActive) Color.White else MaterialTheme.colors.background,
                                    modifier = Modifier.padding(all = 2.dp)
                                )
                            }
                        }
                    }
                }

                Space(dp = 16.dp)


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = promo.value.productName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.background,
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = promo.value.manufacturerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = ConstColors.gray,
                    )
                    Space(dp = 8.dp)
                    val activeIndex = info.getIndex()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column(
                            modifier = Modifier
                                .width(130.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (activeIndex == 0) stringResource(id = R.string.qty).uppercase() else stringResource(
                                        id = R.string.discount
                                    ).uppercase(),
                                    color = ConstColors.gray,
                                    fontSize = 12.sp,
                                )

                                val wasBuy = remember {
                                    mutableStateOf(
                                        if (promo.value.buy.formatted.split(".")
                                                .lastOrNull() == "0"
                                        ) promo.value.buy.formatted.split(".")
                                            .first() else promo.value.buy.formatted
                                    )
                                }
                                val wasDis = remember {
                                    mutableStateOf(
                                        if (promo.value.productDiscount.formatted.split(".")
                                                .lastOrNull() == "0"
                                        ) promo.value.productDiscount.formatted.split(".")
                                            .first() else promo.value.productDiscount.formatted
                                    )
                                }

                                BasicTextField(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    value = TextFieldValue(
                                        if (activeIndex == 0) {
                                            wasBuy.value
                                        } else {
                                            wasDis.value
                                        },
                                        selection = TextRange(
                                            (if (activeIndex == 0) {
                                                wasBuy.value
                                            } else {
                                                wasDis.value
                                            }).length
                                        )
                                    ),
                                    onValueChange = {
                                        val split =
                                            it.text.replace(",", ".").split(".")
                                        val beforeDot = split[0]
                                        val afterDot = split.getOrNull(1)
                                        var modBefore =
                                            beforeDot.toIntOrNull() ?: 0
                                        val modAfter = when (afterDot?.length) {
                                            0 -> "."
                                            in 1..Int.MAX_VALUE -> when (afterDot!!.take(
                                                1
                                            ).toIntOrNull()) {
                                                0 -> ".0"
                                                in 1..4 -> ".0"
                                                5 -> ".5"
                                                in 6..9 -> {
                                                    modBefore++
                                                    ".0"
                                                }
                                                null -> ""
                                                else -> throw UnsupportedOperationException(
                                                    "cant be that"
                                                )
                                            }
                                            null -> ""
                                            else -> throw UnsupportedOperationException(
                                                "cant be that"
                                            )
                                        }
                                        if (activeIndex == 0) {
                                            wasBuy.value = "$modBefore$modAfter"
                                            info.updateQuantity(wasBuy.value.toDouble())
                                        } else {
                                            wasDis.value = "$modBefore$modAfter"
                                            info.updateDiscount(wasDis.value.toDouble())
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    maxLines = 1,
                                    singleLine = true,
                                    readOnly = false,
                                    enabled = true,
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colors.background,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.W700,
                                        textAlign = TextAlign.End,
                                    )
                                )
                            }
                            Divider(
                                color = MaterialTheme.colors.background,
                                thickness = 1.5.dp
                            )
                        }
                        if (activeIndex == 0) {
                            Box(modifier = Modifier.width(130.dp)) {
                                EditField(
                                    label = stringResource(id = R.string.free),
                                    qty = free.value.toString(),
                                    onChange = { info.updateFreeQuantity(it.toDouble()) },
                                    isEnabled = true,
                                )
                            }
                        }
                    }
                }
                Space(dp = 12.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val switchEnabled = remember { mutableStateOf(active.value) }
                    Box(modifier = Modifier.width(120.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.stop),
                                    color = ConstColors.red,
                                    fontSize = 14.sp,
                                    fontWeight = if (!switchEnabled.value) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "/",
                                    color = MaterialTheme.colors.background,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = stringResource(id = R.string.start),
                                    color = ConstColors.lightGreen,
                                    fontSize = 14.sp,
                                    fontWeight = if (switchEnabled.value) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Space(dp = 4.dp)
                            Switch(
                                checked = switchEnabled.value,
                                onCheckedChange = {
                                    switchEnabled.value = it
                                    info.updateActive(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ConstColors.green,
                                    uncheckedThumbColor = ConstColors.red,
                                )
                            )
                            Space(dp = 4.dp)
                        }
                    }

                    Box(modifier = Modifier.width(120.dp)) {
                        MedicoButton(
                            text = stringResource(id = R.string.save),
                            isEnabled = true,
                            height = 35.dp,
                            elevation = null,
                            onClick = onSubscribe,
                            textSize = 14.sp,
                            color = ConstColors.yellow,
                            txtColor = MaterialTheme.colors.background
                        )
                    }
                }
            }
            Space(dp = 16.dp)
        }
    }
}

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EditBatchItemBottomSheet(
    info: BottomSheet.EditBatchSheet,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        val enableButton = info.canSave.flow.collectAsState()
        val quantity = info.quantity.flow.collectAsState()
        val mrp = info.mrp.flow.collectAsState()
        val ptr = info.ptr.flow.collectAsState()
        val expiry = info.expiry.flow.collectAsState()
        val batchNo = info.batchNo.flow.collectAsState()
        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val displayPtrError = remember { mutableStateOf(false) }

        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(vertical = 16.dp, horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.12f),
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(24.dp),
                        ) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = ConstColors.gray,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }

                    Space(dp = 16.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.ptr),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(0.3f),
                                fontWeight = FontWeight.W600
                            )
                            Surface(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(55.dp),
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(1.dp, ConstColors.separator)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    EditText(
                                        modifier = Modifier.padding(end = 10.dp),
                                        canEdit = true,
                                        value = ptr.value,
                                        onChange = { info.updatePtr(it) })
                                }
                            }
                        }
                        Space(dp = 8.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.mrp),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.weight(0.3f)
                            )

                            Surface(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(55.dp),
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(1.dp, ConstColors.separator)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    EditText(
                                        modifier = Modifier.padding(end = 10.dp),
                                        canEdit = true,
                                        value = mrp.value,
                                        onChange = { info.updateMrp(it) })
                                }
                            }
                        }
                        Space(dp = 8.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.stock),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.weight(0.3f)
                            )

                            Surface(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(55.dp),
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(1.dp, ConstColors.separator)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    EditText(
                                        modifier = Modifier.padding(end = 10.dp),
                                        canEdit = true,
                                        value = quantity.value,
                                        onChange = {
                                            if (it.contains(".")) {
                                                info.updateQuantity(
                                                    roundToNearestDecimalOf5(it)
                                                )
                                            } else {
                                                info.updateQuantity(it)
                                            }
                                        })
                                }
                            }
                        }
                        Space(dp = 8.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.exp_mm_yyyy),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.weight(0.3f)
                            )

                            Surface(modifier = Modifier
                                .weight(0.7f)
                                .height(55.dp),
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(1.dp, ConstColors.separator),
                                onClick = {
                                    val now = DateTime.now()
                                    val dialog = DatePickerDialog(
                                        context,
                                        { _, year, month, _ ->
                                            info.updateExpiry("${month + 1}/${year}")
                                        },
                                        now.year,
                                        now.monthOfYear,
                                        now.dayOfMonth,
                                    )

                                    val today = LocalDate.now()
                                    val firstOfNextMonth: LocalDate = today // add one to the month
                                        .withMonthOfYear(today.monthOfYear + 1) // and take the first day of that month
                                        .withDayOfMonth(1)
                                    dialog.datePicker.minDate = firstOfNextMonth.toDate().time
                                    dialog.show()
                                }) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = expiry.value,
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W600,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                        Space(dp = 8.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.batch_no_),
                                color = ConstColors.gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.weight(0.3f)
                            )

                            Surface(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(55.dp),
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(1.dp, ConstColors.separator)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    BasicTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 10.dp),
                                        value = batchNo.value,
                                        onValueChange = {
                                            if (it.length <= 20)
                                                info.updateBatch(it)
                                        },
                                        maxLines = 1,
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                                        enabled = true,
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.W600,
                                            textAlign = TextAlign.End,
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Space(dp = 16.dp)

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MedicoButton(
                        text = stringResource(id = R.string.save),
                        isEnabled = enableButton.value,
                        height = 50.dp,
                        elevation = null,
                        onClick = {
                            if (ptr.value.toDouble() > mrp.value.toDouble()) {
                                displayPtrError.value = true
                            } else {
                                onSubscribe()
                            }
                        },
                        textSize = 14.sp,
                        color = ConstColors.yellow,
                        txtColor = MaterialTheme.colors.background,
                    )
                }
                Space(dp = 16.dp)
            }
            if (displayPtrError.value)
                ShowAlert(stringResource(id = R.string.ptr_more_warning)) {
                    displayPtrError.value = false
                }
        }
    }
}


@Composable
private fun SeasonBoyPreviewItem(entityInfo: HeaderData) {
    val phoneNumber = entityInfo.mobileNumber.formatIndia()
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun NonSeasonBoyPreviewItem(
    entityInfo: HeaderData,
    onSubscribe: (() -> Unit)?,
    bs: BottomSheet.PreviewManagementItem,
    onDismiss: () -> Unit,
    isExpired: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val borderColor = if (isExpired) ConstColors.red else ConstColors.separator

    Space(8.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = entityInfo.geoData.addressLine,
                fontSize = 12.sp,
                color = ConstColors.gray,
            )
            Space(4.dp)
            Divider(thickness = 0.3.dp)
            Space(4.dp)
            GeoLocationSheet(entityInfo.geoData.landmark, isBold = true, textSize = 12.sp)

            entityInfo.geoData.let { data ->
                Space(4.dp)
                Divider(thickness = 0.3.dp)
                Space(4.dp)
                Row {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(maxWidth / 2)) {
                            SingleTextLabel(
                                data = data.location
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(maxWidth / 2)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            SingleTextLabel(
                                data = data.pincode
                            )
                        }
                    }
                }
                Space(4.dp)
            }

            if (onSubscribe != null) {
                Space(4.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    MedicoSmallButton(
                        text = stringResource(id = R.string.subscribe),
                        onClick = onSubscribe,
                    )
                }
            }

        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(all = 12.dp)) {
            when {
                entityInfo.subscriptionData != null -> entityInfo.subscriptionData?.let { data ->
                    Row {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2)) {
                                Row {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_connected),
                                        contentDescription = null,
                                        tint = when (data.status) {
                                            SubscriptionStatus.SUBSCRIBED -> ConstColors.lightGreen
                                            SubscriptionStatus.PENDING -> ConstColors.lightBlue
                                            SubscriptionStatus.REJECTED -> ConstColors.red
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Space(dp = 4.dp)
                                    SingleTextLabel(
                                        data = data.status.serverValue, when (data.status) {
                                            SubscriptionStatus.SUBSCRIBED -> ConstColors.lightGreen
                                            SubscriptionStatus.PENDING -> ConstColors.lightBlue
                                            SubscriptionStatus.REJECTED -> ConstColors.red
                                        }
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Row {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_location),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = ConstColors.orange
                                    )
                                    Space(4.dp)
                                    SingleTextLabel(
                                        data = entityInfo.geoData.formattedDistance
                                    )
                                }
                            }
                        }
                    }
                    Space(4.dp)
                    Divider(thickness = 0.3.dp)
                    Space(4.dp)
                    Row {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2)) {
                                SingleTextLabel(data = entityInfo.gstin)
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.BottomEnd
                            ) {

                                SingleTextLabel(data = entityInfo.panNumber)
                            }
                        }
                    }
                    Space(4.dp)
                    Divider(thickness = 0.3.dp)
                    Space(4.dp)
                    Row {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2)) {
                                DataWithLabel(
                                    label = R.string.payment_method,
                                    data = data.paymentMethod.serverValue, size = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                DataWithLabel(
                                    label = R.string.orders,
                                    data = data.orders.toString(), size = 12.sp
                                )
                            }
                        }
                    }
                    Space(4.dp)
                    Divider(thickness = 0.3.dp)
                    Space(4.dp)
                    DataWithLabel(
                        label = R.string.dl_one,
                        data = entityInfo.drugLicenseNo1,
                        size = 12.sp
                    )
                    Space(4.dp)
                    Divider(thickness = 0.3.dp)
                    Space(4.dp)
                    DataWithLabel(
                        label = R.string.dl_two,
                        data = entityInfo.drugLicenseNo2,
                        size = 12.sp
                    )
                    Space(4.dp)
                    Row {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2)) {
                                DataWithLabel(
                                    label = R.string.expiry,
                                    data = entityInfo.dlExpiryDate.formatted, size = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.BottomEnd
                            ) {

                                DataWithLabel(
                                    label = R.string.expires_in,
                                    data = entityInfo.dlExpiresIn,
                                    size = 12.sp
                                )
                            }
                        }
                    }
                    Space(4.dp)
                    Divider(thickness = 0.3.dp)
                    Space(4.dp)
                    /*entityInfo.fl?.let {
                        DataWithLabel(label = R.string.food_license_number, data = it, size = 12.sp)
                    }
                    Space(4.dp)*/
                    Row {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.width(maxWidth / 2)) {
                                DataWithLabel(
                                    label = R.string.fl_expiry,
                                    data = entityInfo.flExpiryDate.formatted, size = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(maxWidth / 2)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.BottomEnd
                            ) {

                                DataWithLabel(
                                    label = R.string.expires_in,
                                    data = entityInfo.flExpiresIn,
                                    size = 12.sp
                                )
                            }
                        }
                    }

                }
                entityInfo.seasonBoyRetailerData != null -> entityInfo.seasonBoyRetailerData?.let { data ->
                    Row {
                        Text(
                            text = entityInfo.gstin,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.background,
                        )
                        Text(
                            text = entityInfo.panNumber,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.background,
                        )
                    }
                    DataWithLabel(
                        label = R.string.orders,
                        data = data.orders.toString()
                    )
                }
                else -> {
                    Column {
                        Row {
                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.width(maxWidth / 2)) {
                                    Text(
                                        text = entityInfo.gstin,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colors.background,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(maxWidth / 2)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    Text(
                                        text = entityInfo.panNumber,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colors.background,
                                    )
                                }
                            }
                        }
                        Column {
                            Text(
                                text = "${stringResource(id = R.string.dl_one)}:${entityInfo.drugLicenseNo1}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.background,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${stringResource(id = R.string.dl_two)}:${entityInfo.drugLicenseNo2}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.background,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

        }
    }

    if (bs.showConnectOption && bs.userType == UserType.STOCKIST && entityInfo.subscriptionData?.notificationId?.isNotEmpty() == true) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            border = BorderStroke(2.dp, borderColor)
        ) {
            val paymentMethod = bs.paymentMethod.flow.collectAsState()
            val creditDays = bs.creditDays.flow.collectAsState()
            val discount = bs.discount.flow.collectAsState()

            Column(modifier = Modifier.padding(all = 12.dp)) {
                Surface(
                    modifier = Modifier
                        .border(
                            1.dp,
                            Color.White,
                            RoundedCornerShape(4.dp)
                        )
                        .height(50.dp),
                    elevation = 3.dp
                ) {
                    Dropdown(
                        rememberChooseKey = this,
                        value = paymentMethod.value.serverValue,
                        hint = "",
                        dropDownItems = PaymentMethod.values().map { it.serverValue },
                        readOnly = false,
                        onSelected = {
                            val method = when (it) {
                                PaymentMethod.CREDIT.serverValue -> PaymentMethod.CREDIT
                                PaymentMethod.CASH.serverValue -> PaymentMethod.CASH
                                else -> throw UnsupportedOperationException("unknown payment method")
                            }
                            bs.changePaymentMethod(method)
                        }
                    )
                }
                Space(8.dp)
                if (paymentMethod.value == PaymentMethod.CREDIT) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DataWithLabel(R.string.credit_days, "")
                        BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
                        OutlinedTextField(
                            modifier = Modifier.height(50.dp),
                            value = creditDays.value,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = ConstColors.lightBlue,
                                unfocusedBorderColor = ConstColors.gray.copy(1f),
                            ),
                            singleLine = true,
                            maxLines = 1,
                            readOnly = false,
                            onValueChange = {
                                if (it.isEmpty() || it.toIntOrNull() != null) {
                                    bs.changeCreditDays(it)
                                }
                            },
                        )
                    }
                }
                Space(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DataWithLabel(R.string.discount_rate, "")
                    BoxWithConstraints { Spacer(Modifier.width(maxWidth - 100.dp)) }
                    OutlinedTextField(
                        modifier = Modifier.height(50.dp),
                        value = discount.value,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = ConstColors.lightBlue,
                            unfocusedBorderColor = ConstColors.gray.copy(1f),
                        ),
                        maxLines = 1,
                        singleLine = true,
                        readOnly = false,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                bs.changeDiscountRate(it)
                            }
                        },
                    )
                }
                Space(10.dp)
                Row(modifier = Modifier.fillMaxSize()) {

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.accept),
                        onClick = {
                            bs.sendRequest(
                                entityInfo.subscriptionData!!.notificationId,
                                NotificationAction.ACCEPT
                            )
                            onDismiss()
                        },
                        contentColor = Color.White,
                        isEnabled = true,
                    )

                    MedicoButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                            .height(40.dp),
                        text = stringResource(id = R.string.decline),
                        color = ConstColors.lightGrey,
                        onClick = {
                            bs.sendRequest(
                                entityInfo.subscriptionData!!.notificationId,
                                NotificationAction.DECLINE
                            )
                            onDismiss()
                        },
                        isEnabled = true
                    )
                }

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


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ViewLargeImageBottomSheet(
    url: Any,
    type: String? = "",
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
                .clickable(indication = NoOpIndication) { onDismiss() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(indication = null) { /* intercept touches */ }
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.5f),
                elevation = 8.dp,
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.12f),
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Space(16.dp)
                    CoilImageZoom(
                        onError = { Placeholder(R.drawable.ic_placeholder) },
                        src = if (!type.isNullOrEmpty()) {
                            File(url.toString())
                        } else url,
                        modifier = Modifier
                            .width(LocalContext.current.let { it.screenWidth / it.density }.dp - 32.dp)
                            .height(LocalContext.current.let { it.screenHeight / it.density }.dp - 50.dp),
                        onLoading = { CircularProgressIndicator(color = ConstColors.yellow) }
                    )
                    Space(30.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ViewInvoiceBottomSheet(
    info: OrderTaxInfo?,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.12f),
                    shape = CircleShape,
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
            Space(16.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.items))
                        append("  ")
                        val startIndex = length
                        append(info?.noOfItems.toString())
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.darkBlue,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.units))
                        append("  ")
                        val startIndex = length
                        append(info?.noOfUnits.toString())
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.darkBlue,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )

            }

            Space(dp = 16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.adjust))
                        append("  ")
                        val startIndex = length
                        append(info?.adjWithoutRounded?.formatted ?: "")
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.darkBlue,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.rounding))
                        append("  ")
                        val startIndex = length
                        append(info?.adjRounded?.formatted ?: "")
                        addStyle(
                            SpanStyle(color = ConstColors.lightBlue, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.darkBlue,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                )

            }

            Space(dp = 16.dp)
            Divider()
            Space(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.gross_amount),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Text(
                    text = info?.total?.formattedPrice ?: "",
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.End
                )
            }
            Space(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.discount),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Row {
                    Text(
                        text = info?.discount?.formatted ?: "0",
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.End
                    )
                    Space(dp = 4.dp)
                    Divider(
                        modifier = Modifier
                            .height(15.dp)
                            .width(1.dp)
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = info?.orderDiscount?.formatted ?: "0",
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.End
                    )
                }
            }
            Space(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.freight),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Text(
                    text = "0",
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.End
                )
            }
            Space(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.total_tax),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Text(
                    text = info?.totalTaxAmount?.formatted ?: "",
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.End
                )
            }

            Space(16.dp)
            Divider()
            Space(16.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.gst),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Text(
                    text = info?.totalTaxAmount?.formatted ?: "",
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.W600,
                )
            }
            Space(16.dp)

            if (info?.taxType == TaxType.SGST) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Space(dp = 16.dp)
                        Text(
                            text = stringResource(id = R.string.sgst),
                            color = ConstColors.txtGrey,
                            textAlign = TextAlign.Start,
                        )
                    }
                    Text(
                        text = info.totalSGST.formatted,
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.End
                    )
                }
                Space(16.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row {
                        Space(dp = 16.dp)
                        Text(
                            text = stringResource(id = R.string.cgst),
                            color = ConstColors.txtGrey,
                            textAlign = TextAlign.Start,
                        )
                    }

                    Text(
                        text = info.totalCGST.formatted,
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.End
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row {
                        Space(dp = 16.dp)
                        Text(
                            text = stringResource(id = R.string.igst),
                            color = ConstColors.txtGrey,
                            textAlign = TextAlign.Start,
                        )
                    }

                    Text(
                        text = info?.totalIGST?.formatted ?: "",
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.End
                    )
                }
            }

            Space(16.dp)
            Divider()
            Space(16.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.net_payable),
                    color = ConstColors.darkBlue,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W600,
                )

                Text(
                    text = info?.netAmount?.formatted ?: "",
                    color = MaterialTheme.colors.background,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.W600,
                )
            }
            Space(dp = 8.dp)
            Text(
                text = stringResource(id = R.string.note_net_payable),
                color = ConstColors.red,
                fontWeight = FontWeight.W600,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Start),
            )
            //Space(16.dp)
            /*MedicoButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.confirm),
                isEnabled = true,
                onClick = onSubscribe,
            )*/
            Space(30.dp)
        }
    }
}


@Composable
private fun ViewInvoiceItemTaxBottomSheet(
    orderEntry: OrderEntry,
    onDismiss: () -> Unit,
    scope: Scope
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
                        orderEntry.productCode,
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
                        text = orderEntry.productName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                    )
                    Space(4.dp)
                    Row {
                        Text(
                            text = orderEntry.productCode,
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
                                append(orderEntry.standardUnit)
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
                        text = orderEntry.manufacturerName,
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
                    value = orderEntry.requestedQty.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.free),
                    value = orderEntry.freeQty.formatted,
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
                    value = orderEntry.price.formatted,
                    itemTextColor = ConstColors.lightBlue,
                    valueTextColor = MaterialTheme.colors.background
                )
                Space(12.dp)
                ItemValue(
                    Modifier.weight(1f),
                    item = stringResource(id = R.string.total),
                    value = orderEntry.totalAmount.formatted,
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
                        value = orderEntry.discount.formatted,
                        itemTextColor = ConstColors.lightBlue,
                        valueTextColor = MaterialTheme.colors.background
                    )
                }
            }
            Space(10.dp)
            Divider()
            Column(Modifier.background(ConstColors.gray.copy(alpha = 0.05f))) {
                Space(10.dp)
                if (orderEntry.cgstTax.amount.value > 0.0 || orderEntry.sgstTax.amount.value > 0.0
                    || orderEntry.igstTax.amount.value > 0.0
                ) {
                    /*ItemValue(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        item = stringResource(id = R.string.gst) + "(${orderEntry.gstTaxRate.string})",
                        value = orderEntry.sgstTax.amount.value.plus(orderEntry.cgstTax.amount.value)
                            .toString(),
                        valueTextColor = MaterialTheme.colors.background
                    )*/
                    Space(8.dp)
                    Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
                        if (orderEntry.cgstTax.amount.value > 0.0) {
                            ItemValue(
                                Modifier.fillMaxWidth(),
                                item = stringResource(id = R.string.cgst) + "(${orderEntry.cgstTax.percent.formatted})",
                                value = orderEntry.cgstTax.amount.formatted,
                                valueTextColor = MaterialTheme.colors.background,
                                itemTextColor = MaterialTheme.colors.background,
                            )
                            Space(8.dp)
                        }
                        if (orderEntry.sgstTax.amount.value > 0.0) {
                            ItemValue(
                                Modifier.fillMaxWidth(),
                                item = stringResource(id = R.string.sgst) + "(${orderEntry.sgstTax.percent.formatted})",
                                value = orderEntry.sgstTax.amount.formatted,
                                valueTextColor = MaterialTheme.colors.background,
                                itemTextColor = MaterialTheme.colors.background,
                            )
                            Space(8.dp)
                        }
                        if (orderEntry.igstTax.amount.value > 0.0) {
                            ItemValue(
                                Modifier.fillMaxWidth(),
                                item = stringResource(id = R.string.igst) + "(${orderEntry.sgstTax.percent.formatted})",
                                value = orderEntry.igstTax.amount.formatted,
                                valueTextColor = MaterialTheme.colors.background,
                                itemTextColor = MaterialTheme.colors.background,
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
                        value = "0.0",
                        valueTextColor = MaterialTheme.colors.background
                    )
                    Space(8.dp)
                }
                Space(10.dp)
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
private fun ViewEditCartBottomSheet(
    qtyInitial: Double,
    freeQtyInitial: Double,
    cartItem: CartItem,
    onDismiss: () -> Unit,
    onUpdate: (Double, Double) -> Unit,
) {
    val qty = remember { mutableStateOf(qtyInitial) }
    val freeQty = remember { mutableStateOf(freeQtyInitial) }
    val mode = remember {
        mutableStateOf(
            if (qtyInitial > 0 || freeQtyInitial > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
        )
    }
    BaseBottomSheet(onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.12f),
                    shape = CircleShape,
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
            Space(16.dp)
            Text(
                text = cartItem.productName,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 30.dp)
            )
            Space(dp = 4.dp)

            if (cartItem.isPromotionActive) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.promotions))
                        val startIndex = length
                        append(": ")
                        append("(" + cartItem.promotionData?.displayLabel + ")")
                        addStyle(
                            SpanStyle(color = ConstColors.red, fontWeight = FontWeight.W700),
                            startIndex,
                            length,
                        )
                    },
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            val isError = (qty.value + freeQty.value) % 1 != 0.0 || freeQty.value > qty.value
            val wasError = remember { mutableStateOf(isError) }
            val wasErrorSaved = wasError.value
            val focusedError = remember(mode.value) { mutableStateOf(-1) }
            Space(dp = 16.dp)
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

            Space(dp = 16.dp)
            Row {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    color = ConstColors.gray.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.large,
                    onClick = onDismiss,
                    elevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontSize = 14.sp,
                            color = MaterialTheme.colors.background
                        )
                    }

                }
                Spacer(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth()
                )
                MedicoButton(
                    text = stringResource(id = R.string.confirm),
                    isEnabled = (qty.value + freeQty.value) % 1 == 0.0 && qty.value > 0.0 && qty.value >= freeQty.value,
                    onClick = {
                        mode.value =
                            if (qty.value > 0 || freeQty.value > 0) BottomSectionMode.Update else BottomSectionMode.AddToCart
                        onUpdate(qty.value, freeQty.value)
                    },
                    modifier = Modifier.weight(1f),
                    color = ConstColors.yellow,
                    contentColor = MaterialTheme.colors.background,
                    height = 35.dp
                )

            }

        }
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EditIOCBottomSheet(
    scope: BottomSheet.EditIOC,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val activity = LocalContext.current as MainActivity
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val showError = scope.showError.flow.collectAsState()
    val enableButton = scope.enableButton.flow.collectAsState()
    val date = scope.date.flow.collectAsState()
    val amount = scope.amount.flow.collectAsState()
    val type = scope.type.flow.collectAsState()

    BaseBottomSheet(onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
            ) {
                Space(dp = 8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = stringResource(id = R.string.add_payment),
                        fontSize = 14.sp,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.W700
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.12f),
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp),
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ConstColors.gray,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Space(dp = 20.dp)
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier
                            .weight(0.4f)
                            .clickable {
                                val now = DateTime.now()
                                val dialog = DatePickerDialog(
                                    activity,
                                    { _, year, month, day ->
                                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                                        formatter.isLenient = false
                                        val oldTime = "$day/${month + 1}/${year}"
                                        val oldDate: Date? = formatter.parse(oldTime)
                                        val oldMillis: Long? = oldDate?.time
                                        scope.updateDate(oldTime, oldMillis ?: 0)
                                    },
                                    now.year,
                                    now.monthOfYear - 1,
                                    now.dayOfMonth,
                                )
                                dialog.show()
                            }) {
                            Text(
                                text = if (date.value.isNotEmpty()) date.value else stringResource(
                                    id = R.string.ddmmyy
                                ),
                                fontSize = 14.sp,
                                color = if (date.value.isNotEmpty()) MaterialTheme.colors.background else ConstColors.txtGrey
                            )
                            Space(dp = 8.dp)
                            Divider(
                                thickness = 0.5.dp, color = ConstColors.lightBlue,
                            )
                        }
                        Space(dp = 30.dp)
                        Column(modifier = Modifier.weight(0.4f)) {
                            val total = remember {
                                mutableStateOf(
                                    if (amount.value.split(".")
                                            .lastOrNull() == ""
                                    ) amount.value.split(".")
                                        .first() else amount.value
                                )
                            }
                            InputWithError(
                                padding = 0.dp,
                                errorText = if (showError.value) stringResource(id = R.string.total_amount_message).replace(
                                    "$",
                                    scope.outStand.toString()
                                ) else null
                            ) {
                                BasicTextField(
                                    value = if (total.value.isNotEmpty()) total.value else "0.0",
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .scrollOnFocus(scrollState, coroutineScope),
                                    onValueChange = {
                                        val split = it.replace(",", ".").split(".")
                                        val beforeDot = split[0]
                                        val afterDot = split.getOrNull(1)
                                        var modBefore =
                                            beforeDot.toIntOrNull() ?: 0
                                        val modAfter = when (afterDot?.length) {
                                            0 -> "."
                                            in 1..Int.MAX_VALUE -> when (afterDot!!.take(
                                                1
                                            ).toIntOrNull()) {
                                                0 -> ".0"
                                                in 1..4 -> ".0"
                                                5 -> ".5"
                                                in 6..9 -> {
                                                    modBefore++
                                                    ".0"
                                                }
                                                null -> ""
                                                else -> throw UnsupportedOperationException(
                                                    "cant be that"
                                                )
                                            }
                                            null -> ""
                                            else -> throw UnsupportedOperationException(
                                                "cant be that"
                                            )
                                        }
                                        total.value = "$modBefore$modAfter"
                                        scope.updateAmount(total.value)
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        keyboardController?.hide()
                                    }),
                                    singleLine = true,
                                    readOnly = false,
                                    enabled = true,
                                )
                                Space(dp = 8.dp)
                                Divider(
                                    thickness = 0.5.dp, color = ConstColors.lightBlue,
                                )
                            }
                        }
                    }
                    Space(dp = 16.dp)
                    val list = scope.sellerScope.paymentTypes

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FoldableItem(
                            expanded = if (type.value.isNotEmpty()) false else false,
                            headerBackground = Color.White,
                            headerBorder = BorderStroke(0.dp, Color.Transparent),
                            headerMinHeight = 50.dp,
                            header = {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 5.dp, end = 5.dp)
                                    ) {
                                        Row(modifier = Modifier.weight(0.9f)) {
                                            Text(
                                                text = if (type.value.isNotEmpty()) stringResourceByName(
                                                    name = type.value
                                                ) else stringResource(
                                                    id = R.string.type
                                                ),
                                                color = if (type.value.isNotEmpty()) MaterialTheme.colors.background
                                                else ConstColors.txtGrey,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = ConstColors.gray,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                    Space(dp = 8.dp)
                                    Divider(
                                        thickness = 0.5.dp,
                                        color = ConstColors.lightBlue,
                                    )
                                }
                            },
                            childItems = list,
                            hasItemLeadingSpacing = false,
                            hasItemTrailingSpacing = false,
                            itemSpacing = 0.dp,
                            itemHorizontalPadding = 0.dp,
                            itemsBackground = Color.Transparent,
                            item = { value, index ->
                                SpinnerItem(
                                    item = value
                                ) {
                                    scope.updateType(
                                        value.stringId,
                                        value.type
                                    )
                                }

                            }
                        )
                    }
                    Space(dp = 16.dp)
                    MedicoRoundButton(
                        text = stringResource(id = R.string.submit),
                        isEnabled = enableButton.value,
                        elevation = null,
                        onClick = onConfirm,
                        contentColor = MaterialTheme.colors.background,
                        wrapTextSize = true,
                    )
                }
            }
        }
    }
}

