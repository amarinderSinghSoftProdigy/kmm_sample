package com.zealsoftsol.medico.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.data.CustomerAddressData
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.screens.common.NavigationCell
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.rememberPhoneNumberFormatter
import com.zealsoftsol.medico.screens.common.stringResourceByName

@Composable
fun SettingsScreen(scope: SettingsScope) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 32.dp)) {
        when (scope) {
            is SettingsScope.List -> SettingsList(scope.sections)
            is SettingsScope.Profile -> Profile(scope.user)
            is SettingsScope.Address -> Address(scope.addressData)
            is SettingsScope.GstinDetails -> GstinDetails(scope.details)
        }
    }
}

@Composable
private fun SettingsList(sections: List<SettingsScope.List.Section>) {
    sections.forEach {
        NavigationCell(
            icon = vectorResource(
                id = when (it) {
                    SettingsScope.List.Section.PROFILE -> R.drawable.ic_profile
                    SettingsScope.List.Section.CHANGE_PASSWORD -> R.drawable.ic_password_lock
                    SettingsScope.List.Section.ADDRESS -> R.drawable.ic_address
                    SettingsScope.List.Section.GSTIN_DETAILS -> R.drawable.ic_folder
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
    val formatter = rememberPhoneNumberFormatter()
    val formatted = formatter.verifyNumber(user.phoneNumber)
    ReadOnlyField(formatted ?: user.phoneNumber, R.string.phone_number)
}

@Composable
private fun Address(addressData: CustomerAddressData) {
    ReadOnlyField(addressData.pincode.toString(), R.string.pincode)
    Space(12.dp)
    ReadOnlyField(addressData.address, R.string.address_line)
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