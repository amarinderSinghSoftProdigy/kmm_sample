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
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.data.FileType
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
                    bs.supportedFileTypes,
                    !bs.isSeasonBoy,
                    activity,
                    coroutineScope,
                    onFileReady = { bs.handleFileUpload(it) },
                    onDismiss = { dismissBottomSheet() }
                )
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
    BaseBottomSheet(
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

data class BottomSheetCell(val stringId: Int, val iconAsset: ImageVector)

@Composable
private fun BaseBottomSheet(
    title: String,
    cells: List<BottomSheetCell>,
    onCellClick: (BottomSheetCell) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .clickable(indication = null) { onDismiss() }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
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
}