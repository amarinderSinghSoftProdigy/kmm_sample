package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserType
import kotlin.reflect.KClass

internal sealed class Event {
    abstract val typeClazz: KClass<*>

    sealed class Action : Event() {

        sealed class Auth : Action() {
            override val typeClazz: KClass<*> = Auth::class

            object LogIn : Auth()
            object LogOut : Auth()
            data class UpdateAuthCredentials(val emailOrPhone: String, val password: String) :
                Auth()
        }

        sealed class Password : Action() {
            override val typeClazz: KClass<*> = Password::class

            data class SendOtp(val phoneNumber: String) : Password()
            data class SubmitOtp(val otp: String) : Password()
            object ResendOtp : Password()
            data class ChangePassword(val newPassword: String) : Password()
        }

        sealed class Registration : Action() {
            override val typeClazz: KClass<*> = Registration::class

            data class SelectUserType(val userType: UserType) : Registration()
            data class SignUp(val userRegistration: UserRegistration) : Registration()
        }
    }

    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object ForgetPassword : Transition()
        object SignUp : Transition()
        object Back : Transition()
    }
}