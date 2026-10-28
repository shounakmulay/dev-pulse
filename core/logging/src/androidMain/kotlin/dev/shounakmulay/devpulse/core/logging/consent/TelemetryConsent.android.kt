package dev.shounakmulay.devpulse.core.logging.consent

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance

actual fun applyTelemetryConsent(consent: DPAnalyticsConsent) {
    val crashlytics = Firebase.crashlytics
    if (!crashlytics.isCrashlyticsCollectionEnabled || !consent.crashReporting) crashlytics.deleteUnsentReports()
    crashlytics.isCrashlyticsCollectionEnabled = consent.crashReporting
    Firebase.performance.isPerformanceCollectionEnabled = consent.performanceMetrics
    Firebase.analytics.setAnalyticsCollectionEnabled(consent.usageAnalytics)
}
