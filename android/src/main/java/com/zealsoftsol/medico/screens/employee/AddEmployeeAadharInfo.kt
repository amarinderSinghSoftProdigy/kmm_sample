package com.zealsoftsol.medico.screens.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.screens.auth.AadhaarInputFields
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.TextLabel
import com.zealsoftsol.medico.utils.PermissionCheckUI
import com.zealsoftsol.medico.utils.PermissionViewModel

@ExperimentalMaterialApi
@Composable
fun AddEmployeeAadharInfoScreen(
    scope: EmployeeScope.Details.Aadhaar,
    scaffoldState: ScaffoldState,
) {
    val permissionViewModel = PermissionViewModel()
    PermissionCheckUI(scaffoldState, permissionViewModel)
    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 3.0,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.addAadhaar() },
        body = {
            Column {
                val tradeCheck = scope.isVerified.flow.collectAsState()
                AadhaarInputFields(
                    aadhaarData = scope.aadhaarData,
                    showShareCode = false,
                    onCardChange = {
                        scope.changeShareCode("1234")
                        scope.changeCard(it)
                        scope.validate()
                    },
                    onCodeChange = {},
                )
                Space(dp = 16.dp)
                /*Surface(
                    onClick = {
                        permissionViewModel.setPerformLocationAction(true, "TRADE_PROFILE")
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
                            else stringResource(id = R.string.upload_aadhaar),
                            color = if (tradeCheck) MaterialTheme.colors.background else ConstColors.gray,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = if (tradeCheck) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }*/
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeePreview(scope: EmployeeScope.PreviewDetails) {
    BasicAuthSignUpScreenWithButton(
        userType = scope.registrationStep1.userType,
        progress = 4.0,
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
            }
        }
    )
}