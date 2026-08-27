package dev.shounakmulay.devpulse.core.sync

actual class DevPulseBackgroundSyncScheduler : BackgroundSyncScheduler {
    actual override suspend fun initialise(workRequests: List<DevPulseWorkRequest>) {
    }

    actual override suspend fun enqueue(workRequest: DevPulseWorkRequest) {
    }
}