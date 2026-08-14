package dev.shounakmulay.devpulse.core.data.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class DevPulsePreferenceKeyValue<T>(
    val key: DevPulsePreferenceKey<T>,
    val value: T
)

@Suppress("UNCHECKED_CAST")
fun <T> List<DevPulsePreferenceKeyValue<*>>.getValue(key: DevPulsePreferenceKey<T>): T? {
    return firstOrNull { it.key == key }?.value as? T
}

sealed class DevPulsePreferenceKey<T>(val name: String) {
    class IntPreferenceKey(name: String) : DevPulsePreferenceKey<Int>(name)
    class DoublePreferenceKey(name: String) : DevPulsePreferenceKey<Double>(name)
    class FloatPreferenceKey(name: String) : DevPulsePreferenceKey<Float>(name)
    class BooleanPreferenceKey(name: String) : DevPulsePreferenceKey<Boolean>(name)
    class StringPreferenceKey(name: String) : DevPulsePreferenceKey<String>(name)
}

@Suppress("UNCHECKED_CAST")
fun <T> DevPulsePreferenceKey<T>.toDataStorePreferenceKey(): Preferences.Key<T> {
    return when (this) {
        is DevPulsePreferenceKey.BooleanPreferenceKey -> booleanPreferencesKey(name)
        is DevPulsePreferenceKey.IntPreferenceKey -> intPreferencesKey(name)
        is DevPulsePreferenceKey.StringPreferenceKey -> stringPreferencesKey(name)
        is DevPulsePreferenceKey.DoublePreferenceKey -> doublePreferencesKey(name)
        is DevPulsePreferenceKey.FloatPreferenceKey -> floatPreferencesKey(name)
    } as Preferences.Key<T>
}