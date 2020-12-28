package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.BaseScope
import com.zealsoftsol.medico.core.mvi.scope.CommonScope

class Navigator : UiNavigator {

    private val currentScope = DataSource<BaseScope>(BaseScope.Root)
    private val queue = ArrayDeque<BaseScope>()

    override val scope: DataSource<BaseScope>
        get() = currentScope

    override fun handleBack(): Boolean {
        return dropCurrentScope() != null
    }

    fun setProgress(value: Boolean) {
        currentScope.value.isInProgress.value = value
    }

    fun clearQueue(withRoot: Boolean = true) {
        if (withRoot) {
            queue.clear()
        } else {
            while (queue.size > 1) {
                queue.removeFirst()
            }
        }
    }

    fun setCurrentScope(scope: BaseScope) {
        if (queue.firstOrNull()?.queueId == scope.queueId) {
            queue.removeFirst()
        }
        queue.addFirst(scope)
        currentScope.value = scope
    }

    internal inline fun <reified S : BaseScope> withScope(block: Navigator.(S) -> Unit): Boolean {
        val cast = runCatching { currentScope.value as S }
        cast.getOrNull()?.let { block(it) } ?: "error casting scope to ${S::class}".warnIt()
        return cast.isSuccess
    }

    internal inline fun <reified S : CommonScope> withCommonScope(block: Navigator.(S) -> Unit): Boolean {
        val cast = runCatching { currentScope.value as S }
        cast.getOrNull()?.let { block(it) } ?: "error casting common scope to ${S::class}".warnIt()
        return cast.isSuccess
    }

    internal inline fun <reified T> searchQueueFor(): T? {
        return queue.filterIsInstance<T>().firstOrNull()
    }

    fun dropScopesToRoot() {
        if (queue.size > 1) {
            while (queue.size > 1) {
                queue.removeFirst()
            }
            currentScope.value = queue.first()
        } else {
            "can not drop scopes, queue contains single element".errorIt()
        }
    }

    fun dropCurrentScope(updateDataSource: Boolean = true): BaseScope? {
        return if (queue.size > 1) {
            val removed = queue.removeFirst()
            if (updateDataSource) {
                currentScope.value = queue.first()
            }
            removed
        } else {
            "can not go back, queue contains single element".errorIt()
            null
        }
    }
}

interface UiNavigator {
    val scope: DataSource<BaseScope>

    fun handleBack(): Boolean
}

internal inline fun <T> Navigator.withProgress(block: () -> T): T {
    setProgress(true)
    return block().also {
        setProgress(false)
    }
}