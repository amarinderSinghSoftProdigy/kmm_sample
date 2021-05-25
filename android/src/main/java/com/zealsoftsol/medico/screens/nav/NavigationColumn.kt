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
import androidx.compose.material.icons.filled.Circle
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
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.CoilImage
import com.zealsoftsol.medico.screens.common.NavigationCell
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.UserLogoPlaceholder
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun NavigationColumn(
    fullName: String,
    userType: UserType,
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
                )
                Space(4.dp)
                Text(
                    text = stringResourceByName(userType.stringId),
                    color = Color.White,
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                navigationSection.main.forEach {
                    val (icon, text) = it.iconAndText()
                    NavigationCell(
                        icon = icon,
                        text = text,
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
                        onClick = { it.select() }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationOption.iconAndText(): Pair<Painter, String> = when (this) {
    NavigationOption.Dashboard -> painterResource(id = R.drawable.ic_dashboard)
    NavigationOption.Stores -> painterResource(id = R.drawable.ic_stores)
    NavigationOption.Stockists -> painterResource(id = R.drawable.ic_stockist)
    NavigationOption.Retailers -> painterResource(id = R.drawable.ic_retailer)
    NavigationOption.Hospitals -> painterResource(id = R.drawable.ic_hospital)
    NavigationOption.SeasonBoys -> painterResource(id = R.drawable.ic_season_boy)
    NavigationOption.Help -> rememberVectorPainter(Icons.Filled.Help)
    NavigationOption.Orders -> rememberVectorPainter(Icons.Filled.Circle)
    NavigationOption.NewOrders -> rememberVectorPainter(Icons.Filled.Circle)
    NavigationOption.Settings -> rememberVectorPainter(Icons.Filled.Settings)
    NavigationOption.LogOut -> painterResource(id = R.drawable.ic_exit)
} to stringResourceByName(stringId)