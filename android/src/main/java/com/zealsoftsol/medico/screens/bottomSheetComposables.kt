package com.zealsoftsol.medico.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.screens.management.GeoLocation
import dev.chrisbanes.accompanist.coil.CoilImage
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
                onSubscribe = { bs.subscribe() },
                onDismiss = { dismissBottomSheet() },
            )
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
                } else {
                    activity.toast(activity.getString(R.string.something_went_wrong))
                }
            }
        },
        onDismiss = onDismiss,
    )
}

@Composable
private fun PreviewItemBottomSheet(
    entityInfo: EntityInfo,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp)) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.TopEnd)
                    .size(24.dp)
                    .clickable(indication = rememberRipple(radius = 12.dp), onClick = onDismiss),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = ConstColors.gray,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column {
                Text(
                    text = entityInfo.traderName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colors.background,
                )
                Space(4.dp)
                Text(
                    text = entityInfo.city,
                    fontSize = 14.sp,
                    color = ConstColors.gray,
                )
                Space(12.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    CoilImage(
                        data = "",
                        error = { ItemPlaceholder() },
                        loading = { ItemPlaceholder() },
                    )
                    Space(24.dp)
                    Column {
                        Space(8.dp)
                        GeoLocation(entityInfo.location, isBold = true)
                        Space(12.dp)
                        Row {
                            Column {
                                Text(
                                    text = entityInfo.distance,
                                    fontSize = 12.sp,
                                    color = ConstColors.gray,
                                )
                                Space(4.dp)
                                val activity = AmbientContext.current as MainActivity
                                Text(
                                    text = stringResource(id = R.string.see_on_the_map),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ConstColors.lightBlue,
                                    modifier = Modifier.clickable {
                                        activity.openMaps(
                                            entityInfo.sellerGeoPoints.latitude,
                                            entityInfo.sellerGeoPoints.longitude
                                        )
                                    },
                                )
                            }
                            if (entityInfo.getSubscriptionStatus() == null) {
                                Space(14.dp)
                                MedicoSmallButton(
                                    text = stringResource(id = R.string.subscribe),
                                    onClick = onSubscribe,
                                )
                            }
                        }
                    }
                }
                Space(12.dp)
                Column {
                    DataWithLabel(label = R.string.gstin_num, data = entityInfo.gstin)
                    entityInfo.getSubscriptionStatus()?.let {
                        DataWithLabel(label = R.string.status, data = it.serverValue)
                        DataWithLabel(label = R.string.payment_method, data = "")
                        DataWithLabel(label = R.string.orders, data = "")
                    }
                }
            }
        }
    }
}

@Composable
private fun DataWithLabel(label: Int, data: String) {
    Row {
        Text(
            text = "${stringResource(id = label)}:",
            fontSize = 14.sp,
            color = ConstColors.gray,
        )
        Space(4.dp)
        Text(
            text = data,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colors.background,
        )
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
            modifier = Modifier.fillMaxWidth().clickable(indication = null) { }
                .align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            body()
        }
    }
}