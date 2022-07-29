package com.zealsoftsol.medico.screens

import android.view.WindowManager
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TabBar
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.showNotificationAlert
import com.zealsoftsol.medico.screens.dashboard.DashboardScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private var mBottomNavItems: List<BottomNavigationItem>? = null

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TabBarScreen(scope: TabBarScope, coroutineScope: CoroutineScope, activity: MainActivity) {
    val scaffoldState = rememberScaffoldState()
    val navigation = scope.navigationSection.flow.collectAsState()
    val childScope = scope.childScope.flow.collectAsState()

    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = navigation.value != null,
        topBar = {
            val tabBarInfo = scope.tabBar.flow.collectAsState()
            TabBar(isNewDesign = tabBarInfo.value is TabBarInfo.NewDesignLogo) {
                Row(verticalAlignment = CenterVertically) {
                    when (val info = tabBarInfo.value) {
                        is TabBarInfo.NewDesignLogo -> {
                            val keyboard = LocalSoftwareKeyboardController.current
                            Icon(
                                imageVector = info.icon.toLocalIcon(),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(CenterVertically)
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
                        is TabBarInfo.NoHeader -> Box {}

                    }
                }
            }
        },
        content = {
            var padding = 56
            Crossfade(
                childScope.value,
                animationSpec = tween(durationMillis = 200),
                modifier = Modifier.padding(bottom = padding.dp)
            ) {
                when (it) {
                    is DashboardScope -> {
                        DashboardScreen(it)
                        manageBottomNavState(BottomNavKey.DASHBOARD)
                    }
                }
                if (it is CommonScope.WithNotifications) it.showNotificationAlert()
            }
        },
        bottomBar = {
            mBottomNavItems = listOf(
                BottomNavigationItem.Dashboard,
            )
            BottomNavigationBar(mBottomNavItems)
        })
}



/**
 * composable for bottom navigation item
 */
@Composable
fun BottomNavigationBar(items: List<BottomNavigationItem>?, height: Int = 56) {
    Surface(
        elevation = 15.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        border = BorderStroke(1.dp, ConstColors.newDesignGray)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(height.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = CenterVertically,
        ) {


            items?.forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(height.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .height(4.dp)
                            .padding(horizontal = 8.dp),
                        color = if (item.selected.value) ConstColors.lightBlue else Color.White,
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
                    ) {
                        Divider(
                            thickness = 4.dp,
                            color = if (item.selected.value) ConstColors.lightBlue else Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(height.dp)
                            .clickable {
                                if (item.route != null)
                                    EventCollector.sendEvent(item.route!!)
                                else
                                    EventCollector.sendEvent(item.action!!)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Column {
                            Image(
                                modifier = Modifier.align(CenterHorizontally),
                                painter = if (item.selected.value) painterResource(id = item.selectedIcon) else painterResource(
                                    id = item.unSelectedIcon
                                ),
                                contentDescription = null,
                            )
                            Space(5.dp)
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(CenterHorizontally),
                                textAlign = TextAlign.Center,
                                text = item.key.title,
                                fontSize = 12.sp,
                                color = if (item.selected.value) ConstColors.lightBlue else ConstColors.txtGrey
                            )
                        }

                        if (item.cartCount.value > 0) {
                            Text(
                                text = item.cartCount.value.toString(),
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 35.dp, start = 20.dp),
                                fontWeight = FontWeight.W800,
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * bottom nav items , icons and their selected state
 */
sealed class BottomNavigationItem(
    var route: Event.Transition?,
    var unSelectedIcon: Int,
    var selectedIcon: Int,
    var selected: MutableState<Boolean>,
    var cartCount: MutableState<Int> = mutableStateOf(0),
    var key: BottomNavKey,
    var action: Event.Action? = null,
) {
    object Dashboard :
        BottomNavigationItem(
            Event.Transition.Dashboard,
            R.drawable.ic_home,
            R.drawable.ic_home_selected,
            mutableStateOf(true),
            key = BottomNavKey.DASHBOARD
        )

    object Logout :
        BottomNavigationItem(
            null,
            R.drawable.ic_logout_grey,
            R.drawable.ic_logout_selected,
            mutableStateOf(false),
            key = BottomNavKey.LOGOUT,
            action = Event.Action.Auth.LogOut(true),
        )

}

enum class BottomNavKey(val title: String) {
    DASHBOARD("Home"), SETTINGS("Profile"), PO("Orders"), CART("Basket"), MENU("Menu"),
    STORES("Stores"), INSTORES("In-Stores"), LOGOUT("Logout"), DEBT_COLLECTION("IOC"),
    REWARDS("Rewards")
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
