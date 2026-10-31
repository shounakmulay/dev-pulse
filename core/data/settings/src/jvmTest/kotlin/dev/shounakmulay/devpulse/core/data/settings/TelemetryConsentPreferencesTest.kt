package dev.shounakmulay.devpulse.core.data.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dev.shounakmulay.devpulse.core.data.preferences.DatastoreDevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TelemetryConsentPreferencesTest {
    @Test
    fun `Given missing or partial choices When observing Then unanswered and explicit choices remain distinct`() = runBlocking {
        val directory = Files.createTempDirectory("telemetry-consent").toFile()
        val job = SupervisorJob()
        try {
            val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job), produceFile = { directory.resolve("settings.preferences_pb") })
            val preferences = DatastoreDevPulsePreferences(store)
            val repository = TelemetryConsentRepositoryImpl(preferences)
            assertEquals(null, repository.observeConsent().first())
            preferences.set(DevPulsePreferenceKeys.telemetryAnalyticsConsent, false)
            assertEquals(DPAnalyticsConsent(crashReporting = true, usageAnalytics = false, performanceMetrics = false), repository.observeConsent().first())
            preferences.set(DevPulsePreferenceKeys.telemetryCrashlyticsConsent, false)
            assertEquals(DPAnalyticsConsent(crashReporting = false, usageAnalytics = false, performanceMetrics = false), repository.observeConsent().first())
        } finally {
            job.cancelAndJoin()
            directory.deleteRecursively()
        }
    }

    @Test
    fun `Given saved opt out When reopening preferences Then the choice is retained`() = runBlocking {
        val directory = Files.createTempDirectory("telemetry-consent").toFile()
        val file = directory.resolve("settings.preferences_pb")
        val firstJob = SupervisorJob()
        val secondJob = SupervisorJob()
        try {
            val firstStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + firstJob), produceFile = { file })
            TelemetryConsentRepositoryImpl(DatastoreDevPulsePreferences(firstStore)).setConsent(
                DPAnalyticsConsent(crashReporting = false, usageAnalytics = true, performanceMetrics = true),
            )
            firstJob.cancelAndJoin()
            val secondStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + secondJob), produceFile = { file })
            val repository = TelemetryConsentRepositoryImpl(DatastoreDevPulsePreferences(secondStore))
            assertEquals(DPAnalyticsConsent(crashReporting = false, usageAnalytics = true, performanceMetrics = true), repository.observeConsent().first())
            repository.setConsent(DPAnalyticsConsent(crashReporting = true, usageAnalytics = false, performanceMetrics = false))
            assertEquals(DPAnalyticsConsent(crashReporting = true, usageAnalytics = false, performanceMetrics = false), repository.observeConsent().first())
        } finally {
            firstJob.cancelAndJoin()
            secondJob.cancelAndJoin()
            directory.deleteRecursively()
        }
    }
}
