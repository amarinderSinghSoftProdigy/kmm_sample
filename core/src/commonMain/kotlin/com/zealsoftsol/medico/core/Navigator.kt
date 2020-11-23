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