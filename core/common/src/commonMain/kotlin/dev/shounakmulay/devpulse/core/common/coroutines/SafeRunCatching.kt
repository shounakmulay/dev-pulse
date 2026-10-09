package dev.shounakmulay.devpulse.core.common.coroutines

import kotlinx.coroutines.CancellationException

inline fun <T> safeRunCatching(block: () -> T): Result<T> {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return Result.failure(e)
    }
    return Result.success(block())
}