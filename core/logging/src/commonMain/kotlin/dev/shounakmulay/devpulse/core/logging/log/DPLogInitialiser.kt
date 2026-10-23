package dev.shounakmulay.devpulse.core.logging.log

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class DPLogInitialiser(
    private val dpLog: DPLog
) : StartupAsyncInitialiser, KoinComponent {
    override suspend fun initialise() {
        val logSinks = getKoin().getAll<LogSink>()
        dpLog.configure(DPLogConfigs.release(logSinks))
    }
}