package com.zealsoftsol.medico.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.data.FileType
import com.zealsoftsol.medico.core.extensions.density
import com.zealsoftsol.medico.core.extensions.screenHeight
import com.zealsoftsol.medico.core.extensions.screenWidth
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.CoilImageZoom
import com.zealsoftsol.medico.screens.common.NoOpIndication
import com.zealsoftsol.medico.screens.common.Placeholder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

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
                            //bs.handleUpload(it, bs.type, bs.registrationStep1)
                        }
                    },
                    onDismiss = { dismissBottomSheet() },
                )
            }
            is BottomSheet.ViewLargeImage -> ViewLargeImageBottomSheet(
                url = bs.url,
                type = bs.type,
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


