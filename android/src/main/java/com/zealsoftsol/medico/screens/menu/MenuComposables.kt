package com.zealsoftsol.medico.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.MenuScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia

@Composable
fun MenuScreen(scope: MenuScope) {
    val activity = LocalContext.current as MainActivity
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
    scope: MenuScope? = null
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
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            Text(
                text = stringResource(stringResourceId),
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(icon.end, 24.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
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

@Composable
fun ProfileComposable(user: User) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize()
    ) {
        ReadOnlyField(user.firstName, R.string.first_name)
        Space(12.dp)
        ReadOnlyField(user.lastName, R.string.last_name)
        Space(12.dp)
        ReadOnlyField(user.email, R.string.email)
        Space(12.dp)
        ReadOnlyField(user.phoneNumber.formatIndia(), R.string.phone_number)
    }
}

@Composable
fun AddressComposable(addressData: AddressData) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize()
    ) {
        ReadOnlyField(addressData.pincode.toString(), R.string.pincode)
        Space(12.dp)
        ReadOnlyField(addressData.address, R.string.address_line)
        Space(12.dp)
        ReadOnlyField(addressData.landmark, R.string.landmark)
        Space(12.dp)
        ReadOnlyField(addressData.location, R.string.location)
        Space(12.dp)
        ReadOnlyField(addressData.city, R.string.city)
        Space(12.dp)
        ReadOnlyField(addressData.district, R.string.district)
        Space(12.dp)
        ReadOnlyField(addressData.state, R.string.state)
    }
}

@Composable
fun GstinDetailsComposable(details: User.Details.DrugLicense) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize()
    ) {
        ReadOnlyField(details.tradeName, R.string.trade_name)
        Space(12.dp)
        ReadOnlyField(details.gstin, R.string.gstin)
        Space(12.dp)
        ReadOnlyField(details.license1, R.string.drug_license_1)
        Space(12.dp)
        ReadOnlyField(details.license2, R.string.drug_license_2)
    }
}