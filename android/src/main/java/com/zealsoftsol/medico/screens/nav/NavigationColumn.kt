package com.zealsoftsol.medico.screens.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.NavigationCell
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun NavigationColumn(
    fullName: String,
    trialString: String?,
    navigationSection: NavigationSection,
    onSectionSelected: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.CenterStart) {
            Image(
                painter = painterResource(id = R.drawable.nav_bg),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                CoilImage(
                    src = "",
                    size = 96.dp,
                    onError = { UserLogoPlaceholder(fullName) },
                    onLoading = { UserLogoPlaceholder(fullName) },
                )
                Space(8.dp)
                Text(
                    text = fullName,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    fontSize = 14.sp,
                )
                Space(4.dp)
                Text(
                    text = "",
                    color = Color.White,
                    fontSize = 14.sp,
                )
                if (trialString != null) {
                    Space(4.dp)
                    Text(
                        text = trialString,
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                navigationSection.main.forEach {
                    val (icon, text) = it.iconAndText()
                    NavigationCell(
                        icon = icon,
                        text = text,
                        label =
                            {
//                                Space(12.dp)
//                                Text(
//                                    text = "NEW",
//                                    color = Color.White,
//                                    fontSize = 12.sp,
//                                    fontWeight = FontWeight.W600,
//                                    modifier = Modifier
//                                        .background(Color.Red, RoundedCornerShape(percent = 50))
//                                        .padding(vertical = 4.dp, horizontal = 8.dp),
//                                )
                            },
                        onClick = {
                            onSectionSelected()
                            it.select()
                        },
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Separator()
                navigationSection.footer.forEach {
                    val (icon, text) = it.iconAndText()
                    NavigationCell(
                        icon = icon,
                        text = text,
                        color = ConstColors.gray,
                        label = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 18.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = BuildConfig.VERSION_NAME,
                                    color = ConstColors.gray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.W600,
                                )
                            }
                        },
                        onClick = { it.select() }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationOption.iconAndText(): Pair<Painter, String> = when (this) {
    NavigationOption.Dashboard -> painterResource(id = R.drawable.ic_dashboard)
    NavigationOption.LogOut -> painterResource(id = R.drawable.ic_exit)
   
} to stringResourceByName(stringId)