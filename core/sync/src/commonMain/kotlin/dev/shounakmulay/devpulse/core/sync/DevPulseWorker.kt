package dev.shounakmulay.devpulse.core.sync

interface DevPulseWorker {
    val identifier: String
    suspend fun doWork(input: Map<String, Any?>): Result<Map<String, Any?>?>
}
