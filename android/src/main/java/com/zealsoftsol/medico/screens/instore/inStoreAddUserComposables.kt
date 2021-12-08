package com.zealsoftsol.medico.screens.instore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreAddUserScope
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.screens.common.Dropdown
import com.zealsoftsol.medico.screens.common.FoldableItem
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithPrefix
import com.zealsoftsol.medico.screens.common.MedicoRoundButton
import com.zealsoftsol.medico.screens.common.PhoneFormatInputField
import com.zealsoftsol.medico.screens.common.ReadOnlyField
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun InStoreAddUserScreen(scope: InStoreAddUserScope) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(ConstColors.newDesignGray)
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.customer_details),
            color = MaterialTheme.colors.background,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Space(20.dp)
        val registration = scope.registration.flow.collectAsState()
        Dropdown(
            rememberChooseKey = this,
            value = registration.value.paymentMethod.serverValue,
            hint = "",
            dropDownItems = PaymentMethod.values().map { it.serverValue },
            readOnly = false,
            onSelected = { scope.changePaymentMethod(it) }
        )
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.trade_name),
            text = registration.value.tradeName,
            onValueChange = { scope.changeTradeName(it) }
        )
        Space(12.dp)
        PhoneFormatInputField(
            hint = stringResource(id = R.string.phone_number),
            text = registration.value.phoneNumber,
            onValueChange = { phoneNumber ->
                scope.changePhoneNumber(phoneNumber.filter { it == '+' || it.isDigit() })
            },
        )
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.gstin),
            text = registration.value.gstin,
            isValid = Validator.TraderDetails.isGstinValid(registration.value.gstin) || registration.value.gstin.isEmpty(),
            keyboardOptions = KeyboardOptions.Default
                .copy(capitalization = KeyboardCapitalization.Characters),
            onValueChange = { scope.changeGstin(it) },
        )
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.pan_number),
            text = registration.value.panNumber,
            isValid = Validator.TraderDetails.isPanValid(registration.value.panNumber) || registration.value.panNumber.isEmpty(),
            keyboardOptions = KeyboardOptions.Default
                .copy(capitalization = KeyboardCapitalization.Characters),
            onValueChange = { scope.changePan(it) },
        )
        Space(12.dp)
        InputWithPrefix(UserRegistration3.DRUG_LICENSE_1_PREFIX) {
            InputField(
                hint = stringResource(id = R.string.drug_license_1),
                text = registration.value.drugLicenseNo1,
                onValueChange = { scope.changeDrugLicense1(it) },
            )
        }
        Space(12.dp)
        InputWithPrefix(UserRegistration3.DRUG_LICENSE_2_PREFIX) {
            InputField(
                hint = stringResource(id = R.string.drug_license_2),
                text = registration.value.drugLicenseNo2,
                onValueChange = { scope.changeDrugLicense2(it) },
            )
        }
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.pincode),
            text = registration.value.pincode,
            onValueChange = { scope.changePincode(it) }
        )
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.address_line),
            text = registration.value.addressLine1,
            onValueChange = { scope.changeAddressLine(it) }
        )
        Space(12.dp)
        InputField(
            hint = stringResource(id = R.string.landmark),
            text = registration.value.landmark,
            onValueChange = { scope.changeLandmark(it) }
        )
        Space(12.dp)
        val locationData = scope.locationData.flow.collectAsState()
        Dropdown(
            rememberChooseKey = locationData.value,
            value = registration.value.location,
            hint = stringResource(id = R.string.location),
            dropDownItems = locationData.value?.locations.orEmpty(),
            onSelected = { scope.changeLocation(it) }
        )
        Space(dp = 12.dp)
        Dropdown(
            rememberChooseKey = locationData.value,
            value = registration.value.city,
            hint = stringResource(id = R.string.city),
            dropDownItems = locationData.value?.cities.orEmpty(),
            onSelected = { scope.changeCity(it) }
        )
        Space(dp = 12.dp)
        ReadOnlyField(registration.value.district, R.string.district)
        Space(dp = 12.dp)
        ReadOnlyField(registration.value.state, R.string.state)
        val canGoNext = scope.canGoNext.flow.collectAsState()
        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            MedicoRoundButton(
                modifier = Modifier.weight(.3f),
                text = stringResource(id = R.string.reset),
                isEnabled = true,
                height = 48.dp,
                color = Color.Transparent,
                contentColor = ConstColors.lightBlue,
                border = BorderStroke(2.dp, ConstColors.lightBlue)
            ) { scope.reset() }
            Space(dp = 12.dp)
            MedicoRoundButton(
                modifier = Modifier.weight(.7f),
                text = stringResource(id = R.string.add_customer),
                isEnabled = canGoNext.value,
                height = 48.dp,
                color = ConstColors.lightBlue,
                contentColor = Color.White,
            ) { scope.createUser() }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun InStoreUserItem(item: InStoreUser, isSelected: Boolean, onClick: () -> Unit) {
    FoldableItem(
        expanded = isSelected,
        header = { isExpanded ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isSelected,
                        enabled = true,
                        onClick = onClick,
                        colors = RadioButtonDefaults.colors(selectedColor = ConstColors.lightBlue),
                    )
                    Text(
                        text = item.tradeName,
                        color = MaterialTheme.colors.background,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    tint = ConstColors.gray,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        childItems = listOf(Unit),
        item = { _, _ ->
            Text(
                text = item.addressData.fullAddress(),
                color = MaterialTheme.colors.background,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
            )
            Space(4.dp)
            Text(
                text = item.gstin,
                color = ConstColors.lightBlue,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
            )
            Space(4.dp)
            Text(
                text = "DL1: 20B: ${item.drugLicenseNo1}",
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
            Space(4.dp)
            Text(
                text = "DL2: 21B: ${item.drugLicenseNo2}",
                color = ConstColors.gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
            )
            Space(4.dp)
            Text(
                text = buildAnnotatedString {
                    append("Status: ")
                    val startIndex = length
                    append(item.status)
                    addStyle(
                        SpanStyle(color = ConstColors.green),
                        startIndex,
                        length,
                    )
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .background(
                        ConstColors.green.copy(alpha = 0.05f),
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        ConstColors.green,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    )
}