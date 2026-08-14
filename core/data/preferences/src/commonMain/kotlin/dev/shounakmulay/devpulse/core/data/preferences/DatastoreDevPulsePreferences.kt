package dev.shounakmulay.devpulse.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DatastoreDevPulsePreferences(
    private val dataStore: DataStore<Preferences>
) : DevPulsePreferences {
    override suspend fun <T> set(key: DevPulsePreferenceKey<T>, value: T) {
        dataStore.edit {
            it[key.toDataStorePreferenceKey()] = value
        }
    }

    override suspend fun set(vararg pairs: DevPulsePreferenceKeyValue<*>) {
        dataStore.edit {
            for (pair in pairs) {
                it[pair.key.toDataStorePreferenceKey()] = pair.value
            }
        }
    }

    override suspend fun <T> get(key: DevPulsePreferenceKey<T>): T? {
        return dataStore.data.firstOrNull()?.get(key.toDataStorePreferenceKey())
    }

    override fun <T> observe(key: DevPulsePreferenceKey<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            preferences[key.toDataStorePreferenceKey()]
        }
    }

    override fun observe(vararg keys: Pair<DevPulsePreferenceKey<*>, *>): Flow<List<DevPulsePreferenceKeyValue<*>>> {
        return dataStore.data.map { preferences ->
            keys.map { (key, defaultValue) ->
                when (key) {
                    is DevPulsePreferenceKey.BooleanPreferenceKey -> DevPulsePreferenceKeyValue(
                        key,
                        preferences[key.toDataStorePreferenceKey()] ?: defaultValue as Boolean
                    )

                    is DevPulsePreferenceKey.DoublePreferenceKey -> DevPulsePreferenceKeyValue(
                        key,
                        preferences[key.toDataStorePreferenceKey()] ?: defaultValue as Double
                    )

                    is DevPulsePreferenceKey.FloatPreferenceKey -> DevPulsePreferenceKeyValue(
                        key,
                        preferences[key.toDataStorePreferenceKey()] ?: defaultValue as Float
                    )

                    is DevPulsePreferenceKey.IntPreferenceKey -> DevPulsePreferenceKeyValue(
                        key,
                        preferences[key.toDataStorePreferenceKey()] ?: defaultValue as Int
                    )

                    is DevPulsePreferenceKey.StringPreferenceKey -> DevPulsePreferenceKeyValue(
                        key,
                        preferences[key.toDataStorePreferenceKey()] ?: defaultValue as String
                    )
                }
            }
        }
    }
}