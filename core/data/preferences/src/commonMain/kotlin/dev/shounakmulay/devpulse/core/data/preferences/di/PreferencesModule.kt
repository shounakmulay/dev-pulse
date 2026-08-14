package dev.shounakmulay.devpulse.core.data.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.shounakmulay.devpulse.core.data.preferences.DatastoreDevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.getPreferenceDataStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.data.preferences")
class PreferencesModule {

    @Single
    fun provideDataStorePreferences() = getPreferenceDataStore()

    @Single
    fun provideDevPulsePreferences(dataStore: DataStore<Preferences>): DevPulsePreferences =
        DatastoreDevPulsePreferences(dataStore)
}