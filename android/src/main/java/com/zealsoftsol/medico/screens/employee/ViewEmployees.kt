package com.zealsoftsol.medico.screens.employee

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.zealsoftsol.medico.data.EmployeeData
import com.zealsoftsol.medico.screens.common.NoRecords

@Composable
fun ViewEmployees(scope: EmployeeScope.ViewEmployee) {

    val employeeData = scope.employeeData.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (employeeData.value.isNotEmpty()) {
            LazyColumn {
                itemsIndexed(
                    items = employeeData.value,
                    key = { index, _ -> index },
                    itemContent = { index, item ->
                        EmployeeItem(item) {
                            scope.deleteEmployee(item.id, index)
                        }
                    },
                )
            }
        } else {
            NoRecords(
                icon = R.drawable.ic_view_employee,
                text = R.string.no_employee,
                buttonText = stringResource(R.string.go_back)
            ) {
                scope.goBack()
            }
        }
    }
}

@Composable
fun EmployeeItem(item: EmployeeData, onDeleteClick: () -> Unit) {
    Surface(modifier = Modifier.padding(10.dp), color = Color.White, elevation = 5.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_delete), contentDescription = null,
                modifier = Modifier
                    .align(Alignment.End)
                    .size(25.dp)
                    .padding(bottom = 5.dp)
                    .clickable {
                        onDeleteClick()
                    }
            )
            Text(
                text = item.name,
                color = ConstColors.green,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = item.mobileNo,
                color = ConstColors.txtGrey,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${item.addressLine},${item.location},${item.cityOrTown},${item.state},${item.pincode}",
                color = ConstColors.txtGrey,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
            )
        }
    }
}