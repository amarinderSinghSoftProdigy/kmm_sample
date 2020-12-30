package com.zealsoftsol.medico.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.NavAndSearchMainScope
import com.zealsoftsol.medico.screens.auth.DocumentUploadBottomSheet
import com.zealsoftsol.medico.screens.auth.Welcome
import com.zealsoftsol.medico.screens.auth.WelcomeOption
import com.zealsoftsol.medico.screens.auth.handleFileUpload
import com.zealsoftsol.medico.screens.nav.NavigationColumn

@Composable
fun MainView(scope: MainScope) {
    val scaffoldState = rememberScaffoldState()
    val isShowingDocumentUploadBottomSheet = remember { mutableStateOf(false) }
    val user = (scope as? NavAndSearchMainScope)?.run { user.flow.collectAsState() }
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = scaffoldState,
        drawerContent = {
            if (user != null) {
                NavigationColumn(
                    userName = user.value.fullName(),
                    userType = user.value.type,
                    navigationSection = scope.navigationSection,
                )
            }
        },
        topBar = {
            TabBar {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isFullTabBar = user?.value?.isVerified == true
                    Icon(
                        imageVector = vectorResource(id = R.drawable.ic_menu),
                        modifier = Modifier
                            .run { if (isFullTabBar) weight(0.15f) else this }
                            .padding(16.dp)
                            .clickable(onClick = { scaffoldState.drawerState.open() })
                    )
                    if (isFullTabBar) {
                        Row(
                            modifier = Modifier
                                .weight(0.7f)
                                .fillMaxHeight()
                                .padding(vertical = 4.dp)
                                .background(Color.White, MaterialTheme.shapes.medium)
                                .padding(14.dp)
                                .clickable(indication = null) { (scope as? NavAndSearchMainScope)?.goToSearch() },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                tint = ConstColors.gray,
                                modifier = Modifier.size(24.dp),
                            )
                            Text(
                                text = stringResource(id = R.string.search),
                                color = ConstColors.gray.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 24.dp),
                            )
                        }
                        Icon(
                            imageVector = vectorResource(id = R.drawable.ic_cart),
                            modifier = Modifier
                                .weight(0.15f)
                                .padding(16.dp),
                        )
                    }
                }
            }
        },
        bodyContent = {
            when (scope) {
                is MainScope.Dashboard -> {
                }
                is MainScope.LimitedAccess -> user?.value?.let {
                    Welcome(
                        fullName = it.fullName(),
                        option = if (!it.isDocumentUploaded) {
                            if (scope is MainScope.LimitedAccess.SeasonBoy) {
                                WelcomeOption.Upload.Aadhaar(scope) {
                                    isShowingDocumentUploadBottomSheet.value = true
                                }
                            } else {
                                WelcomeOption.Upload.DrugLicense {
                                    isShowingDocumentUploadBottomSheet.value = true
                                }
                            }
                        } else {
                            WelcomeOption.Thanks(null)
                        },
                    )
                }
            }
        },
    )
    if (scope is MainScope.LimitedAccess) {
        DocumentUploadBottomSheet(
            isShowingBottomSheet = isShowingDocumentUploadBottomSheet,
            supportedFileTypes = scope.supportedFileTypes,
            useCamera = scope is MainScope.LimitedAccess.NonSeasonBoy,
            onFileReady = { scope.handleFileUpload(it) },
        )
    }
}