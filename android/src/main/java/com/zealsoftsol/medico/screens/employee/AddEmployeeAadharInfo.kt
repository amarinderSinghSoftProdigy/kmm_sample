package com.zealsoftsol.medico.screens.employee
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.screens.auth.AadhaarInputFields
import com.zealsoftsol.medico.screens.common.Space
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