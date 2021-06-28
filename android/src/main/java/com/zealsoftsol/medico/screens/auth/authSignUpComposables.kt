package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.FlowRow
import com.zealsoftsol.medico.screens.common.GstinOrPanRequiredBadge
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.InputWithPrefix
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputField
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.RectHolder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus
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
                    textRes = R.string.stockist_sub,
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
                    iconRes = R.drawable.ic_hospital,
                    textRes = R.string.hospital,
                    isSelected = selectedType.value == DataUserType.HOSPITAL,
                    onClick = { scope.chooseUserType(DataUserType.HOSPITAL) },
                )
                Spacer(modifier = Modifier.size(18.dp))
                UserType(
                    iconRes = R.drawable.ic_season_boy,
                    textRes = R.string.season_boy,
                    isSelected = selectedType.value == DataUserType.SEASON_BOY,
                    onClick = { scope.chooseUserType(DataUserType.SEASON_BOY) },
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
    val coroutineScope = rememberCoroutineScope()

    val isFirstNameError = registration.value.firstName.any { !it.isLetter() }
    val isLastNameError = registration.value.lastName.any { !it.isLetter() }

    BasicAuthSignUpScreenWithButton(
        progress = 0.4,
        scrollState = scrollState,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        buttonExtraValid = !isFirstNameError && !isLastNameError,
        body = {
            InputWithError(
                errorText = if (isFirstNameError)
                    stringResource(R.string.letters_only)
                else
                    null
            ) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.first_name),
                    text = registration.value.firstName,
                    isValid = !isFirstNameError,
                    onValueChange = { scope.changeFirstName(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(
                errorText = if (isLastNameError)
                    stringResource(R.string.letters_only)
                else
                    null
            ) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.last_name),
                    text = registration.value.lastName,
                    isValid = !isLastNameError,
                    onValueChange = { scope.changeLastName(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.email) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.email),
                    text = registration.value.email,
                    onValueChange = { scope.changeEmail(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.phoneNumber) {
                val isValid = PhoneFormatInputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.phone_number),
                    text = registration.value.phoneNumber,
                    onValueChange = { phoneNumber ->
                        scope.changePhoneNumber(phoneNumber.filter { it == '+' || it.isDigit() })
                    },
                )
                scope.setPhoneNumberValid(isValid)
            }
            Space(dp = 12.dp)
            InputWithError(errorText = validation.value?.password) {
                val rectHolder = RectHolder()
                PasswordFormatInputField(
                    modifier = Modifier.scrollOnFocus(rectHolder, scrollState, coroutineScope),
                    hint = stringResource(id = R.string.password),
                    text = registration.value.password,
                    onValueChange = { scope.changePassword(it) },
                    onPositioned = { rectHolder.rect = it.boundsInParent() },
                )
            }
            Space(dp = 12.dp)
            val isValid =
                registration.value.verifyPassword.isEmpty() || registration.value.password == registration.value.verifyPassword
            InputWithError(
                errorText = if (!isValid) stringResource(id = R.string.password_doesnt_match) else null
            ) {
                val rectHolder = RectHolder()
                PasswordFormatInputField(
                    modifier = Modifier.scrollOnFocus(rectHolder, scrollState, coroutineScope),
                    hint = stringResource(id = R.string.repeat_password),
                    text = registration.value.verifyPassword,
                    isValid = isValid,
                    onValueChange = { scope.changeRepeatPassword(it) },
                    onPositioned = { rectHolder.rect = it.boundsInParent() },
                )
            }
            Space(dp = 12.dp)
            val context = LocalContext.current
            val isTermsAccepted = scope.isTermsAccepted.flow.collectAsState()

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isTermsAccepted.value,
                    colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                    onCheckedChange = { scope.changeTerms(it) },
                )
                Space(dp = 8.dp)
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
    val userValidation = scope.userValidation.flow.collectAsState()
    val pincodeValidation = scope.pincodeValidation.flow.collectAsState()
    val locationData = scope.locationData.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    BasicAuthSignUpScreenWithButton(
        progress = 0.6,
        baseScope = scope,
        scrollState = scrollState,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            InputWithError(errorText = pincodeValidation.value?.pincode) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.pincode),
                    text = registration.value.pincode,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { scope.changePincode(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.addressLine1) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.address_line),
                    text = registration.value.addressLine1,
                    onValueChange = { scope.changeAddressLine(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.landmark) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.landmark),
                    text = registration.value.landmark,
                    onValueChange = { scope.changeLandmark(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.location) {
                Dropdown(
                    rememberChooseKey = locationData.value,
                    value = registration.value.location,
                    hint = stringResource(id = R.string.location),
                    dropDownItems = locationData.value?.locations.orEmpty(),
                    onSelected = { scope.changeLocation(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.city) {
                Dropdown(
                    rememberChooseKey = locationData.value,
                    value = registration.value.city,
                    hint = stringResource(id = R.string.city),
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
    val coroutineScope = rememberCoroutineScope()
    BasicAuthSignUpScreenWithButton(
        progress = 0.8,
        baseScope = scope,
        scrollState = scrollState,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        body = {
            scope.inputFields.forEach {
                if (it == SignUpScope.Details.Fields.TRADE_NAME) {
                    InputWithError(errorText = validation.value?.tradeName) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.trade_name),
                            text = registration.value.tradeName,
                            onValueChange = { scope.changeTradeName(it) },
                        )
                    }
                    Space(dp = 8.dp)
                    GstinOrPanRequiredBadge()
                    Space(dp = 8.dp)
                }
                if (it == SignUpScope.Details.Fields.PAN) {
                    InputWithError(errorText = validation.value?.panNumber) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.pan_number),
                            text = registration.value.panNumber,
                            isValid = Validator.TraderDetails.isPanValid(registration.value.panNumber) || registration.value.panNumber.isEmpty(),
                            keyboardOptions = KeyboardOptions.Default
                                .copy(capitalization = KeyboardCapitalization.Characters),
                            onValueChange = { scope.changePan(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (it == SignUpScope.Details.Fields.GSTIN) {
                    InputWithError(errorText = validation.value?.gstin) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.gstin),
                            text = registration.value.gstin,
                            isValid = Validator.TraderDetails.isGstinValid(registration.value.gstin) || registration.value.gstin.isEmpty(),
                            keyboardOptions = KeyboardOptions.Default
                                .copy(capitalization = KeyboardCapitalization.Characters),
                            onValueChange = { scope.changeGstin(it) },
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (it == SignUpScope.Details.Fields.LICENSE1) {
                    InputWithError(errorText = validation.value?.drugLicenseNo1) {
                        InputWithPrefix(UserRegistration3.DRUG_LICENSE_1_PREFIX) {
                            InputField(
                                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                                hint = stringResource(id = R.string.drug_license_1),
                                text = registration.value.drugLicenseNo1,
                                onValueChange = { scope.changeDrugLicense1(it) },
                            )
                        }
                    }
                    Space(dp = 12.dp)
                }
                if (it == SignUpScope.Details.Fields.LICENSE2) {
                    InputWithError(errorText = validation.value?.drugLicenseNo2) {
                        InputWithPrefix(UserRegistration3.DRUG_LICENSE_2_PREFIX) {
                            InputField(
                                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                                hint = stringResource(id = R.string.drug_license_2),
                                text = registration.value.drugLicenseNo2,
                                onValueChange = { scope.changeDrugLicense2(it) },
                            )
                        }
                    }
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
                painter = painterResource(id = R.drawable.ic_upload),
                contentDescription = null,
                tint = ConstColors.gray,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text(
                text = stringResource(id = stringId),
                color = ConstColors.gray,
                textAlign = TextAlign.Center,
            )
            Space(24.dp)
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.Transparent,
                border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.available_formats),
                        color = ConstColors.gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                    )
                    Space(10.dp)
                    FlowRow(horizontalGap = 12.dp) {
                        scope.supportedFileTypes.map { it.name }.forEach {
                            Text(
                                modifier = Modifier
                                    .background(
                                        color = ConstColors.gray.copy(alpha = .25f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(4.dp),
                                text = it,
                                color = MaterialTheme.colors.background,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                            )
                        }
                    }
                    Space(10.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.max_file_size))
                            val startIndex = length
                            append(" ")
                            append(stringResource(id = R.string.mb_10))
                            addStyle(
                                SpanStyle(
                                    color = MaterialTheme.colors.background,
                                    fontWeight = FontWeight.W600
                                ),
                                startIndex,
                                length,
                            )
                        },
                        color = ConstColors.gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                    )
                }
            }
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
        val isAadhaarValid =
            Validator.Aadhaar.isValid(aadhaar.value.cardNumber) || aadhaar.value.cardNumber.length < Validator.Aadhaar.MAX_LENGTH
        InputWithError(
            errorText = if (!isAadhaarValid) stringResource(id = R.string.aadhaar_card_invalid) else null
        ) {
            InputField(
                hint = stringResource(id = R.string.aadhaar_card),
                text = aadhaar.value.cardNumber,
                isValid = isAadhaarValid,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = onCardChange,
            )
        }
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
            }
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = stringResource(id = textRes),
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BasicAuthSignUpScreenWithButton(
    progress: Double,
    baseScope: SignUpScope,
    scrollState: ScrollState = rememberScrollState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    body: @Composable ColumnScope.() -> Unit,
    buttonText: String,
    buttonExtraValid: Boolean? = null,
    onSkip: (() -> Unit)? = null,
    onButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Box(
            modifier = Modifier
                .background(ConstColors.yellow)
                .size((LocalConfiguration.current.screenWidthDp * progress).dp, 4.dp)
        )
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        val padding = 16.dp
        Column(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    PaddingValues(
                        top = padding,
                        start = padding,
                        end = padding,
                        bottom = padding + 60.dp
                    )
                ),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            body()
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            MedicoButton(
                modifier = Modifier.padding(padding),
                text = buttonText,
                isEnabled = (buttonExtraValid ?: true) && isEnabled.value,
                onClick = onButtonClick,
            )
            onSkip?.let {
                Text(
                    text = stringResource(id = R.string.skip_for_now),
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    color = ConstColors.lightBlue,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = padding)
                        .clickable(onClick = it)
                )
            }
        }
    }
}