package com.zealsoftsol.medico.core.mvi.event

import kotlin.reflect.KClass

sealed class Event {
    abstract val typeClazz: KClass<*>

    sealed class Action : Event() {

        sealed class Auth : Action() {
            override val typeClazz: KClass<*> = Auth::class

            object LogIn : Auth()
            data class LogOut(val notifyServer: Boolean) : Auth()
            data class UpdateAuthCredentials(val emailOrPhone: String, val password: String) :
                Auth()

            object UpdateDashboard : Auth()
        }

    }


    sealed class Transition : Event() {
        override val typeClazz: KClass<*> = Transition::class

        object Back : Transition()
        object Refresh : Transition()
        object Dashboard : Transition()
    }
}