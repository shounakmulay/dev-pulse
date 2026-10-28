package dev.shounakmulay.devpulse.core.domain.settings.telemetry

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCachingOnIO
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import org.koin.core.annotation.Factory

@Factory
class SetTelemetryConsentUseCase(
    private val dispatcherProvider: DispatcherProvider,
    private val consentProvider: DPAnalyticsConsentProvider,
) {
    suspend operator fun invoke(consent: DPAnalyticsConsent): Result<Unit> = dispatcherProvider.runCachingOnIO {
        consentProvider.setConsent(consent)
    }
}
