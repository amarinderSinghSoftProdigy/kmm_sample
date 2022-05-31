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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.ImageLabel
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputFieldForRegister
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.RectHolder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TextLabel
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.utils.PermissionCheckUIForSignUp
import com.zealsoftsol.medico.utils.PermissionViewModel
import com.zealsoftsol.medico.data.UserType as DataUserType


@Composable
fun AuthUserType(scope: SignUpScope.SelectUserType) {
    val selectedType = scope.userType.flow.collectAsState()
    BasicAuthSignUpScreenWithButton(
        userType = "",
        progress = 1.0,//0.2,
        baseScope = scope,
        horizontalAlignment = Alignment.CenterHorizontally,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.goToPersonalData() },
        body = {
            Text(
                text = stringResource(id = R.string.who_are_you),
                fontSize = 20.sp,
                fontWeight = FontWeight.W700,
                color = ConstColors.darkBlue,
            )
            Space(32.dp)
            Row {
                UserType(
                    iconRes = R.drawable.ic_menu_stockist,
                    textRes = R.string.stockist_sub,
                    isSelected = selectedType.value == DataUserType.STOCKIST,
                    onClick = { scope.chooseUserType(DataUserType.STOCKIST) },
                    filter = ColorFilter.tint(color = ConstColors.darkBlue)
                )
                Space(18.dp)
                UserType(
                    iconRes = R.drawable.ic_menu_retailers,
                    textRes = R.string.retailer,
                    isSelected = selectedType.value == DataUserType.RETAILER,
                    onClick = { scope.chooseUserType(DataUserType.RETAILER) },
                    filter = ColorFilter.tint(color = ConstColors.darkBlue)
                )
            }
            Space(18.dp)
            Row {
                UserType(
                    iconRes = R.drawable.ic_menu_hospitals,
                    textRes = R.string.hospital,
                    isSelected = selectedType.value == DataUserType.HOSPITAL,
                    onClick = { scope.chooseUserType(DataUserType.HOSPITAL) },
                    filter = ColorFilter.tint(color = ConstColors.darkBlue)
                )
                /*Space(18.dp)
                UserType(
                    iconRes = R.drawable.ic_season_boy,
                    textRes = R.string.season_boy,
                    isSelected = selectedType.value == DataUserType.SEASON_BOY,
                    onClick = { scope.chooseUserType(DataUserType.SEASON_BOY) },
                    filter = ColorFilter.tint(color = ConstColors.darkBlue),
                )*/
            }
        }
    )
}

@ExperimentalComposeUiApi
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val isTermsAccepted = scope.isTermsAccepted.flow.collectAsState()

    val registrationGlobal = scope.storedRegistration.flow.collectAsState().value.userReg1
    if (registration.value.firstName.isEmpty() && !registrationGlobal?.firstName.isNullOrEmpty()) {
        registration.value.firstName = registrationGlobal?.firstName ?: ""
        registration.value.lastName = registrationGlobal?.lastName ?: ""
        registration.value.email = registrationGlobal?.email ?: ""
        registration.value.phoneNumber = registrationGlobal?.phoneNumber ?: ""
        registration.value.password = registrationGlobal?.password ?: ""
        registration.value.verifyPassword = registrationGlobal?.verifyPassword ?: ""
        scope.changeTerms(true)
    }
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
                    modifier = Modifier
                        .align(Alignment.Start)
                        .scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.first_name),
                    text = registration.value.firstName,
                    isValid = !isFirstNameError,
                    onValueChange = { scope.changeFirstName(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_register),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
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
                    onValueChange = { scope.changeLastName(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_register),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!validEmail) stringResource(id = R.string.email_validation) else null) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.email),
                    text = registration.value.email,
                    onValueChange = { scope.changeEmail(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!validPhone) stringResource(id = R.string.phone_validation) else null) {
                PhoneFormatInputFieldForRegister(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.phone_number),
                    text = registration.value.phoneNumber,
                    onValueChange = { phoneNumber ->
                        scope.changePhoneNumber(phoneNumber.filter { it.isDigit() })
                    },
                    leadingIcon = {
                        Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_phone_number),
                                contentDescription = null,
                            )
                            Space(dp = 4.dp)
                            Text(
                                text = "+91",
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        }
                    },
                    mandatory = true,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
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
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_verify_password),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
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
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_verify_password),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)

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
            Space(18.dp)
        },
    )
}

@ExperimentalComposeUiApi
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val registrationGlobal = scope.storedRegistration.flow.collectAsState().value.userReg2
    if (registration.value.pincode.isEmpty() && registrationGlobal != null) {
        registration.value.pincode = registrationGlobal.pincode
        registration.value.state = registrationGlobal.state
        registration.value.city = registrationGlobal.city
        registration.value.district = registrationGlobal.district
        registration.value.location = registrationGlobal.location
        registration.value.addressLine1 = registrationGlobal.addressLine1
        registration.value.landmark = registrationGlobal.landmark
        scope.checkData()
    }
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
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = { scope.changePincode(it) },
                    mandatory = true,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = userValidation.value?.addressLine1) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.address_line),
                    text = registration.value.addressLine1,
                    onValueChange = { scope.changeAddressLine(it) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(
                errorText = userValidation.value?.landmark
            ) {
                //InputFieldWithCounter(
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.landmark),
                    text = registration.value.landmark,
                    onValueChange = { scope.changeLandmark(it) },
                    trailingIcon = {
                        Text(
                            text = registration.value.landmark.length.toString() + "/" + scope.landmarkLimit,
                            color = ConstColors.gray,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
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
            Divider(thickness = 2.dp)
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
            Divider(thickness = 2.dp)
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

@ExperimentalComposeUiApi
@Composable
fun AuthDetailsTraderData(scope: SignUpScope.Details.TraderData) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    val validFoodLicense = scope.checkFoodLicense(registration.value.foodLicenseNo)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val registrationGlobal = scope.storedRegistration.flow.value.userReg3
    if (registration.value.tradeName.isEmpty() && registrationGlobal != null) {
        registration.value.tradeName = registrationGlobal.tradeName
        registration.value.panNumber = registrationGlobal.panNumber
        registration.value.hasFoodLicense = registrationGlobal.hasFoodLicense
        registration.value.gstin = registrationGlobal.gstin
        registration.value.foodLicenseNo = registrationGlobal.foodLicenseNo
        registration.value.drugLicenseNo1 = registrationGlobal.drugLicenseNo1
        registration.value.drugLicenseNo2 = registrationGlobal.drugLicenseNo2
        scope.checkData()
    }
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
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
                        )
                    }
                    Space(dp = 12.dp)
                }
                if (it == SignUpScope.Details.Fields.PAN) {
                    InputWithError(errorText = validation.value?.panNumber) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.pan_number),
                            text = registration.value.panNumber,
                            isValid = Validator.TraderDetails.isPanValid(registration.value.panNumber) || registration.value.panNumber.isEmpty(),
                            keyboardOptions = KeyboardOptions.Default
                                .copy(
                                    capitalization = KeyboardCapitalization.Characters,
                                    imeAction = ImeAction.Done
                                ),
                            onValueChange = { value -> scope.changePan(value) },
                            trailingIcon = {
                                Text(
                                    text = registration.value.panNumber.length.toString() + "/" + scope.panLimit,
                                    color = ConstColors.gray,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                )
                            },
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
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
                                .copy(
                                    capitalization = KeyboardCapitalization.Characters,
                                    imeAction = ImeAction.Done
                                ),
                            onValueChange = { value -> scope.changeGstin(value) },
                            trailingIcon = {
                                Text(
                                    text = registration.value.gstin.length.toString() + "/" + scope.gstinLimit,
                                    color = ConstColors.gray,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                )
                            },
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
                        )
                    }
                    Space(dp = 8.dp)
                    Text(
                        text = stringResource(id = R.string.gstin_pan_required),
                        color = ConstColors.red,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp),
                    )
                    Space(dp = 8.dp)
                }
                if (it == SignUpScope.Details.Fields.LICENSE1) {
                    InputWithError(errorText = validation.value?.drugLicenseNo1) {
                        //InputWithPrefix(UserRegistration3.DRUG_LICENSE_1_PREFIX) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.drug_license_20b),
                            text = registration.value.drugLicenseNo1,
                            onValueChange = { value -> scope.changeDrugLicense1(value) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
                        )
                        //}
                    }
                    Space(dp = 12.dp)
                }
                if (it == SignUpScope.Details.Fields.LICENSE2) {
                    InputWithError(errorText = validation.value?.drugLicenseNo2) {
                        //InputWithPrefix(UserRegistration3.DRUG_LICENSE_2_PREFIX) {
                        InputField(
                            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                            hint = stringResource(id = R.string.drug_license_21b),
                            text = registration.value.drugLicenseNo2,
                            onValueChange = { value -> scope.changeDrugLicense2(value) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
                        )
                        // }
                    }
                }
                Space(16.dp)
                if (it == SignUpScope.Details.Fields.FOOD_LICENSE) {
                    Column {
                        val switchEnabled =
                            remember { mutableStateOf(registration.value.hasFoodLicense) }
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
                            Space(16.dp)
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
                                errorText = validation.value?.foodLicenseNumber
                            ) {
                                InputField(
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                                    hint = stringResource(id = R.string.food_license_number),
                                    text = registration.value.foodLicenseNo,
                                    onValueChange = { value -> scope.changeFoodLicense(value) },
                                    trailingIcon = {
                                        Text(
                                            text = registration.value.foodLicenseNo.length.toString() + "/" + scope.foodLicenseLimit,
                                            color = ConstColors.gray,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                        )
                                    },
                                    keyboardActions = KeyboardActions(onDone = {
                                        keyboardController?.hide()
                                    })
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
    val permissionViewModel = PermissionViewModel()
    PermissionCheckUIForSignUp(scaffoldState, permissionViewModel, scope.registrationStep1)
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

            var tradeCheck = registration.value.tradeProfile != null
            var drugCheck = registration.value.drugLicense != null
            var foodCheck = registration.value.foodLicense != null

            if (scope is SignUpScope.LegalDocuments.DrugLicense) {
                val registrationGlobal =
                    scope.storedRegistration.flow.collectAsState().value.userReg4
                if (registration.value.tradeProfile == null && registrationGlobal != null) {
                    tradeCheck = registrationGlobal.tradeProfile != null
                    drugCheck = registrationGlobal.drugLicense != null
                    foodCheck = registrationGlobal.foodLicense != null
                    registration.value.tradeProfile = registrationGlobal.tradeProfile
                    registration.value.drugLicense = registrationGlobal.drugLicense
                    registration.value.foodLicense = registrationGlobal.foodLicense
                    scope.checkData()
                }
            }
            Column {
                Surface(
                    onClick = {
                        permissionViewModel.setPerformLocationAction(true, "TRADE_PROFILE")
                        //scope.showBottomSheet("TRADE_PROFILE", scope.registrationStep1)
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
                        permissionViewModel.setPerformLocationAction(true, "DRUG_LICENSE")
                        //scope.showBottomSheet("DRUG_LICENSE", scope.registrationStep1)
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
                if (scope.registrationStep3.hasFoodLicense) {
                    Surface(
                        onClick = {
                            permissionViewModel.setPerformLocationAction(true, "FOOD_LICENSE")
                            /*scope.showBottomSheet(
                                "FOOD_LICENSE",
                                scope.registrationStep1
                            )*/
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
                TextLabel(scope.registrationStep1.firstName, R.drawable.ic_profile_register)
                TextLabel(scope.registrationStep1.lastName, R.drawable.ic_profile_register)
                TextLabel(scope.registrationStep1.email, R.drawable.ic_email)
                TextLabel(scope.registrationStep1.phoneNumber, R.drawable.ic_call)
                TextLabel(scope.registrationStep1.password, R.drawable.ic_verify_password)
                //TextLabel(scope.registrationStep1.verifyPassword, R.drawable.ic_verify_password)
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
                    val url = scope.registrationStep4.tradeProfile?.cdnUrl ?: ""
                    ImageLabel(
                        url
                    ) { scope.previewImage(url) }
                }
                TextLabel(scope.registrationStep3.gstin, labelShow = 1)
                TextLabel(scope.registrationStep3.panNumber, labelShow = 2)
                TextLabel(scope.registrationStep3.drugLicenseNo1)
                TextLabel(scope.registrationStep3.drugLicenseNo2)
                if (scope.registrationStep4.drugLicense != null) {
                    val url = scope.registrationStep4.drugLicense?.cdnUrl ?: ""
                    ImageLabel(
                        url
                    ) { scope.previewImage(url) }
                }
                TextLabel(scope.registrationStep3.foodLicenseNo)
                if (scope.registrationStep4.foodLicense != null) {
                    val url = scope.registrationStep4.foodLicense?.cdnUrl ?: ""
                    ImageLabel(
                        url
                    ) { scope.previewImage(url) }
                }
            }
        }
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AadhaarInputFields(
    aadhaarData: DataSource<AadhaarData>,
    onCardChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    showShareCode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }),
                onValueChange = onCardChange,
            )
        }
        if (showShareCode) {
            Space(dp = 12.dp)
            InputField(
                hint = stringResource(id = R.string.share_code),
                text = aadhaar.value.shareCode,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }),
                onValueChange = onCodeChange,
            )
        }
    }
}

@Composable
private fun UserType(
    iconRes: Int,
    textRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    filter: ColorFilter
) {
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
            modifier = Modifier.size(48.dp),
            colorFilter = filter
        )
        Text(
            text = stringResource(id = textRes),
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = ConstColors.darkBlue
        )
    }
}

@Composable
private fun BasicAuthSignUpScreenWithButton(
    userType: String,
    progress: Double,
    baseScope: SignUpScope,
    scrollState: ScrollState = rememberScrollState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Bottom,
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
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White)
        ) {
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
                        color = ConstColors.darkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box {
                Column(
                    modifier = Modifier
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

        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            MedicoButton(
                modifier = Modifier.padding(start = padding, end = padding, bottom = padding),
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
fun ProgressItem(
    count: Int, progress: Double,
    limit: Int = 5,
    width: Double = 0.2
) {
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
        if (count < limit) {
            Box(
                modifier = Modifier
                    .background(
                        if (count <= progress.toInt()) ConstColors.lightGreen
                        else ConstColors.gray.copy(alpha = 0.5f),
                        MaterialTheme.shapes.small
                    )
                    .size(
                        ((LocalConfiguration.current.screenWidthDp.minus(24 * limit) * width)).dp,
                        4.dp
                    )
            )
        }
    }
}