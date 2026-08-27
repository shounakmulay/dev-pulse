package dev.shounakmulay.devpulse.core.sync

data class DevPulseUniqueWorkRequestMetadata(
    val identifier: String,
    val existingWorkPolicy: DevPulseUniqueWorkerExitingWorkPolicy,
)

enum class DevPulseUniqueWorkerExitingWorkPolicy {
    REPLACE,
    KEEP,
    APPEND,
    APPEND_OR_REPLACE;
}