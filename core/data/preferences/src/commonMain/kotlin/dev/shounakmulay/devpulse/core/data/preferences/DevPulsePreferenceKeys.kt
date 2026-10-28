package dev.shounakmulay.devpulse.core.data.preferences

object DevPulsePreferenceKeys {
    val telemetryCrashlyticsConsent = DevPulsePreferenceKey.BooleanPreferenceKey("telemetryCrashlyticsConsent")
    val telemetryAnalyticsConsent = DevPulsePreferenceKey.BooleanPreferenceKey("telemetryAnalyticsConsent")
    val telemetryPerformanceConsent = DevPulsePreferenceKey.BooleanPreferenceKey("telemetryPerformanceConsent")
    val isAppInBlackMode = DevPulsePreferenceKey.BooleanPreferenceKey("isAppInBlackMode")
    val feedPostListItemVariant = DevPulsePreferenceKey.StringPreferenceKey("feedPostListItemVariant")
    val syncInBackground = DevPulsePreferenceKey.BooleanPreferenceKey("syncFeedsInBackground")
    val appTheme = DevPulsePreferenceKey.StringPreferenceKey("appTheme")
    val reImportExistingPosts =
        DevPulsePreferenceKey.BooleanPreferenceKey("reImportExistingPosts")
    val contentTextScale = DevPulsePreferenceKey.FloatPreferenceKey("contentTextScale")
    val lineHeightScale = DevPulsePreferenceKey.FloatPreferenceKey("lineHeightScale")
}
