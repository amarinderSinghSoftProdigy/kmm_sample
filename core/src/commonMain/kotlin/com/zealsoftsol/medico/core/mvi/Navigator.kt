package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.StartScope
import com.zealsoftsol.medico.data.ErrorCode
import kotlin.reflect.KClass

class Navigator(private val safeCastEnabled: Boolean) : UiNavigator {

    private val hostScope = DataSource<Scope.Host>(StartScope)
    private var activeQueue: KClass<*> = hostScope.value.scopeId
    private val queues: HashMap<KClass<*>, ArrayDeque<Scope>> =
        hashMapOf(hostScope.value.scopeId to ArrayDeque())

    override val scope: DataSource<Scope.Host>
        get() = hostScope

    override fun handleBack(): Boolean = dropScope() != null

    fun setScope(scope: Scope) {
        addToQueue(scope)

        when (scope) {
            is Scope.Host -> {
                hostScope.value = scope
                if (scope is Scope.Host.TabBar) {
                    addToQueue(scope.childScope.value)
                }
            }
            is Scope.Child.TabBar -> {
                val parentQueue = getQueue(scope.parentScopeId)
                (parentQueue.first() as Scope.Host.TabBar).setChildScope(scope)
            }
        }
    }

    fun dropScope(
        strategy: DropStrategy = DropStrategy.FIRST,
        updateDataSource: Boolean = true
    ): Scope? {
        val queue = getQueue(activeQueue)
        return when (strategy) {
            DropStrategy.FIRST -> {
                val old = queue.removeFirst()
                if (queue.isEmpty() && old is Scope.Child) {
                    activeQueue = old.parentScopeId
                    val parentQueue = getQueue(activeQueue)
                    parentQueue.removeFirst()
                    val next = parentQueue.firstOrNull() as? Scope.Host
                    if (updateDataSource && next != null) scope.value = next
                    next
                } else {
                    val next = queue.firstOrNull()
                    if (updateDataSource) when (next) {
                        is Scope.Host -> hostScope.value = next
                        is Scope.Child.TabBar -> (getQueue(next.parentScopeId).first() as Scope.Host.TabBar).setChildScope(
                            next
                        )
                    }
                    next
                }
            }
            DropStrategy.ALL -> {
                val old = queue.last()
                queue.clear()
                if (old is Scope.Child) {
                    activeQueue = old.parentScopeId
                    dropScope(strategy, false)
                }
                if (updateDataSource) hostScope.value = StartScope
                null
            }
            DropStrategy.TO_ROOT -> {
                lateinit var old: Scope
                while (queue.size > if (activeQueue == StartScope.scopeId) 1 else 0) {
                    old = queue.removeFirst()
                }
                val next = if (old is Scope.Child) {
                    activeQueue = old.parentScopeId
                    requireNotNull(dropScope(strategy, false)) { "no valid host scope" }
                } else {
                    queue.first()
                }
                if (updateDataSource) hostScope.value = next as Scope.Host
                next
            }
        }
    }

    fun setHostProgress(value: Boolean) {
        hostScope.value.isInProgress.value = value
    }

    fun setHostError(errorCode: ErrorCode?) {
        hostScope.value.alertError.value = errorCode
    }

    internal inline fun <reified S : Scopable> withScope(block: Navigator.(S) -> Unit): Boolean {
        val actual = getQueue(activeQueue).first()
        val cast = runCatching { actual as S }
        cast.getOrNull()?.let { block(it) } ?: run {
            val msg = "error casting ${actual::class} scope to ${S::class}"
            if (safeCastEnabled) msg.warnIt() else throw Exception(msg)
        }
        return cast.isSuccess
    }

    internal inline fun <reified T> searchQueuesFor(): T? {
        return queues.values.flatten().filterIsInstance<T>().firstOrNull()
    }

    private fun getQueue(id: KClass<*>): ArrayDeque<Scope> = queues.getOrPut(id) { ArrayDeque() }

    private fun addToQueue(scope: Scope) {
        val queue = getQueue(scope.scopeId)
        require(queue.firstOrNull()?.queueId != scope.queueId) { "setting the same scope again is not allowed" }
        queue.addFirst(scope)
        activeQueue = scope.scopeId
    }

    enum class DropStrategy {
        FIRST, ALL, TO_ROOT;
    }
}

interface UiNavigator {
    val scope: DataSource<Scope.Host>

    fun handleBack(): Boolean
}

internal inline fun <T> Navigator.withProgress(block: () -> T): T {
    setHostProgress(true)
    return block().also {
        setHostProgress(false)
    }
}