package dev.shounakmulay.devpulse.core.domain.settings.privacy

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
class ObserveTelemetryPrivacyUseCase(
    private val coordinator: TelemetryPrivacyCoordinator,
) {
    operator fun invoke(): StateFlow<TelemetryPrivacyState> = coordinator.state
}
