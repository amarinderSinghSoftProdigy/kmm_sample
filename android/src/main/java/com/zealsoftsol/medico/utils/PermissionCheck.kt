package com.zealsoftsol.medico.utils

import android.Manifest
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.madhu.locationpermission.permission.PermissionUI
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

@Composable
fun PermissionCheckUI(scaffoldState: ScaffoldState, permissionViewModel: PermissionViewModel) {
    val context = LocalContext.current
    val performAction by permissionViewModel.performAction.collectAsState()
    val performTypeAction by permissionViewModel.performTypeAction.collectAsState()

    if (performAction) {
        PermissionUI(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            stringResource(id = R.string.permission_message),
            scaffoldState
        ) { permissionAction ->
            when (permissionAction) {
                is PermissionAction.OnPermissionGranted -> {
                    permissionViewModel.setPerformLocationAction(false, performTypeAction)
                    if (performTypeAction == "OCR") {
                        EventCollector.sendEvent(
                            Event.Transition.Dashboard
                        )
                    } else {
                        EventCollector.sendEvent(
                            Event.Transition.Dashboard
                        )
                    }
                }
                is PermissionAction.OnPermissionDenied -> {
                    permissionViewModel.setPerformLocationAction(false, performTypeAction)
                }
            }
        }
    }
}


@Composable
fun PermissionCheckUIForInvoice(
    scaffoldState: ScaffoldState,
    permissionViewModel: PermissionViewModel,
) {
    val context = LocalContext.current
    val performAction by permissionViewModel.performAction.collectAsState()
    val performTypeAction by permissionViewModel.performTypeAction.collectAsState()

    if (performAction) {
        PermissionUI(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            stringResource(id = R.string.permission_message),
            scaffoldState
        ) { permissionAction ->
            when (permissionAction) {
                is PermissionAction.OnPermissionGranted -> {
                    permissionViewModel.setPerformLocationAction(false, performTypeAction)
                    EventCollector.sendEvent(
                        Event.Transition.Dashboard
                    )
                }
                is PermissionAction.OnPermissionDenied -> {
                    permissionViewModel.setPerformLocationAction(false, performTypeAction)
                }
            }
        }
    }
}