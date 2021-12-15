package com.zealsoftsol.medico.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.screens.common.*

@Composable
fun SettingsScreen(scope: SettingsScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        when (scope) {
            is SettingsScope.List -> SettingsList(scope.sections)
            is SettingsScope.Profile -> Profile(scope.user)
            is SettingsScope.Address -> Address(scope.addressData)
            is SettingsScope.GstinDetails -> GstinDetails(scope.details)
            is SettingsScope.WhatsAppPreference -> WhatsappPreference()
        }
    }
}

@Composable
private fun SettingsList(sections: List<SettingsScope.List.Section>) {
    sections.forEach {
        NavigationCell(
            icon = painterResource(
                id = when (it) {
                    SettingsScope.List.Section.PROFILE -> R.drawable.ic_profile
                    SettingsScope.List.Section.CHANGE_PASSWORD -> R.drawable.ic_password_lock
                    SettingsScope.List.Section.ADDRESS -> R.drawable.ic_address
                    SettingsScope.List.Section.GSTIN_DETAILS -> R.drawable.ic_folder
                    SettingsScope.List.Section.WHATSAPP_PREFERENCE -> R.drawable.ic_folder
                }
            ),
            text = stringResourceByName(it.stringId),
            clickIndication = null,
            onClick = { it.select() },
        )
        Space(12.dp)
    }
}

@Composable
private fun Profile(user: User) {
    ReadOnlyField(user.firstName, R.string.first_name)
    Space(12.dp)
    ReadOnlyField(user.lastName, R.string.last_name)
    Space(12.dp)
    ReadOnlyField(user.email, R.string.email)
    Space(12.dp)
    ReadOnlyField(user.phoneNumber.formatIndia(), R.string.phone_number)
}

@Composable
private fun Address(addressData: AddressData) {
    ReadOnlyField(addressData.pincode.toString(), R.string.pincode)
    Space(12.dp)
    ReadOnlyField(addressData.address, R.string.address_line)
    Space(12.dp)
    ReadOnlyField(addressData.landmark, R.string.landmark)
    Space(12.dp)
    ReadOnlyField(addressData.location, R.string.location)
    Space(12.dp)
    ReadOnlyField(addressData.city, R.string.city)
    Space(12.dp)
    ReadOnlyField(addressData.district, R.string.district)
    Space(12.dp)
    ReadOnlyField(addressData.state, R.string.state)
}

@Composable
private fun GstinDetails(details: User.Details.DrugLicense) {
    ReadOnlyField(details.tradeName, R.string.trade_name)
    Space(12.dp)
    ReadOnlyField(details.gstin, R.string.gstin)
    Space(12.dp)
    ReadOnlyField(details.license1, R.string.drug_license_1)
    Space(12.dp)
    ReadOnlyField(details.license2, R.string.drug_license_2)
}


@Composable
private fun WhatsappPreference() {
    var text by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Space(12.dp)
        Text(
            text = stringResource(id = R.string.preferred_language),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = Color.Black,
        )
        Space(12.dp)
        LanguagePicker(
            hint = stringResource(id = R.string.language_english),
        )
        Space(20.dp)
        Text(
            text = stringResource(id = R.string.phone_number),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = Color.Black,
        )
        Space(12.dp)
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium),
            value = text,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { text = it },
            placeholder = { Text("+91") }
        )
    }
}

@Composable
private fun LanguagePicker(hint: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .clickable(onClick = {
                })
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = null ?: hint,
                color = Color.Black,
                fontSize = 14.sp,
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ConstColors.gray,
            )
        }
    }

}