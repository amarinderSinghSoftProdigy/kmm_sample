package com.zealsoftsol.medico.screens

import android.view.WindowManager
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
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
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreAddUserScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreCartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreOrderPlacedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreProductsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreUsersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InvoicesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocBuyerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.IocSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.mvi.scope.nested.MenuScope
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
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
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderInvoiceScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.mvi.scope.regular.BatchesScope
import com.zealsoftsol.medico.core.mvi.scope.regular.InventoryScope
import com.zealsoftsol.medico.core.mvi.scope.regular.OrderHsnEditScope
import com.zealsoftsol.medico.core.mvi.scope.regular.QrCodeScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WhatsappPreferenceScope
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.WithTradeName
import com.zealsoftsol.medico.screens.auth.AuthAddressData
import com.zealsoftsol.medico.screens.auth.AuthAwaitVerificationScreen
import com.zealsoftsol.medico.screens.auth.AuthDetailsAadhaar
import com.zealsoftsol.medico.screens.auth.AuthDetailsTraderData
import com.zealsoftsol.medico.screens.auth.AuthLegalDocuments
import com.zealsoftsol.medico.screens.auth.AuthPersonalData
import com.zealsoftsol.medico.screens.auth.AuthPhoneNumberInputScreen
import com.zealsoftsol.medico.screens.auth.AuthPreview
import com.zealsoftsol.medico.screens.auth.AuthUserType
import com.zealsoftsol.medico.screens.auth.WelcomeOption
import com.zealsoftsol.medico.screens.auth.WelcomeScreen
import com.zealsoftsol.medico.screens.batches.ViewBatchesScreen
import com.zealsoftsol.medico.screens.cart.CartOrderCompletedScreen
import com.zealsoftsol.medico.screens.cart.CartPreviewScreen
import com.zealsoftsol.medico.screens.cart.CartScreen
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TabBar
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.showNotificationAlert
import com.zealsoftsol.medico.screens.common.stringResourceByName
import com.zealsoftsol.medico.screens.dashboard.DashboardScreen
import com.zealsoftsol.medico.screens.employee.AddEmployeeAadharInfoScreen
import com.zealsoftsol.medico.screens.employee.AddEmployeeAddressDetailsScreen
import com.zealsoftsol.medico.screens.employee.AddEmployeeScreen
import com.zealsoftsol.medico.screens.employee.AddEmployeeStepOneScreen
import com.zealsoftsol.medico.screens.employee.SuccessEmployees
import com.zealsoftsol.medico.screens.employee.ViewEmployees
import com.zealsoftsol.medico.screens.help.HelpScreens
import com.zealsoftsol.medico.screens.instore.InStoreAddUserScreen
import com.zealsoftsol.medico.screens.instore.InStoreCartScreen
import com.zealsoftsol.medico.screens.instore.InStoreOrderPlacedScreen
import com.zealsoftsol.medico.screens.instore.InStoreProductsScreen
import com.zealsoftsol.medico.screens.instore.InStoreSellersScreen
import com.zealsoftsol.medico.screens.instore.InStoreUsersScreen
import com.zealsoftsol.medico.screens.inventory.InventoryMainComposable
import com.zealsoftsol.medico.screens.invoices.InvoicesScreen
import com.zealsoftsol.medico.screens.invoices.ViewInvoiceScreen
import com.zealsoftsol.medico.screens.ioc.IocBuyerListingScreen
import com.zealsoftsol.medico.screens.ioc.IocListingScreen
import com.zealsoftsol.medico.screens.ioc.IocScreen
import com.zealsoftsol.medico.screens.management.AddRetailerScreen
import com.zealsoftsol.medico.screens.management.ManagementScreen
import com.zealsoftsol.medico.screens.management.StoresScreen
import com.zealsoftsol.medico.screens.menu.MenuScreen
import com.zealsoftsol.medico.screens.notification.NotificationScreen
import com.zealsoftsol.medico.screens.offers.CreateOffersScreen
import com.zealsoftsol.medico.screens.offers.OffersScreen
import com.zealsoftsol.medico.screens.orders.ConfirmOrderScreen
import com.zealsoftsol.medico.screens.orders.OrderHsnEditScreen
import com.zealsoftsol.medico.screens.orders.OrderPlacedScreen
import com.zealsoftsol.medico.screens.orders.OrdersScreen
import com.zealsoftsol.medico.screens.orders.ViewOrderInvoiceScreen
import com.zealsoftsol.medico.screens.orders.ViewOrderScreen
import com.zealsoftsol.medico.screens.password.EnterNewPasswordScreen
import com.zealsoftsol.medico.screens.password.VerifyCurrentPasswordScreen
import com.zealsoftsol.medico.screens.product.BuyProductScreen
import com.zealsoftsol.medico.screens.product.ProductScreen
import com.zealsoftsol.medico.screens.qrcode.QrCodeScreen
import com.zealsoftsol.medico.screens.search.BasicSearchBar
import com.zealsoftsol.medico.screens.search.SearchBarEnd
import com.zealsoftsol.medico.screens.search.SearchScreen
import com.zealsoftsol.medico.screens.settings.AddressComposable
import com.zealsoftsol.medico.screens.settings.GstinDetailsComposable
import com.zealsoftsol.medico.screens.settings.ProfileComposable
import com.zealsoftsol.medico.screens.settings.SettingsScreen
import com.zealsoftsol.medico.screens.whatsappComposables.WhatsappPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private var mBottomNavItems: List<BottomNavigationItem>? = null
private var mUserType: UserType? = null

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TabBarScreen(scope: TabBarScope, coroutineScope: CoroutineScope, activity: MainActivity) {
    val scaffoldState = rememberScaffoldState()
    val notificationList = rememberLazyListState()
    val searchList = rememberLazyListState()
    val navigation = scope.navigationSection.flow.collectAsState()
    val childScope = scope.childScope.flow.collectAsState()

    //assign user type if it is not null or if it has changed. This is being used to draw bottom navigation items
    if (navigation.value?.user?.flow?.value?.type != null && mUserType != navigation.value?.user?.flow?.value?.type) {
        mBottomNavItems = null
        mUserType = navigation.value?.user?.flow?.value?.type
    }
    if (childScope.value is SignUpScope) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    } else {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    Scaffold(
        backgroundColor = if (childScope.value is SignUpScope || childScope.value is CartScope || childScope.value is IocSellerScope) Color.White else MaterialTheme.colors.primary,
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = navigation.value != null,
        topBar = {
            if (childScope.value !is OrderHsnEditScope && childScope.value !is InventoryScope && childScope.value !is IocSellerScope.InvUserListing && childScope.value !is IocBuyerScope.InvUserListing) //don't show top bar for OrderEditHsnScreen and Inventory and IOC listing
            {
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
                            is TabBarInfo.InStoreProductTitle -> InStoreHeaderData(info, scope)
                            //display search bar with product logo
                            is TabBarInfo.NoIconTitle -> NoIconHeader(scope, info)
                            is TabBarInfo.StoreTitle -> StoreHeader(scope, info)
                            is TabBarInfo.OnlyBackHeader -> OnlyBackHeader(scope, info)
                            is TabBarInfo.OfferHeader -> OffersHeader(
                                scope,
                                info,
                            )
                        }
                    }
                }
            }
        },
        content = {
            var padding = 56
            if (childScope.value is OrderHsnEditScope || childScope.value is ViewOrderScope || childScope.value is SignUpScope) {// no bottom padding while editing order entries
                padding = 0
            }

            Crossfade(
                childScope.value,
                animationSpec = tween(durationMillis = 200),
                modifier = Modifier.padding(bottom = padding.dp)
            ) {
                when (it) {
                    is OtpScope.PhoneNumberInput -> AuthPhoneNumberInputScreen(it)
                    is OtpScope.AwaitVerification -> AuthAwaitVerificationScreen(it)
                    is PasswordScope.VerifyCurrent -> VerifyCurrentPasswordScreen(it)
                    is PasswordScope.EnterNew -> EnterNewPasswordScreen(it)

                    is SignUpScope.SelectUserType -> {
                        mUserType = null
                        AuthUserType(it)
                    }
                    is SignUpScope.PersonalData -> {
                        mUserType = null
                        AuthPersonalData(it)
                    }
                    is SignUpScope.AddressData -> {
                        mUserType = null
                        AuthAddressData(it)
                    }
                    is SignUpScope.Details.TraderData -> {
                        mUserType = null
                        AuthDetailsTraderData(it)
                    }
                    is SignUpScope.Details.Aadhaar -> {
                        mUserType = null
                        AuthDetailsAadhaar(it)
                    }
                    is SignUpScope.LegalDocuments -> {
                        mUserType = null
                        AuthLegalDocuments(it, scaffoldState)
                    }
                    is SignUpScope.PreviewDetails -> {
                        mUserType = null
                        AuthPreview(it)
                    }

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
                    is DashboardScope -> {
                        DashboardScreen(it)
                        manageBottomNavState(BottomNavKey.DASHBOARD)
                    }
                    is SearchScope -> SearchScreen(it, searchList)
                    is ProductInfoScope -> ProductScreen(it)
                    is BuyProductScope<*> -> BuyProductScreen(it as BuyProductScope<WithTradeName>)
                    is SettingsScope -> {
                        SettingsScreen(it, scaffoldState)
                        if (mUserType != UserType.STOCKIST) // this will not change bottom nave when user type is stockist
                            manageBottomNavState(BottomNavKey.SETTINGS)
                    }
                    is ManagementScope.User -> ManagementScreen(it, scope.isInProgress)
                    is ManagementScope.AddRetailer -> AddRetailerScreen(it)
                    is NotificationScope -> NotificationScreen(it, notificationList)
                    is StoresScope -> {
                        StoresScreen(it)
                        manageBottomNavState(BottomNavKey.STORES)
                    }
                    is CartScope -> {
                        CartScreen(it)
                        manageBottomNavState(BottomNavKey.CART)
                    }
                    is CartPreviewScope -> CartPreviewScreen(it)
                    is CartOrderCompletedScope -> CartOrderCompletedScreen(it)
                    is HelpScope -> HelpScreens(it)
                    //is HelpScope -> TermsConditionsPrivacyPolicyScreen(it)
                    is OrdersScope -> {
                        OrdersScreen(it, scope.isInProgress)
                        manageBottomNavState(BottomNavKey.PO)
                    }
                    is ViewOrderScope -> ViewOrderScreen(it)
                    is ViewOrderInvoiceScope -> ViewOrderInvoiceScreen(it)
                    is ConfirmOrderScope -> ConfirmOrderScreen(it)
                    is InvoicesScope -> InvoicesScreen(it)
                    is ViewInvoiceScope -> ViewInvoiceScreen(it)
                    is OrderPlacedScope -> OrderPlacedScreen(it)
                    is InStoreSellerScope -> {
                        InStoreSellersScreen(it)
                        manageBottomNavState(BottomNavKey.INSTORES)
                    }
                    is InStoreProductsScope -> InStoreProductsScreen(it)
                    is InStoreUsersScope -> InStoreUsersScreen(it)
                    is InStoreAddUserScope -> InStoreAddUserScreen(it)
                    is InStoreCartScope -> InStoreCartScreen(it)
                    is InStoreOrderPlacedScope -> InStoreOrderPlacedScreen(it)
                    is WhatsappPreferenceScope -> WhatsappPreference(it)
                    is OffersScope.ViewOffers -> OffersScreen(it)
                    is OffersScope.CreateOffer -> CreateOffersScreen(it)
                    is OrderHsnEditScope -> OrderHsnEditScreen(it)
                    is InventoryScope -> InventoryMainComposable(it)
                    is SettingsScope.Profile -> ProfileComposable(it.user)
                    is SettingsScope.Address -> AddressComposable(it.user.addressData)
                    is SettingsScope.GstinDetails -> GstinDetailsComposable(
                        it.user.details as User.Details.DrugLicense,
                    )
                    is MenuScope -> {
                        MenuScreen(it)
                        manageBottomNavState(BottomNavKey.MENU)
                    }
                    is BatchesScope -> ViewBatchesScreen(it)
                    is QrCodeScope -> QrCodeScreen(it)
                    is IocSellerScope.InvUserListing -> IocListingScreen(it)
                    is IocSellerScope.InvListing -> IocListingScreen(it)
                    is IocSellerScope.InvDetails -> IocListingScreen(it)
                    is IocSellerScope.IOCListing -> IocScreen(it, scaffoldState)
                    is IocSellerScope.IOCCreate -> IocScreen(it, scaffoldState)
                    is IocBuyerScope.InvUserListing -> IocBuyerListingScreen(it)
                    is IocBuyerScope.InvListing -> IocBuyerListingScreen(it)
                    is IocBuyerScope.InvDetails -> IocBuyerListingScreen(it)
                    is IocBuyerScope.IOCPaymentMethod -> IocBuyerListingScreen(it)
                    is IocBuyerScope.IOCPayNow -> IocBuyerListingScreen(it)
                    is EmployeeScope.SelectUserType -> AddEmployeeScreen(it)
                    is EmployeeScope.PersonalData -> AddEmployeeStepOneScreen(it)
                    is EmployeeScope.AddressData -> AddEmployeeAddressDetailsScreen(it)
                    is EmployeeScope.Details.Aadhaar -> AddEmployeeAadharInfoScreen(it, scaffoldState)
                    is EmployeeScope.ViewEmployee -> ViewEmployees(it)
                    is EmployeeScope.SuccessEmployee -> SuccessEmployees(it)
                }
                if (it is CommonScope.WithNotifications) it.showNotificationAlert()
            }
        },
        bottomBar = {
            if (mBottomNavItems.isNullOrEmpty() && mUserType != null) {
                if (mUserType == UserType.STOCKIST) {
                    mBottomNavItems = listOf(
                        BottomNavigationItem.Dashboard,
                        BottomNavigationItem.InStores,
                        BottomNavigationItem.PurchaseOrders,
                        BottomNavigationItem.Cart,
                        BottomNavigationItem.Drawer
                    )
                } else {
                    mBottomNavItems = listOf(
                        BottomNavigationItem.Dashboard,
                        BottomNavigationItem.Settings,
                        BottomNavigationItem.Stores,
                        BottomNavigationItem.Cart,
                        BottomNavigationItem.Drawer
                    )
                }
            }
            if (mUserType != null) {
                if (childScope.value !is OrderHsnEditScope && childScope.value !is ViewOrderScope) {
                    BottomNavigationBar(mBottomNavItems)
                }
            }
        }
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
    if (info.icon != ScopeIcon.NO_ICON || info.icon != ScopeIcon.HAMBURGER) {
        val keyboard = LocalSoftwareKeyboardController.current
        Icon(
            tint = if (info.titleColor != -1L) {
                Color(info.titleColor)
            } else {
                MaterialTheme.colors.background
            },
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
            color = if (info.titleColor != -1L) {
                Color(info.titleColor)
            } else {
                MaterialTheme.colors.background
            }
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

    info.notificationItemsCount?.let {
        Box(
            modifier = Modifier
                .weight(0.15f)
                .clickable(indication = null) { info.goToNotifications() }
                .padding(10.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(
                    Color(0xFF003657)
                )

            )
            val notificationItems = it.flow.collectAsState()

            if (notificationItems.value > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(5.dp)) {
                        drawCircle(Color.Red)
                    }
                    /* Text(
                        text = cartItems.value.toString(),
                        color = ConstColors.red,
                        fontWeight = FontWeight.W700,
                        fontSize = 10.sp,
                    )*/
                }
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

    if (info.icon != ScopeIcon.HAMBURGER) {
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
    } else {
        Box(
            modifier = Modifier.weight(0.05f)
        )
    }
    Surface(elevation = 5.dp, modifier = Modifier.weight(0.7f)) {

        Row(
            modifier = Modifier
                .height(40.dp)
                .clickable(indication = null) { info.goToSearch() }
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(horizontal = 14.dp),
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
    }
    Box(
        modifier = Modifier
            .weight(0.15f)
            .clickable(indication = null) { info.goToNotifications() }
            .padding(10.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
                .align(Alignment.Center),
            colorFilter = ColorFilter.tint(
                Color(0xFF003657)
            )
        )
        val notification = info.notificationItemsCount.flow.collectAsState()
        if (notification.value > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(5.dp)) {
                    drawCircle(Color.Red)
                }
                /* Text(
                    text = cartItems.value.toString(),
                    color = ConstColors.red,
                    fontWeight = FontWeight.W700,
                    fontSize = 10.sp,
                )*/
            }
        }


        /*val cartCount = info.cartItemsCount?.flow?.collectAsState()
        if (cartCount != null && cartCount.value > 0) {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = cartCount.value
        } else {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = 0
        }*/
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

/**
 * display header data for instore seller details
 */
@Composable
private fun InStoreHeaderData(info: TabBarInfo.InStoreProductTitle, scope: TabBarScope) {
    val activity = LocalContext.current as MainActivity

    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = info.icon.toLocalIcon(),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
                .padding(start = 16.dp)
                .clickable(
                    indication = null,
                    onClick = {
                        scope.goBack()
                    },
                )
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val (name, address, phone, phoneLogo, locationLogo) = createRefs()
            Text(
                text = info.title,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                modifier = Modifier.constrainAs(name) {
                    width = Dimension.preferredWrapContent
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, margin = 5.dp)
                }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_address_small),
                contentDescription = null,
                modifier = Modifier.constrainAs(locationLogo) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.bottom, margin = 5.dp)
                }
            )
            Text(
                text = info.address,
                textAlign = TextAlign.Start,
                fontSize = 14.sp,
                color = MaterialTheme.colors.background,
                modifier = Modifier.constrainAs(address) {
                    width = Dimension.fillToConstraints
                    start.linkTo(locationLogo.end, margin = 3.dp)
                    end.linkTo(phoneLogo.start, margin = 5.dp)
                    top.linkTo(locationLogo.top)
                    bottom.linkTo(locationLogo.bottom)
                }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_call),
                contentDescription = null,
                modifier = Modifier.constrainAs(phoneLogo) {
                    end.linkTo(phone.start, margin = 3.dp)
                    bottom.linkTo(locationLogo.bottom)
                }
            )
            ClickableText(
                text = AnnotatedString(info.phone),
                style = TextStyle(color = MaterialTheme.colors.background, fontSize = 14.sp),
                onClick = { activity.openDialer(info.phone) },
                modifier = Modifier.constrainAs(phone) {
                    width = Dimension.preferredWrapContent
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(phoneLogo.top)
                    bottom.linkTo(phoneLogo.bottom)
                }
            )
        }
    }
}


/**
 * display header data for instore seller details
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NoIconHeader(
    scope: TabBarScope,
    info: TabBarInfo.NoIconTitle,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (mUserType == UserType.STOCKIST) {
            Image(
                modifier = Modifier
                    .weight(0.15f)
                    .height(25.dp)
                    .width(25.dp)
                    .clickable {
                        EventCollector.sendEvent(Event.Transition.Settings(true))
                    },
                painter = painterResource(id = R.drawable.ic_personal),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    Color(0xFF003657)
                )
            )
        } else {
            Image(
                modifier = Modifier
                    .weight(0.15f)
                    .height(30.dp)
                    .width(30.dp),
                painter = painterResource(id = R.drawable.ic_small_logo),
                contentDescription = null
            )
        }
        when (scope.childScope.flow.collectAsState().value) {
            is StoresScope -> {
                Box(modifier = Modifier.weight(0.7f))
            }
            is OrdersScope -> {
                Box(modifier = Modifier.weight(0.7f))
            }
            is InStoreSellerScope -> {
                Row(modifier = Modifier.weight(0.7f)) {
                    val scopeCustom =
                        scope.childScope.flow.collectAsState().value as InStoreSellerScope
                    BasicSearchBar(
                        start = 0.dp,
                        input = scopeCustom.searchText.flow.collectAsState().value,
                        hint = R.string.search_tradename,
                        searchBarEnd = SearchBarEnd.Eraser,
                        icon = Icons.Default.Search,
                        elevation = 2.dp,
                        isSearchFocused = false,
                        onSearch = { v, _ ->
                            scopeCustom.search(v)
                        },
                    )
                }
            }
            else -> {
                Surface(elevation = 5.dp, modifier = Modifier.weight(0.7f)) {
                    Row(
                        modifier = Modifier
                            .clickable(indication = null) { info.goToSearch() }
                            .background(Color.White, MaterialTheme.shapes.medium)
                            .padding(horizontal = 14.dp)
                            .height(40.dp),
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
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(0.15f)
                .clickable(indication = null) { info.goToNotifications() }
                .padding(10.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(
                    Color(0xFF003657)
                )
            )
            val cartItems = info.cartItemsCount?.flow?.collectAsState()
            if (cartItems?.value != null && cartItems.value > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(5.dp)) {
                        drawCircle(Color.Red)
                    }
                    /* Text(
                        text = cartItems.value.toString(),
                        color = ConstColors.red,
                        fontWeight = FontWeight.W700,
                        fontSize = 10.sp,
                    )*/
                }
            }
        }
    }

    val cartCount = info.cartItemsCount?.flow?.collectAsState()
    if (cartCount != null) {
        if (cartCount.value > 0) {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = cartCount.value
        } else {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = 0
        }
    }
}

/**
 * display header data for instore seller details
 */
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun StoreHeader(
    scope: TabBarScope,
    info: TabBarInfo.StoreTitle,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = info.icon.toLocalIcon(),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
                .padding(start = 16.dp)
                .clickable(
                    indication = null,
                    onClick = {
                        scope.goBack()
                    },
                )
        )
        Box(modifier = Modifier.weight(0.75f)) {
            Surface(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
                border = BorderStroke(1.dp, ConstColors.ltgray),
                onClick = { info.openBottomSheet() }) {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = info.storeName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W700,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Space(dp = 4.dp)
                    Image(
                        painter = painterResource(id = R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(0.15f)
                .clickable(indication = null) { info.goToNotifications() }
                .padding(10.dp),
        ) {
            if (info.showNotifications) {

                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Center),
                    colorFilter = ColorFilter.tint(
                        Color(0xFF003657)
                    )
                )
                val notificationCount = info.notificationItemsCount?.flow?.collectAsState()
                if (notificationCount?.value != null && notificationCount.value > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(5.dp)) {
                            drawCircle(Color.Red)
                        }
                    }
                }
            }
        }
    }

    val cartCount = info.cartItemsCount?.flow?.collectAsState()
    if (cartCount != null) {
        if (cartCount.value > 0) {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = cartCount.value
        } else {
            val cart = mBottomNavItems?.find { it.key == BottomNavKey.CART }
            cart?.cartCount?.value = 0
        }
    }
}

/**
 * use this as header when only back icon is required on header and nothing else
 */
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OnlyBackHeader(
    scope: TabBarScope,
    info: TabBarInfo.OnlyBackHeader,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        if (info.title.isNotEmpty())
            Text(
                text = if (info.title.contains("_")) stringResourceByName(info.title) else info.title,
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
    }
}

/**
 * use this as header when only back icon is required on header and nothing else
 */
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OffersHeader(
    scope: TabBarScope,
    info: TabBarInfo.OfferHeader,
) {
    val childScope = scope.childScope.flow.collectAsState().value as OffersScope.ViewOffers
    val switchEnabled = remember { mutableStateOf(false) }
    val showManufacturers = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
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
                            if (switchEnabled.value) {
                                switchEnabled.value = false
                            } else {
                                scope.goBack()
                            }
                        },
                    )
            )
            if (info.title.isNotEmpty() && !switchEnabled.value)
                Text(
                    text = stringResourceByName(info.title),
                    color = MaterialTheme.colors.background,
                    fontWeight = FontWeight.W700,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

        }

        if (switchEnabled.value) {
            val search =
                childScope.productSearch.flow.collectAsState()
            Row {
                BasicSearchBar(
                    input = search.value,
                    hint = R.string.search_by_product,
                    icon = null,
                    horizontalPadding = 12.dp,
                    isSearchFocused = true,
                    searchBarEnd = SearchBarEnd.Eraser,
                    onSearch = { value, _ ->
                        if (value.isEmpty()) {
                            switchEnabled.value = false
                        }
                        childScope.startSearch(search = value)
                    },
                    isSearchCross = true
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = Color.Transparent,
                onClick = {
                    showManufacturers.value = !showManufacturers.value
                    EventCollector.sendEvent(Event.Action.Offers.ShowManufacturers(showManufacturers.value))
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_manufacturer_toolbar),
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .padding(2.dp),
                )

            }
            Space(dp = 12.dp)
            Surface(
                color = Color.Transparent,
                onClick = { switchEnabled.value = !switchEnabled.value }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .padding(2.dp),
                )

            }
        }
        Space(dp = 16.dp)
    }
}


/**
 * composable for bottom navigation item
 */
@Composable
fun BottomNavigationBar(items: List<BottomNavigationItem>?, height: Int = 56) {
    if (mUserType != null) {
        Surface(
            elevation = 5.dp, color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(height.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                items?.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable {
                                EventCollector.sendEvent(item.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = if (item.selected.value) painterResource(id = item.selectedIcon) else painterResource(
                                id = item.unSelectedIcon
                            ),
                            contentDescription = null,
                        )

                        if (item.cartCount.value > 0) {
                            Text(
                                text = item.cartCount.value.toString(),
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 20.dp, start = 20.dp),
                                fontWeight = FontWeight.W800,
                            )
                        }
                    }
                }
            }
        }
    } else {
        Box {}
    }

}


/**
 * bottom nav items , icons and their selected state
 */
sealed class BottomNavigationItem(
    var route: Event.Transition,
    var unSelectedIcon: Int,
    var selectedIcon: Int,
    var selected: MutableState<Boolean>,
    var cartCount: MutableState<Int> = mutableStateOf(0),
    var key: BottomNavKey
) {
    object Dashboard :
        BottomNavigationItem(
            Event.Transition.Dashboard,
            R.drawable.ic_home,
            R.drawable.ic_home_selected,
            mutableStateOf(true),
            key = BottomNavKey.DASHBOARD
        )

    object Settings :
        BottomNavigationItem(
            Event.Transition.Settings(false),
            R.drawable.ic_account,
            R.drawable.ic_account_selected,
            mutableStateOf(false),
            key = BottomNavKey.SETTINGS
        )

    object PurchaseOrders :
        BottomNavigationItem(
            Event.Transition.PoOrdersAndHistory,
            R.drawable.ic_po,
            R.drawable.ic_po_selected,
            mutableStateOf(false),
            key = BottomNavKey.PO
        )

    object Cart :
        BottomNavigationItem(
            Event.Transition.Cart,
            R.drawable.ic_grey_cart,
            R.drawable.ic_cart_selected,
            mutableStateOf(false),
            key = BottomNavKey.CART
        )

    object Drawer :
        BottomNavigationItem(
            Event.Transition.Menu,
            R.drawable.ic_hamburger,
            R.drawable.ic_hamburger_selected,
            mutableStateOf(false),
            key = BottomNavKey.MENU
        )

    object Stores :
        BottomNavigationItem(
            Event.Transition.Stores,
            R.drawable.ic_stores,
            R.drawable.ic_strores_selected,
            mutableStateOf(false),
            mutableStateOf(0),
            key = BottomNavKey.STORES
        )

    object InStores :
        BottomNavigationItem(
            Event.Transition.InStore,
            R.drawable.ic_instore_unselected,
            R.drawable.ic_instore_selected,
            mutableStateOf(false),
            key = BottomNavKey.INSTORES
        )
}

enum class BottomNavKey {
    DASHBOARD, SETTINGS, PO, CART, MENU, STORES, INSTORES
}

/**
 * Show selected and unselected icon on bottom nav based on navigation stack
 */
private fun manageBottomNavState(selectedKey: BottomNavKey) {

    mBottomNavItems?.forEach {
        it.selected.value = false
    }

    val selectedItem = mBottomNavItems?.find { it.key == selectedKey }
    selectedItem?.selected?.value = true

}

private inline fun ScopeIcon.toLocalIcon(): ImageVector = when (this) {
    ScopeIcon.HAMBURGER -> Icons.Default.Menu
    ScopeIcon.BACK -> Icons.Default.ArrowBack
    ScopeIcon.NO_ICON -> throw Exception("no icon")
}
