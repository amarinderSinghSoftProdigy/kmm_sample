package com.zealsoftsol.medico.screens.settings

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
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun SettingsScreen(scope: SettingsScope) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_acc_place), contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_user_placeholder),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, top = 120.dp)
                .height(90.dp)
                .width(90.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = stringResource(id = R.string.my_account),
                fontWeight = FontWeight.W600,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            when (scope) {
                is SettingsScope.List -> SettingsList(scope.sections)
                is SettingsScope.Profile -> Profile(scope.user)
                is SettingsScope.Address -> Address(scope.addressData)
                is SettingsScope.GstinDetails -> GstinDetails(scope.details)
            }
            Divider(color = ConstColors.separator, thickness = (0.5).dp)
            AccountContentItem(
                altRoute = Event.Action.Help.GetHelp,
                drawableResourceId = R.drawable.ic_terms_cond,
                stringResourceId = R.string.tc_privacy_policy
            )
            Divider(color = ConstColors.separator, thickness = (0.5).dp)
            AccountContentItem(
                altRoute = Event.Action.Help.GetHelp,
                drawableResourceId = R.drawable.ic_customer_care_acc,
                stringResourceId = R.string.customer_care
            )
        }
    }
}

@Composable
private fun SettingsList(sections: List<SettingsScope.List.Section>) {
    sections.forEach {
        Column {
            Divider(color = ConstColors.separator, thickness = 1.dp)

            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(
                    indication = rememberRipple(),
                    onClick = {
                        it.select()
                    }
                )) {

                val (icon, text, arrow) = createRefs()

                Image(
                    painter = painterResource(
                        id = when (it) {
                            SettingsScope.List.Section.PROFILE -> R.drawable.ic_personal
                            SettingsScope.List.Section.CHANGE_PASSWORD -> R.drawable.ic_password
                            SettingsScope.List.Section.ADDRESS -> R.drawable.ic_address_account
                            SettingsScope.List.Section.GSTIN_DETAILS -> R.drawable.ic_gstin_account
                            SettingsScope.List.Section.WHATSAPP_PREFERENCE -> R.drawable.ic_whatsapp
                        }
                    ), contentDescription = null,
                    modifier = Modifier.constrainAs(icon) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                )
                Text(
                    text = stringResourceByName(it.stringId),
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.constrainAs(text) {
                        start.linkTo(icon.end, 16.dp)
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
    stringResourceId: Int
) {
    Column {
        Divider(color = ConstColors.separator, thickness = 1.dp)

        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                indication = rememberRipple(),
                onClick = {
                    if (route != null)
                        EventCollector.sendEvent(route)
                    else if (altRoute != null)
                        EventCollector.sendEvent(altRoute)

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
                    start.linkTo(icon.end, 16.dp)
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
private fun Profile(user: User) {
    ReadOnlyField(user.firstName, R.string.first_name)
    Space(12.dp)
    ReadOnlyField(user.lastName, R.string.last_name)
    Space(12.dp)
    ReadOnlyField(user.email, R.string.email)
    Space(12.dp)
    ReadOnlyField(user.phoneNumber.formatIndia(), R.string.phone_number)
}

@Composable
private fun Address(addressData: AddressData) {
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

@Composable
private fun GstinDetails(details: User.Details.DrugLicense) {
    ReadOnlyField(details.tradeName, R.string.trade_name)
    Space(12.dp)
    ReadOnlyField(details.gstin, R.string.gstin)
    Space(12.dp)
    ReadOnlyField(details.license1, R.string.drug_license_1)
    Space(12.dp)
    ReadOnlyField(details.license2, R.string.drug_license_2)
}