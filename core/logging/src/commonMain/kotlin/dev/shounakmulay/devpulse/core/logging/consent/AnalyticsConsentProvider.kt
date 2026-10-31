package dev.shounakmulay.devpulse.core.logging.consent

import kotlinx.coroutines.flow.Flow

data class DPAnalyticsConsent(
    val crashReporting: Boolean = true,
    val usageAnalytics: Boolean = false,
    val performanceMetrics: Boolean = false,
)

interface AnalyticsConsentProvider {
    fun getConsent(): DPAnalyticsConsent?
    fun observeConsent(): Flow<DPAnalyticsConsent?>
}

expect fun applyTelemetryConsent(consent: DPAnalyticsConsent)
