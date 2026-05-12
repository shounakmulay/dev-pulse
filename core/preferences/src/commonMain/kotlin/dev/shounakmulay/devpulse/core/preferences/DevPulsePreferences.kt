package dev.shounakmulay.devpulse.core.preferences

import kotlinx.coroutines.flow.Flow


interface DevPulsePreferences {

    suspend fun <T> set(key: DevPulsePreferenceKey<T>, value: T)
    suspend fun <T> get(key: DevPulsePreferenceKey<T>): T?

    fun <T> observe(key: DevPulsePreferenceKey<T>): Flow<T?>
}



