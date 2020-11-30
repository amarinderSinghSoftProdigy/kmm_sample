package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.CanGoBack
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.screens.BasicTabBar
import com.zealsoftsol.medico.screens.InputField
import com.zealsoftsol.medico.screens.InputWithError
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.PhoneFormatInputField
import com.zealsoftsol.medico.screens.Space
import com.zealsoftsol.medico.data.UserType as DataUserType

@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = scope.userType.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.who_are_you),
        progress = 0.2,
        back = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.goToPersonalData() },
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
                        onClick = { scope.chooseUserType(DataUserType.STOCKIST) },
                    )
                    Spacer(modifier = Modifier.size(18.dp))
                    UserType(
                        iconRes = R.drawable.ic_retailer,
                        textRes = R.string.retailer,
                        isSelected = selectedType.value == DataUserType.RETAILER,
                        onClick = { scope.chooseUserType(DataUserType.RETAILER) },
                    )
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    UserType(
                        iconRes = R.drawable.ic_season_boy,
                        textRes = R.string.season_boy,
                        isSelected = selectedType.value == DataUserType.SEASON_BOY,
                        onClick = { scope.chooseUserType(DataUserType.SEASON_BOY) },
                    )
                    Spacer(modifier = Modifier.size(18.dp))
                    UserType(
                        iconRes = R.drawable.ic_hospital,
                        textRes = R.string.hospital,
                        isSelected = selectedType.value == DataUserType.HOSPITAL,
                        onClick = { scope.chooseUserType(DataUserType.HOSPITAL) },
                    )
                }
            }
            true
        }
    )
}

@Composable
fun AuthPersonalData(scope: SignUpScope.PersonalData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.personal_data),
        progress = 0.4,
        back = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.tryToSignUp(registration.value) },
        body = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                InputField(
                    hint = stringResource(id = R.string.first_name),
                    text = registration.value.firstName,
                    onValueChange = { scope.changeFirstName(it) }
                )
                Space(dp = 12.dp)
                InputField(
                    hint = stringResource(id = R.string.last_name),
                    text = registration.value.lastName,
                    onValueChange = { scope.changeLastName(it) }
                )
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.email) {
                    InputField(
                        hint = stringResource(id = R.string.email),
                        text = registration.value.email,
                        onValueChange = { scope.changeEmail(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.phoneNumber) {
                    PhoneFormatInputField(
                        hint = stringResource(id = R.string.phone_number),
                        text = registration.value.phoneNumber,
                        onValueChange = { scope.changePhoneNumber(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.password) {
                    PasswordFormatInputField(
                        hint = stringResource(id = R.string.password),
                        text = registration.value.password,
                        onValueChange = { scope.changePassword(it) }
                    )
                }
                Space(dp = 12.dp)
                val isValid =
                    registration.value.password.isEmpty() || (registration.value.password == registration.value.verifyPassword)
                InputWithError(
                    errorText = if (!isValid) stringResource(id = R.string.password_doesnt_match) else null
                ) {
                    PasswordFormatInputField(
                        hint = stringResource(id = R.string.repeat_password),
                        text = registration.value.verifyPassword,
                        isValid = isValid,
                        onValueChange = { scope.changeRepeatPassword(it) }
                    )
                }
                Space(dp = 12.dp)
                Text(
                    text = stringResource(id = R.string.tos_line_1),
                    color = ConstColors.gray,
                    style = MaterialTheme.typography.caption,
                )
                val context = ContextAmbient.current
                Text(
                    text = stringResource(id = R.string.tos_line_2),
                    color = ConstColors.lightBlue,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.W600,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.clickable(onClick = {
                        val tabs = CustomTabsIntent.Builder().build()
                        val uri = context.getString(R.string.tos_url).toUri()
                        runCatching {
                            tabs.launchUrl(context, uri)
                        }.getOrNull() ?: runCatching {
                            startActivity(
                                context,
                                Intent(Intent.ACTION_VIEW).also {
                                    it.data = uri
                                },
                                null
                            )
                        }.getOrNull() ?: context.toast(R.string.something_went_wrong)
                    })
                )
            }
            scope.isRegistrationValid
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
    body: @Composable BoxScope.() -> Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
) {
    BasicAuthSignUpScreen(title, progress, back) {
        val isButtonActive = body()
        MedicoButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            text = buttonText,
            isEnabled = isButtonActive,
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