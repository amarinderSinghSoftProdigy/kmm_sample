package com.zealsoftsol.medico.screens.ocr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val recognisedTextList = scope.listOfText.flow.collectAsState()

    val permissionViewModel = PermissionViewModel()
    PermissionCheckUI(scaffoldState, permissionViewModel)

    if (imagePath.value.isNotEmpty()) {
        activity.startOcr(imagePath.value, scope)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ImageLabel(
            imagePath.value, direct = false, verified = false,
        ) { scope.previewImage(imagePath.value) }

        MedicoButton(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(R.string.upload_new_document),
            isEnabled = true,
            elevation = null,
            onClick = {
                permissionViewModel.setPerformLocationAction(true, "OCR")
            },
        )

        recognisedTextList.value.let {
            LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
                itemsIndexed(
                    items = it,
                    key = { pos, _ -> pos },
                    itemContent = { _, item ->
                        Surface(elevation = 5.dp, modifier = Modifier.padding(5.dp)) {
                            Text(
                                text = item, color = Color.Black, fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth().padding(10.dp)
                            )
                        }
                    },
                )
            }
        }
    }

}