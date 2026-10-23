package dev.shounakmulay.devpulse.core.logging.performance

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class DPPerformanceInitialiser(
    private val dpPerformance: DPPerformance
) : StartupAsyncInitialiser, KoinComponent{
    override suspend fun initialise() {
        val sinks = getKoin().getAll<PerformanceSink>()
        dpPerformance.configure(sinks)
    }
}