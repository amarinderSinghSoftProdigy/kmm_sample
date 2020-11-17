package com.zealsoftsol.medico.core.viewmodel.mock

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.PasswordReset
import com.zealsoftsol.medico.data.UiStateWithProgress

class MockAuthViewModel : AuthViewModelFacade {
    override val credentials: DataSource<AuthCredentials> = DataSource(AuthCredentials("", null, ""))
    override val authState: DataSource<AuthState?> = DataSource(null)
    override val resetPasswordUiState: DataSource<UiStateWithProgress<PasswordReset>> = DataSource(UiStateWithProgress(PasswordReset.Done, false))

    override fun tryLogIn() {
    }

    override fun logOut() {
    }

    override fun updateAuthCredentials(emailOrPhone: String, password: String) {
    }

    override fun clearAuthState() {
    }

    override fun previousPasswordResetScreen(): Boolean = false

    override fun sendOtp(phoneNumber: String) {

    }

    override fun changePassword(newPassword: String) {

    }

    override fun clearPasswordResetState() {

    }

    override fun submitOtp(otp: String) {

    }

    override fun resendOtp() {

    }
}