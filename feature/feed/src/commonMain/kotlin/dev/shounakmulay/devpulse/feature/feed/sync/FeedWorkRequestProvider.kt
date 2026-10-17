package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.sync.DevPulsePeriodicWorkerExitingWorkPolicy
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkRequest
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkRequestConstraints
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import kotlin.time.Duration.Companion.hours

object FeedWorkRequestProvider {
    fun createFeedWorkRequests() = listOf(
        createFeedSyncWorkRequest(),
        createFeedDiscoverySyncWorkRequest()
    )

    private fun createFeedSyncWorkRequest(): DevPulseWorkRequest.PeriodicWorkRequest {
        return DevPulseWorkRequest.PeriodicWorkRequest(
            identifier = DevPulseWorkerType.FEED_SYNC,
            interval = 1.hours,
            existingPeriodicWorkPolicy = DevPulsePeriodicWorkerExitingWorkPolicy.KEEP,
            constraints = DevPulseWorkRequestConstraints.requiresNetwork(),
        )
    }

    private fun createFeedDiscoverySyncWorkRequest(): DevPulseWorkRequest.PeriodicWorkRequest {
        return DevPulseWorkRequest.PeriodicWorkRequest(
            identifier = DevPulseWorkerType.FEED_DISCOVERY_SYNC,
            interval = 48.hours,
            initialDelay = 48.hours,
            existingPeriodicWorkPolicy = DevPulsePeriodicWorkerExitingWorkPolicy.KEEP,
            constraints = DevPulseWorkRequestConstraints.requiresNetwork(),
        )
    }
}
