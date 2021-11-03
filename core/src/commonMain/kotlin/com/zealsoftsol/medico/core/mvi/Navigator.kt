package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.StartScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Response
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
        if (scope is TabBarScope) addToQueue(scope.childScope.value)

        updateCurrentScope(scope)
    }

    fun refresh() {
        queues[activeQueue]?.firstOrNull()?.let { updateCurrentScope(it) }
    }

    fun dropScope(
        strategy: DropStrategy = DropStrategy.First,
        updateDataSource: Boolean = true
    ): Scope? {
        if (hostScope.value.bottomSheet.value != null) {
            hostScope.value.dismissBottomSheet()
        }
        if (hostScope.value.alertError.value != null) {
            hostScope.value.dismissAlertError()
        }
        val queue = getQueue(activeQueue)
        return when (strategy) {
            is DropStrategy.First -> {
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
                    if (updateDataSource && next != null) {
                        if (next is TabBarScope) {
                            activeQueue = next.childScope.value.scopeId
                        }
                        updateCurrentScope(next)
                    }
                    next
                }
            }
            is DropStrategy.All -> {
                val old = queue.last()
                queue.clear()
                if (old is Scope.Child) {
                    activeQueue = old.parentScopeId
                    dropScope(strategy, false)
                }
                if (updateDataSource) hostScope.value = StartScope
                null
            }
            is DropStrategy.ToRoot -> {
                var old: Scope? = null
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
            is DropStrategy.To -> {
                var old: Scope? = null
                while (queue.size > if (activeQueue == StartScope.scopeId) 1 else 0) {
                    old = queue.removeFirst()
                    queue.firstOrNull()?.let {
                        if (it::class == strategy.scopeClass) {
                            if (updateDataSource) updateCurrentScope(it)
                            return it
                        }
                    }
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

    internal inline fun <reified S : Scopable> withScope(
        forceSafe: Boolean = false,
        block: Navigator.(S) -> Unit
    ): Boolean {
        val actual = getQueue(activeQueue).first()
        val cast = runCatching { actual as S }
        cast.getOrNull()?.let { block(it) } ?: run {
            val msg = "error casting ${actual::class} scope to ${S::class}"
            if (safeCastEnabled || forceSafe) msg.warnIt() else throw Exception(msg)
        }
        return cast.isSuccess
    }

    internal inline fun <reified T> searchQueuesFor(): T? {
        return queues.values.flatten().filterIsInstance<T>().firstOrNull()
    }

    private fun getQueue(id: KClass<*>): ArrayDeque<Scope> = queues.getOrPut(id) { ArrayDeque() }

    private fun addToQueue(scope: Scope) {
        val queue = getQueue(scope.scopeId)
        if (scope is Scope.Child.TabBar && scope.isRoot) {
            queue.clear()
        }
        if (queue.firstOrNull()?.queueId == scope.queueId) {
            val removed = queue.removeFirst()
            "setting the same scope again $scope => $removed".warnIt()
        }
//        require(queue.firstOrNull()?.queueId != scope.queueId) { "setting the same scope again is not allowed" }
        queue.addFirst(scope)
        activeQueue = scope.scopeId
    }

    private fun updateCurrentScope(scope: Scope) {
        when (scope) {
            is Scope.Host -> {
                hostScope.value = scope
            }
            is Scope.Child.TabBar -> {
                (getQueue(scope.parentScopeId).first() as TabBarScope)
                    .setChildScope(scope, getQueue(scope.scopeId).size)
            }
        }
    }

    sealed class DropStrategy {
        object First : DropStrategy()
        object All : DropStrategy()
        object ToRoot : DropStrategy()
        class To(val scopeClass: KClass<*>) : DropStrategy()
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

internal inline fun Response<*, *>.onError(navigator: Navigator) = apply {
    onError { navigator.setHostError(it) }
}