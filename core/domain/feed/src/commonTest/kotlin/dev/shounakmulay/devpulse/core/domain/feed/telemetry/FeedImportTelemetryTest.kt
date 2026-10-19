package dev.shounakmulay.devpulse.core.domain.feed.telemetry

import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.telemetry.api.Analytics
import dev.shounakmulay.devpulse.core.telemetry.api.AnalyticsEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedImportTelemetryTest {
    @Test
    fun `Given a batch When accepted Then each import has one start event`() {
        val events = mutableListOf<String>()
        val telemetry = telemetry(events)
        telemetry.queued(2)
        telemetry.queued(0)
        assertEquals(listOf("feed_add_started", "feed_add_started"), events)
    }

    @Test
    fun `Given background sync and user import When terminal Then only user import emits outcomes`() {
        val events = mutableListOf<String>()
        val telemetry = telemetry(events)
        for (requestor in RssFeedQueueActionRequestor.entries) {
            for (action in RssFeedQueueActionType.entries) {
                val entry = entry(requestor, action)
                telemetry.completed(entry)
                telemetry.failed(entry)
            }
        }
        assertEquals(listOf("feed_add_completed", "feed_add_failed"), events)
    }

    private fun telemetry(events: MutableList<String>) = FeedImportTelemetry(object : Analytics {
        override fun track(event: AnalyticsEvent) {
            events.add(event.definition.name)
        }
    })

    private fun entry(requestor: RssFeedQueueActionRequestor, action: RssFeedQueueActionType) =
        RssFeedQueueEntry(
            feedId = null,
            url = "https://private.example/feed?token=secret",
            name = "Private subscription",
            feedType = RssFeedType.CONTENT,
            actionType = action,
            requestor = requestor,
            status = RssFeedQueueStatus.PROCESSING,
            createdAt = 0L,
            updatedAt = 0L,
        )
}
