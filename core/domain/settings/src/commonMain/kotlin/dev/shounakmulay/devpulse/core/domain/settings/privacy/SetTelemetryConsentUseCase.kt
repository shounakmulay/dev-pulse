package dev.shounakmulay.devpulse.core.domain.settings.privacy

import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryPurpose
import org.koin.core.annotation.Factory

@Factory
class SetTelemetryConsentUseCase(
    private val coordinator: TelemetryPrivacyCoordinator,
) {
    suspend operator fun invoke(purpose: TelemetryPurpose, enabled: Boolean): Result<Unit> =
        coordinator.setConsent(purpose = purpose, enabled = enabled)
}
