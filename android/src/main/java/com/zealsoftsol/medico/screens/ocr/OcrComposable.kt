package com.zealsoftsol.medico.screens.ocr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.OcrScope
import com.zealsoftsol.medico.screens.common.ImageLabel
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.utils.PermissionCheckUI
import com.zealsoftsol.medico.utils.PermissionViewModel

@Composable
fun OcrScreen(
    scope: OcrScope, scaffoldState: ScaffoldState
) {

    val imagePath = scope.imagePath.flow.collectAsState()
    val activity = LocalContext.current as MainActivity

    val permissionViewModel = PermissionViewModel()
    PermissionCheckUI(scaffoldState, permissionViewModel)

    if(imagePath.value.isNotEmpty()){
        activity.startOcr(imagePath.value)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ImageLabel(
            imagePath.value, direct = false, verified = false,
        ) { scope.previewImage(imagePath.value) }

        MedicoButton(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = stringResource(R.string.upload_new_document),
            isEnabled = true,
            elevation = null,
            onClick = {
                permissionViewModel.setPerformLocationAction(true, "OCR")
            },
        )
    }

}