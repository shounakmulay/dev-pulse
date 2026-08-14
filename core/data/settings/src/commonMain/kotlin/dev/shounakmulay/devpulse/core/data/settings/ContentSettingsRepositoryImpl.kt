package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeyValue
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.data.preferences.getValue
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.ContentTextSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ContentSettingsRepositoryImpl(
    private val preferences: DevPulsePreferences
) : ContentSettingsRepository {
    override fun observeContentTextSettings(defaultSettings: ContentTextSettings): Flow<ContentTextSettings> {
        return preferences.observe(
            DevPulsePreferenceKeys.contentTextScale to defaultSettings.textScale,
            DevPulsePreferenceKeys.lineHeightScale to defaultSettings.lineHeightScale
        ).map {
            val textScale = it.getValue(
                key = DevPulsePreferenceKeys.contentTextScale
            ) ?: defaultSettings.textScale
            val textSpacing = it.getValue(
                key = DevPulsePreferenceKeys.lineHeightScale
            ) ?: defaultSettings.lineHeightScale
            ContentTextSettings(textScale, textSpacing)
        }
    }

    override suspend fun updateContentTextSettings(settings: ContentTextSettings) {
        preferences.set(
            DevPulsePreferenceKeyValue(
                DevPulsePreferenceKeys.contentTextScale,
                settings.textScale
            ),
            DevPulsePreferenceKeyValue(
                DevPulsePreferenceKeys.lineHeightScale,
                settings.lineHeightScale
            )
        )
    }

    override suspend fun setContentTextScale(scale: Float) {
        preferences.set(DevPulsePreferenceKeys.contentTextScale, scale)
    }

    override suspend fun setContentLineHeightScale(spacing: Float) {
        preferences.set(DevPulsePreferenceKeys.lineHeightScale, spacing)
    }
}