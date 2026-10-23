package dev.shounakmulay.devpulse.core.logging.events

import org.koin.core.annotation.Single
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException

@Single
object DPEvent {
    @Volatile
    private var sinks: List<EventSink>? = null

    fun configure(sinks: List<EventSink>) {
        this.sinks = sinks
    }

    fun track(event: DPEvents, data: Map<String, String> = emptyMap()) {
        val configuredSinks = requireNotNull(sinks) {
            "DPEvent configuration is missing. Did you call DPEvent.configure()"
        }
        val entry = EventEntry(name = event.name, data = data)
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
