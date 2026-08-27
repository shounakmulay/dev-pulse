package dev.shounakmulay.devpulse.core.sync


interface BackgroundSyncScheduler {
    suspend fun initialise(workRequests: List<DevPulseWorkRequest>)
    suspend fun enqueue(workRequest: DevPulseWorkRequest)
}

expect class DevPulseBackgroundSyncScheduler : BackgroundSyncScheduler {
    override suspend fun initialise(workRequests: List<DevPulseWorkRequest>)
    override suspend fun enqueue(workRequest: DevPulseWorkRequest)
}
