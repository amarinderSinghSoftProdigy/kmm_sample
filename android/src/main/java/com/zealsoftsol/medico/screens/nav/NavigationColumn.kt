package com.zealsoftsol.medico.screens.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.Separator
import com.zealsoftsol.medico.screens.Space
import com.zealsoftsol.medico.screens.stringResourceByName

@Composable
fun NavigationColumn(
    userName: String,
    userType: UserType,
    navigationSection: NavigationSection,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (bg, userInfo, mainSection, logOutSection) = createRefs()

        Image(
            bitmap = imageResource(id = R.drawable.nav_bg),
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
                bitmap = imageResource(id = R.drawable.avatar),
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
                    onClick = { it.select() },
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
        NavigationOption.Settings -> Icons.Filled.Settings to stringResource(R.string.settings)
        NavigationOption.LogOut -> vectorResource(id = R.drawable.ic_exit) to stringResource(R.string.log_out)
    }

@Composable
private fun NavigationCell(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = color,
            modifier = Modifier.padding(start = 18.dp),
        )
        Text(
            text = text,
            fontSize = MaterialTheme.typography.body2.fontSize,
            color = color,
            modifier = Modifier.padding(start = 32.dp),
        )
    }
}