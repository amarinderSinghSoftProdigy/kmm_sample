package com.zealsoftsol.medico.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartOrderCompletedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreAddUserScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreCartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreOrderPlacedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreProductsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreUsersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InvoicesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderPlacedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewInvoiceScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.WithTradeName
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
import com.zealsoftsol.medico.screens.cart.CartOrderCompletedScreen
import com.zealsoftsol.medico.screens.cart.CartPreviewScreen
import com.zealsoftsol.medico.screens.cart.CartScreen
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TabBar
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.showNotificationAlert
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.dashboard.DashboardScreen
import com.zealsoftsol.medico.screens.help.HelpScreen
import com.zealsoftsol.medico.screens.instore.InStoreAddUserScreen
import com.zealsoftsol.medico.screens.instore.InStoreCartScreen
import com.zealsoftsol.medico.screens.instore.InStoreOrderPlacedScreen
import com.zealsoftsol.medico.screens.instore.InStoreProductsScreen
import com.zealsoftsol.medico.screens.instore.InStoreSellersScreen
import com.zealsoftsol.medico.screens.instore.InStoreUsersScreen
import com.zealsoftsol.medico.screens.invoices.InvoicesScreen
import com.zealsoftsol.medico.screens.invoices.ViewInvoiceScreen
import com.zealsoftsol.medico.screens.management.AddRetailerScreen
import com.zealsoftsol.medico.screens.management.ManagementScreen
import com.zealsoftsol.medico.screens.management.StoresScreen
import com.zealsoftsol.medico.screens.nav.NavigationColumn
import com.zealsoftsol.medico.screens.notification.NotificationScreen
import com.zealsoftsol.medico.screens.orders.ConfirmOrderScreen
import com.zealsoftsol.medico.screens.orders.OrderPlacedScreen
import com.zealsoftsol.medico.screens.orders.OrdersScreen
import com.zealsoftsol.medico.screens.orders.ViewOrderScreen
import com.zealsoftsol.medico.screens.password.EnterNewPasswordScreen
import com.zealsoftsol.medico.screens.password.VerifyCurrentPasswordScreen
import com.zealsoftsol.medico.screens.product.BuyProductScreen
import com.zealsoftsol.medico.screens.product.ProductScreen
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.screens.search.SearchScreen
import com.zealsoftsol.medico.screens.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TabBarScreen(scope: TabBarScope, coroutineScope: CoroutineScope) {
    val scaffoldState = rememberScaffoldState()
    val notificationList = rememberLazyListState()
    val searchList = rememberLazyListState()
    val navigation = scope.navigationSection.flow.collectAsState()
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = scaffoldState,
        drawerContent = {
            navigation.value?.let {
                val user = it.user.flow.collectAsState()
                NavigationColumn(
                    fullName = user.value.fullName(),
                    userType = user.value.type,
                    trialString = user.value.subscription?.validUntil,
                    navigationSection = it,
                    onSectionSelected = { coroutineScope.launch { scaffoldState.drawerState.close() } }
                )
            } ?: run {
                if (scaffoldState.drawerState.isOpen) {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            }
        },
        drawerGesturesEnabled = navigation.value != null,
        topBar = {
            val tabBarInfo = scope.tabBar.flow.collectAsState()
            TabBar(isNewDesign = tabBarInfo.value is TabBarInfo.NewDesignLogo) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (val info = tabBarInfo.value) {
                        is TabBarInfo.Simple -> SimpleTabBar(
                            scope,
                            info,
                            scaffoldState,
                            coroutineScope
                        )
                        is TabBarInfo.Search -> SearchTabBar(
                            scope,
                            info,
                            scaffoldState,
                            coroutineScope
                        )
                        is TabBarInfo.ActiveSearch -> ActiveSearchTabBar(scope, info)
                        is TabBarInfo.NewDesignLogo -> {
                            val keyboard = LocalSoftwareKeyboardController.current
                            Icon(
                                imageVector = info.icon.toLocalIcon(),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxHeight()
                                    .padding(16.dp)
                                    .clickable(
                                        indication = null,
                                        onClick = {
                                            when (info.icon) {
                                                ScopeIcon.BACK -> scope.goBack()
                                                ScopeIcon.HAMBURGER -> {
                                                    keyboard?.hide()
                                                    coroutineScope.launch { scaffoldState.drawerState.open() }
                                                }
                                            }
                                        },
                                    )
                            )
                            Space(4.dp)
                            Image(
                                painter = painterResource(id = R.drawable.medico_logo),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 16.dp),
                            )
                        }
                        is TabBarInfo.NewDesignTitle -> {
                            Text(
                                text = info.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W600,
                                color = MaterialTheme.colors.background,
                                modifier = Modifier
                                    .weight(0.7f)
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 16.dp),
                            )
                        }
                        //display header in instore section from side menu when a retailer is selected
                        is TabBarInfo.InStoreProductTitle -> {
                            val keyboard = LocalSoftwareKeyboardController.current
                            Icon(
                                imageVector = info.icon.toLocalIcon(),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxHeight()
                                    .padding(16.dp)
                                    .clickable(
                                        indication = null,
                                        onClick = {
                                                scope.goBack()
                                        },
                                    )
                            )
                            Space(4.dp)
                            Image(
                                painter = painterResource(id = R.drawable.medico_logo),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 16.dp),
                            )
                        }
                    }
                }
            }
        },
        content = {
            val childScope = scope.childScope.flow.collectAsState()
            Crossfade(childScope.value, animationSpec = tween(durationMillis = 200)) {
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
                                if (it is AadhaarDataComponent) {
                                    WelcomeOption.Upload.Aadhaar(it) { it.showBottomSheet() }
                                } else {
                                    WelcomeOption.Upload.DrugLicense { it.showBottomSheet() }
                                }
                            } else {
                                WelcomeOption.Thanks(null)
                            },
                        )
                    }
                    is DashboardScope -> DashboardScreen(it)
                    is SearchScope -> SearchScreen(it, searchList)
                    is ProductInfoScope -> ProductScreen(it)
                    is BuyProductScope<*> -> BuyProductScreen(it as BuyProductScope<WithTradeName>)
                    is SettingsScope -> SettingsScreen(it)
                    is ManagementScope.User -> ManagementScreen(it, scope.isInProgress)
                    is ManagementScope.AddRetailer -> AddRetailerScreen(it)
                    is NotificationScope -> NotificationScreen(it, notificationList)
                    is StoresScope -> StoresScreen(it)
                    is CartScope -> CartScreen(it)
                    is CartPreviewScope -> CartPreviewScreen(it)
                    is CartOrderCompletedScope -> CartOrderCompletedScreen(it)
                    is HelpScope -> HelpScreen(it)
                    is OrdersScope -> OrdersScreen(it, scope.isInProgress)
                    is ViewOrderScope -> ViewOrderScreen(it)
                    is ConfirmOrderScope -> ConfirmOrderScreen(it)
                    is InvoicesScope -> InvoicesScreen(it)
                    is ViewInvoiceScope -> ViewInvoiceScreen(it)
                    is OrderPlacedScope -> OrderPlacedScreen(it)
                    is InStoreSellerScope -> InStoreSellersScreen(it)
                    is InStoreProductsScope -> InStoreProductsScreen(it)
                    is InStoreUsersScope -> InStoreUsersScreen(it)
                    is InStoreAddUserScope -> InStoreAddUserScreen(it)
                    is InStoreCartScope -> InStoreCartScreen(it)
                    is InStoreOrderPlacedScope -> InStoreOrderPlacedScreen(it)
                }
                if (it is CommonScope.WithNotifications) it.showNotificationAlert()
            }
        },
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RowScope.SimpleTabBar(
    scope: TabBarScope,
    info: TabBarInfo.Simple,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    if (info.icon != ScopeIcon.NO_ICON) {
        val keyboard = LocalSoftwareKeyboardController.current
        Icon(
            imageVector = info.icon.toLocalIcon(),
            contentDescription = null,
            modifier = Modifier
                .weight(0.15f)
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
                .padding(16.dp)
                .clickable(
                    indication = null,
                    onClick = {
                        when (info.icon) {
                            ScopeIcon.BACK -> scope.goBack()
                            ScopeIcon.HAMBURGER -> {
                                keyboard?.hide()
                                coroutineScope.launch { scaffoldState.drawerState.open() }
                            }
                        }
                    },
                )
        )
    }
    when (val res = info.title) {
        is StringResource.Static -> Text(
            text = stringResourceByName(res.id),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.7f)
                .padding(start = 16.dp),
        )
        is StringResource.Raw -> Text(
            text = res.string.orEmpty(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .weight(0.7f)
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp),
        )
    }

    info.cartItemsCount?.let {
        Box(
            modifier = Modifier
                .weight(0.15f)
                .clickable(indication = null) { info.goToCart() }
                .padding(10.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.Center)
            )
            val cartItems = it.flow.collectAsState()
            Box(modifier = Modifier.align(Alignment.TopEnd), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(15.dp)) {
                    drawCircle(Color.White)
                }
                Text(
                    text = cartItems.value.toString(),
                    color = ConstColors.red,
                    fontWeight = FontWeight.W700,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RowScope.SearchTabBar(
    scope: TabBarScope,
    info: TabBarInfo.Search,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Icon(
        imageVector = info.icon.toLocalIcon(),
        contentDescription = null,
        modifier = Modifier
            .weight(0.15f)
            .clickable(indication = null) {
                when (info.icon) {
                    ScopeIcon.BACK -> scope.goBack()
                    ScopeIcon.HAMBURGER -> {
                        keyboard?.hide()
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }
                }
            }
            .padding(16.dp)
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
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(id = R.string.search_products),
            color = ConstColors.gray.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 24.dp),
        )
    }
    Box(
        modifier = Modifier
            .weight(0.15f)
            .clickable(indication = null) { info.goToCart() }
            .padding(10.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_cart),
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
                .align(Alignment.Center)
        )
        val cartItems = info.cartItemsCount.flow.collectAsState()
        Box(modifier = Modifier.align(Alignment.TopEnd), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(15.dp)) {
                drawCircle(Color.White)
            }
            Text(
                text = cartItems.value.toString(),
                color = ConstColors.red,
                fontWeight = FontWeight.W700,
                fontSize = 10.sp,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ActiveSearchTabBar(
    scope: TabBarScope,
    info: TabBarInfo.ActiveSearch,
) {
    val search = info.search.flow.collectAsState()
    val activeFilterIds = info.activeFilterIds.flow.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    BasicSearchBar(
        input = search.value,
        hint = R.string.search_products,
        icon = Icons.Default.ArrowBack,
        searchBarEnd = SearchBarEnd.Filter(isHighlighted = activeFilterIds.value.isNotEmpty()) {
            keyboard?.hide()
            info.toggleFilter()
        },
        onIconClick = { scope.goBack() },
        isSearchFocused = scope.storage.restore("focus") as? Boolean ?: true,
        onSearch = { value, isFromKeyboard ->
            info.searchProduct(
                value,
                withAutoComplete = !isFromKeyboard
            )
        },
    )
    scope.storage.save("focus", false)
}

private inline fun ScopeIcon.toLocalIcon(): ImageVector = when (this) {
    ScopeIcon.HAMBURGER -> Icons.Default.Menu
    ScopeIcon.BACK -> Icons.Default.ArrowBack
    ScopeIcon.NO_ICON -> throw Exception("no icon")
}