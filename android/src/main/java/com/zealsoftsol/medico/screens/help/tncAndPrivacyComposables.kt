package com.zealsoftsol.medico.screens.help

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun TermsConditionsPrivacyPolicyScreen(scope: HelpScope) {
    val activity = LocalContext.current as MainActivity
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    ) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val tabs = ArrayList<HelpScope.Tab>()
                tabs.add(HelpScope.Tab.TERMS_AND_CONDITIONS)
                tabs.add(HelpScope.Tab.PRIVACY_POLICY)
                var isActive = true

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(41.dp)
                        .background(MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
                ) {
                    tabs.forEach {
                        var boxMod = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                        boxMod = if (tabs.size == 1) {
                            boxMod
                        } else {
                            boxMod
                                .padding(5.dp)
                                .clickable { /*scope.selectTab(it)*/ }
                        }
                        boxMod = if (isActive) {
                            boxMod.background(ConstColors.lightBlue, MaterialTheme.shapes.medium)
                        } else {
                            boxMod
                        }
                        Row(
                            modifier = boxMod,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResourceByName(it.stringId),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                color = if (isActive) Color.White else MaterialTheme.colors.background,
                            )
                        }
                        isActive = false
                    }
                }


                loadWebUrl(url = scope.helpData.tosUrl, activity = activity)

                /*Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
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
                }*/
            }
        }
    }
}


@Composable
fun loadWebUrl(url: String, activity: Context) {
    AndroidView(factory = {
        WebView(activity).apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}