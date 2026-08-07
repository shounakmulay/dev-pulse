package dev.shounakmulay.devpulse.core.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ApplicationScope(
    dispatcherProvider: DispatcherProvider
) : CoroutineScope {
    override val coroutineContext = SupervisorJob() + dispatcherProvider.defaultDispatcher
}