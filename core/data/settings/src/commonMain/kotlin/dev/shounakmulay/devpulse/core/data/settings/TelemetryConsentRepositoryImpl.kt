package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeyValue
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Factory

@Factory
class TelemetryConsentRepositoryImpl(private val preferences: DevPulsePreferences) : TelemetryConsentRepository {
    override fun observeConsent(): Flow<DPAnalyticsConsent?> = combine(
        preferences.observe(DevPulsePreferenceKeys.telemetryAnalyticsConsent),
        preferences.observe(DevPulsePreferenceKeys.telemetryCrashlyticsConsent),
        preferences.observe(DevPulsePreferenceKeys.telemetryPerformanceConsent),
    ) { analytics, crashlytics, performance ->
        if (analytics == null && crashlytics == null && performance == null) {
            null
        } else {
            DPAnalyticsConsent(
                crashReporting = crashlytics ?: true,
                usageAnalytics = analytics ?: false,
                performanceMetrics = performance ?: false,
            )
        }
    }.distinctUntilChanged()

    override suspend fun setConsent(consent: DPAnalyticsConsent) {
        preferences.set(
            DevPulsePreferenceKeyValue(
                key = DevPulsePreferenceKeys.telemetryAnalyticsConsent,
                value = consent.usageAnalytics,
            ),
            DevPulsePreferenceKeyValue(
                key = DevPulsePreferenceKeys.telemetryCrashlyticsConsent,
                value = consent.crashReporting,
            ),
            DevPulsePreferenceKeyValue(
                key = DevPulsePreferenceKeys.telemetryPerformanceConsent,
                value = consent.performanceMetrics,
            ),
        )
    }
}
