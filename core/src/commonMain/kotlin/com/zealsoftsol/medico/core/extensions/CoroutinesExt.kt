@file:Suppress("NOTHING_TO_INLINE")

package com.zealsoftsol.medico.core.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

suspend inline fun <T, R> Collection<T>.asyncMap(
    context: CoroutineContext = Dispatchers.Default,
    crossinline onItem: suspend (T) -> R
): Collection<Deferred<R>> {
    val scope = CoroutineScope(context)
    return map { scope.async(context) { onItem(it) } }
}

inline val printExceptionHandler
    get() = CoroutineExceptionHandler { ctx, throwable ->
        println("caught exception in $ctx, msg ${throwable.message}")
    }

inline fun CoroutineContext.toScope() = CoroutineScope(this)

inline fun <T, R> CoroutineScope.coroutineWorker(
    inChannel: ReceiveChannel<T>,
    outChannel: SendChannel<Pair<T, R>>? = null,
    capacity: Int = 1,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline onItem: suspend (T) -> R
): List<Deferred<Unit>> {
    return List(capacity) {
        async(dispatcher) {
            for (item in inChannel) {
                val result = onItem(item)
                outChannel?.send(Pair(item, result))
            }
        }
    }
}

inline fun <T> Flow<T>.observe(scope: CoroutineScope, noinline block: suspend (T) -> Unit): Job {
    return onEach(block).launchIn(scope)
}

suspend inline fun <T> Flow<T>.observe(noinline block: suspend (T) -> Unit) {
    onEach(block).collect()
}