package dev.shounakmulay.devpulse.core.common.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

fun <T> Flow<Result<T>>.onEachSuccess(block: (T?) -> Unit): Flow<Result<T>> {
    return filter { it.isSuccess }
        .onEach { result ->
            val data = result.getOrNull()
            data?.let {
                block(it)
            }
        }
}

fun <T> Flow<Result<T>>.mapToSuccess(): Flow<T?> {
    return map {
        it.getOrNull()
    }
}

fun <T> Flow<Result<T>>.mapToSuccessNotNull(): Flow<T> {
    return mapNotNull {
        it.getOrNull()
    }
}