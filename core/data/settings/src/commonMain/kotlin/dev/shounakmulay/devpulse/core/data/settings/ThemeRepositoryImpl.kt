package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Factory

@Factory
class ThemeRepositoryImpl(
    private val preferences: DevPulsePreferences
) : ThemeRepository {
    override fun observeThemeSettings(
        fallbackThemeMode: ThemeMode,
        fallbackBlackMode: Boolean
    ): Flow<ThemeSettings> {
        return combine(
            preferences.observe(DevPulsePreferenceKeys.appTheme),
            preferences.observe(DevPulsePreferenceKeys.isAppInBlackMode),
        ) { theme, isAppInBlackMode ->
            ThemeSettings(
                mode = theme?.let { ThemeMode.valueOf(it) } ?: fallbackThemeMode,
                blackMode = isAppInBlackMode ?: fallbackBlackMode
            )
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        preferences.set(DevPulsePreferenceKeys.appTheme, mode.name)
    }

    override suspend fun getThemeMode(): ThemeMode? {
        val themeModePref =
            preferences.get(DevPulsePreferenceKeys.appTheme) ?: return null
        return ThemeMode.valueOf(themeModePref)
    }

    override suspend fun setBlackMode(blackMode: Boolean) {
        preferences.set(DevPulsePreferenceKeys.isAppInBlackMode, blackMode)
    }

    override suspend fun getBlackMode(): Boolean? {
        return preferences.get(DevPulsePreferenceKeys.isAppInBlackMode)
    }
}