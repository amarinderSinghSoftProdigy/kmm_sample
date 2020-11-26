package com.zealsoftsol.medico.core.viewmodel.interfaces

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.data.AuthCredentials

interface AuthViewModel {
    val credentials: DataSource<AuthCredentials>

    fun updateAuthCredentials(emailOrPhone: String, password: String)

    fun tryLogIn()

    fun logOut()

    fun sendOtp(phoneNumber: String)

    fun submitOtp(otp: String)

    fun resendOtp()

    fun changePassword(newPassword: String)
}