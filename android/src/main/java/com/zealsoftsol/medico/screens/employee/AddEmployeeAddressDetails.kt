package com.zealsoftsol.medico.screens.employee

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.AddEmployeeScope
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus

@ExperimentalComposeUiApi
@Composable
fun AddEmployeeAddressDetailsScreen(scope: AddEmployeeScope.AddressData) {
    val registration = scope.registration.flow.collectAsState()
    val userValidation = scope.userValidation.flow.collectAsState()
    val pincodeValidation = scope.pincodeValidation.flow.collectAsState()
    val locationData = scope.locationData.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val lengthValid =
        if (registration.value.landmark.isNotEmpty()) registration.value.landmark.length == 30 else false
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 2.0,
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