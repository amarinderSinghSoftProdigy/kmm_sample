package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import android.util.Base64
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.CanGoBack
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.screens.BasicTabBar
import com.zealsoftsol.medico.screens.InputField
import com.zealsoftsol.medico.screens.InputWithError
import com.zealsoftsol.medico.screens.MainActivity
import com.zealsoftsol.medico.screens.MedicoButton
import com.zealsoftsol.medico.screens.PasswordFormatInputField
import com.zealsoftsol.medico.screens.PhoneFormatInputField
import com.zealsoftsol.medico.screens.Space
import com.zealsoftsol.medico.screens.showErrorAlert
import kotlinx.coroutines.launch
import java.io.File
import com.zealsoftsol.medico.data.UserType as DataUserType

@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = scope.userType.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.who_are_you),
        progress = 0.2,
        baseScope = scope,
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
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
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
                        onValueChange = { scope.changePhoneNumber(it.filter { it.isDigit() }) }
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
        },
    )
}

@Composable
fun AuthAddressData(scope: SignUpScope.AddressData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    val locationData = scope.locationData.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.address),
        progress = 0.6,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                InputWithError(
                    errorText = if (locationData.value == Location.Unknown)
                        stringResource(id = R.string.wrong_code)
                    else
                        null
                ) {
                    InputField(
                        hint = stringResource(id = R.string.pincode),
                        text = registration.value.pincode,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = { scope.changePincode(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.addressLine1) {
                    InputField(
                        hint = stringResource(id = R.string.address_line),
                        text = registration.value.addressLine1,
                        onValueChange = { scope.changeAddressLine(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.location) {
                    LocationSelector(
                        chooseRemember = locationData.value,
                        chosenValue = registration.value.location.takeIf { it.isNotEmpty() },
                        defaultName = stringResource(id = R.string.location),
                        dropDownItems = (locationData.value as? Location.Data)?.locations.orEmpty(),
                        onSelected = { scope.changeLocation(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.city) {
                    LocationSelector(
                        chooseRemember = locationData.value,
                        chosenValue = registration.value.city.takeIf { it.isNotEmpty() },
                        defaultName = stringResource(id = R.string.city),
                        dropDownItems = (locationData.value as? Location.Data)?.cities.orEmpty(),
                        onSelected = { scope.changeCity(it) }
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.district) {
                    Text(
                        text = if (registration.value.district.isEmpty()) stringResource(id = R.string.district) else registration.value.district,
                        color = if (registration.value.district.isEmpty()) ConstColors.gray else Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White)
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                    )
                }
                Space(dp = 12.dp)
                InputWithError(errorText = validation.value?.state) {
                    Text(
                        text = if (registration.value.state.isEmpty()) stringResource(id = R.string.state) else registration.value.state,
                        color = if (registration.value.state.isEmpty()) ConstColors.gray else Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White)
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                    )
                }
            }
        }
    )
}

@Composable
fun AuthTraderDetails(scope: SignUpScope.TraderData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.trader_details),
        progress = 0.8,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                if (SignUpScope.TraderData.Fields.TRADE_NAME in scope.inputFields) {
                    InputWithError(errorText = validation.value?.tradeName) {
                        InputField(
                            hint = stringResource(id = R.string.trade_name),
                            text = registration.value.tradeName,
                            onValueChange = { scope.changeTradeName(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (SignUpScope.TraderData.Fields.PAN in scope.inputFields) {
                    InputWithError(errorText = validation.value?.panNumber) {
                        InputField(
                            hint = stringResource(id = R.string.pan_number),
                            text = registration.value.panNumber,
                            isValid = scope.isPanValid,
                            keyboardOptions = KeyboardOptions.Default
                                .copy(capitalization = KeyboardCapitalization.Characters),
                            onValueChange = { scope.changePan(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (SignUpScope.TraderData.Fields.GSTIN in scope.inputFields) {
                    InputWithError(errorText = validation.value?.gstin) {
                        InputField(
                            hint = stringResource(id = R.string.gstin),
                            text = registration.value.gstin,
                            isValid = scope.isGstinValid,
                            keyboardOptions = KeyboardOptions.Default
                                .copy(capitalization = KeyboardCapitalization.Characters),
                            onValueChange = { scope.changeGstin(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (SignUpScope.TraderData.Fields.LICENSE1 in scope.inputFields) {
                    InputWithError(errorText = validation.value?.drugLicenseNo1) {
                        InputField(
                            hint = stringResource(id = R.string.drug_license_1),
                            text = registration.value.drugLicenseNo1,
                            onValueChange = { scope.changeDrugLicense1(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (SignUpScope.TraderData.Fields.LICENSE2 in scope.inputFields) {
                    InputWithError(errorText = validation.value?.drugLicenseNo2) {
                        InputField(
                            hint = stringResource(id = R.string.drug_license_2),
                            text = registration.value.drugLicenseNo2,
                            onValueChange = { scope.changeDrugLicense2(it) },
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AuthLegalDocuments(scope: SignUpScope.LegalDocuments) {
    val isShowingBottomSheet = remember { mutableStateOf(false) }
    BasicAuthSignUpScreenWithButton(
        title = stringResource(id = R.string.legal_documents),
        progress = 1.0,
        baseScope = scope,
        buttonText = stringResource(
            id = if (scope is SignUpScope.LegalDocuments.Aadhaar)
                R.string.upload_aadhaar
            else
                R.string.upload_new_document
        ),
        onButtonClick = {
            when (scope) {
                is SignUpScope.LegalDocuments.DrugLicense -> {
                    isShowingBottomSheet.value = true
                }
                is SignUpScope.LegalDocuments.Aadhaar -> {
                    "open picker"
                }
            }
        },
        onSkip = { scope.skip() },
        body = {
            when (scope) {
                is SignUpScope.LegalDocuments.DrugLicense -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            asset = vectorResource(id = R.drawable.ic_upload),
                            tint = ConstColors.gray,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Text(
                            text = stringResource(id = R.string.provide_drug_license_hint),
                            color = ConstColors.gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is SignUpScope.LegalDocuments.Aadhaar -> {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        val aadhaar = scope.aadhaarData.flow.collectAsState()
                        InputField(
                            hint = stringResource(id = R.string.aadhaar_card),
                            text = aadhaar.value.cardNumber,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = { scope.changeCard(it) },
                        )
                        Space(dp = 12.dp)
                        InputField(
                            hint = stringResource(id = R.string.share_code),
                            text = aadhaar.value.shareCode,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = { scope.changeShareCode(it) },
                        )
                    }
                }
            }
        }
    )
    if (isShowingBottomSheet.value) Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .clickable(indication = null) { isShowingBottomSheet.value = false }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column {
                Row(
                    modifier = Modifier.height(52.dp)
                        .padding(start = 18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.actions),
                        color = ConstColors.gray,
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                val activity = ContextAmbient.current as MainActivity
                Row(
                    modifier = Modifier.height(48.dp)
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                val file = activity.openFilePicker(scope.supportedFileTypes)
                                if (file != null) {
                                    isShowingBottomSheet.value = false
                                    scope.handleFileUpload(file)
                                } else {
                                    activity.toast(activity.getString(R.string.something_went_wrong))
                                }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        asset = Icons.Filled.CloudUpload,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.upload),
                        color = ConstColors.gray,
                    )
                }
                if (scope is SignUpScope.LegalDocuments.DrugLicense) {
                    Row(
                        modifier = Modifier.height(48.dp)
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    val file = activity.takePicture()
                                    if (file != null) {
                                        isShowingBottomSheet.value = false
                                        scope.handleFileUpload(file)
                                    } else {
                                        activity.toast(activity.getString(R.string.something_went_wrong))
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            asset = Icons.Filled.CameraAlt,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.use_camera),
                            color = ConstColors.gray,
                        )
                    }
                }
            }
        }
    }
    scope.showErrorAlert()
}

private inline fun SignUpScope.LegalDocuments.handleFileUpload(file: File) {
    val bytes = file.readBytes()
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    when (this) {
        is SignUpScope.LegalDocuments.DrugLicense -> upload(
            base64,
            FileType.fromExtension(file.extension)
        )
        is SignUpScope.LegalDocuments.Aadhaar -> upload(base64)
    }
}

@Composable
private fun LocationSelector(
    chooseRemember: Any?,
    chosenValue: String?,
    defaultName: String,
    dropDownItems: List<String>,
    onSelected: (String) -> Unit,
) {
    val choosing = remember(chooseRemember) { mutableStateOf(false) }
    DropdownMenu(
        toggle = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(color = Color.White)
                    .clickable(onClick = {
                        if (dropDownItems.isNotEmpty()) {
                            choosing.value = true
                        }
                    })
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = chosenValue ?: defaultName,
                    color = if (chosenValue == null) ConstColors.gray else Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
                Icon(
                    asset = vectorResource(id = R.drawable.ic_drop_down),
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = ConstColors.gray,
                )
            }
        },
        expanded = choosing.value,
        onDismissRequest = { choosing.value = false },
        dropdownContent = {
            dropDownItems.forEach {
                DropdownMenuItem(
                    onClick = {
                        choosing.value = false
                        onSelected(it)
                    },
                    content = { Text(it) },
                )
            }
        }
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
    baseScope: SignUpScope,
    body: @Composable BoxScope.() -> Unit,
    buttonText: String,
    onSkip: (() -> Unit)? = null,
    onButtonClick: () -> Unit,
) {
    BasicAuthSignUpScreen(title, progress, baseScope) {
        body()
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            MedicoButton(
                modifier = Modifier.padding(16.dp),
                text = buttonText,
                isEnabled = isEnabled.value,
                onClick = onButtonClick,
            )
            onSkip?.let {
                Text(
                    text = stringResource(id = R.string.skip_for_now),
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    color = ConstColors.lightBlue,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                        .clickable(onClick = it)
                )
            }
        }
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