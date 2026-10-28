package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.flow.Flow

interface TelemetryConsentRepository {
    fun observeConsent(): Flow<DPAnalyticsConsent?>
    suspend fun setConsent(consent: DPAnalyticsConsent)
}
