package com.zealsoftsol.medico.core.viewmodel.mock

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.viewmodel.interfaces.AuthViewModel
import com.zealsoftsol.medico.data.AuthCredentials

class MockAuthViewModel : AuthViewModel {
    override val credentials: DataSource<AuthCredentials> = DataSource(AuthCredentials("", null, ""))

    override fun tryLogIn() {
    }

    override fun logOut() {
    }

    override fun updateAuthCredentials(emailOrPhone: String, password: String) {
    }

    override fun sendOtp(phoneNumber: String) {

    }

    override fun changePassword(newPassword: String) {

    }

    override fun submitOtp(otp: String) {

    }

    override fun resendOtp() {

    }
}