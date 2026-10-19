package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryConsent

interface TelemetryConsentRepository {
    suspend fun load(): Result<TelemetryConsent>
    suspend fun save(consent: TelemetryConsent): Result<Unit>
}
