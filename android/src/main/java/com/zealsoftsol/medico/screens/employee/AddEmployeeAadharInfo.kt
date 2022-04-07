package com.zealsoftsol.medico.screens.employee

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.AddEmployeeScope
import com.zealsoftsol.medico.screens.auth.AadhaarInputFields

@Composable
fun AddEmployeeAadharInfoScreen(scope: AddEmployeeScope.Details.Aadhaar) {
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