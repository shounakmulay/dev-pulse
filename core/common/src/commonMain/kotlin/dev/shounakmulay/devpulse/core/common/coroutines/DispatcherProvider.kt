package dev.shounakmulay.devpulse.core.common.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface DispatcherProvider {
    val defaultDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val mainDispatcher: MainCoroutineDispatcher
        get() = Dispatchers.Main
}

suspend inline fun <T> DispatcherProvider.runCatchingOnDefault(crossinline block: suspend CoroutineScope.() -> T): Result<T> = withContext(defaultDispatcher) {
    try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (cause: Exception) {
        Result.failure(cause)
    }
}

fun <T> Flow<T>.flowCachingOnDefault(dispatcherProvider: DispatcherProvider): Flow<Result<T>> = map {
    Result.success(it)
}.catch {
    if (it is CancellationException) throw it
    emit(Result.failure(it))
}
    .flowOn(dispatcherProvider.defaultDispatcher)

suspend inline fun <T> DispatcherProvider.runCachingOnIO(crossinline block: suspend CoroutineScope.() -> T): Result<T> = withContext(ioDispatcher) {
    try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (cause: Exception) {
        Result.failure(cause)
    }
}
