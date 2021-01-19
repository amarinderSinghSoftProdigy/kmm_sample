package com.zealsoftsol.medico.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataHolder
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.screens.auth.AuthAddressData
import com.zealsoftsol.medico.screens.auth.AuthAwaitVerificationScreen
import com.zealsoftsol.medico.screens.auth.AuthDetailsAadhaar
import com.zealsoftsol.medico.screens.auth.AuthDetailsTraderData
import com.zealsoftsol.medico.screens.auth.AuthLegalDocuments
import com.zealsoftsol.medico.screens.auth.AuthPersonalData
import com.zealsoftsol.medico.screens.auth.AuthPhoneNumberInputScreen
import com.zealsoftsol.medico.screens.auth.AuthUserType
import com.zealsoftsol.medico.screens.auth.WelcomeOption
import com.zealsoftsol.medico.screens.auth.WelcomeScreen
import com.zealsoftsol.medico.screens.nav.NavigationColumn
import com.zealsoftsol.medico.screens.password.EnterNewPasswordScreen
import com.zealsoftsol.medico.screens.password.VerifyCurrentPasswordScreen
import com.zealsoftsol.medico.screens.product.ProductScreen
import com.zealsoftsol.medico.screens.settings.SettingsScreen

@Composable
fun TabBarScreen(scope: Scope.Host.TabBar) {
    val scaffoldState = rememberScaffoldState()
    val navigation = scope.navigationSection.flow.collectAsState()
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = scaffoldState,
        drawerContent = {
            navigation.value?.let {
                val user = it.user.flow.collectAsState()
                NavigationColumn(
                    userName = user.value.fullName(),
                    userType = user.value.type,
                    navigationSection = it,
                    onSectionSelected = { scaffoldState.drawerState.close() }
                )
            }
        },
        drawerGesturesEnabled = navigation.value != null,
        topBar = {
            TabBar {
                val tabBarInfo = scope.tabBar.flow.collectAsState()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (val info = tabBarInfo.value) {
                        is TabBarInfo.Simple -> {
                            if (info.icon != ScopeIcon.NO_ICON) {
                                Icon(
                                    imageVector = info.icon.toLocalIcon(),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .fillMaxHeight()
                                        .padding(16.dp)
                                        .clickable(
                                            indication = null,
                                            onClick = {
                                                when (info.icon) {
                                                    ScopeIcon.BACK -> scope.goBack()
                                                    ScopeIcon.HAMBURGER -> scaffoldState.drawerState.open()
                                                }
                                            },
                                        )
                                )
                            }
                            info.titleId?.let { stringId ->
                                Text(
                                    text = stringResourceByName(stringId),
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .padding(start = 16.dp),
                                )
                            }
                        }
                        is TabBarInfo.Search -> {
                            Icon(
                                imageVector = info.icon.toLocalIcon(),
                                modifier = Modifier
                                    .weight(0.15f)
                                    .padding(16.dp)
                                    .clickable(onClick = { scaffoldState.drawerState.open() })
                            )
                            Row(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .fillMaxHeight()
                                    .clickable(indication = null) { info.goToSearch() }
                                    .padding(vertical = 4.dp)
                                    .background(Color.White, MaterialTheme.shapes.medium)
                                    .padding(14.dp),
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
            }
        },
        bodyContent = {
            val childScope = scope.childScope.flow.collectAsState()
            Crossfade(childScope.value, animation = tween(durationMillis = 200)) {
                when (it) {
                    is OtpScope.PhoneNumberInput -> AuthPhoneNumberInputScreen(it)
                    is OtpScope.AwaitVerification -> AuthAwaitVerificationScreen(it)
                    is PasswordScope.VerifyCurrent -> VerifyCurrentPasswordScreen(it)
                    is PasswordScope.EnterNew -> EnterNewPasswordScreen(it)
                    is SignUpScope.SelectUserType -> AuthUserType(it)
                    is SignUpScope.PersonalData -> AuthPersonalData(it)
                    is SignUpScope.AddressData -> AuthAddressData(it)
                    is SignUpScope.Details.TraderData -> AuthDetailsTraderData(it)
                    is SignUpScope.Details.Aadhaar -> AuthDetailsAadhaar(it)
                    is SignUpScope.LegalDocuments -> AuthLegalDocuments(it)
                    is LimitedAccessScope -> {
                        val user = it.user.flow.collectAsState()
                        WelcomeScreen(
                            fullName = user.value.fullName(),
                            option = if (!user.value.isDocumentUploaded) {
                                if (it is AadhaarDataHolder) {
                                    WelcomeOption.Upload.Aadhaar(it) { it.showBottomSheet() }
                                } else {
                                    WelcomeOption.Upload.DrugLicense { it.showBottomSheet() }
                                }
                            } else {
                                WelcomeOption.Thanks(null)
                            },
                        )
                    }
                    is DashboardScope -> Unit
                    is ProductInfoScope -> ProductScreen(it)
                    is SettingsScope -> SettingsScreen(it)
                }
            }
        },
    )
}

private inline fun ScopeIcon.toLocalIcon(): ImageVector = when (this) {
    ScopeIcon.HAMBURGER -> Icons.Default.Menu
    ScopeIcon.BACK -> Icons.Default.ArrowBack
    ScopeIcon.NO_ICON -> throw Exception("no icon")
}