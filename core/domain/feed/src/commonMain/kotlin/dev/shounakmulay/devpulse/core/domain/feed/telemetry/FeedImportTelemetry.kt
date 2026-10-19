package dev.shounakmulay.devpulse.core.domain.feed.telemetry

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.telemetry.api.*
import org.koin.core.annotation.Factory

@Factory
class FeedImportTelemetry(private val analytics: Analytics = NoOpTelemetry) {
    fun queued(count: Int) {
        repeat(count) { analytics.track(FeedAddStarted) }
    }

    fun completed(entry: RssFeedQueueEntry) {
        if (entry.isUserImport()) analytics.track(FeedAddCompleted)
    }

    fun failed(entry: RssFeedQueueEntry) {
        if (entry.isUserImport()) analytics.track(FeedAddFailed)
    }

    private fun RssFeedQueueEntry.isUserImport() =
        requestor == RssFeedQueueActionRequestor.USER && actionType == RssFeedQueueActionType.IMPORT
}

data object FeedAddStarted : AnalyticsEvent {
    override val definition = EventDefinition("feed_add_started", version = 1, owner = "feed")
    override val attributes = SafeAttributes.Empty
}

data object FeedAddCompleted : AnalyticsEvent {
    override val definition = EventDefinition("feed_add_completed", version = 1, owner = "feed")
    override val attributes = SafeAttributes.Empty
}

data object FeedAddFailed : AnalyticsEvent {
    override val definition = EventDefinition("feed_add_failed", version = 1, owner = "feed")
    override val attributes = SafeAttributes.Empty
}
