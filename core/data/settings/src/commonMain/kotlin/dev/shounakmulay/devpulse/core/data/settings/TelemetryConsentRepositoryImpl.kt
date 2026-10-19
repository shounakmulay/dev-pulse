package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryConsent
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory

@Factory
class TelemetryConsentRepositoryImpl(
    private val preferences: DevPulsePreferences,
) : TelemetryConsentRepository {
    private val json = Json

    override suspend fun load(): Result<TelemetryConsent> = attempt {
        val encoded = preferences.get(DevPulsePreferenceKeys.telemetryConsent)
            ?: return@attempt TelemetryConsent()
        if (encoded.length > 1024) return@attempt TelemetryConsent()
        val snapshot = try {
            json.decodeFromString<StoredConsent>(encoded)
        } catch (_: SerializationException) {
            return@attempt TelemetryConsent()
        }
        if (snapshot.schemaVersion != 1 || snapshot.policyVersion != 1) {
            return@attempt TelemetryConsent()
        }
        TelemetryConsent(
            analytics = snapshot.analytics,
            diagnostics = snapshot.diagnostics,
            policyVersion = snapshot.policyVersion,
        )
    }

    override suspend fun save(consent: TelemetryConsent): Result<Unit> = attempt {
        require(consent.policyVersion == 1)
        val snapshot = StoredConsent(
            schemaVersion = 1,
            policyVersion = consent.policyVersion,
            analytics = consent.analytics,
            diagnostics = consent.diagnostics,
        )
        preferences.set(
            key = DevPulsePreferenceKeys.telemetryConsent,
            value = json.encodeToString(snapshot),
        )
    }

    private suspend fun <T> attempt(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Exception) {
        Result.failure(failure)
    }

    @Serializable
    private data class StoredConsent(
        val schemaVersion: Int,
        val policyVersion: Int,
        val analytics: Boolean,
        val diagnostics: Boolean,
    )
}
