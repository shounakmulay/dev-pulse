package dev.shounakmulay.devpulse.core.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.shounakmulay.devpulse.core.preferences.DatastoreDevPulsePreferences
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.preferences.getPreferenceDataStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.preferences")
class PreferencesModule {

    @Single
    fun provideDataStorePreferences() = getPreferenceDataStore()

    @Single
    fun provideDevPulsePreferences(dataStore: DataStore<Preferences>): DevPulsePreferences =
        DatastoreDevPulsePreferences(dataStore)
}