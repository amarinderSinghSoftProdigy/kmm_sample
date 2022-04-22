package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class AddEmployee(val info: String)

@Serializable
data class ViewEmployee(
    val results: List<EmployeeData>
)

@Serializable
data class EmployeeData(
    val id: String,
    val userType: String,
    val name: String,
    val mobileNo: String,
    val addressLine: String,
    val location: String,
    val cityOrTown: String,
    val state: String,
    val pincode: String
)