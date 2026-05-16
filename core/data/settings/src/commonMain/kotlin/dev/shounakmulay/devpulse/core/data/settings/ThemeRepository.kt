package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun getThemeMode(): ThemeMode?
    suspend fun setBlackMode(blackMode: Boolean)
    suspend fun getBlackMode(): Boolean?
    fun observeThemeSettings(
        fallbackThemeMode: ThemeMode,
        fallbackBlackMode: Boolean
    ): Flow<ThemeSettings>
}