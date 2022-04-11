package com.zealsoftsol.medico.screens.employee

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun AddEmployeeScreen(scope: EmployeeScope.SelectUserType) {

    val optionSelected = remember { mutableStateOf<EmployeeScope.OptionSelected?>(null) }

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
                    titleId = R.string.add_employee,
                    icon = R.drawable.ic_add_employee,
                    isSelected = optionSelected.value != null && optionSelected.value == EmployeeScope.OptionSelected.ADD_EMPLOYEE
                ) {
                    scope.chooseUserType(UserType.EMPLOYEE)
                    optionSelected.value = EmployeeScope.OptionSelected.ADD_EMPLOYEE
                }
                Space(20.dp)
                ChooseOption(
                    modifier = Modifier.weight(1f),
                    titleId = R.string.view_employee,
                    icon = R.drawable.ic_view_employee,
                    isSelected = optionSelected.value != null && optionSelected.value == EmployeeScope.OptionSelected.VIEW_EMPLOYEE
                ) {
                    optionSelected.value = EmployeeScope.OptionSelected.VIEW_EMPLOYEE
                }
            }
        }
        MedicoButton(
            text = stringResource(id = R.string.next), isEnabled = optionSelected.value != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (optionSelected.value == EmployeeScope.OptionSelected.ADD_EMPLOYEE) {
                scope.goToPersonalData()
            } else {
                scope.goToViewEmployee()
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
    onClick: () -> Unit
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