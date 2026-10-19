package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKey
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.remoteconfig.ConfigStore
import kotlinx.coroutines.CancellationException

internal class PreferencesConfigStore(private val preferences: DevPulsePreferences) : ConfigStore {
    private val key = DevPulsePreferenceKey.StringPreferenceKey("observability.config.v1")

    override suspend fun read(): Result<String?> = safely { preferences.get(key) }
    override suspend fun write(value: String): Result<Unit> = safely { preferences.set(key, value) }
    override suspend fun clear(): Result<Unit> = safely { preferences.set(key, "") }

    private suspend fun <T> safely(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        Result.failure(failure)
    }
}
