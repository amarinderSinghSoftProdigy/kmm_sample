package com.zealsoftsol.medico.screens.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.NavigationCell
import com.zealsoftsol.medico.screens.common.Separator
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun NavigationColumn(
    userName: String,
    userType: UserType,
    navigationSection: NavigationSection,
    onSectionSelected: () -> Unit,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (bg, userInfo, mainSection, logOutSection) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.nav_bg),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
                .constrainAs(bg) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
        )
        Column(
            modifier = Modifier.constrainAs(userInfo) {
                start.linkTo(parent.start)
                centerVerticallyTo(bg)
            }.padding(start = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
            Space(8.dp)
            Text(
                text = userName,
                fontWeight = FontWeight.W700,
                color = Color.White,
            )
            Space(4.dp)
            Text(
                text = stringResourceByName(userType.stringId),
                color = Color.White,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().constrainAs(mainSection) {
                top.linkTo(bg.bottom)
                centerHorizontallyTo(parent)
            }
        ) {
            navigationSection.main.forEach {
                val (icon, text) = it.iconAndText
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
        Column(
            modifier = Modifier.fillMaxWidth().constrainAs(logOutSection) {
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
            }
        ) {
            Separator()
            navigationSection.footer.forEach {
                val (icon, text) = it.iconAndText
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

@Composable
private inline val NavigationOption.iconAndText: Pair<ImageVector, String>
    get() = when (this) {
        NavigationOption.Settings -> Icons.Filled.Settings
        NavigationOption.Stockists -> vectorResource(id = R.drawable.ic_stockist)
        NavigationOption.Retailers -> vectorResource(id = R.drawable.ic_retailer)
        NavigationOption.Hospitals -> vectorResource(id = R.drawable.ic_hospital)
        NavigationOption.SeasonBoys -> vectorResource(id = R.drawable.ic_season_boy)
        NavigationOption.LogOut -> vectorResource(id = R.drawable.ic_exit)
    } to stringResourceByName(stringId)