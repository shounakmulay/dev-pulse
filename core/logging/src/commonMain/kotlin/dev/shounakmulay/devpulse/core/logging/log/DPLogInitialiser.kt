package dev.shounakmulay.devpulse.core.logging.log

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.common.startup.StartupPriority
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class DPLogInitialiser(
    private val dpLog: DPLog
) : StartupAsyncInitialiser, KoinComponent {
    override val priority: StartupPriority
        get() = StartupPriority.HIGHEST

    override suspend fun initialise() {
        val logSinks = getKoin().getAll<LogSink>()
        dpLog.configure(DPLogConfigs.release(logSinks))
    }
}
