package dev.shounakmulay.devpulse.core.domain.settings.privacy

import org.koin.core.annotation.Factory

@Factory
class RetryTelemetryPrivacyUseCase(
    private val coordinator: TelemetryPrivacyCoordinator,
) {
    suspend operator fun invoke(): Result<Unit> = coordinator.retry()
}
