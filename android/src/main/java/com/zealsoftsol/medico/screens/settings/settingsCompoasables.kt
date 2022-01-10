package com.zealsoftsol.medico.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.formatIndia

@Composable
fun SettingsScreen(scope: SettingsScope) {
    val activity = LocalContext.current as MainActivity
    val user = scope.mUser
    val userType = user.type

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_acc_place), contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_user_placeholder),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, top = 100.dp)
                .height(90.dp)
                .width(90.dp)
        )

            Text(
                text = if (userType == UserType.STOCKIST) {
                    (user.details as User.Details.DrugLicense).tradeName
                } else {
                    user.fullName()
                },
                color = Color.Black,
                modifier = Modifier.padding(start = 115.dp, top = 155.dp),
                fontSize = 16.sp
            )

            ClickableText(
                text = AnnotatedString(user.phoneNumber),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                ),
                onClick = { activity.openDialer(user.phoneNumber) },
                modifier = Modifier.padding(start = 115.dp, top = 170.dp)
            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 230.dp)
        ) {

            Text(
                text = stringResource(id = R.string.my_account),
                fontWeight = FontWeight.W700,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            Separator(thickness = 0.5f)
            AccountContentItem(
                route = Event.Transition.Profile,
                drawableResourceId = R.drawable.ic_personal,
                stringResourceId = R.string.personal_profile,
                scope = scope
            )
            Separator(thickness = 0.5f)
            AccountContentItem(
                route = Event.Transition.ChangePassword,
                drawableResourceId = R.drawable.ic_password,
                stringResourceId = R.string.change_password,
                scope = scope
            )
            Separator(thickness = 0.5f)
            AccountContentItem(
                route = Event.Transition.Address,
                drawableResourceId = R.drawable.ic_address_account,
                stringResourceId = R.string.address,
                scope = scope
            )
            Separator(thickness = 0.5f)
            AccountContentItem(
                route = Event.Transition.GstinDetails,
                drawableResourceId = R.drawable.ic_gstin_account,
                stringResourceId = R.string.gstin_details,
                scope = scope
            )

            //show ui options based on user type
            if (userType == UserType.STOCKIST) {
                Separator(thickness = 1f)
                Text(
                    text = stringResource(id = R.string.preference),
                    fontWeight = FontWeight.W700,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, bottom = 18.dp, top = 18.dp)
                )
                Separator(thickness = 0.5f)
                AccountContentItem(
                    route = Event.Transition.WhatsappPreference,
                    drawableResourceId = R.drawable.ic_whatsapp,
                    stringResourceId = R.string.whatsapp,
                    scope = scope
                )
//                Separator(thickness = 0.5f)
//                AccountContentItem(
//                    drawableResourceId = R.drawable.ic_invoice_pref,
//                    stringResourceId = R.string.invoice_preferences,
//                    scope = scope
//                )
//                Separator(thickness = 0.5f)
//                AccountContentItem(
//                    drawableResourceId = R.drawable.ic_order_value,
//                    stringResourceId = R.string.order_value,
//                    scope = scope
//                )
            } else {
                AccountContentItem(
                    route = Event.Transition.WhatsappPreference,
                    drawableResourceId = R.drawable.ic_whatsapp,
                    stringResourceId = R.string.whatsapp_preference,
                    scope = scope
                )
            }
            Separator(thickness = 1f)
            //get website url
            val website = stringResource(id = R.string.website_name)
            Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.BottomStart) {
                ClickableText(
                    text = AnnotatedString(website),
                    style = TextStyle(
                        color = ConstColors.lightBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                    ),
                    onClick = { activity.openUrl("https://" + website) },
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 10.dp)
                )
            }
            Separator(thickness = 0.5f)
            AccountContentItem(
                altRoute = Event.Action.Help.GetHelp,
                drawableResourceId = R.drawable.ic_terms_cond,
                stringResourceId = R.string.tc_privacy_policy,
                scope = scope
            )
            Separator(thickness = 0.5f)
            AccountContentItem(
                altRoute = Event.Action.Help.GetHelp,
                drawableResourceId = R.drawable.ic_customer_care_acc,
                stringResourceId = R.string.customer_care,
                scope = scope
            )
            Separator(thickness = 0.5f)
            Box(modifier = Modifier.height(56.dp))
            Separator(thickness = 0.5f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(60.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                ) {
                    Text(
                        text = "${stringResource(id = R.string.version)} ${BuildConfig.VERSION_NAME}",
                        color = ConstColors.txtGrey,
                        fontSize = 15.sp
                    )
                    Text(
                        text = stringResource(id = R.string.copyright),
                        color = ConstColors.txtGrey,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            scope.sendEvent(action = Event.Action.Auth.LogOut(true))
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.log_out),
                        color = ConstColors.red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

            }

        }
    }
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
    scope: SettingsScope? = null
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
                fontSize = 15.sp,
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