package dev.shounakmulay.devpulse.core.logging.events

import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException

object DPEvent : KoinComponent {
    @Volatile
    private var sinks: List<EventSink>? = null

    private val analyticsConsentProvider by inject<AnalyticsConsentProvider>()

    fun configure(sinks: List<EventSink>) {
        this.sinks = sinks
    }

    fun track(event: DPEvents, data: Map<String, String> = emptyMap()) {
        if (analyticsConsentProvider.getConsent()?.usageAnalytics != true) return

        val configuredSinks = requireNotNull(sinks) {
            "DPEvent configuration is missing. Did you call DPEvent.configure()"
        }
        val entry = EventEntry(name = event.eventName, data = data)
        configuredSinks.forEach { sink ->
            try {
                if (sink.isTrackable(entry)) sink.track(entry)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
            }
        }
    }
}
