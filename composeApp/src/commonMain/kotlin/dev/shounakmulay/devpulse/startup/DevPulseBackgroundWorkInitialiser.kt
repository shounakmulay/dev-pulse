package dev.shounakmulay.devpulse.startup

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.sync.BackgroundSyncScheduler
import dev.shounakmulay.devpulse.feature.feed.sync.FeedWorkRequestProvider
import org.koin.core.annotation.Single

@Single
class DevPulseBackgroundWorkInitialiser(
    private val backgroundSyncScheduler: BackgroundSyncScheduler
) : StartupAsyncInitialiser {

    override suspend fun initialise() {
        backgroundSyncScheduler.initialise(
            buildList {
                addAll(FeedWorkRequestProvider.createFeedWorkRequests())
            }
        )
    }
}