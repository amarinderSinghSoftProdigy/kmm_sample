package com.zealsoftsol.medico.screens.employee

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.MainActivity
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.data.EmployeeData
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.NoRecords
import com.zealsoftsol.medico.screens.common.ShowAlert
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun AddEmployeeScreen(scope: EmployeeScope.SelectUserType) {

    val optionSelected = remember { mutableStateOf<EmployeeScope.OptionSelected?>(null) }
    val employeeData = scope.employeeData.flow.collectAsState()
    val activity = LocalContext.current as MainActivity
    val showWarning = scope.showWarning.flow.collectAsState()
    val showNoEmployeeView = scope.showNoEmployee.flow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp, horizontal = 10.dp)
            ) {
                ChooseOption(
                    modifier = Modifier.weight(1f),
                    titleId = R.string.add_partner,
                    icon = R.drawable.ic_add_employee,
                    isSelected = optionSelected.value != null && optionSelected.value == EmployeeScope.OptionSelected.ADD_PARTNER,
                ) {
                    scope.chooseUserType(UserType.PARTNER)
                    optionSelected.value = EmployeeScope.OptionSelected.ADD_PARTNER
                    if (canAddMoreItems(employeeData.value, UserType.PARTNER.serverValue)) {
                        scope.goToPersonalData()
                    } else {
                        scope.updateWarningVisibility(true)
                    }
                }
                Space(20.dp)
                ChooseOption(
                    modifier = Modifier.weight(1f),
                    titleId = R.string.add_employee,
                    icon = R.drawable.ic_add_employee,
                    isSelected = optionSelected.value != null && optionSelected.value == EmployeeScope.OptionSelected.ADD_EMPLOYEE,
                ) {
                    scope.chooseUserType(UserType.EMPLOYEE)
                    optionSelected.value = EmployeeScope.OptionSelected.ADD_EMPLOYEE
                    if (canAddMoreItems(employeeData.value, UserType.EMPLOYEE.serverValue)) {
                        scope.goToPersonalData()
                    } else {
                        scope.updateWarningVisibility(true)
                    }
                }
            }
            Space(dp = 10.dp)
            if (employeeData.value.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(3.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = employeeData.value,
                        key = { index, _ -> index },
                        itemContent = { index, item ->
                            EmployeeItem(activity, item) {
                                scope.deleteEmployee(item.id, index)
                            }
                        },
                    )
                }
            }
            if (showNoEmployeeView.value)
                NoRecords(
                    icon = R.drawable.ic_view_employee,
                    text = R.string.no_employee,
                    buttonText = stringResource(R.string.go_back)
                ) {
                    scope.goBack()
                }
        }
        if (showWarning.value) {
            ShowAlert(
                message = if (optionSelected.value == EmployeeScope.OptionSelected.ADD_EMPLOYEE) stringResource(
                    id = R.string.warning_employee_add
                ) else stringResource(id = R.string.warning_partner_add)
            ) {
                scope.updateWarningVisibility(false)
            }
        }
    }
}

/**
 * to check if the list already contains either an employee or a partner
 */
private fun canAddMoreItems(employeeList: List<EmployeeData>, type: String): Boolean {
    return if (employeeList.isEmpty()) {
        true
    } else {
        !employeeList.any { it.userType.uppercase() in type }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EmployeeItem(activity: MainActivity, item: EmployeeData, onDeleteClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.padding(horizontal = 10.dp),
        color = Color.White,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = item.name,
                color = MaterialTheme.colors.background,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
            )
            Text(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .clickable {
                        activity.openDialer(item.mobileNo)
                    },
                text = "+91-${item.mobileNo}",
                color = ConstColors.txtGrey,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.user_type))
                        append(" ")
                        val startIndex = length
                        append(item.userType)
                        addStyle(
                            SpanStyle(color = ConstColors.green),
                            startIndex,
                            length,
                        )
                    },
                    color = ConstColors.txtGrey,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500,
                )

                Text(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .clickable {
                            visible = !visible
                        },
                    text = if (visible) stringResource(id = R.string.less_details) else stringResource(
                        id = R.string.more_details
                    ),
                    color = MaterialTheme.colors.background,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                )
            }
            AnimatedVisibility(
                visible = visible
            ) {
                Column {
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = "${item.addressLine},${item.location},${item.cityOrTown},${item.state},${item.pincode}",
                        color = ConstColors.txtGrey,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Space(10.dp)
                    MedicoButton(text = stringResource(id = R.string.delete), isEnabled = true) {
                        onDeleteClick()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChooseOption(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int,
    @DrawableRes icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable {
            onClick()
        },
        elevation = 5.dp,
        color = Color.White,
        border = if (isSelected) BorderStroke(
            1.dp,
            ConstColors.yellow.copy(alpha = 0.5f),
        ) else BorderStroke(
            1.dp,
            Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(55.dp),
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Space(5.dp)
            Text(
                text = stringResource(id = titleId),
                color = MaterialTheme.colors.background,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600
            )
        }
    }
}