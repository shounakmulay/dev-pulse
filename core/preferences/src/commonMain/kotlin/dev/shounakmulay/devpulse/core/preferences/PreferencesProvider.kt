package dev.shounakmulay.devpulse.core.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "devpulse_default.preferences_pb"

internal expect fun getFilePath(fileName: String = dataStoreFileName): String

internal fun getPreferenceDataStore() = PreferenceDataStoreFactory.createWithPath {
    getFilePath(dataStoreFileName).toPath()
}

