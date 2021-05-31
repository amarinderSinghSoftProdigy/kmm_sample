package com.zealsoftsol.medico.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.DataWithLabel
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.rememberPhoneNumberFormatter
import com.zealsoftsol.medico.screens.management.GeoLocation
import com.zealsoftsol.medico.screens.product.PlusMinusQuantity
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
                val quantity = bs.quantity.flow.collectAsState()
                val isChecked = bs.isChecked.flow.collectAsState()
                ModifyOrderEntryBottomSheet(
                    bs.orderEntry,
                    quantity.value,
                    canEdit = bs.canEdit,
                    isChecked = isChecked.value,
                    onChecked = { bs.toggleCheck() },
                    onInc = { bs.inc() },
                    onDec = { bs.dec() },
                    onSave = { bs.save() },
                    onDismiss = { dismissBottomSheet() },
                )
            }
        }
    }
}

@Composable
private fun ModifyOrderEntryBottomSheet(
    entry: OrderEntry,
    quantity: Int,
    canEdit: Boolean,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp)) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.TopEnd)
                    .size(24.dp)
                    .clickable(
                        indication = rememberRipple(radius = 12.dp),
                        onClick = onDismiss,
                    ),
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
                    if (canEdit) {
                        Checkbox(
                            checked = isChecked,
                            colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                            onCheckedChange = onChecked,
                        )
                        Space(18.dp)
                    }
                    Column {
                        Text(
                            text = entry.productName,
                            color = MaterialTheme.colors.background,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = "${stringResource(id = R.string.batch_no)} ${entry.batchNo}",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Space(8.dp)
                        Text(
                            text = "${stringResource(id = R.string.expiry)} ${entry.expiryDate?.formatted.orEmpty()}",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Space(20.dp)
                Divider()
                Space(20.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = if (canEdit) Alignment.CenterVertically else Alignment.Top,
                ) {
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.price))
                                append(": ")
                                val startIndex = length
                                append(entry.price.formatted)
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
                        if (!canEdit) {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.mrp))
                                    append(": ")
                                    val startIndex = length
                                    append(entry.mrp.formatted)
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
                        }
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.requested_qty))
                                append(" ")
                                val startIndex = length
                                append(entry.requestedQty.formatted)
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
                    Column {
                        if (canEdit) {
                            PlusMinusQuantity(
                                quantity = quantity,
                                onInc = onInc,
                                onDec = onDec,
                                isEnabled = true,
                            )
                        } else {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.served_qty))
                                    append(" ")
                                    val startIndex = length
                                    append(entry.servedQty.formatted)
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
                Space(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${stringResource(id = R.string.subtotal)}: ${entry.totalAmount.formatted}",
                        color = MaterialTheme.colors.background,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (canEdit) {
                        MedicoSmallButton(
                            text = stringResource(id = R.string.save),
                            enabledColor = ConstColors.lightBlue,
                            contentColor = Color.White,
                            onClick = onSave,
                        )
                    }
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

@Composable
private fun PreviewItemBottomSheet(
    entityInfo: EntityInfo,
    isForSeasonBoy: Boolean,
    onSubscribe: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp)) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.TopEnd)
                    .size(24.dp)
                    .clickable(
                        indication = rememberRipple(radius = 12.dp),
                        onClick = onDismiss,
                    ),
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
    val formatter = rememberPhoneNumberFormatter()
    val phoneNumber = entityInfo.phoneNumber?.let { formatter.verifyNumber(it) ?: it }.orEmpty()
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
            modifier = Modifier.fillMaxWidth().heightIn(min = 123.dp),
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
                modifier = Modifier.height(52.dp)
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
                    modifier = Modifier.height(48.dp)
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
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable(indication = NoOpIndication) { onDismiss() })
        Surface(
            modifier = Modifier.fillMaxWidth()
                .clickable(indication = null) { /* intercept touches */ }
                .align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            body()
        }
    }
}