package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.Product
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserType
import kotlin.reflect.KClass

internal sealed class Event {
    abstract val typeClazz: KClass<*>

    sealed class Action : Event() {

        sealed class Auth : Action() {
            override val typeClazz: KClass<*> = Auth::class

            object LogIn : Auth()
            data class LogOut(val notifyServer: Boolean) : Auth()
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
            data class AddAadhaar(val aadhaarData: AadhaarData) : Registration()
            data class UpdatePincode(val pincode: String) : Registration()
            data class UploadAadhaar(
                val phoneNumber: String,
                val email: String,
                val aadhaarAsBase64: String,
            ) : Registration()

            data class UploadDrugLicense(
                val phoneNumber: String,
                val email: String,
                val licenseAsBase64: String,
                val fileType: FileType,
            ) : Registration()

            object SignUp : Registration()
            object Skip : Registration()
            object AcceptWelcome : Registration()
        }

        sealed class Search : Action() {
            override val typeClazz: KClass<*> = Search::class

            data class SearchProduct(val value: String) : Search()
            data class SelectFilter(val filter: Filter, val option: Option<String>) : Search()
            data class ClearFilter(val filter: Filter?) : Search()
            data class SearchManufacturer(val value: String) : Search()
            data class SelectProduct(val product: Product) : Search()
        }
    }

    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object Back : Transition()
        object SignUp : Transition()
        object ForgetPassword : Transition()
        object Search : Transition()
    }
}