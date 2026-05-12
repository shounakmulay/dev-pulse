package dev.shounakmulay.devpulse.core.preferences

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

    override suspend fun <T> get(key: DevPulsePreferenceKey<T>): T? {
        return dataStore.data.firstOrNull()?.get(key.toDataStorePreferenceKey())
    }

    override fun <T> observe(key: DevPulsePreferenceKey<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            preferences[key.toDataStorePreferenceKey()]
        }
    }

}