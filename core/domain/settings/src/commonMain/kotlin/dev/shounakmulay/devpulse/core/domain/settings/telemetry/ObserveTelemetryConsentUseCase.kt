package dev.shounakmulay.devpulse.core.domain.settings.telemetry

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.TelemetryConsentRepository
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveTelemetryConsentUseCase(
    private val repository: TelemetryConsentRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(): Flow<Result<DPAnalyticsConsent?>> = repository.observeConsent()
        .flowCachingOnDefault(dispatcherProvider)
}
