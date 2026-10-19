package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKey
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeyValue
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.getValue
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryConsent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TelemetryConsentRepositoryTest {
    @Test
    fun `Given no saved choice When loading Then both purposes are denied`() = runTest {
        val repository = TelemetryConsentRepositoryImpl(MemoryPreferences())

        assertEquals(TelemetryConsent(), repository.load().getOrThrow())
    }

    @Test
    fun `Given separate purpose choices When saving and reloading Then one snapshot preserves both`() = runTest {
        val preferences = MemoryPreferences()
        val repository = TelemetryConsentRepositoryImpl(preferences)

        repository.save(TelemetryConsent(analytics = true, diagnostics = false)).getOrThrow()
        assertEquals(
            TelemetryConsent(analytics = true, diagnostics = false),
            TelemetryConsentRepositoryImpl(preferences).load().getOrThrow(),
        )
        repository.save(TelemetryConsent(analytics = false, diagnostics = true)).getOrThrow()
        assertEquals(
            TelemetryConsent(analytics = false, diagnostics = true),
            repository.load().getOrThrow(),
        )
        assertEquals("telemetryConsent.v1", preferences.stored?.key?.name)
    }

    @Test
    fun `Given malformed or unsupported snapshots When loading Then grants are rejected`() = runTest {
        val preferences = MemoryPreferences()
        val repository = TelemetryConsentRepositoryImpl(preferences)
        repository.save(TelemetryConsent(analytics = true)).getOrThrow()
        assertEquals(TelemetryConsent(analytics = true), repository.load().getOrThrow())

        listOf(
            "not json",
            """{"schemaVersion":2,"policyVersion":1,"analytics":true,"diagnostics":true}""",
            """{"schemaVersion":1,"policyVersion":2,"analytics":true,"diagnostics":true}""",
            """{"schemaVersion":1,"policyVersion":1,"analytics":true}""",
            """{"schemaVersion":1,"policyVersion":1,"analytics":"yes","diagnostics":false}""",
        ).forEach { snapshot ->
            preferences.set(DevPulsePreferenceKeys.telemetryConsent, snapshot)
            assertEquals(TelemetryConsent(), repository.load().getOrThrow(), snapshot)
        }
    }

    @Test
    fun `Given a failed write When saving a grant Then the prior denied snapshot survives`() = runTest {
        val preferences = MemoryPreferences()
        val repository = TelemetryConsentRepositoryImpl(preferences)
        repository.save(TelemetryConsent()).getOrThrow()
        preferences.failure = IllegalStateException("disk full")

        val result = repository.save(TelemetryConsent(analytics = true))
        assertEquals("disk full", result.exceptionOrNull()?.message)
        preferences.failure = null
        assertEquals(TelemetryConsent(), repository.load().getOrThrow())
    }

    @Test
    fun `Given canceled storage When loading or saving Then cancellation propagates`() = runTest {
        val preferences = MemoryPreferences()
        val repository = TelemetryConsentRepositoryImpl(preferences)
        preferences.failure = CancellationException("canceled")

        assertFailsWith<CancellationException> { repository.load() }
        assertFailsWith<CancellationException> { repository.save(TelemetryConsent()) }
        preferences.failure = null
        repository.save(TelemetryConsent(diagnostics = true)).getOrThrow()
        assertEquals(TelemetryConsent(diagnostics = true), repository.load().getOrThrow())
    }

    private class MemoryPreferences : DevPulsePreferences {
        var stored: DevPulsePreferenceKeyValue<*>? = null
        var failure: Exception? = null

        override suspend fun <T> set(key: DevPulsePreferenceKey<T>, value: T) {
            failure?.let { throw it }
            stored = DevPulsePreferenceKeyValue(key, value)
        }

        override suspend fun <T> get(key: DevPulsePreferenceKey<T>): T? {
            failure?.let { throw it }
            return listOfNotNull(stored).getValue(key)
        }

        override fun <T> observe(key: DevPulsePreferenceKey<T>): Flow<T?> =
            flowOf(listOfNotNull(stored).getValue(key))

        override suspend fun set(vararg pairs: DevPulsePreferenceKeyValue<*>) {
            failure?.let { throw it }
            stored = pairs.single()
        }

        override fun observe(
            vararg keys: Pair<DevPulsePreferenceKey<*>, *>,
        ): Flow<List<DevPulsePreferenceKeyValue<*>>> = flowOf(listOfNotNull(stored))
    }
}
