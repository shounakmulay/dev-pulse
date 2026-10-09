package dev.shounakmulay.devpulse.core.common.coroutines

import kotlinx.coroutines.CancellationException

inline fun <T> safeRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
