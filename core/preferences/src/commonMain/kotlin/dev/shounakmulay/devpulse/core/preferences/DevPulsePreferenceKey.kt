package dev.shounakmulay.devpulse.core.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class DevPulsePreferenceKey<T>(val name: String) {
    class IntPreferenceKey(name: String) : DevPulsePreferenceKey<Int>(name)
    class BooleanPreferenceKey(name: String) : DevPulsePreferenceKey<Boolean>(name)
    class StringPreferenceKey(name: String) : DevPulsePreferenceKey<String>(name)
}

@Suppress("UNCHECKED_CAST")
fun <T> DevPulsePreferenceKey<T>.toDataStorePreferenceKey(): Preferences.Key<T> {
    return when (this) {
        is DevPulsePreferenceKey.BooleanPreferenceKey -> booleanPreferencesKey(name)
        is DevPulsePreferenceKey.IntPreferenceKey -> intPreferencesKey(name)
        is DevPulsePreferenceKey.StringPreferenceKey -> stringPreferencesKey(name)
    } as Preferences.Key<T>
}