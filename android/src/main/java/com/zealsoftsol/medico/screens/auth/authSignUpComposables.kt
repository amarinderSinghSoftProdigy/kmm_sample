package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.AmbientContext
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
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.LocationSelector
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputField
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.data.UserType as DataUserType

@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = scope.userType.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        progress = 0.2,
        baseScope = scope,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.goToPersonalData() },
        body = {
            Text(
                text = stringResource(id = R.string.who_are_you),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colors.background,
            )
            Space(32.dp)
            Row {
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
            Row {
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
    )
}

@Composable
fun AuthPersonalData(scope: SignUpScope.PersonalData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    val scrollState = rememberScrollState()
    BasicAuthSignUpScreenWithButton(
        progress = 0.4,
        scrollState = scrollState,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            InputField(
                autoScrollOnFocus = scrollState,
                hint = stringResource(id = R.string.first_name),
                text = registration.value.firstName,
                onValueChange = { scope.changeFirstName(it) }
            )
            Space(dp = 12.dp)
            InputField(
                autoScrollOnFocus = scrollState,
                hint = stringResource(id = R.string.last_name),
                text = registration.value.lastName,
                onValueChange = { scope.changeLastName(it) }
            )
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.email) {
                InputField(
                    autoScrollOnFocus = scrollState,
                    hint = stringResource(id = R.string.email),
                    text = registration.value.email,
                    onValueChange = { scope.changeEmail(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.phoneNumber) {
                PhoneFormatInputField(
                    autoScrollOnFocus = scrollState,
                    hint = stringResource(id = R.string.phone_number),
                    text = registration.value.phoneNumber,
                    onValueChange = { scope.changePhoneNumber(it.filter { it.isDigit() }) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.password) {
                PasswordFormatInputField(
                    autoScrollOnFocus = scrollState,
                    hint = stringResource(id = R.string.password),
                    text = registration.value.password,
                    onValueChange = { scope.changePassword(it) }
                )
            }
            Space(dp = 12.dp)
            val isValid =
                registration.value.verifyPassword.isEmpty() || registration.value.password == registration.value.verifyPassword
            InputWithError(
                errorText = if (!isValid) stringResource(id = R.string.password_doesnt_match) else null
            ) {
                PasswordFormatInputField(
                    autoScrollOnFocus = scrollState,
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
            val context = AmbientContext.current
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
        },
    )
}

@Composable
fun AuthAddressData(scope: SignUpScope.AddressData) {
    val registration = scope.registration.flow.collectAsState()
    val userValidation = scope.userValidation.flow.collectAsState()
    val pincodeValidation = scope.pincodeValidation.flow.collectAsState()
    val locationData = scope.locationData.flow.collectAsState()
    val scrollState = rememberScrollState()
    BasicAuthSignUpScreenWithButton(
        progress = 0.6,
        baseScope = scope,
        scrollState = scrollState,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            InputWithError(errorText = pincodeValidation.value?.pincode) {
                InputField(
                    autoScrollOnFocus = scrollState,
                    hint = stringResource(id = R.string.pincode),
                    text = registration.value.pincode,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { scope.changePincode(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.addressLine1) {
                InputField(
                    autoScrollOnFocus = scrollState,
                    hint = stringResource(id = R.string.address_line),
                    text = registration.value.addressLine1,
                    onValueChange = { scope.changeAddressLine(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.location) {
                LocationSelector(
                    chooseRemember = locationData.value,
                    chosenValue = registration.value.location.takeIf { it.isNotEmpty() },
                    defaultName = stringResource(id = R.string.location),
                    dropDownItems = locationData.value?.locations.orEmpty(),
                    onSelected = { scope.changeLocation(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.city) {
                LocationSelector(
                    chooseRemember = locationData.value,
                    chosenValue = registration.value.city.takeIf { it.isNotEmpty() },
                    defaultName = stringResource(id = R.string.city),
                    dropDownItems = locationData.value?.cities.orEmpty(),
                    onSelected = { scope.changeCity(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.district) {
                ReadOnlyField(registration.value.district, R.string.district)
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.state) {
                ReadOnlyField(registration.value.state, R.string.state)
            }
        }
    )
}

@Composable
fun AuthDetailsTraderData(scope: SignUpScope.Details.TraderData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    val scrollState = rememberScrollState()
    BasicAuthSignUpScreenWithButton(
        progress = 0.8,
        baseScope = scope,
        scrollState = scrollState,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            if (SignUpScope.Details.Fields.TRADE_NAME in scope.inputFields) {
                InputWithError(errorText = validation.value?.tradeName) {
                    InputField(
                        autoScrollOnFocus = scrollState,
                        hint = stringResource(id = R.string.trade_name),
                        text = registration.value.tradeName,
                        onValueChange = { scope.changeTradeName(it) },
                    )
                }
                Space(dp = 12.dp)
            }
            if (SignUpScope.Details.Fields.PAN in scope.inputFields) {
                InputWithError(errorText = validation.value?.panNumber) {
                    InputField(
                        autoScrollOnFocus = scrollState,
                        hint = stringResource(id = R.string.pan_number),
                        text = registration.value.panNumber,
                        isValid = Validator.TraderDetails.isPanValid(registration.value.panNumber),
                        keyboardOptions = KeyboardOptions.Default
                            .copy(capitalization = KeyboardCapitalization.Characters),
                        onValueChange = { scope.changePan(it) },
                    )
                }
                Space(dp = 12.dp)
            }
            if (SignUpScope.Details.Fields.GSTIN in scope.inputFields) {
                InputWithError(errorText = validation.value?.gstin) {
                    InputField(
                        autoScrollOnFocus = scrollState,
                        hint = stringResource(id = R.string.gstin),
                        text = registration.value.gstin,
                        isValid = Validator.TraderDetails.isGstinValid(registration.value.gstin),
                        keyboardOptions = KeyboardOptions.Default
                            .copy(capitalization = KeyboardCapitalization.Characters),
                        onValueChange = { scope.changeGstin(it) },
                    )
                }
                Space(dp = 12.dp)
            }
            if (SignUpScope.Details.Fields.LICENSE1 in scope.inputFields) {
                InputWithError(errorText = validation.value?.drugLicenseNo1) {
                    InputField(
                        autoScrollOnFocus = scrollState,
                        hint = stringResource(id = R.string.drug_license_1),
                        text = registration.value.drugLicenseNo1,
                        onValueChange = { scope.changeDrugLicense1(it) },
                    )
                }
                Space(dp = 12.dp)
            }
            if (SignUpScope.Details.Fields.LICENSE2 in scope.inputFields) {
                InputWithError(errorText = validation.value?.drugLicenseNo2) {
                    InputField(
                        autoScrollOnFocus = scrollState,
                        hint = stringResource(id = R.string.drug_license_2),
                        text = registration.value.drugLicenseNo2,
                        onValueChange = { scope.changeDrugLicense2(it) },
                    )
                }
            }
        }
    )
}

@Composable
fun AuthDetailsAadhaar(scope: SignUpScope.Details.Aadhaar) {
    BasicAuthSignUpScreenWithButton(
        progress = 0.8,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.addAadhaar() },
        body = {
            AadhaarInputFields(
                aadhaarData = scope.aadhaarData,
                onCardChange = scope::changeCard,
                onCodeChange = scope::changeShareCode,
            )
        }
    )
}

@Composable
fun AuthLegalDocuments(scope: SignUpScope.LegalDocuments) {
    BasicAuthSignUpScreenWithButton(
        progress = 1.0,
        baseScope = scope,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        buttonText = stringResource(
            id = if (scope is SignUpScope.LegalDocuments.Aadhaar)
                R.string.upload_aadhaar
            else
                R.string.upload_new_document
        ),
        onButtonClick = { scope.showBottomSheet() },
        onSkip = { scope.skip() },
        body = {
            val stringId = when (scope) {
                is SignUpScope.LegalDocuments.DrugLicense -> R.string.provide_drug_license_hint
                is SignUpScope.LegalDocuments.Aadhaar -> R.string.provide_aadhaar_hint
            }
            Icon(
                imageVector = vectorResource(id = R.drawable.ic_upload),
                tint = ConstColors.gray,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text(
                text = stringResource(id = stringId),
                color = ConstColors.gray,
                textAlign = TextAlign.Center,
            )
        }
    )
}

@Composable
fun AadhaarInputFields(
    aadhaarData: DataSource<AadhaarData>,
    onCardChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
        val aadhaar = aadhaarData.flow.collectAsState()
        InputField(
            hint = stringResource(id = R.string.aadhaar_card),
            text = aadhaar.value.cardNumber,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = onCardChange,
        )
        Space(dp = 12.dp)
        InputField(
            hint = stringResource(id = R.string.share_code),
            text = aadhaar.value.shareCode,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = onCodeChange,
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
        Image(imageVector = vectorResource(id = iconRes))
        Text(text = stringResource(id = textRes), modifier = Modifier.padding(4.dp))
    }
}

@OptIn(InternalLayoutApi::class)
@Composable
private fun BasicAuthSignUpScreenWithButton(
    progress: Double,
    baseScope: SignUpScope,
    scrollState: ScrollState = rememberScrollState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    body: @Composable ColumnScope.() -> Unit,
    buttonText: String,
    onSkip: (() -> Unit)? = null,
    onButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary)
    ) {
        Box(
            modifier = Modifier
                .background(ConstColors.yellow)
                .size((AmbientConfiguration.current.screenWidthDp * progress).dp, 4.dp)
        )
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        val padding = 16.dp
        ScrollableColumn(
            scrollState = scrollState,
            modifier = Modifier.padding(top = 4.dp).fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding,
                start = padding,
                end = padding,
                bottom = padding + 60.dp
            ),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            body()
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            MedicoButton(
                modifier = Modifier.padding(padding),
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
                        .padding(bottom = padding)
                        .clickable(onClick = it)
                )
            }
        }
    }
}