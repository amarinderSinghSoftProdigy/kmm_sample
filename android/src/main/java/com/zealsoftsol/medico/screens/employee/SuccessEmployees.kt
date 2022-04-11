package com.zealsoftsol.medico.screens.employee

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.screens.common.MedicoButton

@Composable
fun SuccessEmployees(scope: EmployeeScope.SuccessEmployee) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ic_success), contentDescription = null)
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(id = R.string.employee_reg_success),
            color = ConstColors.green
        )
        MedicoButton(
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp),
            text = stringResource(id = R.string.done),
            isEnabled = true
        ) {
        }
    }
}