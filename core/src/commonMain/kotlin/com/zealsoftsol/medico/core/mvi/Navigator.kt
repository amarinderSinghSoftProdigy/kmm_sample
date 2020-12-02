package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.BaseScope

class Navigator : UiNavigator {

    private val currentScope = DataSource<BaseScope>(BaseScope.Root)
    private val queue = ArrayDeque<BaseScope>()

    override val scope: DataSource<BaseScope>
        get() = currentScope

    override fun handleBack(): Boolean {
        return dropCurrentScope() != null
    }

    fun setProgress(value: Boolean) {
        currentScope.value = currentScope.value.changeProgress(value)
    }

    fun clearQueue() {
        queue.clear()
    }

    fun setCurrentScope(scope: BaseScope, updateDataSource: Boolean = true) {
        if (queue.firstOrNull()?.queueId == scope.queueId) {
            queue.removeFirst()
        }
        queue.addFirst(scope)
        if (updateDataSource) {
            currentScope.value = scope
        }
    }

    internal inline fun <reified S : BaseScope> withScope(block: Navigator.(S) -> Unit): Boolean {
        val cast = runCatching { currentScope.value as S }
        cast.getOrNull()?.let { block(it) } ?: "error casting scope to ${S::class}".warnIt()
        return cast.isSuccess
    }

    fun dropCurrentScope(updateDataSource: Boolean = true): BaseScope? {
        return if (queue.size > 1) {
            val removed = queue.removeFirst()
            if (updateDataSource) {
                currentScope.value = queue.first()
            }
            removed
        } else {
            "can not go back in Navigator, queue contains single element".errorIt()
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