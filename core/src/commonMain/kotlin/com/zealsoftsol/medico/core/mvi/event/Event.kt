package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.data.FileType
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

        sealed class Otp : Action() {
            override val typeClazz: KClass<*> = Otp::class

            data class Send(val phoneNumber: String) : Otp()
            data class Submit(val otp: String) : Otp()
            object Resend : Otp()
        }

        sealed class ResetPassword : Action() {
            override val typeClazz: KClass<*> = ResetPassword::class

            data class Send(val newPassword: String) : ResetPassword()
            object Finish : ResetPassword()
        }

        sealed class Registration : Action() {
            override val typeClazz: KClass<*> = Registration::class

            data class SelectUserType(val userType: UserType) : Registration()
            data class Validate(val userRegistration: UserRegistration) : Registration()
            data class UpdatePincode(val pincode: String) : Registration()
            data class UploadAadhaar(val aadhaar: String, val fileType: FileType) : Registration()
            data class UploadDrugLicense(val license: String, val fileType: FileType) :
                Registration()

            object SignUp : Registration()
            object Skip : Registration()
        }
    }

    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object ForgetPassword : Transition()
        object SignUp : Transition()
        object Back : Transition()
    }
}