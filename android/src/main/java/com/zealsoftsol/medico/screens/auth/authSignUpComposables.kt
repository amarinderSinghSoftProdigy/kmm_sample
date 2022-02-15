package com.zealsoftsol.medico.screens.auth

import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.GstinOrPanRequiredBadge
import com.zealsoftsol.medico.screens.common.ImageLabel
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.InputWithPrefix
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputField
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.RectHolder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TextLabel
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.utils.PermissionCheckUI
import com.zealsoftsol.medico.utils.PermissionViewModel
import com.zealsoftsol.medico.data.UserType as DataUserType

@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = scope.userType.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        userType = "",
        progress = 1.0,//0.2,
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
                Space(18.dp)
                UserType(
                    iconRes = R.drawable.ic_retailer,
                    textRes = R.string.retailer,
                    isSelected = selectedType.value == DataUserType.RETAILER,
                    onClick = { scope.chooseUserType(DataUserType.RETAILER) },
                )
            }
            Space(18.dp)
            Row {
                UserType(
                    iconRes = R.drawable.ic_hospital,
                    textRes = R.string.hospital,
                    isSelected = selectedType.value == DataUserType.HOSPITAL,
                    onClick = { scope.chooseUserType(DataUserType.HOSPITAL) },
                )
                /*Space(18.dp)
                UserType(
                    iconRes = R.drawable.ic_season_boy,
                    textRes = R.string.season_boy,
                    isSelected = selectedType.value == DataUserType.SEASON_BOY,
                    onClick = { *//*scope.chooseUserType(DataUserType.SEASON_BOY)*//* },
                )*/
            }
        }
    )
}

@Composable
fun AuthPersonalData(scope: SignUpScope.PersonalData) {
    val registration = scope.registration.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val isFirstNameError = registration.value.firstName.any { !it.isLetter() }
    val isLastNameError = registration.value.lastName.any { !it.isLetter() }
    val validEmail = scope.validEmail(registration.value.email)
    val validPhone = scope.validPhone(registration.value.phoneNumber)
    val password = scope.isValidPassword(registration.value.password)

    BasicAuthSignUpScreenWithButton(
        userType = registration.value.userType,
        progress = 2.0,//0.4,
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
            InputWithError(errorText = if (!validEmail) stringResource(id = R.string.email_validation) else null) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.email),
                    text = registration.value.email,
                    onValueChange = { scope.changeEmail(it) }
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!validPhone) stringResource(id = R.string.phone_validation) else null) {
                PhoneFormatInputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.phone_number),
                    text = registration.value.phoneNumber,
                    onValueChange = { phoneNumber ->
                        scope.changePhoneNumber(phoneNumber.filter { it == '+' || it.isDigit() })
                    },
                )
                //scope.setPhoneNumberValid(isValid)
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!password) stringResource(id = R.string.password_requirement) else null) {
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
                    text = stringResource(id = R.string.consent_terms),
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
    val lengthValid =
        if (registration.value.landmark.isNotEmpty()) registration.value.landmark.length == 30 else false
    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 3.0,//0.6,
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
            InputWithError(
                errorText = if (lengthValid) stringResource(
                    id = R.string.max_30_chars
                ) else null
            ) {
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
        userType = scope.registrationStep1.userType,
        progress = 4.0,//0.8,
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
                            onValueChange = { value -> scope.changeTradeName(value) },
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
                            onValueChange = { value -> scope.changePan(value) },
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
                            onValueChange = { value -> scope.changeGstin(value) },
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
                                onValueChange = { value -> scope.changeDrugLicense1(value) },
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
                                onValueChange = { value -> scope.changeDrugLicense2(value) },
                            )
                        }
                    }
                }
                Space(16.dp)
                if (it == SignUpScope.Details.Fields.FOOD_LICENSE) {
                    Column {
                        val switchEnabled = remember { mutableStateOf(false) }
                        Text(
                            text = stringResource(id = R.string.do_you_have_food_license),
                            color = ConstColors.gray,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        )
                        Space(8.dp)
                        Row {
                            RadioButton(
                                selected = switchEnabled.value,
                                onClick = {
                                    switchEnabled.value = true
                                    scope.changeFoodLicenseStatus(true)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
                            )
                            Space(12.dp)
                            Text(
                                text = stringResource(id = R.string.yes),
                                color = Color.Black,
                            )
                            RadioButton(
                                selected = !switchEnabled.value,
                                onClick = {
                                    switchEnabled.value = false
                                    scope.changeFoodLicenseStatus(false)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
                            )
                            Space(12.dp)
                            Text(
                                text = stringResource(id = R.string.no),
                                color = Color.Black,
                            )
                        }
                        Space(16.dp)
                        if (switchEnabled.value) {
                            InputWithError(
                                errorText = if (!registration.value.foodLicense) {
                                    stringResource(id = R.string.add_food_license)
                                } else {
                                    null
                                }
                            ) {
                                InputField(
                                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                                    hint = stringResource(id = R.string.food_license_number),
                                    text = registration.value.foodLicenseNumber,
                                    onValueChange = { value -> scope.changeFoodLicense(value) },
                                )
                            }
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
        userType = scope.registrationStep1.userType,
        progress = 4.0,//0.8,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AuthLegalDocuments(scope: SignUpScope.LegalDocuments, scaffoldState: ScaffoldState) {
    val registration = scope.registrationStep4.flow.collectAsState()
    /*val permissionViewModel = PermissionViewModel()
    PermissionCheckUI(
        scaffoldState, permissionViewModel, Event.Action.Registration.ShowUploadBottomSheets(
            permissionViewModel.performTypeAction.value,
            scope.registrationStep1
        )
    )*/
    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 5.0,//1.0,
        baseScope = scope,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        buttonText = stringResource(
            id = if (scope is SignUpScope.LegalDocuments.Aadhaar)
                R.string.upload_aadhaar
            else
                R.string.preview
        ),
        onButtonClick = { scope.validate(registration.value) },
        /*onSkip = { scope.skip() },*/
        body = {
            val stringId = when (scope) {
                is SignUpScope.LegalDocuments.DrugLicense -> R.string.provide_drug_license_validation
                is SignUpScope.LegalDocuments.Aadhaar -> R.string.provide_aadhaar_hint
            }

            val tradeCheck = registration.value.tradeProfile != null
            val drugCheck = registration.value.drugLicense != null
            val foodCheck = registration.value.foodLicense != null


            Column {
                Surface(
                    onClick = {
                        //permissionViewModel.setPerformLocationAction(true, "TRADE_PROFILE")
                        scope.showBottomSheet("TRADE_PROFILE", scope.registrationStep1)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = MaterialTheme.shapes.large,
                    color = Color.White,
                    border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_img_placeholder),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                        )
                        Space(dp = 4.dp)
                        Text(
                            text = if (tradeCheck) stringResource(id = R.string.file_uploaded_successfully)
                            else stringResource(id = R.string.upload_trade_profile),
                            color = if (tradeCheck) MaterialTheme.colors.background else ConstColors.gray,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = if (tradeCheck) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Space(dp = 16.dp)
                Surface(
                    onClick = {
                        //permissionViewModel.setPerformLocationAction(true, "DRUG_LICENSE")
                        scope.showBottomSheet("DRUG_LICENSE", scope.registrationStep1)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = MaterialTheme.shapes.large,
                    color = Color.White,
                    border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_img_placeholder),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                        )
                        Space(dp = 4.dp)
                        Text(
                            text = if (drugCheck) stringResource(id = R.string.file_uploaded_successfully)
                            else stringResource(id = R.string.upload_drug_license),
                            color = if (drugCheck) MaterialTheme.colors.background else ConstColors.gray,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = if (drugCheck) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Space(dp = 4.dp)
                Text(
                    text = stringResource(id = stringId),
                    color = ConstColors.red,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                )
                Space(dp = 16.dp)
                if (scope.registrationStep3.foodLicense) {
                    Surface(
                        onClick = {
                            //permissionViewModel.setPerformLocationAction(true, "FOOD_LICENSE")
                            scope.showBottomSheet(
                                "FOOD_LICENSE",
                                scope.registrationStep1
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = MaterialTheme.shapes.large,
                        color = Color.White,
                        border = BorderStroke(1.dp, ConstColors.gray.copy(alpha = .2f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_img_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                            )
                            Space(dp = 4.dp)
                            Text(
                                text = if (foodCheck) stringResource(id = R.string.file_uploaded_successfully)
                                else stringResource(id = R.string.upload_food_license),
                                color = if (foodCheck) MaterialTheme.colors.background else ConstColors.gray,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = if (foodCheck) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    Space(dp = 16.dp)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AuthPreview(scope: SignUpScope.PreviewDetails) {
    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 5.0,//1.0,
        baseScope = scope,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        buttonText = stringResource(id = R.string.submit),
        onButtonClick = { scope.submit() },
        body = {
            Column {
                Text(
                    text = stringResource(id = R.string.customer_info),
                    fontSize = 16.sp,
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W600,
                )
                Space(dp = 16.dp)
                TextLabel(scope.registrationStep1.firstName)
                TextLabel(scope.registrationStep1.lastName)
                TextLabel(scope.registrationStep1.email)
                TextLabel(scope.registrationStep1.phoneNumber)
                TextLabel(scope.registrationStep1.password)
                TextLabel(scope.registrationStep1.verifyPassword)
                Space(dp = 4.dp)
                Text(
                    text = stringResource(id = R.string.address_info),
                    fontSize = 16.sp,
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W600,
                )
                Space(dp = 16.dp)
                TextLabel(scope.registrationStep2.addressLine1)
                TextLabel(scope.registrationStep2.landmark)
                TextLabel(scope.registrationStep2.location)
                TextLabel(scope.registrationStep2.city)
                TextLabel(scope.registrationStep2.district)
                TextLabel(scope.registrationStep2.state)
                TextLabel(scope.registrationStep2.pincode)
                Space(dp = 4.dp)
                Text(
                    text = stringResource(id = R.string.trader_details),
                    fontSize = 16.sp,
                    color = ConstColors.lightBlue,
                    fontWeight = FontWeight.W600,
                )
                Space(dp = 16.dp)
                TextLabel(scope.registrationStep3.tradeName)
                if (scope.registrationStep4.tradeProfile != null) {
                    Log.e(" data ", " " + scope.registrationStep4.tradeProfile?.cdnUrl)
                    ImageLabel(scope.registrationStep4.tradeProfile?.cdnUrl ?: "")
                }
                TextLabel(scope.registrationStep3.gstin)
                TextLabel(scope.registrationStep3.panNumber)
                TextLabel(scope.registrationStep3.drugLicenseNo1)
                TextLabel(scope.registrationStep3.drugLicenseNo2)
                if (scope.registrationStep4.drugLicense != null) {
                    Log.e(" data ", " " + scope.registrationStep4.drugLicense?.cdnUrl)
                    ImageLabel(scope.registrationStep4.drugLicense?.cdnUrl ?: "")
                }
                TextLabel(scope.registrationStep3.foodLicenseNumber)
                if (scope.registrationStep4.foodLicense != null) {
                    Log.e(" data ", " " + scope.registrationStep4.foodLicense?.cdnUrl)
                    ImageLabel(scope.registrationStep4.foodLicense?.cdnUrl ?: "")
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
    userType: String,
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
    padding: Dp = 16.dp,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        /*Box(
            modifier = Modifier
                .background(ConstColors.yellow, MaterialTheme.shapes.small)
                .size((LocalConfiguration.current.screenWidthDp * progress).dp, 4.dp)
        )*/
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        Column {
            Box(
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp)
            ) {
                LazyRow {
                    itemsIndexed(
                        items = baseScope.inputProgress,
                        itemContent = { _, item ->
                            ProgressItem(count = item, progress)
                        },
                    )
                }
            }
            if (userType.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.user_type),
                        fontSize = 14.sp,
                        color = ConstColors.gray.copy(alpha = 0.5f),
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = userType,
                        fontSize = 16.sp,
                        color = ConstColors.lightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
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

@Composable
fun ProgressItem(count: Int, progress: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(all = 4.dp)
                .background(
                    if (count <= progress.toInt()) ConstColors.lightGreen else ConstColors.gray.copy(
                        alpha = 0.5f
                    ), CircleShape
                )
                .size(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
            )
        }
        if (count < 5) {
            Box(
                modifier = Modifier
                    .background(
                        if (count <= progress.toInt()) ConstColors.lightGreen
                        else ConstColors.gray.copy(alpha = 0.5f),
                        MaterialTheme.shapes.small
                    )
                    .size(((LocalConfiguration.current.screenWidthDp.minus(120) * 0.2)).dp, 4.dp)
            )
        }
    }
}