package com.zealsoftsol.medico.screens.help

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.screens.common.MedicoSmallButton
import com.zealsoftsol.medico.screens.common.Space

@ExperimentalComposeUiApi
@Composable
fun HelpScreens(scope: HelpScope) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (scope) {
            is HelpScope.ContactUs -> ContactUsScreen(scope)
            is HelpScope.TandC -> TermsConditionsPrivacyPolicyScreen(scope)
        }
    }
}


@Composable
fun HelpScreen(scope: HelpScope) {
    val activity = LocalContext.current as MainActivity
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 50.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.background(ConstColors.lightBlue).fillMaxWidth()
                        .height(50.dp).padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Space(10.dp)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_help),
                            contentDescription = null,
                            tint = Color.White,
                        )
                        Space(10.dp)
                        Text(
                            text = stringResource(id = R.string.connect_with_us),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                    MedicoSmallButton(
                        text = stringResource(R.string.call_now),
                        onClick = { activity.openDialer(scope.helpData.contactUs.customerCarePhoneNumber) },
                    )
                }
                Text(
                    text = stringResource(id = R.string.contact_info),
                    color = MaterialTheme.colors.background,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
                )
                Divider()
                Space(12.dp)
                Row(
                    modifier = Modifier.height(50.dp).fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.sales),
                        color = MaterialTheme.colors.background,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Button(
                        onClick = { activity.openDialer(scope.helpData.contactUs.salesPhoneNumber) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = ConstColors.lightBlue.copy(alpha = 0.12f),
                            contentColor = ConstColors.lightBlue,
                        ),
                        border = BorderStroke(1.dp, ConstColors.lightBlue),
                        elevation = null,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                        )
                        Space(12.dp)
                        Text(
                            text = scope.helpData.contactUs.salesPhoneNumber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }
                }
                Row(
                    modifier = Modifier.height(50.dp).fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.email_short),
                        color = MaterialTheme.colors.background,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Button(
                        onClick = { activity.sendMail(scope.helpData.contactUs.email) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = ConstColors.lightBlue.copy(alpha = 0.12f),
                            contentColor = ConstColors.lightBlue,
                        ),
                        border = BorderStroke(1.dp, ConstColors.lightBlue),
                        elevation = null,
                    ) {
                        Text(
                            text = scope.helpData.contactUs.email,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }
                }
                Space(12.dp)
                Divider()
                Space(12.dp)
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                        .clickable { activity.openUrl(scope.helpData.tosUrl) }
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tos),
                            contentDescription = null,
                            tint = MaterialTheme.colors.background,
                        )
                        Space(24.dp)
                        Text(
                            text = stringResource(id = R.string.tos),

                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = ConstColors.lightBlue,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                        .clickable { activity.openUrl(scope.helpData.privacyPolicyUrl) }
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_privacy),
                            contentDescription = null,
                            tint = MaterialTheme.colors.background,
                        )
                        Space(24.dp)
                        Text(
                            text = stringResource(id = R.string.privacy_policy),
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = ConstColors.lightBlue,
                    )
                }
            }
        }
    }
}