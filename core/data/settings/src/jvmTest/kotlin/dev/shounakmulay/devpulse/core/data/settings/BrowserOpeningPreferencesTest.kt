package dev.shounakmulay.devpulse.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.shounakmulay.devpulse.core.data.preferences.DatastoreDevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class BrowserOpeningPreferencesTest {
    @Test
    fun `Given no preference or unknown value When observing Then the reader is the default`() = runBlocking {
        val directory = Files.createTempDirectory("article-opening").toFile()
        val job = SupervisorJob()
        try {
            val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job), produceFile = { directory.resolve("settings.preferences_pb") })
            val preferences = DatastoreDevPulsePreferences(store)
            val repository = ContentSettingsRepositoryImpl(preferences)
            assertEquals(BrowserOpeningMode.IN_APP_BROWSER, repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first())
            preferences.set(DevPulsePreferenceKeys.browserOpeningMode, "FUTURE_MODE")
            assertEquals(BrowserOpeningMode.IN_APP_BROWSER, repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first())
        } finally {
            job.cancelAndJoin()
            directory.deleteRecursively()
        }
    }

    @Test
    fun `Given browser choice When reopening preferences Then the choice is retained`() = runBlocking {
        val directory = Files.createTempDirectory("article-opening").toFile()
        val file = directory.resolve("settings.preferences_pb")
        val firstJob = SupervisorJob()
        val secondJob = SupervisorJob()
        try {
            val firstStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + firstJob), produceFile = { file })
            ContentSettingsRepositoryImpl(DatastoreDevPulsePreferences(firstStore)).setBrowserOpeningMode(BrowserOpeningMode.EXTERNAL_BROWSER)
            firstJob.cancelAndJoin()
            val secondStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + secondJob), produceFile = { file })
            val repository = ContentSettingsRepositoryImpl(DatastoreDevPulsePreferences(secondStore))
            assertEquals(BrowserOpeningMode.EXTERNAL_BROWSER, repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first())
            repository.setBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER)
            assertEquals(BrowserOpeningMode.IN_APP_BROWSER, repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first())
        } finally {
            firstJob.cancelAndJoin()
            secondJob.cancelAndJoin()
            directory.deleteRecursively()
        }
    }

    @Test
    fun `Given storage failure When reading or writing Then repository throws the failure`() = runBlocking {
        val failure = IllegalStateException("disk unavailable")
        val store = object : DataStore<Preferences> {
            override val data = flow<Preferences> { throw failure }
            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences = throw failure
        }
        val repository = ContentSettingsRepositoryImpl(DatastoreDevPulsePreferences(store))
        assertSame(failure, assertFailsWith<IllegalStateException> { repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first() })
        assertSame(failure, assertFailsWith<IllegalStateException> { repository.setBrowserOpeningMode(BrowserOpeningMode.EXTERNAL_BROWSER) })
    }
}
