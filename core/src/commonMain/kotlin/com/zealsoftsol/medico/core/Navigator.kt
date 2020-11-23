package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

internal class Navigator(private val userRepo: UserRepo) : UiNavigator {

    private val currentScope = DataSource<Scope>(
        when (userRepo.authState) {
            AuthState.AUTHORIZED -> Scope.Main()
            AuthState.NOT_AUTHORIZED -> Scope.LogIn()
        }
    )
    private val queue = ArrayDeque<Scope>().apply {
        addFirst(currentScope.value)
    }

    init {
        GlobalScope.launch(compatDispatcher) {
            for (request in requests.openSubscription()) {
                when (request) {
                    is Request.GoTo -> {
                        if (request.replaceScope)
                            dropCurrentScope(updateQueue = false)
                        setCurrentScope(request.scope)
                    }
                    is Request.GoBack -> dropCurrentScope()
                }
            }
        }
    }

    override val scope: DataSource<Scope>
        get() = currentScope

    override fun handleBack(): Boolean {
        return dropCurrentScope() != null
    }

    fun setProgress(value: Boolean) {
        currentScope.value = currentScope.value.changeProgress(value)
    }

    inline fun <reified S : Scope> withScope(block: S.() -> Unit): Boolean {
        val cast = runCatching { currentScope.value as S }
        cast.getOrNull()?.block() ?: "error casting scope to ${S::class}".warnIt()
        return cast.isSuccess
    }

    fun updateScope(scope: Scope) = setCurrentScope(scope)

    private fun setCurrentScope(scope: Scope) {
        if (queue.first().queueId == scope.queueId) {
            queue.removeFirst()
        }
        queue.addFirst(scope)
        currentScope.value = scope
    }

    private fun dropCurrentScope(updateQueue: Boolean = true): Scope? {
        return if (queue.size > 1) {
            val removed = queue.removeFirst()
            if (updateQueue) {
                currentScope.value = queue.first()
            }
            removed
        } else {
            "can not go back, queue contains single element".errorIt()
            null
        }
    }

    internal sealed class Request {
        data class GoTo(val scope: Scope, val replaceScope: Boolean = false) : Request()
        object GoBack : Request()
    }

    companion object {
        internal val requests = ConflatedBroadcastChannel<Request>()
    }
}

interface UiNavigator {
    val scope: DataSource<Scope>

    fun handleBack(): Boolean
}

internal inline fun <T> Navigator.withProgress(block: () -> T): T {
    setProgress(true)
    return block().also {
        setProgress(false)
    }
}

sealed class Scope {
    abstract val isInProgress: Boolean
    val queueId: String = this::class.simpleName.orEmpty()

    internal fun changeProgress(value: Boolean): Scope {
        return when (this) {
            is LogIn -> copy(isInProgress = value)
            is Main -> copy(isInProgress = value)
            is ForgetPassword.PhoneNumberInput -> copy(isInProgress = value)
            is ForgetPassword.AwaitVerification -> copy(isInProgress = value)
            is ForgetPassword.EnterNewPassword -> copy(isInProgress = value)
        }
    }

    data class LogIn(
        val success: BooleanEvent = BooleanEvent.none,
        override val isInProgress: Boolean = false,
    ) : Scope() {

        internal fun goToMain() {
            Navigator.requests.offer(Navigator.Request.GoTo(Main()))
        }

        fun goToForgetPassword() {
            Navigator.requests.offer(Navigator.Request.GoTo(ForgetPassword.PhoneNumberInput()))
        }

        fun goToSignUp() {

        }
    }

    data class Main(
        override val isInProgress: Boolean = false,
    ) : Scope() {

        internal fun goToLogin() {
            Navigator.requests.offer(Navigator.Request.GoTo(LogIn()))
        }
    }

    sealed class ForgetPassword : Scope() {

        fun goBack() {
            Navigator.requests.offer(Navigator.Request.GoBack)
        }

        data class PhoneNumberInput(
            val success: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToAwaitVerification(phoneNumber: String) {
                Navigator.requests.offer(Navigator.Request.GoTo(AwaitVerification(phoneNumber)))
            }
        }

        data class AwaitVerification(
            val phoneNumber: String,
            val timeBeforeResend: Long = RESEND_TIMER,
            val attemptsLeft: Int = MAX_RESEND_ATTEMPTS,
            val codeValidity: BooleanEvent = BooleanEvent.none,
            val resendSuccess: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToEnterNewPassword(phoneNumber: String) {
                Navigator.requests.offer(Navigator.Request.GoTo(EnterNewPassword(phoneNumber), replaceScope = true))
            }

            companion object {
                const val RESEND_TIMER = 3 * 60 * 1000L
                private const val MAX_RESEND_ATTEMPTS = 3
            }
        }

        data class EnterNewPassword(
            val phoneNumber: String,
            val success: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToLogin() {
                Navigator.requests.offer(Navigator.Request.GoTo(LogIn()))
            }
        }
    }
}

open class OneShotEvent<T> internal constructor(private val internalValue: T, private var wasAccessed: Boolean = false) {

    fun debug(): T = internalValue

    val value: T?
        get() {
            if (wasAccessed) return null
            wasAccessed = true
            return internalValue
        }
}

class BooleanEvent private constructor(isTrue: Boolean, wasAccessed: Boolean = false) : OneShotEvent<Boolean>(isTrue, wasAccessed) {

    inline val isTrue: Boolean
        get() = value == true
    inline val isFalse: Boolean
        get() = value == false

    companion object {
        internal val `true`: BooleanEvent
            get() = BooleanEvent(true)
        internal val `false`: BooleanEvent
            get() = BooleanEvent(false)
        internal val none: BooleanEvent
            get() = BooleanEvent(false, true)

        internal inline fun of(value: Boolean) = if (value) `true` else `false`
    }
}