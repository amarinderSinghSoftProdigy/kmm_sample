package com.zealsoftsol.medico.screens.management

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.ManagementScope
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.InputWithPrefix
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus

@Composable
fun AddRetailerScreen(scope: ManagementScope.AddRetailer) {
    val canGoNext = scope.canGoNext.flow.collectAsState()
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val padding = 16.dp
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = padding,
                    start = padding,
                    end = padding,
                    bottom = padding + 60.dp
                )
        ) {
            when (scope) {
                is ManagementScope.AddRetailer.TraderDetails -> TraderDetails(scope, scrollState)
                is ManagementScope.AddRetailer.Address -> Address(scope, scrollState)
            }
        }
        MedicoButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(padding),
            text = stringResource(id = R.string.submit),
            isEnabled = canGoNext.value,
            onClick = { scope.next() },
        )
    }

}

@Composable
private fun TraderDetails(
    scope: ManagementScope.AddRetailer.TraderDetails,
    scrollState: ScrollState
) {
    val registration = scope.registration.flow.collectAsState()
    val validation = scope.validation.flow.collectAsState()
    val isTermsAccepted = scope.isTermsAccepted.flow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = stringResource(id = R.string.add_retailer),
        color = MaterialTheme.colors.background,
        fontWeight = FontWeight.W600,
        fontSize = 17.sp,
    )
    Space(dp = 12.dp)
    InputWithError(errorText = validation.value?.tradeName) {
        InputField(
            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.trade_name),
            text = registration.value.tradeName,
            onValueChange = { scope.changeTradeName(it) },
        )
    }
    Space(dp = 12.dp)
    InputWithError(errorText = validation.value?.gstin) {
        InputField(
            modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
            hint = stringResource(id = R.string.gstin),
            text = registration.value.gstin,
            isValid = Validator.TraderDetails.isGstinValid(registration.value.gstin),
            keyboardOptions = KeyboardOptions.Default
                .copy(capitalization = KeyboardCapitalization.Characters),
            onValueChange = { scope.changeGstin(it) },
        )
    }
    Space(dp = 12.dp)
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
    Space(dp = 12.dp)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isTermsAccepted.value,
            colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
            onCheckedChange = { scope.changeTerms(it) },
        )
        Space(8.dp)
        Text(
            text = stringResource(id = R.string.consent_terms),
            fontSize = 12.sp,
            color = ConstColors.gray,
        )
    }
}

@Composable
private fun Address(scope: ManagementScope.AddRetailer.Address, scrollState: ScrollState) {
    val registration = scope.registration.flow.collectAsState()
    val pincodeValidation = scope.pincodeValidation.flow.collectAsState()
    val locationData = scope.locationData.flow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = stringResource(id = R.string.add_retailer_address),
        color = MaterialTheme.colors.background,
        fontWeight = FontWeight.W600,
        fontSize = 17.sp,
    )
    Space(dp = 12.dp)
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
    InputField(
        modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
        hint = stringResource(id = R.string.address_line),
        text = registration.value.addressLine1,
        onValueChange = { scope.changeAddressLine(it) }
    )
    Space(dp = 12.dp)
    Dropdown(
        modifier = Modifier.fillMaxWidth(),
        rememberChooseKey = locationData.value,
        value = registration.value.location.takeIf { it.isNotEmpty() }
            ?: stringResource(id = R.string.location),
        dropDownItems = locationData.value?.locations.orEmpty(),
        onSelected = { scope.changeLocation(it) }
    )
    Space(dp = 12.dp)
    Dropdown(
        modifier = Modifier.fillMaxWidth(),
        rememberChooseKey = locationData.value,
        value = registration.value.city.takeIf { it.isNotEmpty() }
            ?: stringResource(id = R.string.city),
        dropDownItems = locationData.value?.cities.orEmpty(),
        onSelected = { scope.changeCity(it) }
    )
    Space(dp = 12.dp)
    ReadOnlyField(registration.value.district, R.string.district)
    Space(dp = 12.dp)
    ReadOnlyField(registration.value.state, R.string.state)
}