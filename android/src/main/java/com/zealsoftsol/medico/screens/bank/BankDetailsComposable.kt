package com.zealsoftsol.medico.screens.bank

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.BankDetailsScope
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BankDetailsScreen(scope: BankDetailsScope.AccountDetails) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val accountDetails = scope.bankDetails.flow.collectAsState()
    val reEnterAccountNumber = scope.reEnterAccountNumber.flow.collectAsState()
    val canSubmitDetails = scope.canSubmitDetails.flow.collectAsState()
    val accountNumberErrorText = scope.accountNumberErrorText.flow.collectAsState()
    val ifscErrorText = scope.ifscErrorText.flow.collectAsState()
    val mobErrorText = scope.mobileErrorText.flow.collectAsState()
    val reenterAccountErrorText = scope.reenterAccountNumberErrorText.flow.collectAsState()
    val canEditDetails = scope.canEditData.flow.collectAsState()
    val showToast = scope.showToast.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        InputWithError(errorText = null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.name),
                text = accountDetails.value.name,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { scope.updateName(it) },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        InputWithError(errorText = if (!accountNumberErrorText.value) context.resources.getString(R.string.acc_num_warning) else null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.account_number),
                text = accountDetails.value.accountNumber,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        scope.updateAccountNumber(it)
                    }
                },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        InputWithError(errorText = if (!reenterAccountErrorText.value) context.resources.getString(R.string.reenter_acc_warning) else null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.re_account_number),
                text = reEnterAccountNumber.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        scope.updateReEnterAccountNumber(it)
                    }
                },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        InputWithError(errorText = if (!ifscErrorText.value) context.resources.getString(R.string.ifsc_warning) else null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.ifsc_code),
                text = accountDetails.value.ifscCode,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { scope.updateIfscCode(it) },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        InputWithError(errorText = if (!mobErrorText.value) context.resources.getString(R.string.phone_validation) else null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.phone_number),
                text = accountDetails.value.mobileNumber,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        scope.updateMobile(it)
                    }
                },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        if(canEditDetails.value) {
            MedicoButton(
                text = stringResource(id = R.string.submit),
                isEnabled = canSubmitDetails.value
            ) {
                scope.submitAccountDetails()
            }
        }
    }

    if (showToast.value) {
        ShowToastGlobal(msg = stringResource(id = R.string.success))
        scope.hideToast()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpiDetailsScreen(scope: BankDetailsScope.UpiDetails) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val canSubmitDetails = scope.canSubmitDetails.flow.collectAsState()
    val name = scope.name.flow.collectAsState()
    val upiAddress = scope.upiAddress.flow.collectAsState()
    val upiErrorText = scope.upiErrorText.flow.collectAsState()
    val context = LocalContext.current
    val canEditDetails = scope.canEditData.flow.collectAsState()
    val showToast = scope.showToast.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        InputWithError(errorText = "") {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.name),
                text = name.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { scope.updateName(it) },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        InputWithError(errorText = if (!upiErrorText.value) context.resources.getString(R.string.valid_upi) else null) {
            InputField(
                modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                hint = stringResource(id = R.string.upi_hint),
                text = upiAddress.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { scope.updateUpiAddress(it) },
                mandatory = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                enabled = canEditDetails.value
            )
        }
        Space(dp = 12.dp)
        if(canEditDetails.value) {
            MedicoButton(
                text = stringResource(id = R.string.submit),
                isEnabled = canSubmitDetails.value
            ) {
                scope.submitUpiDetails()
            }
        }
    }

    if (showToast.value) {
        ShowToastGlobal(msg = stringResource(id = R.string.success))
        scope.hideToast()
    }
}