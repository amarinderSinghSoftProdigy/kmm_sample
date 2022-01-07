package com.zealsoftsol.medico.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.MenuScope
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.clickable

@Composable
fun MenuScreen(scope: MenuScope) {
    val user = scope.user
    val userType = user.type

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        Image(
            painter = painterResource(id = R.drawable.medico_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .height(30.dp)
        )


        //show view based on user type
        if (userType == UserType.STOCKIST) {
            Text(
                text = (user.details as User.Details.DrugLicense).tradeName,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 50.dp, top = 50.dp),
                fontWeight = FontWeight.W700
            )
        } else {
            Text(
                text = user.fullName(),
                color = Color.Black,
                modifier = Modifier.padding(start = 50.dp, top = 50.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {

            AccountContentItem(
                route = Event.Transition.Dashboard,
                drawableResourceId = R.drawable.ic_menu_dashboard,
                stringResourceId = R.string.your_dashboard,
                scope = scope
            )
            Separator(thickness = 0.5f)
            AccountContentItem(
                route = Event.Transition.Notifications,
                drawableResourceId = R.drawable.ic_menu_notifications,
                stringResourceId = R.string.notifications,
                scope = scope
            )

            //display rest of the user menu based on user type
            if (userType == UserType.STOCKIST) {
                StockistMenu(scope)
            } else {
                RetailerAndHospitalMenu(scope)
            }
            Separator(thickness = 1f)
        }
    }
}

/**
 * Menu items to be shown to retailer and hospitals
 */
@Composable
fun RetailerAndHospitalMenu(scope: MenuScope) {
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Orders,
        drawableResourceId = R.drawable.ic_menu_orders,
        stringResourceId = R.string.your_orders,
        scope = scope
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.MyInvoices,
        drawableResourceId = R.drawable.ic_menu_invoice,
        stringResourceId = R.string.your_invoices,
        scope = scope
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Stores,
        drawableResourceId = R.drawable.ic_menu_stores,
        stringResourceId = R.string.stores,
        scope = scope
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Search(),
        drawableResourceId = R.drawable.ic_menu_search,
        stringResourceId = R.string.search,
        scope = scope
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Management(UserType.STOCKIST),
        drawableResourceId = R.drawable.ic_menu_stockist,
        stringResourceId = R.string.stockists,
        scope = scope
    )
}

/**
 * Menu items to be shown to stockist
 */
@Composable
fun StockistMenu(scope: MenuScope) {
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Settings,
        drawableResourceId = R.drawable.ic_personal,
        stringResourceId = R.string.my_account,
        scope = scope,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        drawableResourceId = R.drawable.ic_menu_po,
        stringResourceId = R.string.purchase_orders,
        scope = scope,
        fontWeight = FontWeight.W700,
        arrowVisibility = false
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.PoOrdersAndHistory,
        drawableResourceId = R.drawable.ic_menu_po_history,
        stringResourceId = R.string.purchase_orders_history,
        scope = scope,
        paddingStart = 50,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.PoInvoices,
        drawableResourceId = R.drawable.ic_menu_po_invoice,
        stringResourceId = R.string.po_invoices,
        scope = scope,
        paddingStart = 50
    )
    Separator(thickness = 0.5f)
 /*   AccountContentItem(
        route = Event.Transition.Inventory,
        drawableResourceId = R.drawable.ic_menu_inventory,
        stringResourceId = R.string.inventory,
        scope = scope,
    )
    Separator(thickness = 0.5f)*/
    AccountContentItem(
        route = Event.Transition.Stores,
        drawableResourceId = R.drawable.ic_menu_stores,
        stringResourceId = R.string.stores,
        scope = scope,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        drawableResourceId = R.drawable.ic_menu_connections,
        stringResourceId = R.string.connections,
        scope = scope,
        fontWeight = FontWeight.W700,
        arrowVisibility = false
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        Event.Transition.Management(UserType.STOCKIST),
        drawableResourceId = R.drawable.ic_menu_stockist,
        stringResourceId = R.string.stockists,
        scope = scope,
        paddingStart = 50,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Management(UserType.RETAILER),
        drawableResourceId = R.drawable.ic_menu_retailers,
        stringResourceId = R.string.retailers,
        scope = scope,
        paddingStart = 50,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Management(UserType.HOSPITAL),
        drawableResourceId = R.drawable.ic_menu_hospitals,
        stringResourceId = R.string.hospitals,
        scope = scope,
        paddingStart = 50,
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.Orders,
        drawableResourceId = R.drawable.ic_menu_orders,
        stringResourceId = R.string.your_orders,
        scope = scope
    )
    Separator(thickness = 0.5f)
    AccountContentItem(
        route = Event.Transition.MyInvoices,
        drawableResourceId = R.drawable.ic_menu_invoice,
        stringResourceId = R.string.your_invoices,
        scope = scope
    )
}


@Composable
fun Separator(thickness: Float) {
    Divider(color = ConstColors.separator.copy(alpha = 0.5f), thickness = thickness.dp)
}

/**
 * Show the text view for account content
 * @param route - where to transition on click
 * @param altRoute - in case the transition is of type Action rather than Transition
 * @param drawableResourceId - resource id to display before text
 * @param stringResourceId - resource id of text to be displayed
 */
@Composable
private fun AccountContentItem(
    route: Event.Transition? = null,
    altRoute: Event.Action? = null,
    drawableResourceId: Int,
    stringResourceId: Int,
    scope: MenuScope? = null,
    paddingStart: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    arrowVisibility: Boolean = true,
) {
    Column {
        Separator(thickness = 1f)

        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                indication = rememberRipple(),
                onClick = {
                    if (route != null)
                        scope?.sendEvent(transition = route)
                    else if (altRoute != null)
                        scope?.sendEvent(action = altRoute)

                }
            )) {

            val (icon, text, arrow) = createRefs()

            Image(
                painter = painterResource(drawableResourceId), contentDescription = null,
                modifier = Modifier
                    .constrainAs(icon) {
                        start.linkTo(parent.start, paddingStart.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .width(22.dp)
            )
            Text(
                text = stringResource(stringResourceId),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = fontWeight,
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(icon.end, 24.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            if (arrowVisibility) {
                Image(painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.constrainAs(arrow) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    })
            }
        }
    }
}