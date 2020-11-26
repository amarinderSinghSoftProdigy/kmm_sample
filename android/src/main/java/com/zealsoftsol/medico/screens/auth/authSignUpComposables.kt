package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.CanGoBack
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.screens.BasicTabBar
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.data.UserType as DataUserType

@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = remember { mutableStateOf(scope.userType) }
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.who_are_you),
        progress = 0.2,
        back = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.goToPersonalData(selectedType.value) },
        body = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(32.dp)
                    .align(Alignment.Center)
            ) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    UserType(
                        iconRes = R.drawable.ic_stockist,
                        textRes = R.string.stockist,
                        isSelected = selectedType.value == DataUserType.STOCKIST,
                        onClick = { selectedType.value = DataUserType.STOCKIST },
                    )
                    Spacer(modifier = Modifier.size(18.dp))
                    UserType(
                        iconRes = R.drawable.ic_retailer,
                        textRes = R.string.retailer,
                        isSelected = selectedType.value == DataUserType.RETAILER,
                        onClick = { selectedType.value = DataUserType.RETAILER },
                    )
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    UserType(
                        iconRes = R.drawable.ic_season_boy,
                        textRes = R.string.season_boy,
                        isSelected = selectedType.value == DataUserType.SEASON_BOY,
                        onClick = { selectedType.value = DataUserType.SEASON_BOY },
                    )
                    Spacer(modifier = Modifier.size(18.dp))
                    UserType(
                        iconRes = R.drawable.ic_hospital,
                        textRes = R.string.hospital,
                        isSelected = selectedType.value == DataUserType.HOSPITAL,
                        onClick = { selectedType.value = DataUserType.HOSPITAL },
                    )
                }
            }
        }
    )
}

@Composable
fun AuthPersonalData(scope: SignUpScope.PersonalData) {
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.personal_data),
        progress = 0.4,
        back = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope },
        body = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp)
                    .align(Alignment.Center)
            ) {
//                InputField(hint = stringResource(id = R.string.first_name), text = , onValueChange =)
            }
        },
    )
}

@Composable
private fun BasicAuthSignUpScreen(
    title: String,
    progress: Double,
    back: CanGoBack,
    body: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        BasicTabBar(back = back, title = title)
        Box(
            modifier = Modifier
                .background(ConstColors.yellow)
                .size((ConfigurationAmbient.current.screenWidthDp * progress).dp, 4.dp)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            body()
        }
    }
}

@Composable
private fun BasicAuthSignUpScreenWithButton(
    title: String,
    progress: Double,
    back: CanGoBack,
    body: @Composable BoxScope.() -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
) {
    BasicAuthSignUpScreen(title, progress, back) {
        body()
        MedicoButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            text = buttonText,
            isEnabled = true,
            onClick = onButtonClick,
        )
    }
}

@Composable
private fun UserType(iconRes: Int, textRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .aspectRatio(1f)
            .background(Color.White, RoundedCornerShape(8.dp))
            .run {
                if (isSelected) {
                    border(2.dp, ConstColors.yellow, RoundedCornerShape(8.dp))
                } else {
                    this
                }
            }.clickable(onClick = onClick, indication = null),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(asset = vectorResource(id = iconRes))
        Text(text = stringResource(id = textRes), modifier = Modifier.padding(4.dp))
    }
}