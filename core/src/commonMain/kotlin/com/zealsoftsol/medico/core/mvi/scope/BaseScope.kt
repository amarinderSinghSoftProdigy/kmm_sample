package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

abstract class BaseScope {
    abstract val isInProgress: Boolean
    val queueId: String = this::class.simpleName.orEmpty()

    internal fun changeProgress(value: Boolean): BaseScope {
        return when (this) {
            is LogInScope -> copy(isInProgress = value)
            is MainScope -> copy(isInProgress = value)
            is ForgetPasswordScope.PhoneNumberInput -> copy(isInProgress = value)
            is ForgetPasswordScope.AwaitVerification -> copy(isInProgress = value)
            is ForgetPasswordScope.EnterNewPassword -> copy(isInProgress = value)
            is SignUpScope.SelectUserType -> this
            is SignUpScope.PersonalData -> copy(isInProgress = value)
            is SignUpScope.AddressData -> copy(isInProgress = value)
            is SignUpScope.TraderData -> copy(isInProgress = value)
            else -> throw UnsupportedOperationException("Changing progress in scope $this is not supported")
        }
    }

    object Root : BaseScope() {
        override val isInProgress: Boolean
            get() = throw UnsupportedOperationException("Root does not support progress")
    }
}

interface CanGoBack {
    fun goBack() {
        EventCollector.sendEvent(Event.Transition.Back)
    }
}