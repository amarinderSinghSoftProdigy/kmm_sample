package com.zealsoftsol.medico.screens.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.data.EmployeeData

@Composable
fun ViewEmployees(scope: EmployeeScope.ViewEmployee) {

    val employeeData = scope.employeeData.flow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            itemsIndexed(
                items = employeeData.value,
                key = { index, _ -> index },
                itemContent = { _, item ->
                    EmployeeItem(item)
                },
            )
        }
    }
}

@Composable
fun EmployeeItem(item: EmployeeData) {
    Surface(modifier = Modifier.padding(10.dp), color = Color.White, elevation = 5.dp) {
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = item.name,
            color = ConstColors.green,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = item.mobileNo,
            color = ConstColors.gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = "${item.addressLine},${item.location},${item.cityOrTown},${item.state},${item.pincode}",
            color = ConstColors.gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
    }

}