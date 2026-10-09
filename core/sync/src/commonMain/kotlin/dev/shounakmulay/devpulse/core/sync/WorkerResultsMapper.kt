package dev.shounakmulay.devpulse.core.sync

fun <T> Result<T>.toWorkerResult(data: Map<String, Any?>? = null): DevPulseWorkerResult {
    fold(
        onSuccess = { return Result.success(data) },
        onFailure = { return Result.failure(it) }
    )
}