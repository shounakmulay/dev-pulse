package dev.shounakmulay.devpulse.core.logging.events

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.common.startup.StartupPriority
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class DPEventInitialiser(
    private val dpEvent: DPEvent,
) : StartupAsyncInitialiser,
    KoinComponent {
    override val priority: StartupPriority
        get() = StartupPriority.HIGH

    override suspend fun initialise() {
        val sinks = getKoin().getAll<EventSink>()
        dpEvent.configure(sinks)
    }
}
